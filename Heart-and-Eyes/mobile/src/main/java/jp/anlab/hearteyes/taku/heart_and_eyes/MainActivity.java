package jp.anlab.hearteyes.taku.heart_and_eyes;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
//import com.jins_jp.meme.MemeLib;
//import com.jins_jp.meme.MemeScanListener;
//import com.jins_jp.meme.MemeStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jins_jp.meme.*;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,  DataApi.DataListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private GoogleApiClient googleApiClient;

    private static final String startWearableActivityPath = "/start/wearable/activity/path";
    private static final String finishWearableActivityPath = "/finish/wearable/activity/path";
    private static final String HEART_RATE_DATA_KEY = "heart_rate_key";
    private static final String NOW_UNIXTIME_KEY = "unixtime_key";
    private static final String appClientId = "644888294591737";
    private static final String appClientSecret = "thcs0frfoihf03ux5m2mj26rv0c26onh";

    private Button startWearableActivityBtn;
    private Button finishWearableActivityBtn;
    boolean wearlableAppRunning = false;
    boolean graphDrawing = false;

//    XYSeriesCollection dataset;
//    XYSeries series;
//    AFreeChart chart;

    GraphView heartbeatgraph;
    ArrayList<Integer> wellnessArray;
    GraphView blinkgraph;
    ArrayList<Integer> blinkArray;

    ArrayList<Integer> outWellnessArray;
    ArrayList<Long> outTimeArray;

    long utc;
    FileOutputStream fos = null;
    String FILE_PATH;

    // ViewFlipper
    ViewFlipper viewFlipper;
    Button prebutton;
    Button nextbutton;
    int graphTitleJudge;

    Toolbar toolBar;

    // JiNSMEME
    MemeLib memeLib;
    List<String> scannedAddresses;
    int blinkTimes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        if (savedInstanceState == null){
            jinsinit();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // toobarの初期化&設定
        toolbarIniti();

    }

    private void toolbarIniti() {
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        googleApiClient.connect();
//        wellnessArray = new ArrayList<Integer>();
        outWellnessArray = new ArrayList<>();
        outTimeArray = new ArrayList<>();
        utc = System.currentTimeMillis();
        File file = new File(Environment.getExternalStorageDirectory(), this.getPackageName());
        if (!file.exists()) {
            Log.d(TAG, "フォルダ作成しました");
            file.mkdir();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                break;
            case R.id.action_scan:
                if (!memeLib.isScanning()){
                    jinsmemeScan();
                } else if (memeLib.isScanning()) {
                    memeLib.stopScan();
                    scannedAddresses.clear();
                }
                break;
            case R.id.action_disconnect:
                memeLib.disconnect();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void jinsmemeScan() {
        if (memeLib.isScanning()) {
            return;
        }
        MemeStatus status = memeLib.startScan(new MemeScanListener() {
            @Override
            public void memeFoundCallback(String address) {
                scannedAddresses.add(address);
                if (scannedAddresses.size() == 0) {
                    Toast.makeText(MainActivity.this, "JiNSMEME not found", Toast.LENGTH_SHORT).show();
                } else {
        //            // TODO: 2016/04/13 ダイアログイベント
        //            Log.d(TAG, "jins found");
                    final String[] items = (String[]) scannedAddresses.toArray(new String[0]);
                    new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Select JiNSMEME")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // item_which pressed
                                memeLib.setMemeConnectListener(new MemeConnectListener() {
                                    @Override
                                    public void memeConnectCallback(boolean status) {
                                        memeLib.startDataReport(memeRealtimeListener);
                                    }
                                    @Override
                                    public void memeDisconnectCallback() {
                                        Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                memeLib.connect(items[which]);
                            }
                        }).show();
                }
            }
        });
        if (status == MemeStatus.MEME_ERROR_APP_AUTH) {
            Toast.makeText(this, "App Auth Failed", Toast.LENGTH_LONG).show();
        }
    }

    // TODO: 2016/04/13 JiNSMEME DATA
    final MemeRealtimeListener memeRealtimeListener = new MemeRealtimeListener() {
        @Override
        public void memeRealtimeCallback(final MemeRealtimeData memeRealtimeData) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    jinsmemeData(memeRealtimeData);
                }
            });
        }
    };

    private void jinsmemeData(MemeRealtimeData memeRealtimeData) {
        Log.d("fit_status", "" + memeRealtimeData.getFitError());
        Log.d("walking", "" + memeRealtimeData.isWalking());
        Log.d("noise_status", "" + memeRealtimeData.isNoiseStatus());
        Log.d("power_left", "" + memeRealtimeData.getPowerLeft());
        Log.d("eye_move_up", "" + memeRealtimeData.getEyeMoveUp());
        Log.d("eye_move_down", "" + memeRealtimeData.getEyeMoveDown());
        Log.d("eye_move_left", "" + memeRealtimeData.getEyeMoveLeft());
        Log.d("eye_move_right", "" + memeRealtimeData.getEyeMoveRight());
        Log.d("blink_streangth", "" + memeRealtimeData.getBlinkStrength());
        Log.d("blink_speed", "" + memeRealtimeData.getBlinkSpeed());
        Log.d("roll", "" + memeRealtimeData.getRoll());
        Log.d("pitch", "" + memeRealtimeData.getPitch());
        Log.d("yaw", "" + memeRealtimeData.getYaw());
        Log.d("acc_x", "" + memeRealtimeData.getAccX());
        Log.d("acc_y", "" + memeRealtimeData.getAccY());
        Log.d("acc_z", "" + memeRealtimeData.getAccZ());
        if (memeRealtimeData.getBlinkSpeed() > 0){
            blinkTimes++;
        }
    }

    // TODO: 2016/04/07 output csv
    public void fileOutPut() {
        String message = "";
        FILE_PATH = Environment.getExternalStorageDirectory() + "/" + this.getPackageName() + "/" + utc + ".csv";
        // 書き込み
        Log.d(TAG, "write run");
        try {
            fos = new FileOutputStream(FILE_PATH,true);
            for (int i = 0; i < wellnessArray.size(); i++) {
                String outStr = outTimeArray.get(i) + ":" + outWellnessArray.get(i) + ",";
                fos.write(outStr.getBytes());
            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initialize() {
        startWearableActivityBtn = (Button)findViewById(R.id.start_wearable_activity);
        startWearableActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!wearlableAppRunning) {
                    new StartWearableActivityThread().start();
                    wearlableAppRunning = true;
                    startWearableActivityBtn.setText("Graph Drawing");
                } else if (wearlableAppRunning) {
                    // TODO: 2016/04/12 グラフの描画開始
                    graphDrawing = true;
                }
            }
        });

        finishWearableActivityBtn = (Button)findViewById(R.id.finish_wearable_activity);
        finishWearableActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FinishWearableActivityThread().start();
                fileOutPut();
            }
        });

        viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
        graphTitleJudge = 0;

        prebutton = (Button)findViewById(R.id.prebtn);
        prebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showPrevious();
            }
        });

        nextbutton = (Button)findViewById(R.id.nextbtn);
        nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
            }
        });


        /**
         *
         * @param createChart(ArrayList, GraphTitle, Ymin, Ymax)
         */
        heartbeatgraph = (GraphView) findViewById(R.id.graphview);
        // 初期化
        // グラフに表示するデータを生成
        wellnessArray = new ArrayList<Integer>();
        // グラフを生成
        heartbeatgraph.createChart(wellnessArray, "心拍数", 40.0d, 150.0d);

        blinkgraph = (GraphView)findViewById(R.id.blinkgraph);
        blinkArray = new ArrayList<Integer>();
        blinkgraph.createChart(blinkArray, "まばたき", 0.0d, 10.0d);
    }

    private void jinsinit() {
        MemeLib.setAppClientID(getApplicationContext(), appClientId, appClientSecret);
        memeLib = MemeLib.getInstance();
        scannedAddresses = new ArrayList<>();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(googleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            // TYPE_DELETEDがデータ削除時、TYPE_CHANGEDがデータ登録・変更時
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.d("TAG", "DataItem deleted: " + event.getDataItem().getUri());
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.d("TAG", "DataItem changed: " + event.getDataItem().getUri());
                // 更新されたデータを取得する
                DataMap dataMap = DataMap.fromByteArray(event.getDataItem().getData());
//                data = dataMap.getInt("key");
                Log.d(TAG, "data : " + dataMap.getInt(HEART_RATE_DATA_KEY) + " : " + dataMap.getLong(NOW_UNIXTIME_KEY));
                outWellnessArray.add(dataMap.getInt(HEART_RATE_DATA_KEY));
                outTimeArray.add(dataMap.getLong(NOW_UNIXTIME_KEY));
                drawGraph(dataMap.getInt(HEART_RATE_DATA_KEY), dataMap.getLong(NOW_UNIXTIME_KEY));
            }
        }
    }

    private void drawGraph(int anInt, long aLong) {
        if (graphDrawing) {
            wellnessArray.add(anInt);
            heartbeatgraph.createChart(wellnessArray, "心拍数", 40.0d, 150.0d);
            heartbeatgraph.invalidate();
            blinkArray.add(blinkTimes);
            blinkgraph.createChart(blinkArray, "まばたき", 0.0d, 10.0d);
            blinkgraph.invalidate();
        }
    }

    private class StartWearableActivityThread extends Thread{
        public void run() {
            NodeApi.GetConnectedNodesResult nodesResult = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
            for (Node node : nodesResult.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleApiClient, node.getId(), startWearableActivityPath, null).await();
                if (result.getStatus().isSuccess()) {
                    Log.d(TAG, "message is ok");
                    Log.d(TAG, "To : " + node.getDisplayName());
                } else {
                    Log.d(TAG, "send error");
                }
            }
        }
    }

    private class FinishWearableActivityThread extends Thread{
        public void run() {
            NodeApi.GetConnectedNodesResult nodesResult = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
            for (Node node : nodesResult.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleApiClient, node.getId(), finishWearableActivityPath, null).await();
                if (result.getStatus().isSuccess()) {
                    Log.d(TAG, "message is ok");
                    Log.d(TAG, "To : " + node.getDisplayName());
                } else {
                    Log.d(TAG, "send error");
                }
            }
        }
    }
}
