package com.example.slt_ver2;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

<<<<<<< HEAD
=======

import static com.example.slt_ver2.MainActivity.bleCallbacks;
import static com.example.slt_ver2.utils.Constants.LEFT;
import static com.example.slt_ver2.utils.Constants.MAC_ADDR_left;
import static com.example.slt_ver2.utils.Constants.MAC_ADDR_right;
import static com.example.slt_ver2.utils.Constants.RIGHT;
import static com.example.slt_ver2.utils.Constants.STATE_CONNECTED;
import static com.example.slt_ver2.utils.Constants.STATE_CONNECTING;
import static com.example.slt_ver2.utils.Constants.STATE_DISCONNECTED;
import static com.example.slt_ver2.utils.Constants.STATE_LISTEN;
import static com.example.slt_ver2.utils.Constants.UUID_CHAR;

>>>>>>> parent of ab5bef1... Revert "200821 YJH SLT_ble"
public class BluetoothService extends Service {
    //Debugging
    private static final String TAG = "BluetoothService";

    static BluetoothAdapter BA;
    static BluetoothDevice B0, B1;

    final String SPP_UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB"; //SPP UUID
    final UUID SPP_UUID = UUID.fromString(SPP_UUID_STRING);

//    private Fragment mFragment;
    private Activity mActivity;
    private Handler mHandler;

    private  ConnectThread ConnectThread0;
    private ConnectThread ConnectThread1;
    private ConnectedThread mConnectedThread;
//    ConnectedThread connectedThread0, connectedThread1;

    private int mState;

<<<<<<< HEAD
    // 상태를 나타내는 상태 변수
    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTEN = 1; // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3; // now connected to a remote device

    boolean IsConnect0 = false;
    boolean IsConnect1 = false;
=======

    private static long SCAN_PERIOD             = 1000;
    private static boolean mScanning_right            = false;
    private static boolean mScanning_left             = false;
    private static String FILTER_SERVICE        = "";

    public static int state_right = STATE_DISCONNECTED;
    public static int state_left = STATE_DISCONNECTED;
>>>>>>> parent of ab5bef1... Revert "200821 YJH SLT_ble"

    // Constructors
    public BluetoothService(Activity ac, Handler handler) {
        mActivity = ac;
        mHandler = handler;
        mState = STATE_NONE;
        // BluetoothAdapter 얻기
        BA = BluetoothAdapter.getDefaultAdapter();
        if(!BA.isEnabled()){
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(i,5000);
        }
    }

    public  void getDeviceInfo(String mac_address, int index) {
        // Get the device MAC address
        String address = mac_address;
        // Get the BluetoothDevice object
        B0 = BA.getRemoteDevice(address);
        Log.d(TAG, "Get Device Info(0) \n" + "address : " + address + "index: " + index);
        try {
            connect(B0, index);
            Log.d(TAG, "Connect B0 \n" + "address : " + address);
        } catch (IOException e) {
            e.printStackTrace();
        }

<<<<<<< HEAD
    }

    public  void getDeviceInfo_right(String mac_address, int index) {
        // Get the device MAC address
        String address = mac_address;
        // Get the BluetoothDevice object
        B1 = BA.getRemoteDevice(address);
        Log.d(TAG, "Get Device Info(1) \n" + "address : " + address + "index: " + index);
        try {
            connect(B1, index);
            Log.d(TAG, "Connect B1 \n" + "address : " + address);
        } catch (IOException e) {
            e.printStackTrace();
=======
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
            else if(device == device_left && isConnected_left()) {
                if(connectThread_left == null) {}
                else {
                    connectThread_left.cancel();
                    connectThread_left = null;
                    setState(STATE_DISCONNECTED);
                    sendDisconnectMsg(LEFT);
                }
            }
        }

        if(device == device_right) {
            if(readThread_right == null) {}
            else {
                readThread_right.cancel();
                readThread_right= null;
            }
        }
        else if(device == device_left) {
            if(readThread_left == null) {}
            else {
                readThread_left.cancel();
                readThread_left = null;
            }
        }

        if(device == device_right && !isConnected_right()) {
            Log.d(TAG, "connect 안 = " + device.getName());

            if(mBluetoothGatt_right == null && !isConnected_right()) {
                bleCallback = _bleCallback;
                mBluetoothGatt_right = device.connectGatt(act, true, mGattCallback);

                readThread_right = new ReadThread(RIGHT);
                readThread_right.start();
                Log.d(TAG, "readThread_right.start()");
            }
        }
        else if(device == device_left && !isConnected_left()) {
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
                readThreadStop(RIGHT);
                mBluetoothGatt_right.disconnect();
                mBluetoothGatt_right.close();
                mBluetoothGatt_right = null;
            }
        }
        else if(index == LEFT) {
            if(mBluetoothGatt_left != null && isConnected_left()) {
                state_left = STATE_DISCONNECTED;
                readThreadStop(LEFT);
                mBluetoothGatt_left.disconnect();
                mBluetoothGatt_left.close();
                mBluetoothGatt_left = null;
            }
>>>>>>> parent of ab5bef1... Revert "200821 YJH SLT_ble"
        }

    }

<<<<<<< HEAD
    public class ConnectThread extends Thread {
        BluetoothSocket BS;
        BluetoothDevice BD;
=======
    public void readThreadStop(int index) {
        if (index == RIGHT && !readThread_right.isInterrupted()) {
            readThread_right.interrupt();
        }
        else if (index == LEFT && !readThread_left.isInterrupted()) {
            readThread_left.interrupt();
        }
    }

    public boolean isReadyForScan(){
>>>>>>> parent of ab5bef1... Revert "200821 YJH SLT_ble"

        int bluetooth_index;
        ConnectedThread connectedThread;

        ConnectThread(BluetoothDevice device , int index){
            BD = device;
            bluetooth_index = index;
        }

        public void run() {
            try {
                BS = BD.createRfcommSocketToServiceRecord(SPP_UUID);
                BS.connect();
                Log.i(TAG, "BS.connect" + bluetooth_index);
                connectedThread = new ConnectedThread(BS, bluetooth_index);
                connectedThread.start();
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
                try {
                    cancel();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            Log.d(TAG, "Connect Success");
        }


        public void cancel() throws IOException{
            if(bluetooth_index == 0) {
                if(BS != null) {
                    BS.close();
                    Log.d("BS"+bluetooth_index+" close", "ㅠㅠ");
                    BS = null;
                }
            }
            else if(bluetooth_index == 1) {
                if(BS != null) {
                    BS.close();
                    Log.d("BS"+bluetooth_index+" close", "ㅠㅠ");
                    BS = null;
                }
            }

            if(mConnectedThread != null){
                try {
                    mConnectedThread.cancel();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    private class ConnectedThread extends Thread {
        private InputStream in;

        int bluetooth_index;

        boolean is = false;

        public ConnectedThread(BluetoothSocket socket, int index) {
            bluetooth_index = index;

            Log.d(TAG, "create ConnectedThread");
            try {
<<<<<<< HEAD
                in = socket.getInputStream();
                is = true;
                if(bluetooth_index == 0){
                    IsConnect0 = is;
                    sendConnectMsg(0);
                }
                else if(bluetooth_index == 1){
                    IsConnect1 = is;
                    sendConnectMsg(1);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread()");
            if( in == null)
            {
                System.out.println("in is null");
            }
            BufferedReader Buffer_in = new BufferedReader(new InputStreamReader(in));
            // Keep listening to the InputStream while connected
            while (is) {
                try {
                    String s = Buffer_in.readLine();
                    Log.i(TAG, "mConnectedThread(): " + s);
                    if(!s.equals("")){
                        sendMessage(TranslationFragment.MESSAGE_READ, s, bluetooth_index);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
=======
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, index + "ReadThread실행");

            if(index == RIGHT) {
                sendConnectMsg(RIGHT);
                while(isConnected_right() && !readThread_right.isInterrupted()) {
                    read(Constants.SERVICE_STRING, Constants.CHARACTERISTIC_STRING, RIGHT);
                }
            }
            else if(index == LEFT) {
                sendConnectMsg(LEFT);
                while(isConnected_left() && !readThread_left.isInterrupted()) {
                    read(Constants.SERVICE_STRING, Constants.CHARACTERISTIC_STRING, LEFT);
>>>>>>> parent of ab5bef1... Revert "200821 YJH SLT_ble"
                }
            }
        }

        public void cancel() throws IOException {
<<<<<<< HEAD
            is = false;

            if (bluetooth_index == 0) IsConnect0 = is;
            else IsConnect1 = is;

            if (in != null) {
                in.close();
                in = null;
            }
            mHandler.obtainMessage(TranslationFragment.MESSAGE_STATE_CHANGE, STATE_NONE, -1).sendToTarget();
=======
            if(index == RIGHT) {
                readThread_right.interrupt();
                }

            else if(index == LEFT) {
                readThread_left.interrupt();
            }
        }
    }

    public void read(String service, String characteristic, int index){
        if(index == RIGHT) {
            mBluetoothGatt_right.readCharacteristic(mBluetoothGatt_right.getService(Constants.UUID_SERVICE).getCharacteristic(UUID_CHAR));
        }
        else if(index == LEFT) {
            mBluetoothGatt_left.readCharacteristic(mBluetoothGatt_left.getService(Constants.UUID_SERVICE).getCharacteristic(UUID_CHAR));
>>>>>>> parent of ab5bef1... Revert "200821 YJH SLT_ble"
        }
    }

    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        //핸들러에 새로운 상태를 넘겨준다. UI 액티비티에 수정될 수 있도록..?
        mHandler.obtainMessage(TranslationFragment.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized void connect(BluetoothDevice device, int index) throws IOException {
        Log.d(TAG, "connect to: " + device);

        if (mState == STATE_CONNECTING) {
            if (device == B0 && IsConnect0 && index == 0) {
                if (ConnectThread0 == null) {
                } else {
                    ConnectThread0.cancel();
                    ConnectThread0 = null;
                    setState(STATE_NONE);

                    sendDisconnectMsg(0);
                }
            } else if (device == B1 && IsConnect1 && index == 1) {
                if (ConnectThread1 == null) {
                } else {
                    ConnectThread1.cancel();
                    ConnectThread1 = null;
                    setState(STATE_NONE);

                    sendDisconnectMsg(1);
                }
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread == null) { }
        else {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        if(device == B0 && IsConnect0 == false) {
            ConnectThread0 = new ConnectThread(device, 0);
            ConnectThread0.start();
            setState(STATE_CONNECTING);
            sendConnectingMsg(0);
        }

        else if(device == B1 && IsConnect1==false ) {
            ConnectThread1 = new ConnectThread(device,1);
            ConnectThread1.start();
            setState(STATE_CONNECTING);
            sendConnectingMsg(1);

        }
    }


    // ConnectedThread 초기화
    public synchronized void connected(BluetoothSocket socket, int index) throws IOException {
        Log.d(TAG, "connected");
        // Cancel the thread that completed the connection
        if (ConnectThread0 == null) { }
        else {
            ConnectThread0.cancel();
            ConnectThread0 = null;
        }

        if (ConnectThread1 == null) { }
        else {
            ConnectThread1.cancel();
            ConnectThread1 = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread == null) { }
        else {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        // Start the thread to manage the connection and perform transmissions
<<<<<<< HEAD
        mConnectedThread = new ConnectedThread(socket, index);
        mConnectedThread.start();
        Log.d(TAG, "mConnectedThread.start()");
        setState(STATE_CONNECTED);
=======
        if(index == RIGHT) {
            setState(STATE_CONNECTED);
            readThread_right = new ReadThread(index);
            readThread_right.start();
            Log.d(TAG, "readThread_right.start()");

        }
        else if(index == LEFT) {
            setState(STATE_CONNECTED);
            readThread_left = new ReadThread(index);
            readThread_left.start();
            Log.d(TAG, "readThread_left.start()");

        }
>>>>>>> parent of ab5bef1... Revert "200821 YJH SLT_ble"
    }
    // 모든 thread stop
    public synchronized void stop() throws IOException {
        Log.d(TAG, "stop");
        if (ConnectThread0 != null) {
            ConnectThread0.cancel();
            ConnectThread0 = null;
        }
        if (ConnectThread1 != null) {
            ConnectThread1.cancel();
            ConnectThread1 = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        setState(STATE_NONE);
    }

<<<<<<< HEAD
    public void DIsconnectThread(int index) throws IOException {
        if(index == 0) {
            ConnectThread0.cancel();
            ConnectThread0 = null;
            IsConnect0 = false;
        }

        else if(index == 1) {
            ConnectThread1.cancel();
            ConnectThread1 = null;
            IsConnect1 = false;
        }
=======
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
                Log.i(TAG, "onCharacteristicRead");
                bleCallback.onBleRead(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                Log.i(TAG, "onCharacteristicChanged");
                bleCallback.onBleCharacteristicChange(gatt, characteristic);
            }

        };
>>>>>>> parent of ab5bef1... Revert "200821 YJH SLT_ble"
    }

    // 연결 해제시 BluetoothDialog로 메시지
    public void sendDisconnectMsg(int index) {
        Message msg = new Message();
        msg.what = index;
        msg.arg1 = BluetoothDialog.DISCONNECT;
        BluetoothDialog.handler.sendMessage(msg);
    }

    // 연결 중 BluetoothDIalog로 메시지
    public void sendConnectingMsg(int index) {
        Message msg = new Message();
        msg.what = index;
        msg.arg1 = BluetoothDialog.CONNECTING;
        BluetoothDialog.handler.sendMessage(msg);
    }

    // 연결 성공시 BluetoothDialog로 메시지
    public void sendConnectMsg(int index) {
        Message msg = new Message();
        msg.what = index;
        msg.arg1 = BluetoothDialog.CONNECTED;
        BluetoothDialog.handler.sendMessage(msg);
    }

    public void sendMessage(int arg, String s, int index){
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

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}