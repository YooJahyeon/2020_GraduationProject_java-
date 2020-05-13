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
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTING;

public class BluetoothService extends Service {

    //Debugging
    private static final String TAG = "BluetoothService";

    final String B0MA = "98:D3:41:FD:6A:4E"; //Bluetooth0 MacAddress
    final String B1MA = "98:D3:91:FD:86:0E"; //Bluetooth1 MacAddress

    final String SPP_UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB"; //SPP UUID
    final UUID SPP_UUID = UUID.fromString(SPP_UUID_STRING);

    //Member fields
    private final BluetoothAdapter BA;
    private final Handler mHandler;
    private final Activity mActivity;

    private  ConnectThread connectThread0;
    private ConnectThread connectThread1;
    private ConnectedThread mConnectedThread;

    private int mState;

    // 상태를 나타내는 상태 변수
    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTEN = 1; // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3; // now connected to a remote device

    static boolean IsConnect0 = false;
    static boolean IsConnect1 = false;

    public BluetoothService(Activity ac, Handler handler) {
        mActivity = ac;
        BA = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
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
                //ConnectThread0 = null;
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
                if(bluetooth_index == 0) IsConnect_L = is;
                else IsConnect_R = is;
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
            int bytes;
            // Keep listening to the InputStream while connected
            while (is) {
                try {
//                    String s = Buffer_in.readLine();
                    bytes = in.read(buffer);
                    mHandler.obtainMessage(MainActivity.MESSAGE_READ, bytes, -1, buffer).sendToTarget();;
//                    if(!s.equals("")) {
//                        sendMessage(MainActivity.INPUTDATA, s);
//                    }
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
////           handler.sendMessage(m);
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

            if(bluetooth_index == 0) IsConnect_L = is;
            else IsConnect_R = is;

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

   //상태 얻어오는 메소드
    public synchronized int getState() {
        return mState;
    }

    // 아두이노->앱 데이터 전송 시작하는 메소드
    public synchronized void start()
    {
        // 연결을 위한 스레드 객체 생성
     mConnectThread = new ConnectThread(device);
     //스레드 시작
        mConnectThread.start();
        //상태 바꿈
        setState(STATE_CONNECTING);
    }


    public synchronized  void connected(BluetoothDevice device)
    {
        mConnectedThread = new ConnectedThread
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
