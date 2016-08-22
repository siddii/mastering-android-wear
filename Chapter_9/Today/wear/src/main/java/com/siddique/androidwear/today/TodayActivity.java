package com.siddique.androidwear.today;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;


public class TodayActivity extends Activity implements
        WearableListView.ClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = TodayActivity.class.getName();
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError;
    private String spokenText;


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
        } else if (viewHolder.getAdapterPosition() == 1) {
            Intent intent = new Intent(this, OnThisDayActivity.class);
            startActivity(intent);
        } else if (viewHolder.getAdapterPosition() == 2) {
            Intent intent = new Intent(this, TodosActivity.class);
            startActivity(intent);
        } else if (viewHolder.getAdapterPosition() == 3) {
            Intent intent = new Intent(this, StepCounterActivity.class);
            startActivity(intent);
        }
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == Constants.SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            spokenText = results.get(0);
            // Do something with spokenText
            Log.i(TAG, "Spoken Text = " + spokenText);

            if (spokenText.startsWith("home") || spokenText.startsWith("work")) {
                Log.i(TAG, "Creating Google Api Client");
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addApi(Wearable.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();
                mGoogleApiClient.connect();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, Constants.SPEECH_REQUEST_CODE);
    }

    @Override
    public void onTopEmptyRegionClick() {

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to Data Api");
        if (spokenText != null) {
            if (spokenText.startsWith("home")) {
                String todoItem = spokenText.substring("home".length());
                sendMessage(Constants.HOME_TODO_ITEM, todoItem.getBytes());
            } else if (spokenText.startsWith("work")) {
                String todoItem = spokenText.substring("work".length());
                sendMessage(Constants.WORK_TODO_ITEM, todoItem.getBytes());
            }
        }
    }

    private void sendMessage(final String path, final byte[] data) {
        Log.i(TAG, "Sending message to path " + path);
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(
                new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                        for (Node node : nodes.getNodes()) {
                            Wearable.MessageApi
                                    .sendMessage(mGoogleApiClient, node.getId(), path, data);
                            spokenText = null;
                        }
                    }
                });
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Connection Failed " + connectionResult);
    }

    @Override
    protected void onStop() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
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
