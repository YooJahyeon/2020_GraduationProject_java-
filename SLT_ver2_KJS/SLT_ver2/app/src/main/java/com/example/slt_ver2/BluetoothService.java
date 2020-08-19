package com.example.slt_ver2;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.slt_ver2.interfaces.BleCallback;
import com.example.slt_ver2.utils.Constants;
import com.example.slt_ver2.utils.Functions;
import com.example.slt_ver2.utils.Permissions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.UUID;


import static com.example.slt_ver2.utils.Constants.LEFT;
import static com.example.slt_ver2.utils.Constants.MAC_ADDR_left;
import static com.example.slt_ver2.utils.Constants.MAC_ADDR_right;
import static com.example.slt_ver2.utils.Constants.RIGHT;
import static com.example.slt_ver2.utils.Constants.STATE_CONNECTING;
import static com.example.slt_ver2.utils.Constants.STATE_DISCONNECTED;
import static com.example.slt_ver2.utils.Constants.STATE_LISTEN;
import static com.example.slt_ver2.utils.Constants.UUID_CHAR;

public class BluetoothService extends Service {
    //Debugging
    private static final String TAG = "BluetoothService";

//    private Fragment mFragment;
    private Activity act;
    private Handler mHandler;

    private BleCallback bleCallback;
    private BluetoothGatt mBluetoothGatt_right;
    private BluetoothGatt    mBluetoothGatt_left;
    private BluetoothAdapter mBluetoothAdapter;

    public static BluetoothDevice device_right;
    public static BluetoothDevice device_left;

    protected ConnectThread connectThread_right;
    protected ConnectThread connectThread_left;

    private ReadThread readThread_right;
    private ReadThread readThread_left;

    private static String recv_RightData;
    private static String recv_LeftData;

    private int mState;

    // 상태를 나타내는 상태 변수
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTED    = 1;


    private static long SCAN_PERIOD             = 3000;
    private static boolean mScanning_right            = false;
    private static boolean mScanning_left             = false;
    private static String FILTER_SERVICE        = "";

    public static int state_right = STATE_DISCONNECTED;
    public static int state_left = STATE_DISCONNECTED;

    // Constructors
    public BluetoothService(Activity ac, Handler handler) {
        act = ac;
        mHandler = handler;
        mState = STATE_DISCONNECTED;

        // BluetoothAdapter 얻기
        if(Functions.isBleSupported(ac)) {
            act = ac;
            BluetoothManager bluetoothManager = (BluetoothManager) act.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            if(!mBluetoothAdapter.isEnabled()) {
                Intent i = new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);
                act.startActivityForResult(i,5000);
            }
        }
    }

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
                    Log.d(TAG, "mScanning_rihgt = " + mScanning_right);
                    Log.d(TAG, "mScanning_left = " + mScanning_left);

                    if(mScanning_right && device.getAddress().equals(MAC_ADDR_right)) {
                        Log.d(TAG, "LeScanCallback에서 connectThread실행 : " + device.getAddress());
                        connectThread_right = new ConnectThread(device, bleCallbacks());
                        connectThread_right.start();
                    }
                    else if(mScanning_left && device.getAddress().equals(MAC_ADDR_left)) {
                        Log.d(TAG, "LeScanCallback에서 connectThread실행 : " + device.getAddress());
                        connectThread_left = new ConnectThread(device, bleCallbacks());
                        connectThread_left.start();
                    }
                }
            });
        }
    };

    public synchronized void connect(BluetoothDevice device, BleCallback _bleCallback) throws  IOException{

        if (mState == STATE_CONNECTING) {
            if(device == device_right && isConnected_right()) {
                if(connectThread_right == null) { }
                else {
                    connectThread_right.cancel();
                    connectThread_right = null;
                    setState(STATE_DISCONNECTED);

                    sendDisconnectMsg(RIGHT);
                }
            }
        }
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
                connect(device, bleCallbacks());
            } catch (Exception e) {
                disconnect(index);
                Log.e(TAG, index + "connect fail", e);
                e.printStackTrace();
            }
        }

        public void cancel() throws IOException{
            if(index == RIGHT) {
                if(mBluetoothGatt_right != null) {
                    mBluetoothGatt_right.close();
                    Log.d(TAG,"Gatt_right Close");
                    mBluetoothGatt_right = null;
                }
            }
            else if(index == LEFT) {
                if(mBluetoothGatt_left != null) {
                    mBluetoothGatt_left.close();
                    Log.d(TAG,"Gatt_left Close");
                    mBluetoothGatt_left = null;
                }
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

        public void cancel() throws IOException {

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

    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        //핸들러에 새로운 상태를 넘겨준다. UI 액티비티에 수정될 수 있도록..?
        mHandler.obtainMessage(TranslationFragment.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }


    // readThread 초기화
    public synchronized void startRead(int index) throws IOException {
        Log.d(TAG, "startRead");

        // Cancel the thread that completed the connection
        if (connectThread_right == null) { }
        else {
            connectThread_right.cancel();
            connectThread_right= null;
        }

        if (connectThread_left == null) { }
        else {
            connectThread_left.cancel();
            connectThread_left = null;
        }

        // Cancel any thread currently running a connection
        if (readThread_right == null) { }
        else {
            readThread_right.cancel();
            readThread_right = null;
        }
        if (readThread_left == null) {}
        else {
            readThread_left.cancel();
            readThread_left = null;
        }
        // Start the thread to manage the connection and perform transmissions
        if(index == RIGHT) {
            readThread_right = new ReadThread(index);
            readThread_right.start();
            Log.d(TAG, "readThread_right.start()");
            setState(STATE_CONNECTED);
        }
        else if(index == LEFT) {
            readThread_left = new ReadThread(index);
            readThread_left.start();
            Log.d(TAG, "readThread_left.start()");
            setState(STATE_CONNECTED);
        }
    }

    public void disconnectThread(int index) throws IOException {
        if(index == RIGHT) {
            connectThread_right.cancel();
            connectThread_right = null;
            state_right = STATE_DISCONNECTED;
        }
        else if(index == LEFT) {
            connectThread_left.cancel();
            connectThread_left= null;
            state_left = STATE_DISCONNECTED;
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

    public static BleCallback bleCallbacks(){

        return new BleCallback(){

            @Override
            public void onBleConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onBleConnectionStateChange(gatt, status, newState);

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    if(gatt.getDevice().equals(device_right)) {
                        //오른쪽 연결
                    }
                    else {
                        //왼쪽 연결
                    }

                }
                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    if(gatt.getDevice().equals(device_right)) {
                        //오른쪽 연결 해제
                    }
                    else {
                        //왼쪽 연결 해제
                    }
                }
            }

            @Override
            public void onBleServiceDiscovered(BluetoothGatt gatt, int status) {
                super.onBleServiceDiscovered(gatt, status);
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    Log.e("Ble ServiceDiscovered","onServicesDiscovered received: " + status);
                }
            }

            @Override
            public void onBleCharacteristicChange(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onBleCharacteristicChange(gatt, characteristic);
                Log.i("BluetoothLEHelper","onCharacteristicChanged Value: " + Arrays.toString(characteristic.getValue()));
                Log.i(TAG, "onBleCharacteristicChange");

            }

            @Override
            public void onBleRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onBleRead(gatt, characteristic, status);

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.i(TAG, "Main의 onBleRead");
                    Log.i("TAG", Arrays.toString(characteristic.getValue()));
                    byte[] readByte = characteristic.getValue();

                    if(gatt.getDevice() == device_right) {
                        recv_RightData = new String(readByte);
                        Log.i("TAG", "오른손 데이터: " + recv_RightData);
                        sendMessage(TranslationFragment.MESSAGE_READ, recv_RightData, RIGHT);
                    }

                    else if(gatt.getDevice() == device_left) {
                        recv_LeftData = new String(readByte);
                        Log.i("TAG", "왼손 데이터: " + recv_LeftData);
                        sendMessage(TranslationFragment.MESSAGE_READ, recv_LeftData, LEFT);

                    }


//                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "onBleRead : " + recv_RightData, Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onBleWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onBleWrite(gatt, characteristic, status);
            }
        };
    }

    // 연결 해제시 BluetoothDialog로 메시지
    public void sendDisconnectMsg(int index) {
        Message msg = new Message();
        msg.what = index;
        msg.arg1 = STATE_DISCONNECTED;
        BluetoothDialog.handler.sendMessage(msg);
    }

    // 연결 중 BluetoothDIalog로 메시지
    public void sendConnectingMsg(int index) {
        Message msg = new Message();
        msg.what = index;
        msg.arg1 = STATE_CONNECTING;
        BluetoothDialog.handler.sendMessage(msg);
    }

    // 연결 성공시 BluetoothDialog로 메시지
    public void sendConnectMsg(int index) {
        Message msg = new Message();
        msg.what = index;
        msg.arg1 = STATE_CONNECTED;
        BluetoothDialog.handler.sendMessage(msg);
    }

    public static void sendMessage(int arg, String s, int index){
        Message m = new Message();
        m.what = index;
        m.arg1 = arg;
        m.obj = s;

        TranslationFragment.handler.sendMessage(m);
//        ListFragment.handler2.sendMessage(m);
    }

    // 연결 실패했을때
    private void connectionFailed() {
        setState(STATE_LISTEN);
    }
    // 연결을 잃었을 때
    private void connectionLost() {
        setState(STATE_LISTEN);
    }

    public boolean isScanning_right() {return mScanning_right;}
    public boolean isScanning_left(){ return mScanning_left;  }

    public boolean isConnected_right() {
        return state_right == STATE_CONNECTED;
    }
    public boolean isConnected_left() {
        return state_left == STATE_CONNECTED;
    }

    public void setFilterService(String filterService){
        FILTER_SERVICE = filterService;
    }

    public BluetoothGattCallback getGatt(){
        return mGattCallback;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}