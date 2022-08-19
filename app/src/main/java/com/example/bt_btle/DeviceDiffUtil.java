package com.example.bt_btle;

import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;

public class DeviceDiffUtil extends DiffUtil.Callback {
    private final ArrayList<String> mOldList;
    private final ArrayList<String> mNewList;

    DeviceDiffUtil(ArrayList<String> oldList, ArrayList<String> newList) {
        mOldList = oldList;
        mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).equals(mNewList.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).equals(mNewList.get(newItemPosition));
    }
}
