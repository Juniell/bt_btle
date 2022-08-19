package com.example.bt_btle;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt_btle.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final static String SAVED_ARRAY = "array"; // used to identify array in Bundle

    private BluetoothAdapter mBtAdapter;
    private BtDeviceAdapter mBtArrayAdapter;
    private ActivityResultLauncher<Intent> bluetoothOnLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mBtAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        mBtArrayAdapter = new BtDeviceAdapter(new ArrayList<>());
        RecyclerView mDevicesRecyclerView = binding.devicesRecyclerView;
        mDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mDevicesRecyclerView.setAdapter(mBtArrayAdapter); // assign model to view

        String[] permissions = new String[0];
        ArrayList<String> permissionsList = new ArrayList<>();

        // Ask for location permission if not already allowed
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // Ask for bluetooth permission for API 31 and higher if not already allowed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
                permissionsList.add(Manifest.permission.BLUETOOTH_CONNECT);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)
                permissionsList.add(Manifest.permission.BLUETOOTH_SCAN);
        }

        // Ask permission
        if (!permissionsList.isEmpty())
            ActivityCompat.requestPermissions(this, permissionsList.toArray(permissions), 1);


        if (mBtAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(getApplicationContext(), "Bluetooth device not found!", Toast.LENGTH_SHORT).show();
        } else {
            binding.scan.setOnClickListener(v -> bluetoothOn());
            binding.off.setOnClickListener(v -> bluetoothOff());
            binding.discover.setOnClickListener(v -> discover());

            bluetoothOnLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        // Make sure the request was successful
                        if (result.getResultCode() == RESULT_OK) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Bluetooth turned on",
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Unable to turn on Bluetooth",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<String> savedValues = savedInstanceState.getStringArrayList(SAVED_ARRAY);
        if (savedValues != null)
            mBtArrayAdapter.setValues(savedValues);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(SAVED_ARRAY, mBtArrayAdapter.getValues());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        try {
            unregisterReceiver(blReceiver);
        } catch (IllegalArgumentException e) {
            Log.e("BroadcastReceiver", e.getMessage());
        }
    }

    @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
    private void bluetoothOn() {
        if (!mBtAdapter.isEnabled()) {
            Intent intent = new Intent();
            intent.setAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            bluetoothOnLauncher.launch(intent);
        } else
            Toast.makeText(getApplicationContext(), "Bluetooth is already on", Toast.LENGTH_SHORT).show();
    }

    @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
    private void bluetoothOff() {
        mBtAdapter.disable(); // turn off
        Toast.makeText(getApplicationContext(), "Bluetooth turned Off", Toast.LENGTH_SHORT).show();
    }

    @RequiresPermission(value = "android.permission.BLUETOOTH_SCAN")
    private void discover() {
        // Check if the device is already discovering
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(), "Discovery stopped", Toast.LENGTH_SHORT).show();
        } else {
            if (mBtAdapter.isEnabled()) {
                mBtArrayAdapter.clear(); // clear items
                boolean res = mBtAdapter.startDiscovery();
                System.out.println("START DISCOVERY = " + res);
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                String name = device.getName();
                if (name == null)
                    name = "";
                mBtArrayAdapter.add(name + "\n" + device.getAddress());
            }
        }
    };
}

