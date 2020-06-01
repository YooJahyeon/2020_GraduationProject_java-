package com.example.slt_ver2;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTING;

public class BluetoothService extends Service {

    //Debugging
    private static final String TAG = "BluetoothService";

    static BluetoothAdapter BA;
    static BluetoothDevice B0,B1;

    final String SPP_UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB"; //SPP UUID
    final UUID SPP_UUID = UUID.fromString(SPP_UUID_STRING);

    private Fragment mFragment;
    private Handler mHandler;

    private  ConnectThread ConnectThread0;
    private ConnectThread ConnectThread1;
    private ConnectedThread mConnectedThread;

    private int mState;

    // 상태를 나타내는 상태 변수
    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTEN = 1; // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3; // now connected to a remote device

    static boolean IsConnect0 = false;
    static boolean IsConnect1 = false;

    // Constructors
    public BluetoothService(Fragment fr, Handler handler) {
        mFragment = fr;
        mHandler = handler;
        mState = STATE_NONE;
        // BluetoothAdapter 얻기
        BA = BluetoothAdapter.getDefaultAdapter();

        if(!BA.isEnabled()){
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mFragment.startActivityForResult(i,5000);
        }
    }

    public  void getDeviceInfo(String b0MA) {
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
            BufferedReader Buffer_in = new BufferedReader(new InputStreamReader(in));
            // Keep listening to the InputStream while connected
            while (is) {
                try {
                    String s = Buffer_in.readLine();
                    Log.i(TAG, "mConnectedThread: "+s);
                    Message msg = new Message();
                    msg.what = TranslationFragment.MESSAGE_READ;
                    msg.obj = s;
                    mHandler.sendMessage(msg);
                    Log.i(TAG, "msg : "+msg);


                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }


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
                mHandler.obtainMessage(TranslationFragment.MESSAGE_STATE_CHANGE, STATE_NONE, -1).sendToTarget();
            }
        }
    }

    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        //핸들러에 새로운 상태를 넘겨준다. UI 액티비티에 수정될 수 있도록..?
        mHandler.obtainMessage(TranslationFragment.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

   //상태 얻어오는 메소드
    public synchronized int getState() {
        return mState;
    }

    // 아두이노->앱 데이터 전송 시작하는 메소드
    public synchronized void start() throws IOException {
        Log.d(TAG, "start");
        // Cancel any thread attempting to make a connection
        if (ConnectThread0 == null) { }
        else {
            ConnectThread0.cancel();
            ConnectThread0 = null;

        }
        // Cancel any thread currently running a connection
        if (mConnectedThread == null) { }
        else {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }
    public synchronized void connect(BluetoothDevice device) throws IOException {
        Log.d(TAG, "connect to: " + device);
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (ConnectThread0 == null) { }
            else {
                ConnectThread0.cancel();
                ConnectThread0 = null;
            }
        }
        // Cancel any thread currently running a connection
        if (mConnectedThread == null) { }
        else {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        // Start the thread to connect with the given device
        ConnectThread0 = new ConnectThread(device, 0);
        ConnectThread0.start();

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

        } // Cancel any thread currently running a connection
        if (mConnectedThread == null) { }
        else {
            mConnectedThread.cancel();
            mConnectedThread = null;
        } // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket, index);
        mConnectedThread.start();
        setState(STATE_CONNECTED);
    }
    // 모든 thread stop
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
    }

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
