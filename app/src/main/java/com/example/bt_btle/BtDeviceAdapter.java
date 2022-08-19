package com.example.bt_btle;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt_btle.databinding.ItemActivityMainBinding;

import java.util.ArrayList;

public class BtDeviceAdapter extends RecyclerView.Adapter<BtDeviceAdapter.DeviceViewHolder> {

    private ArrayList<String> mValues;

    BtDeviceAdapter(ArrayList<String> values) {
        mValues = values;
    }

    @NonNull
    @Override
    public BtDeviceAdapter.DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeviceViewHolder(
                ItemActivityMainBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull BtDeviceAdapter.DeviceViewHolder holder, int position) {
        String itemInfo = mValues.get(position);
        holder.mDeviceInfo.setText(itemInfo);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public ArrayList<String> getValues() {
        return mValues;
    }

    public void setValues(ArrayList<String> values) {
        DeviceDiffUtil diffUtil = new DeviceDiffUtil(mValues, values);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtil);
        mValues = values;
        diffResult.dispatchUpdatesTo(this);
    }

    public void add(String el) {
        ArrayList<String> newList = new ArrayList<>(mValues);
        newList.add(el);
        DeviceDiffUtil diffUtil = new DeviceDiffUtil(mValues, newList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtil);

        mValues = newList;
        diffResult.dispatchUpdatesTo(this);
    }

    public void clear() {
        DeviceDiffUtil diffUtil = new DeviceDiffUtil(mValues, new ArrayList<>());
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtil);

        mValues.clear();
        diffResult.dispatchUpdatesTo(this);
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {

        public TextView mDeviceInfo;

        public DeviceViewHolder(ItemActivityMainBinding binding) {
            super(binding.getRoot());
            mDeviceInfo = binding.textItem;
        }
    }
}