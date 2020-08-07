package com.example.ble2;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.vise.baseble.ViseBle;
import com.vise.baseble.callback.IConnectCallback;
import com.vise.baseble.callback.scan.IScanCallback;
import com.vise.baseble.callback.scan.ScanCallback;
import com.vise.baseble.callback.scan.SingleFilterScanCallback;
import com.vise.baseble.common.BleConfig;
import com.vise.baseble.common.ConnectState;
import com.vise.baseble.core.BluetoothGattChannel;
import com.vise.baseble.core.DeviceMirror;
import com.vise.baseble.core.DeviceMirrorPool;
import com.vise.baseble.exception.TimeoutException;
import com.vise.baseble.model.BluetoothLeDevice;
import com.vise.baseble.model.BluetoothLeDeviceStore;
import com.vise.log.ViseLog;

import java.util.UUID;

public class BLEActivity extends AppCompatActivity {

    Button connectBtn, stopScan;
    TextView txt_value;
    ListView listView;
    String dataValue;

    private static final String MacAddress_right = "4C:11:AE:C9:6C:B6";
    private static final UUID ServiceUUID = UUID.fromString("19b10000-e8f2-537e-4f6c-d104768a1214");
    private static final UUID CharacteristicUUID = UUID.fromString("00002A19-0000-1000-8000-00805F9B34FB");

    public static final String TAG = "BLE_ACTIVITY";

    final ViseBle viseBleSet = ViseBle.getInstance();
    BluetoothGattChannel bluetoothGattChannel_Noti;

    private Context context;//上下文
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private DeviceMirrorPool deviceMirrorPool;
    private DeviceMirror lastDeviceMirror;

    private static ViseBle instance;//入口操作管理
    private static BleConfig bleConfig = BleConfig.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);

        init(context);
        this.SetListener();
    }

    public void init(Context context) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

        connectBtn = (Button) findViewById(R.id.btn_startScan_ble);
        stopScan = (Button) findViewById(R.id.btn_stopScan_ble);
        txt_value = (TextView) findViewById(R.id.txt_value);
        listView = (ListView) findViewById(R.id.listView_git);

        if (this.context == null && context != null) {
            this.context = context.getApplicationContext();
            bluetoothManager = (BluetoothManager) this.context.getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
            deviceMirrorPool = new DeviceMirrorPool();
        }
    }

    public void SetListener() {
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viseBleSet.connectByMac(MacAddress_right, scanCallback);
            }
        });

    }

    public void connect(BluetoothLeDevice bluetoothLeDevice, IConnectCallback connectCallback) {
        if (bluetoothLeDevice == null || connectCallback == null) {
            ViseLog.e("This bluetoothLeDevice or connectCallback is null.");
            return;
        }
        if (deviceMirrorPool != null && !deviceMirrorPool.isContainDevice(bluetoothLeDevice)) {
            DeviceMirror deviceMirror = new DeviceMirror(bluetoothLeDevice);
            if (lastDeviceMirror != null && !TextUtils.isEmpty(lastDeviceMirror.getUniqueSymbol())
                    && lastDeviceMirror.getUniqueSymbol().equals(deviceMirror.getUniqueSymbol())) {
                deviceMirror = lastDeviceMirror;//防止重复创建设备镜像
            }
            deviceMirror.connect(connectCallback);
            lastDeviceMirror = deviceMirror;
        } else {
            ViseLog.i("This device is connected.");
        }
    }

    //연결
     public void connectByMac(String mac, final IConnectCallback connectCallback) {
        if (mac == null || connectCallback == null) {
            ViseLog.e("This mac or connectCallback is null.");
            return;
        }
        startScan(new SingleFilterScanCallback(new IScanCallback() {
            @Override
            public void onDeviceFound(BluetoothLeDevice bluetoothLeDevice) {

            }

            @Override
            public void onScanFinish(final BluetoothLeDeviceStore bluetoothLeDeviceStore) {
                if (bluetoothLeDeviceStore.getDeviceList().size() > 0) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            connect(bluetoothLeDeviceStore.getDeviceList().get(0), connectCallback);
                        }
                    });
                } else {
                    connectCallback.onConnectFailure(new TimeoutException());
                }
            }

            @Override
            public void onScanTimeout() {
                connectCallback.onConnectFailure(new TimeoutException());
            }

        }).setDeviceMac(mac));
    }

    public void startScan(ScanCallback scanCallback) {
        if (scanCallback == null) {
            throw new IllegalArgumentException("this ScanCallback is Null!");
        }
        scanCallback.setScan(true).scan();
    }

    public DeviceMirror getDeviceMirror(BluetoothLeDevice bluetoothLeDevice) {
        if (deviceMirrorPool != null) {
            return deviceMirrorPool.getDeviceMirror(bluetoothLeDevice);
        }
        return null;
    }

    public ConnectState getConnectState(BluetoothLeDevice bluetoothLeDevice) {
        if (deviceMirrorPool != null) {
            return deviceMirrorPool.getConnectState(bluetoothLeDevice);
        }
        return ConnectState.CONNECT_DISCONNECT;
    }

    public boolean isConnect(BluetoothLeDevice bluetoothLeDevice) {
        if (deviceMirrorPool != null) {
            return deviceMirrorPool.isContainDevice(bluetoothLeDevice);
        }
        return false;
    }

    public void disconnect(BluetoothLeDevice bluetoothLeDevice) {
        if (deviceMirrorPool != null) {
            deviceMirrorPool.disconnect(bluetoothLeDevice);
        }
    }

    public void disconnect() {
        if (deviceMirrorPool != null) {
            deviceMirrorPool.disconnect();
        }
    }

    public void clear() {
        if (deviceMirrorPool != null) {
            deviceMirrorPool.clear();
        }
    }

    public Context getContext() {
        return context;
    }

    public BluetoothManager getBluetoothManager() {
        return bluetoothManager;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public DeviceMirrorPool getDeviceMirrorPool() {
        return deviceMirrorPool;
    }

    public int getConnectRetryCount() {
        if (lastDeviceMirror == null) {
            return 0;
        }
        return lastDeviceMirror.getConnectRetryCount();
    }

    public int getReceiveDataRetryCount() {
        if (lastDeviceMirror == null) {
            return 0;
        }
        return lastDeviceMirror.getReceiveDataRetryCount();
    }



}
