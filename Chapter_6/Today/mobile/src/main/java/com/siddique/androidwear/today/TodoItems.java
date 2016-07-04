package com.siddique.androidwear.today;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by siddique on 7/3/16.
 */
public class TodoItems {


    private static final String PREF_NAME = "TodoItems";
    public static final String TAG = TodoItems.class.getName();

    public static void saveItems(Context context, String todoType, Set<String> todoItems) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(todoType, todoItems);
        editor.apply();
    }

    public static Set<String> readItems(Context context, String todoType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet(todoType, new HashSet<String>());
    }

    public static void removeItem(Context context, String[] todoItemTypes, String itemText) {
        for (String todoItemType : todoItemTypes) {
            Set<String> todoItems = readItems(context, todoItemType);
            Log.i(TAG, "Todo Items = " + todoItems + " - contains = " + todoItems.contains(todoItemType + " - " + itemText));
            for (String todoItem : todoItems) {
                if ((todoItemType + " - " + todoItem).equals(itemText)) {
                    todoItems.remove(todoItem);
                    saveItems(context, todoItemType, todoItems);
                    return;
                }
            }
        }
    }
}
