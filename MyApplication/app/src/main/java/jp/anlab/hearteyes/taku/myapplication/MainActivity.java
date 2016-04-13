package jp.anlab.hearteyes.taku.myapplication;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ViewFlipper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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

//    GraphView heartbeatgraph;
    ArrayList<Integer> wellnessArray;
//    GraphView blinkgraph;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        initialize();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

//        startWearableActivityBtn = (Button)findViewById(R.id.start_wearable_activity);
//        startWearableActivityBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!wearlableAppRunning) {
//                    new StartWearableActivityThread().start();
//                    wearlableAppRunning = true;
//                    startWearableActivityBtn.setText("Graph Drawing");
//                } else if (wearlableAppRunning) {
//                    // TODO: 2016/04/12 グラフの描画開始
//                    graphDrawing = true;
//                }
//            }
//        });
//
//        finishWearableActivityBtn = (Button)findViewById(R.id.finish_wearable_activity);
//        finishWearableActivityBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new FinishWearableActivityThread().start();
//                fileOutPut();
//            }
//        });
//
//        viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
////        graphTitleText = (TextView)findViewById(R.id.textView2);
//        graphTitleJudge = 0;
////        graphTitle();
//
//        prebutton = (Button)findViewById(R.id.prebtn);
//        prebutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewFlipper.showPrevious();
//            }
//        });
//
//        nextbutton = (Button)findViewById(R.id.nextbtn);
//        nextbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewFlipper.showNext();
//            }
//        });
//
//
//        heartbeatgraph = (GraphView) findViewById(R.id.graphview);
//        // 初期化
//        // グラフに表示するデータを生成
//        wellnessArray = new ArrayList<Integer>();
//        // グラフを生成
//        heartbeatgraph.createChart(wellnessArray, "心拍数");
//
//        blinkgraph = (GraphView)findViewById(R.id.blinkgraph);
//        blinkArray = new ArrayList<Integer>();
//        blinkgraph.createChart(blinkArray, "まばたき");

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
//            heartbeatgraph.createChart(wellnessArray, "心拍数");
//            heartbeatgraph.invalidate();
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
