package com.huc.android_ble_monitor;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final long SCAN_PERIOD = 10000;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning = false;
    private Handler mHandler = new Handler();
    private List<ScanResult> mScanResultList = new ArrayList<>();
    private ListView mListView;
    private ScanResultArrayAdapter mScanResultAdapter;
    private BluetoothLeScanner mBleScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.switchFromSplashToMainTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        // setSupportActionBar(myToolbar);

        mListView = findViewById(R.id.deviceList);

        mScanResultAdapter = new ScanResultArrayAdapter(this, mScanResultList);
        mListView.setAdapter(mScanResultAdapter);

        requestLocationPermission();
        checkBleAvailability();

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        if(checkForBluetoothEnabled()){
            scanBleDevices(true);
        }
    }

    private void scanBleDevices(final boolean enable){
        if(enable){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mScanning) {
                        mScanning = false;
                        mBleScanner.stopScan(mScanCallback);
                    }
                }
            }, SCAN_PERIOD);
            mScanning = true;
            mBleScanner.startScan(mScanCallback);
        }else{
            mScanning = false;
            mBleScanner.stopScan(mScanCallback);
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        mScanning = false;
                        break;
                    case BluetoothAdapter.STATE_ON:
                        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        mBleScanner = mBluetoothAdapter.getBluetoothLeScanner();
                        scanBleDevices(true);
                        break;
                }
            }
        }
    };

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if(!containsDevice(mScanResultList, result)){
                mScanResultList.add(result);
                mScanResultAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.scanBleDevices(false);
        unregisterReceiver(mReceiver);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void checkBleAvailability(){
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean checkForBluetoothEnabled(){
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBleScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (this.mBluetoothAdapter == null || !this.mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            // if bluetooth enabled
            scanBleDevices(true);
        }
    }

    private boolean containsDevice(List<ScanResult> resList, ScanResult res) {
        for (ScanResult dev : resList) {
            if (dev.getDevice().getAddress().equals(res.getDevice().getAddress())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets back main theme to default AppTheme
     */
    private void switchFromSplashToMainTheme() {
        setTheme(R.style.AppTheme); // Go back from splash screen to main theme on activity create
	}
}
