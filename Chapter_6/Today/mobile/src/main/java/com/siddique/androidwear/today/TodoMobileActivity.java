package com.siddique.androidwear.today;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class TodoMobileActivity extends AppCompatActivity {

    private ListView mTaskListView;
    private ArrayAdapter<String> mAdapter;

    public static final String TAG = TodoMobileActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_mobile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTaskListView = (ListView) findViewById(R.id.list_todo);
        refreshItems();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addTodo);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View addTodoItemView = inflater.inflate(R.layout.add_todo_item, null);


                    final Spinner spinner = (Spinner) addTodoItemView.findViewById(R.id.todoItemType);
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(TodoMobileActivity.this,
                            R.array.todoItemTypes, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                    AlertDialog dialog = new AlertDialog.Builder(TodoMobileActivity.this)
                            .setTitle("Add a new todo item")
                            .setView(addTodoItemView)
                            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText taskEditText = (EditText) addTodoItemView.findViewById(R.id.todoItem);
                                    Log.i(TAG, "Todo Item = " + taskEditText.getText());

                                    Spinner todoItemTypeSpinner = (Spinner) addTodoItemView.findViewById(R.id.todoItemType);
                                    String todoItemType = (String) todoItemTypeSpinner.getSelectedItem();
                                    Log.i(TAG, "Todo Item type = " + todoItemType);

                                    String task = String.valueOf(taskEditText.getText());
                                    Set<String> todoItems = TodoItems.readItems(TodoMobileActivity.this, todoItemType);
                                    todoItems.add(task);
                                    TodoItems.saveItems(TodoMobileActivity.this, todoItemType, todoItems);
                                    refreshItems();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .create();
                    dialog.show();
                }
            });
        }
    }

    private void refreshItems() {
        ArrayList<String> taskList = new ArrayList<>();

        String[] todoItemTypes = getResources().getStringArray(R.array.todoItemTypes);
        for (String todoItemType : todoItemTypes) {
            Set<String> todoItems = TodoItems.readItems(this, todoItemType);
            for (String todoItem : todoItems) {
                taskList.add(todoItemType + " - " + todoItem);
            }
        }

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.item_todo,
                    R.id.task_title,
                    taskList);
            mTaskListView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void deleteTodoItem(View view) {
        View parent = (View) view.getParent();
        TextView textView = (TextView) parent.findViewById(R.id.task_title);

        String removingItem = (String) textView.getText();
        Log.i(TAG, "Removing Item = " + removingItem);


        String[] todoItemTypes = getResources().getStringArray(R.array.todoItemTypes);
        TodoItems.removeItem(this, todoItemTypes, removingItem);
        refreshItems();
    }
}
