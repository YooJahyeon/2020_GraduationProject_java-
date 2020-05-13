package com.example.bluetest;

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
import java.io.OutputStream;
import java.util.UUID;


public class BluetoothService extends Service {
    // Debugging
    private static final String TAG = "BluetoothService";
    // Intent request code
//    private static final int REQUEST_CONNECT_DEVICE = 1;
//    private static final int REQUEST_ENABLE_BT = 2;
    static BluetoothAdapter BA;
    static BluetoothDevice B0,B1;

    final String SPP_UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB"; //SPP UUID
    final UUID SPP_UUID = UUID.fromString(SPP_UUID_STRING);

    private Activity mActivity;
    private Handler mHandler;

    // RFCOMM Protocol
    private ConnectThread ConnectThread0;
    private ConnectThread ConnectThread1;
    private ConnectedThread mConnectedThread;

    private int mState;
    // 상태를 나타내는 상태 변수
    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTEN = 1; // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3; // now connected to a remote device

    static boolean IsConnect0 = false;
    boolean IsConnect1 = false;

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

//        B0 = BA.getRemoteDevice(B0MA);
//        B1 = BA.getRemoteDevice(B1MA);
    }

    public void getDeviceInfo(String b0MA) {
        // Get the device MAC address
        String address = b0MA;
        // Get the BluetoothDevice object
        // BluetoothDevice device = btAdapter.getRemoteDevice(address);
        BluetoothDevice device = BA.getRemoteDevice(address);
        Log.d(TAG, "Get Device Info \n" + "address : " + address);
        try {
            connect(device);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public class ConnectThread extends Thread {
        private BluetoothSocket BS;
        private final BluetoothDevice BD;

        int bluetooth_index;

        ConnectedThread connectedThread;

        ConnectThread(BluetoothDevice device , int index) {
            BD = device;
            bluetooth_index = index;
            BluetoothSocket tmp = null;

            // 디바이스 정보를 얻어서 BluetoothSocket 생성
            try {
                tmp = device.createRfcommSocketToServiceRecord(SPP_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            BS = tmp;
        }

        public void run() {
            BA.cancelDiscovery();
            // BluetoothSocket 연결 시도
            try {
                // BluetoothSocket 연결 시도에 대한 return 값은 succes 또는 exception이다.
                BS.connect();
//                connectedThread = new ConnectedThread(BS, bluetooth_index);
//                connectedThread.start();
                Log.d(TAG, "Connect Success");
            } catch (IOException e) {
                connectionFailed();
                // 연결 실패시 불러오는 메소드
                Log.d(TAG, "Connect Fail");
                // socket을 닫는다.
                try {
                    BS.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                // 연결중? 혹은 연결 대기상태인 메소드를 호출한다.
                try {
                    BluetoothService.this.start();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                if(connectedThread != null) {
                    connectedThread.cancel();
                }
                return;
            }
            // ConnectThread 클래스를 reset한다.
            synchronized(BluetoothService .this)
            {
                ConnectThread0 = null;
                ConnectThread1 = null;
            }

            // ConnectThread를 시작한다.
            try {
                connected(BS, BD, bluetooth_index);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void cancel() throws IOException{
            if(BS != null) {
                BS.close();
                BS = null;
            }

            if(connectedThread != null){
                connectedThread.cancel();
            }

//            sendMessage(STATE_NONE);
        }
//        public void sendMessage(int arg){
//            Message m = new Message();
//            m.what = bluetooth_index;
//            m.arg1 = STATE_CONNECTING;
//
//            handler.sendMessage(m);
//        }
    }

    private class ConnectedThread extends Thread {
        BluetoothSocket BS;
        private InputStream in;

        int bluetooth_index;

        boolean is = false;

        public ConnectedThread(BluetoothSocket socket, int index) {
            bluetooth_index = index;
            Log.d(TAG, "create ConnectedThread");
            BS = socket;
            InputStream tmpIn = null;

            // BluetoothSocket의 inputstream 과 outputstream을 얻는다.
            try {
                tmpIn = socket.getInputStream();
                is = true;
                if(bluetooth_index == 0) IsConnect0 = is;
                else IsConnect1 = is;
//                sendMessage(STATE_CONNECTED);
            } catch (IOException e) {
                cancel();
                Log.e(TAG, "temp sockets not created", e);
            }

            in = tmpIn;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
//            BufferedReader Buffer_in = new BufferedReader(new InputStreamReader(in));
            byte[] buffer = new byte[1024];
            int bytes = 0;
            // Keep listening to the InputStream while connected
            while (is) {
                try {
//                    String s = Buffer_in.readLine();
                    bytes = in.read(buffer);
//                    int nul = Integer.parseInt((String)null);
                    int r = 0;

//                    Log.d("========  ", );


//                    mHandler.obtainMessage(bluetooth_index);

                    mHandler.obtainMessage(MainActivity.MESSAGE_READ, bytes, r, buffer).sendToTarget();
//                    mHandler.sendMessage(MainActivity.MESSAGE_READ, s);
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }
//        public void sendMessage(int arg){
//            Message m = new Message();
//            m.what = bluetooth_index;
//            m.arg1 = arg;
//
//           mHandler.sendMessage(m);
//        }
////
//        public void sendMessage(int arg, String s){
//            Message m = new Message();
//            m.what = bluetooth_index;
//            m.arg1 = arg;
//            m.obj = s;
//
//            handler.sendMessage(m);
//        }

        public void cancel() {
            is = false;

            if(bluetooth_index == 0) IsConnect0 = is;
            else IsConnect1 = is;

            if(in != null){
                try{
                    in.close();
                    in = null;
                    BS.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                sendMessage(STATE_NONE);
            }
        }
    }
    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        //핸들러에 새로운 상태를 넘겨준다. UI 액티비티에 수정될 수 있도록..?
        mHandler.obtainMessage(MainActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }
    // Bluetooth 상태 get
    public synchronized int getState() { return mState; }
    public synchronized void start() throws IOException {
        Log.d(TAG, "start");
        // Cancel any thread attempting to make a connection
        if (ConnectThread0 == null) { }
        else {
            ConnectThread0.cancel();
            ConnectThread0 = null;
//            ConnectThread1.cancel();
//            ConnectThread1 = null;
        } // Cancel any thread currently running a connection
        if (mConnectedThread == null) { }
        else {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    } // ConnectThread 초기화 device의 모든 연결 제거
    public synchronized void connect(BluetoothDevice device) throws IOException {
        Log.d(TAG, "connect to: " + device);
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (ConnectThread0 == null) { }
            else {
                ConnectThread0.cancel();
                ConnectThread0 = null;
//                ConnectThread1.cancel();
//                ConnectThread1 = null;
            }
        } // Cancel any thread currently running a connection
        if (mConnectedThread == null) { }
        else {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        // Start the thread to connect with the given device
        ConnectThread0 = new ConnectThread(device, 0);
//        ConnectThread1 = new ConnectThread(device, 1);
        ConnectThread0.start();
//        ConnectThread1.start();
        setState(STATE_CONNECTING);
    }
    // ConnectedThread 초기화
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, int index) throws IOException {
        Log.d(TAG, "connected");
        // Cancel the thread that completed the connection
        if (ConnectThread0 == null) { }
        else {
            ConnectThread0.cancel();
            ConnectThread0 = null;
//            ConnectThread1.cancel();
//            ConnectThread1 = null;
        } // Cancel any thread currently running a connection
        if (mConnectedThread == null) { }
        else {
            mConnectedThread.cancel();
            mConnectedThread = null;
        } // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket, index);
        mConnectedThread.start();
        setState(STATE_CONNECTED);
    } // 모든 thread stop
    public synchronized void stop() throws IOException {
        Log.d(TAG, "stop");
        if (ConnectThread0 != null) {
            ConnectThread0.cancel();
            ConnectThread0 = null;

        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        setState(STATE_NONE);
    } // 값을 쓰는 부분(보내는 부분)
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r; // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        } // Perform the write unsynchronized r.write(out); }
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

