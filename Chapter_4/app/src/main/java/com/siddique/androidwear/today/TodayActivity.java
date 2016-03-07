package com.siddique.androidwear.today;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;


public class TodayActivity extends Activity implements WearableListView.ClickListener {

    private static final String TAG = TodayActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WearableListView listView = (WearableListView) findViewById(R.id.action_list);
        listView.setAdapter(new ListViewAdapter(this));
        listView.setClickListener(this);
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        Log.i(TAG, "Clicked list item" + viewHolder.getAdapterPosition());
        if (viewHolder.getAdapterPosition() == 0) {
            Intent intent = new Intent(this, DayOfYearActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onTopEmptyRegionClick() {

    }

    private static final class ListViewAdapter extends WearableListView.Adapter {
        private final Context mContext;
        private final LayoutInflater mInflater;
        private String[] actions = null;

        private ListViewAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
            actions = mContext.getResources().getStringArray(R.array.actions);
        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WearableListView.ViewHolder(
                    mInflater.inflate(R.layout.list_item, null));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            TextView view = (TextView) holder.itemView.findViewById(R.id.name);
            view.setText(actions[position]);
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return actions.length;
        }
    }

}
