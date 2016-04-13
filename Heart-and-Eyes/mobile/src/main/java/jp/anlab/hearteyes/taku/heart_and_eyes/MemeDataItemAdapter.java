package jp.anlab.hearteyes.taku.heart_and_eyes;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jins_jp.meme.MemeRealtimeData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taku on 2016/04/13.
 */
public class MemeDataItemAdapter extends BaseAdapter {
    private static final String TAG = MemeDataItemAdapter.class.getSimpleName();
    private final MemeDataItemAdapter self = this;
    List<String[]> items;
    Context context;

    public MemeDataItemAdapter(Context context) {
        this.context = context;
        updateMemeData(new MemeRealtimeData());
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(context, R.layout.meme_data_item, null);
        }

        TextView labelTextView = (TextView)view.findViewById(R.id.item_label);
        TextView valueTextView = (TextView)view.findViewById(R.id.item_value);

        String[] item = items.get(i);
        labelTextView.setText(item[0]);
        valueTextView.setText(item[1]);

        return view;
    }

    public void updateMemeData(MemeRealtimeData d) {
        items = new ArrayList<>();
        addItem(R.string.fit_status, d.getFitError());
        addItem(R.string.walking, d.isWalking());
        addItem(R.string.noise_status, d.isNoiseStatus());
        addItem(R.string.power_left, d.getPowerLeft());
        addItem(R.string.eye_move_up, d.getEyeMoveUp());
        addItem(R.string.eye_move_down, d.getEyeMoveDown());
        addItem(R.string.eye_move_left, d.getEyeMoveLeft());
        addItem(R.string.eye_move_right, d.getEyeMoveRight());
        addItem(R.string.blink_streangth, d.getBlinkStrength());
        addItem(R.string.blink_speed, d.getBlinkSpeed());
        addItem(R.string.roll, d.getRoll());
        addItem(R.string.pitch, d.getPitch());
        addItem(R.string.yaw, d.getYaw());
        addItem(R.string.acc_x, d.getAccX());
        addItem(R.string.acc_y, d.getAccY());
        addItem(R.string.acc_z, d.getAccZ());
    }

    private String getLabel(int resourceId) {
        return context.getResources().getString(resourceId);
    }

    private void addItem(int resourceId, Object value) {
        items.add(new String[] {getLabel(resourceId), String.valueOf(value)});
    }
}
