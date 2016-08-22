package com.siddique.androidwear.today;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.drawer.WearableActionDrawer;
import android.support.wearable.view.drawer.WearableDrawerLayout;
import android.support.wearable.view.drawer.WearableNavigationDrawer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class TodosActivity extends WearableActivity implements
        WearableActionDrawer.OnMenuItemClickListener {

    private static final String TAG = TodosActivity.class.getName();

    private WearableDrawerLayout mWearableDrawerLayout;
    private WearableNavigationDrawer mWearableNavigationDrawer;
    private WearableActionDrawer mWearableActionDrawer;

    private List<TodoItemType> todoItemTypes = Arrays.asList(TodoItemType.HOME, TodoItemType.WORK);
    private TodoItemType mSelectedTodoItemType;

    private TodoItemTypeFragment mTodoItemTypeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_todo_main);
        setAmbientEnabled();

        mSelectedTodoItemType = TodoItemType.HOME;

        // Initialize content
        mTodoItemTypeFragment = new TodoItemTypeFragment();
        Bundle args = new Bundle();

        args.putString(TodoItemTypeFragment.ARG_TODO_TYPE, mSelectedTodoItemType.toString());

        mTodoItemTypeFragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, mTodoItemTypeFragment).commit();


        // Main Wearable Drawer Layout that wraps all content
        mWearableDrawerLayout = (WearableDrawerLayout) findViewById(R.id.drawer_layout);

        // Top Navigation Drawer
        mWearableNavigationDrawer =
                (WearableNavigationDrawer) findViewById(R.id.top_navigation_drawer);

        Log.i(TAG, "mWearableNavigationDrawer  = " + mWearableNavigationDrawer);
        mWearableNavigationDrawer.setAdapter(new NavigationAdapter(this));

        // Peeks Navigation drawer on the top.
        mWearableDrawerLayout.peekDrawer(Gravity.TOP);

        // Bottom Action Drawer
        mWearableActionDrawer =
                (WearableActionDrawer) findViewById(R.id.bottom_action_drawer);

        mWearableActionDrawer.setOnMenuItemClickListener(this);

        // Peeks action drawer on the bottom.
        mWearableDrawerLayout.peekDrawer(Gravity.BOTTOM);

    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        Log.d(TAG, "onMenuItemClick(): " + menuItem);

        final int itemId = menuItem.getItemId();

        String toastMessage = "";

        switch (itemId) {
            case R.id.menu_add_todo:
                toastMessage = "Adding " + mSelectedTodoItemType.getTypeValue() + " Todo";
                break;
            case R.id.menu_update_todo:
                toastMessage = "Updating " + mSelectedTodoItemType.getTypeValue() + " Todo";
                break;
            case R.id.menu_clear_todos:
                toastMessage = "Clearing " + mSelectedTodoItemType.getTypeValue() + " Todos";
                break;
        }

        mWearableDrawerLayout.closeDrawer(mWearableActionDrawer);

        if (toastMessage.length() > 0) {
            Toast toast = Toast.makeText(
                    getApplicationContext(),
                    toastMessage,
                    Toast.LENGTH_SHORT);
            toast.show();
            return true;
        } else {
            return false;
        }
    }

    private final class NavigationAdapter
            extends WearableNavigationDrawer.WearableNavigationDrawerAdapter {

        private final Context mContext;

        public NavigationAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return todoItemTypes.size();
        }

        @Override
        public void onItemSelected(int position) {
            Log.d(TAG, "WearableNavigationDrawerAdapter.onItemSelected(): " + position);
            mSelectedTodoItemType = todoItemTypes.get(position);

            String selectedTodoImage = mSelectedTodoItemType.getBackgroundImage();
            int drawableId =
                    getResources().getIdentifier(selectedTodoImage, "drawable", getPackageName());
            mTodoItemTypeFragment.updateFragment(mSelectedTodoItemType);
        }

        @Override
        public String getItemText(int pos) {
            return todoItemTypes.get(pos).getTypeValue();
        }

        @Override
        public Drawable getItemDrawable(int position) {

            mSelectedTodoItemType = todoItemTypes.get(position);

            String navigationIcon = mSelectedTodoItemType.getBackgroundImage();

            int drawableNavigationIconId =
                    getResources().getIdentifier(navigationIcon, "drawable", getPackageName());

            return mContext.getDrawable(drawableNavigationIconId);
        }
    }

    /**
     * Fragment that appears in the "content_frame", just shows the currently selected todo item type.
     */
    public static class TodoItemTypeFragment extends Fragment {
        public static final String ARG_TODO_TYPE = "todo_type";

        TextView titleView = null;
        TextView descView = null;

        public TodoItemTypeFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_todo_item, container, false);

            titleView = (TextView) rootView.findViewById(R.id.todo_card_title);
            descView = (TextView) rootView.findViewById(R.id.todo_card_desc);

            String todoType = getArguments().getString(ARG_TODO_TYPE);
            TodoItemType todoItemType = TodoItemType.valueOf(todoType);
            updateFragment(todoItemType);

            return rootView;
        }

        public void updateFragment(TodoItemType todoItemType) {
            titleView.setText(todoItemType.getTypeValue() + " Todos");
            descView.setText("List description");
        }
    }
}
