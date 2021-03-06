package com.huc.android_ble_monitor.activities;

import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.huc.android_ble_monitor.R;
import com.huc.android_ble_monitor.adapters.ServicesListAdapter;
import com.huc.android_ble_monitor.models.BluLeDevice;
import com.huc.android_ble_monitor.util.ActivityUtil;
import com.huc.android_ble_monitor.util.PropertyResolver;
import com.huc.android_ble_monitor.viewmodels.DeviceDetailViewModel;

import java.util.ArrayList;

public class DeviceDetailActivity extends BaseActivity<DeviceDetailViewModel> {
    private static final String TAG = "BLEM_DeviceDetailAct";

    public static BluLeDevice staticBleDevice;
    private ListView mListViewOfServices;
    private PropertyResolver mResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
        ActivityUtil.setToolbar(this, false);
        mResolver = new PropertyResolver();
        setObservers();
    }

    @Override
    protected void onServiceBinded() {
        mBluetoothLeService.requestRssi(true);
        mBluetoothLeService.getBluetoothDevice().observe(DeviceDetailActivity.this, new Observer<BluLeDevice>() {
            @Override
            public void onChanged(BluLeDevice bleDevice) {
                mViewModel.updateBleDevie(bleDevice);
            }
        });
        mBluetoothLeService.getCurrentRssi().observe(DeviceDetailActivity.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer rssi) {
                updateRSSI(rssi);
            }
        });
        mListViewOfServices.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ServiceDetailActivity.staticGattService = (BluetoothGattService)parent.getAdapter().getItem(position);
                ServiceDetailActivity.staticBleDevice = DeviceDetailActivity.staticBleDevice;
                Intent intent = new Intent(DeviceDetailActivity.this, ServiceDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setObservers(){
        mViewModel.getmBleDevice().observe(this, new Observer<BluLeDevice>() {
            @Override
            public void onChanged(BluLeDevice device) {
                Log.d(TAG, "onChanged: BleDevice value changed");
                if(device == null) return;
                initializeViews(device);
            }
        });
    }

    public void updateRSSI(int newRssi){
        TextView tvRssi = findViewById(R.id.RSSI_TextView);
        tvRssi.setText(mResolver.deviceRssiResolver(newRssi));
    }

    public void initializeViews(BluLeDevice device) {
        TextView tvName = findViewById(R.id.DeviceName_TextView);
        TextView tvAddress = findViewById(R.id.DeviceUUID_TextView);
        TextView tvBonded = findViewById(R.id.BondState_TextView);
        TextView tvRssi = findViewById(R.id.RSSI_TextView);
        TextView tvConnectability = findViewById(R.id.Connectability_TextView);
        TextView tvCompanyIdentifier = findViewById(R.id.CompanyIdentifier_TextView);
        ImageView ivBondstate = findViewById(R.id.BondState_ImageView);
        TextView tvServices = findViewById(R.id.Services_TextView);
        mListViewOfServices = findViewById(R.id.lv_services);

        //setting ListView Adapter
        ServicesListAdapter adapter = new ServicesListAdapter(this, new ArrayList<BluetoothGattService>());
        mListViewOfServices.setAdapter(adapter);

        ScanResult bleScanResult = device.mScanResult;
        tvBonded.setText(mResolver.bondStateTextResolver(bleScanResult));
        ivBondstate.setImageResource(mResolver.bondStateImageResolver(bleScanResult));
        tvName.setText(mResolver.deviceNameResolver(bleScanResult));
        tvAddress.setText(bleScanResult.getDevice().getAddress());
        tvRssi.setText(mResolver.deviceRssiResolver(device.mCurrentRssi));
        tvCompanyIdentifier.setText(mResolver.deviceManufacturerResolver(bleScanResult));
        tvConnectability.setText(mResolver.deviceConnectabilityResolver(bleScanResult));

        if(device.mBluetoothGatt != null)
            tvServices.setText("Services (" + device.mBluetoothGatt.getServices().size() + ")");
        else
            tvServices.setText("Services (0)");

        if (device.mBluetoothGatt != null) {
            adapter.clear();
            adapter.addAll(device.mBluetoothGatt.getServices());
            adapter.notifyDataSetChanged();
            if(adapter.getCount() > 0) {
                findViewById(R.id.loading_spinner).setVisibility(View.GONE);
                findViewById(R.id.lv_services).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void initializeViewModel() {
        mViewModel = new ViewModelProvider(this).get(DeviceDetailViewModel.class);
        mViewModel.init(staticBleDevice);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                BluLeDevice bleDevice =  mViewModel.getmBleDevice().getValue();
                if (bleDevice != null) {
                    if (bleDevice.mConnectionState == BluetoothProfile.STATE_CONNECTED) {
                        mBluetoothLeService.requestRssi(false);
                        mBluetoothLeService.disconnect();
                    }
                    return false;
                }
                break;
        }
        return true;
    }
}
