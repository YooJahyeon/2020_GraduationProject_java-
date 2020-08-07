package com.example.ble2;

import android.Manifest;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.vise.baseble.ViseBle;
import com.vise.baseble.callback.scan.IScanCallback;
import com.vise.baseble.callback.scan.ScanCallback;
import com.vise.baseble.core.BluetoothGattChannel;
import com.vise.baseble.model.BluetoothLeDevice;
import com.vise.baseble.model.BluetoothLeDeviceStore;
import com.vise.log.ViseLog;

import java.util.ArrayList;
import java.util.UUID;

// 스캔 기능만 있음
public class ScanActivity extends AppCompatActivity {

    Button startScan, stopScan;
    TextView txt_value;
    ListView listView;
    String dataValue;

    private static final UUID ServiceUUID = UUID.fromString("19b10000-e8f2-537e-4f6c-d104768a1214");
    private static final UUID CharacteristicUUID = UUID.fromString("00002A19-0000-1000-8000-00805F9B34FB");

    public static final String TAG = "BLE_ACTIVITY";

    final ViseBle viseBleSet = ViseBle.getInstance();
    BluetoothGattChannel bluetoothGattChannel_Noti;

    final ArrayList<String> arrayList_information = new ArrayList<>();
    final ArrayList<String> arrayList_address = new ArrayList<>();
    final ArrayList<String> arrayList_name = new ArrayList<>();
    final ArrayList<Integer> arrayList_rssi = new ArrayList<>();
    final ArrayList<ParcelUuid[]> arrayList_uuid = new ArrayList<>();
    final ArrayList<BluetoothLeDevice> arrayList_device = new ArrayList<>();

    private IScanCallback iScanCallback = new IScanCallback() {
        @Override
        public void onDeviceFound(BluetoothLeDevice bluetoothLeDevice) {
            arrayList_address.add(bluetoothLeDevice.getAddress());
            arrayList_name.add(bluetoothLeDevice.getName());
            arrayList_rssi.add(bluetoothLeDevice.getRssi());
            arrayList_device.add(bluetoothLeDevice);
            arrayList_uuid.add(bluetoothLeDevice.getDevice().getUuids());

            arrayList_information.add(("Device = " + bluetoothLeDevice.getName()
                    + "  UUID = " + bluetoothLeDevice.getDevice().getUuids()
                    + "Address = " + bluetoothLeDevice.getAddress()));

            ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    arrayList_information);
            listView.setAdapter(arrayAdapter);

        }

        @Override
        public void onScanFinish(BluetoothLeDeviceStore bluetoothLeDeviceStore) {
            ViseLog.i("scan finish " + bluetoothLeDeviceStore);
        }

        @Override
        public void onScanTimeout() {
            ViseLog.i("scan timeout");
        }
    };

    ScanCallback scanCallback = new ScanCallback(iScanCallback);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);

        init();
        this.SetListener();
    }

    public void init() {
        ViseBle.config()
                .setScanTimeout(-1)
                .setConnectTimeout(10 * 1000)
                .setOperateTimeout(5 * 1000)
                .setConnectRetryCount(3)
                .setConnectRetryInterval(1000)
                .setOperateRetryCount(3)
                .setOperateRetryInterval(1000)
                .setMaxConnectCount(3);
        ViseBle.getInstance().init(this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

        startScan = (Button) findViewById(R.id.btn_startScan_ble);
        stopScan = (Button) findViewById(R.id.btn_stopScan_ble);
        txt_value = (TextView) findViewById(R.id.txt_value);
        listView = (ListView) findViewById(R.id.listView_git);
    }

    public void SetListener() {
        startScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viseBleSet.startScan(scanCallback);
            }
        });

        stopScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viseBleSet.stopScan(scanCallback);
            }
        });
    }






}
