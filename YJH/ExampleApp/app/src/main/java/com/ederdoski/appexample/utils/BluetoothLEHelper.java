package com.ederdoski.appexample.utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ederdoski.appexample.MainActivity;
import com.ederdoski.appexample.interfaces.BleCallback;
import com.ederdoski.appexample.models.BluetoothLE;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.support.constraint.Constraints.TAG;
import static com.ederdoski.appexample.MainActivity.bleCallbacks;
import static com.ederdoski.appexample.utils.Constants.LEFT;
import static com.ederdoski.appexample.utils.Constants.MAC_ADDR_left;
import static com.ederdoski.appexample.utils.Constants.MAC_ADDR_right;
import static com.ederdoski.appexample.utils.Constants.RIGHT;
import static com.ederdoski.appexample.utils.Constants.UUID_CHAR;


public class BluetoothLEHelper {

    private Activity act;

    private Map<String, BluetoothDevice> scanResults;
    private ScanCallback scanCallback;
    private BluetoothLeScanner bluetoothLeScanner;
    public Handler scanHandler;

    private ArrayList<BluetoothLE> aDevices     = new ArrayList<>();

    private BleCallback bleCallback;
    private BluetoothGatt    mBluetoothGatt_right;
    private BluetoothGatt    mBluetoothGatt_left;
    private BluetoothAdapter mBluetoothAdapter;

    public static BluetoothDevice device_right;
    public static BluetoothDevice device_left;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTED    = 1;
    private int              mConnectionState   = STATE_DISCONNECTED;
    private int state_right = STATE_DISCONNECTED;
    private int state_left = STATE_DISCONNECTED;

    private static long SCAN_PERIOD             = 3000;
    private static boolean mScanning_right            = false;
    private static boolean mScanning_left             = false;
    private static String FILTER_SERVICE        = "";

    private ConnectThread connectThread_right;
    private ConnectThread connectThread_left;

    private ReadThread readThread_right;
    private ReadThread readThread_left;

    public BluetoothLEHelper(Activity _act){
        if(Functions.isBleSupported(_act)) {
            act = _act;
            BluetoothManager bluetoothManager = (BluetoothManager) act.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            mBluetoothAdapter.enable();
        }
    }

//    public void startScan(int index) {
//        Handler mHandler = new Handler();
//
//        String MAC = "";
//        if (index == RIGHT) {
//            mScanning_right = true;
//            MAC = Constants.MAC_ADDR_right;
//        }
//        else if (index == LEFT) {
//            mScanning_left = true;
//            MAC = Constants.MAC_ADDR_left;
//        }
//
////        List<ScanFilter> filters = new ArrayList<>();
////        ScanFilter scan_filter = new ScanFilter.Builder()
////                .setDeviceAddress(MAC)
////                .build();
////
////        filters.add(scan_filter);
////
////        // ScanSettings 설정. 저전력 모드로 스캔
////        ScanSettings settings = new ScanSettings.Builder()
////                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
////                .build();
////
////        // ScanCallback 설정
////        scanResults = new HashMap<>();
////        scanCallback = new BLEScanCallback(scanResults);
//
//        // 스캐너 startScan
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(index == RIGHT) {
//                    mScanning_right = false;
//                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                }
//                else if(index == LEFT) {
//                    mScanning_left = false;
//                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                }
//            }
//        }, SCAN_PERIOD);
//
//        if(!FILTER_SERVICE.equals("")) {
//            UUID[] filter  = new UUID[1];
//            filter [0]     = UUID.fromString(FILTER_SERVICE);
//            mBluetoothAdapter.startLeScan(filter, mLeScanCallback);
//        }else{
//            mBluetoothAdapter.startLeScan(mLeScanCallback);
//        }
//
////        bluetoothLeScanner.startScan(filters, settings, scanCallback);
//    }

//    public class BLEScanCallback extends ScanCallback {
//        private Map<String, BluetoothDevice> cbScanResults;
//
//        BLEScanCallback(Map<String, BluetoothDevice> scanResults) {
//            cbScanResults = scanResults;
//        }
//
//        @Override
//        public void onScanResult(int callbackType, ScanResult result) {
//            Log.d(TAG, "onScanResult");
//            addScanResult(result);
//        }
//
//        @Override
//        public void onBatchScanResults(List<ScanResult> results) {
//            for(ScanResult result: results) {
//                addScanResult(result);
//            }
//        }
//
//        @Override
//        public void onScanFailed(int error) {
//            Log.e(TAG, "BLE scan failed with code" + error);
//        }
//
//        private void addScanResult(ScanResult result) {
//            BluetoothDevice device = result.getDevice();
//            String deviceAddress = device.getAddress();
//            cbScanResults.put(deviceAddress, device);
//
//            Log.d(TAG, "scan results device: " + device);
//
//            // BLE Scanner 선언하고 스캔 방식 바꾸기..ㅅㅂ...
//        }
//
//    }


//public void stopScan(int index) {
//        if (index == RIGHT) {
//            if(mScanning_right && mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && bluetoothLeScanner != null) {
//                bluetoothLeScanner.stopScan(scanCallback);
//                scanComplete(index);
//            }
//            mScanning_right = false;
//        }
//        else if (index == LEFT) {
//            if (mScanning_left && mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && bluetoothLeScanner != null) {
//                bluetoothLeScanner.stopScan(scanCallback);
//                scanComplete(index);
//            }
//            mScanning_left = false;
//        }
//        if(!isScanning_right() && !isScanning_left()) {
//            scanCallback = null;
//            scanHandler = null;
//        }
//}
//
//private void scanComplete(int index) {
//        if(scanResults.isEmpty()) {
//            Toast.makeText(act, "스캔 결과가 없습니다.", Toast.LENGTH_SHORT).show();
//        }
//
//        for(String deviceAddr : scanResults.keySet()) {
//            Log.d(TAG, "Found device: " + deviceAddr);
//            if(index == RIGHT) {
//                device_right = scanResults.get(deviceAddr);
//                if(MAC_ADDR_right.equals(deviceAddr)) {
//                    Log.d(TAG, "connecting device: " + deviceAddr);
//                    //connectDevice(device);
//                    connect(device_right, bleCallbacks());
//                }
//            }
//            else if(index == LEFT) {
//                device_left = scanResults.get(deviceAddr);
//                if(MAC_ADDR_left.equals(deviceAddr)) {
//                    Log.d(TAG, "connecting device: " + deviceAddr);
//                    //connectDevice(device);
//                    connect(device_left, bleCallbacks());
//                }
//            }
//
//        }
//
//}

    // 스캔
    public void scanLeDevice(boolean enable, int index) {
        Handler mHandler = new Handler();

        if (enable) {
            if(index == RIGHT) {
                mScanning_right = true;
                Log.d(TAG, "오른쪽 scanLeDevice");
            }
            else if(index == LEFT) {
                mScanning_left = true;
                Log.d(TAG, "왼쪽 scanLeDevice");
            }

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(index == RIGHT) {mScanning_right = false;}
                    else if(index == LEFT) {mScanning_left = false;}
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            if(!FILTER_SERVICE.equals("")) {
                UUID[] filter  = new UUID[1];
                filter [0]     = UUID.fromString(FILTER_SERVICE);
                Log.d(TAG, "startLeScan 실행");
                mBluetoothAdapter.startLeScan(filter, mLeScanCallback);
            }
        }
        else {
            if(index == RIGHT) {mScanning_right = false;}
            else if (index == LEFT) {mScanning_left = false;}
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            Log.d(TAG, "onLeScan: " +device.getAddress());
            act.runOnUiThread(new Runnable() {
                public void run() {
//                    if(aDevices.size() > 0) {
//
//                        boolean isNewItem = true;
//                        Log.d(TAG, "aDevices 0이상");
//
//                        for (int i = 0; i < aDevices.size(); i++) {
//                            Log.d(TAG, "aDevices: " + aDevices.get(i).getMacAddress());
//                            if (aDevices.get(i).getMacAddress().equals(device.getAddress())) {
//                                Log.d(TAG, "현재 devcie: " + device.getAddress());
//                                isNewItem = false;
//                            }
//                        }
//
//                        if(isNewItem) {
//                            if(mScanning_right) {
//                                if(device.getAddress().equals(MAC_ADDR_right)) {
//                                    connectThread_right = new ConnectThread(device, bleCallbacks());
//                                    connectThread_right.start();
//                                }
//                            }
//                            else if(mScanning_left) {
//                                if(device.getAddress().equals(MAC_ADDR_left)) {
//                                    connectThread_left = new ConnectThread(device, bleCallbacks());
//                                    connectThread_left.start();
//                                }
//                            }
//
//                        }
//
//                    }
                    Log.d(TAG, "mScanning_rihgt = " + mScanning_right);
                    Log.d(TAG, "mScanning_left = " + mScanning_left);

                    if(mScanning_right && device.getAddress().equals(MAC_ADDR_right)) {
                        Log.d(TAG, "LeScanCallback에서 connectThread실행 : " + device.getAddress());
                        connectThread_right = new ConnectThread(device, MainActivity.bleCallbacks());
                        connectThread_right.start();
                    }
                    else if(mScanning_left && device.getAddress().equals(MAC_ADDR_left)) {
                        Log.d(TAG, "LeScanCallback에서 connectThread실행 : " + device.getAddress());
                        connectThread_left = new ConnectThread(device, MainActivity.bleCallbacks());
                        connectThread_left.start();
                    }
                }
            });
        }
    };

    public ArrayList<BluetoothLE> getListDevices(){
        return aDevices;
    }

    public void connect(BluetoothDevice device, BleCallback _bleCallback){

        if(device == device_right) {
            Log.d(TAG, "helper의 connect 안 = " + device.getName());

            if(mBluetoothGatt_right == null && !isConnected_right()) {
                bleCallback = _bleCallback;
                mBluetoothGatt_right = device.connectGatt(act, true, mGattCallback);
                readThread_right = new ReadThread(RIGHT);
                readThread_right.start();
            }
        }
        else if(device == device_left) {
            Log.d(TAG, "helper의 connect 안 = " + device.getName());

            if(mBluetoothGatt_left == null && !isConnected_left()) {
                bleCallback = _bleCallback;
                mBluetoothGatt_left = device.connectGatt(act, true, mGattCallback);
                readThread_left = new ReadThread(LEFT);
                readThread_left.start();
            }
        }
    }

    public void disconnect(int index){
        if(index == RIGHT) {
            if(mBluetoothGatt_right != null && isConnected_right()) {
                state_right = STATE_DISCONNECTED;
                mBluetoothGatt_right.disconnect();
                mBluetoothGatt_right.close();
                mBluetoothGatt_right = null;
            }
        }
        else if(index == LEFT) {
            if(mBluetoothGatt_left != null && isConnected_left()) {
                state_left = STATE_DISCONNECTED;
                mBluetoothGatt_left.disconnect();
                mBluetoothGatt_left.close();
                mBluetoothGatt_left = null;
            }
        }
    }

    public boolean isReadyForScan(){

        return Permissions.checkPermisionStatus(act, Manifest.permission.BLUETOOTH)
                && Permissions.checkPermisionStatus(act, Manifest.permission.BLUETOOTH_ADMIN)
                && Permissions.checkPermisionStatus(act, Manifest.permission.ACCESS_COARSE_LOCATION) && Functions.getStatusGps(act);
    }

    public class ConnectThread extends  Thread {
        BluetoothDevice device;
        BleCallback bleCallback;
        int index;

        ConnectThread(BluetoothDevice _device, BleCallback _bleCallback) {
            device = _device;
            bleCallback = _bleCallback;
            if(device.getAddress().equals(MAC_ADDR_right)) {
                index = RIGHT;
                device_right = device;
            }
            if(device.getAddress().equals(MAC_ADDR_left)) {
                index = LEFT;
                device_left = device;
            }
        }
        public void run() {
            try {
                Log.d(TAG, device.getName() + "쓰레드 안");
                connect(device, MainActivity.bleCallbacks());
            } catch (Exception e) {
                disconnect(index);
                Log.e(TAG, index + "connect fail", e);
                e.printStackTrace();
            }
        }
    }

    public class ReadThread extends Thread {
        int index = -1;

        public ReadThread(int _index) {
            index = _index;
        }

        public void run() {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, index + "Read실행");

            if(index == RIGHT) {
                while(isConnected_right()) {
                    read(Constants.SERVICE_STRING, Constants.CHARACTERISTIC_STRING, RIGHT);
                }
            }
            else if(index == LEFT) {
                while(isConnected_left()) {
                    read(Constants.SERVICE_STRING, Constants.CHARACTERISTIC_STRING, LEFT);
                }
            }
        }
    }

    public void read(String service, String characteristic, int index){
        Log.i(TAG, "Helper의 read 실행");
        if(index == RIGHT) {
            mBluetoothGatt_right.readCharacteristic(mBluetoothGatt_right.getService(Constants.UUID_SERVICE).getCharacteristic(UUID_CHAR));
        }
        else if(index == LEFT) {
            mBluetoothGatt_left.readCharacteristic(mBluetoothGatt_left.getService(Constants.UUID_SERVICE).getCharacteristic(UUID_CHAR));
        }
    }

    private final BluetoothGattCallback mGattCallback;
    {
        mGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.i("BluetoothLEHelper", "Attempting to start service discovery: " + gatt.discoverServices());
                    if(gatt.getDevice().equals(device_right)) {
                        state_right = STATE_CONNECTED;
                    }
                    else if(gatt.getDevice().equals(device_left)) {
                        state_left = STATE_CONNECTED;
                    }
                }

                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    if(gatt.getDevice().equals(device_right)) {
                        state_right = STATE_DISCONNECTED;
                    }
                    else if(gatt.getDevice().equals(device_left)) {
                        state_left = STATE_DISCONNECTED;
                    }
                }

                bleCallback.onBleConnectionStateChange(gatt, status, newState);
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                bleCallback.onBleServiceDiscovered(gatt, status);
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                bleCallback.onBleWrite(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                Log.i(TAG, "Helper의 onCharacteristicRead");
                bleCallback.onBleRead(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                Log.i(TAG, "Helper의 onCharacteristicChanged");
                bleCallback.onBleCharacteristicChange(gatt, characteristic);


            }

        };
    }


//
//    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
//        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
//            Log.w(TAG, "BluetoothAdapter not initialized");
//            return;
//        }
//        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
//
//        if(Constants.UUID_CHAR.equals(characteristic.getUuid())) {
//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(Constants.UUID_DESCRIPTOR);
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            mBluetoothGatt.writeDescriptor(descriptor);
//        }
//    }


    public boolean isScanning_right() {return mScanning_right;}
    public boolean isScanning_left(){ return mScanning_left;  }

    public boolean isConnected_right() {
        return state_right == STATE_CONNECTED;
    }
    public boolean isConnected_left() {
        return state_left == STATE_CONNECTED;
    }

    public void setScanPeriod(int scanPeriod){
        SCAN_PERIOD = scanPeriod;
    }

    public long getScanPeriod(){
        return SCAN_PERIOD;
    }

    public void setFilterService(String filterService){
        FILTER_SERVICE = filterService;
    }

    public BluetoothGattCallback getGatt(){
        return mGattCallback;
    }


}