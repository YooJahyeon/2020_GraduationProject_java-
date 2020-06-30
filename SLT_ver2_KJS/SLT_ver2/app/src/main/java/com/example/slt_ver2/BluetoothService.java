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
    static BluetoothDevice B0, B1;

    final String SPP_UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB"; //SPP UUID
    final UUID SPP_UUID = UUID.fromString(SPP_UUID_STRING);

    private Fragment mFragment;
    private Handler mHandler;

    private  ConnectThread ConnectThread0;
    private ConnectThread ConnectThread1;
    private ConnectedThread mConnectedThread;
//    ConnectedThread connectedThread0, connectedThread1;

    private int mState;

    // 상태를 나타내는 상태 변수
    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTEN = 1; // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3; // now connected to a remote device

    boolean IsConnect0 = false;
    boolean IsConnect1 = false;

    // Constructors
    public BluetoothService(Fragment fr, Handler handler) {
//        mActivity = ac;
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

    }

    public  void getDeviceInfo_right(String mac_address, int index) {
        // Get the device MAC address
        String address = mac_address;
        // Get the BluetoothDevice object
        B1 = BA.getRemoteDevice(address);
        Log.d(TAG, "Get Device Info(1) \n" + "address : " + address + "index: " + index);
        try {
            connect(B1, index);
//            IsConnect1 = true;
            Log.d(TAG, "Connect B1 \n" + "address : " + address);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public class ConnectThread extends Thread {
        BluetoothSocket BS;
        BluetoothDevice BD;

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
                }
            }
        }

        public void cancel() throws IOException {
            is = false;

            if (bluetooth_index == 0) IsConnect0 = is;
            else IsConnect1 = is;

            if (in != null) {
                in.close();
                in = null;
            }
            mHandler.obtainMessage(TranslationFragment.MESSAGE_STATE_CHANGE, STATE_NONE, -1).sendToTarget();
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
        mConnectedThread = new ConnectedThread(socket, index);
        mConnectedThread.start();
        Log.d(TAG, "mConnectedThread.start()");
        setState(STATE_CONNECTED);
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