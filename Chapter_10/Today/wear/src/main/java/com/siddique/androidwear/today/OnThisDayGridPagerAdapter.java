/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.siddique.androidwear.today;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class OnThisDayGridPagerAdapter extends FragmentGridPagerAdapter {

    private final Context mContext;
    private OnThisDay onThisDay;
    private List<Row> mRows;
    private ColorDrawable mDefaultBg;

    private ColorDrawable mClearBg;

    public OnThisDayGridPagerAdapter(Context ctx, FragmentManager fm, OnThisDay onThisDay) {
        super(fm);
        mContext = ctx;
        this.onThisDay = onThisDay;

        mRows = new ArrayList<OnThisDayGridPagerAdapter.Row>();

        ArrayList<String> listItems = onThisDay.getListItems();

        for (String listItem: listItems) {
            mRows.add(new Row(cardFragment("On This Day - " + (listItems.indexOf(listItem) + 1), listItem)));
        }
    }

    private Fragment cardFragment(String title, String content) {
        Resources res = mContext.getResources();
        CardFragment fragment =
                CardFragment.create(title, content);
        // Add some extra bottom margin to leave room for the page indicator
        fragment.setCardMarginBottom(
                res.getDimensionPixelSize(R.dimen.card_margin_bottom));
        return fragment;
    }


    /** A convenient container for a row of fragments. */
    private class Row {
        final List<Fragment> columns = new ArrayList<Fragment>();

        public Row(Fragment... fragments) {
            for (Fragment f : fragments) {
                add(f);
            }
        }

        public void add(Fragment f) {
            columns.add(f);
        }

        Fragment getColumn(int i) {
            return columns.get(i);
        }

        public int getColumnCount() {
            return columns.size();
        }
    }

    @Override
    public Fragment getFragment(int row, int col) {
        Row adapterRow = mRows.get(row);
        return adapterRow.getColumn(col);
    }

    @Override
    public Drawable getBackgroundForRow(final int row) {
        return mContext.getResources().getDrawable(R.drawable.page_background);
    }

    @Override
    public int getRowCount() {
        return mRows.size();
    }

    @Override
    public int getColumnCount(int rowNum) {
        return mRows.get(rowNum).getColumnCount();
    }

}
