package com.example.slt_ver31;

import android.bluetooth.BluetoothServerSocket;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.util.ArrayList;
import java.util.UUID;

public class BluetoothService extends Service {
    //Debugging
    private static final String TAG = "BluetoothService";
    private static final boolean D = true;



    final String SPP_UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB"; //SPP UUID
    final UUID SPP_UUID = UUID.fromString(SPP_UUID_STRING);



    //멤버 필드
    static BluetoothAdapter BA;
    static BluetoothDevice B0,B1;
    private Fragment mFragment;
    private Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectThread ConnectThread0;
    private ConnectThread ConnectThread1;
    private ConnectedThread mConnectedThread;
    private ArrayList<UUID> mUuids;
    private ArrayList<ConnectedThread> mConnThreads;
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
        mConnThreads = new ArrayList<ConnectedThread>();
        // BluetoothAdapter 얻기
        BA = BluetoothAdapter.getDefaultAdapter();

        if(!BA.isEnabled()){
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mFragment.startActivityForResult(i,5000);
        }

        mUuids = new ArrayList<UUID>();
        mUuids.add(UUID.fromString("b7746a40-c758-4868-aa19-7ac6b3475dfc"));
        mUuids.add(UUID.fromString("2d64189d-5a2c-4511-a074-77f199fd0834"));
        mUuids.add(UUID.fromString("e442e09a-51f3-4a7b-91cb-f638491d1412"));
        mUuids.add(UUID.fromString("a81d6504-4536-49ee-a475-7d96d09439e4"));
        mUuids.add(UUID.fromString("aa91eab1-d8ad-448e-abdb-95ebba4a9b55"));
        mUuids.add(UUID.fromString("4d34da73-d0a4-4f40-ac38-917e0a9dee97"));
        mUuids.add(UUID.fromString("5e14d4df-9c8a-4db7-81e4-c937564c86e0"));
    }

    public  void getDeviceInfo(String b0MA, String b1MA) {
        // Get the BluetoothDevice object

        B0 = BA.getRemoteDevice(b0MA);
        B1 = BA.getRemoteDevice(b1MA);

        Log.d(TAG, "Get Device Info \n" + "Device0 : " + b0MA + "/ Device1: " + b1MA);
        try {

            if(IsConnect0) {
                System.out.println("IsConnect0 == true");
                if(ConnectThread0 != null) {
                    try {
                        ConnectThread0.cancel();

                        Message m = new Message();
                        m.what = 0;
                        m.arg1 = STATE_NONE;
                        mHandler.sendMessage(m);

                        ConnectThread0 = null;
                    } catch (IOException e) { }
                }
            }
            if(!IsConnect0) {
                System.out.println("IsConnect0 == false");

                ConnectThread0 = new ConnectThread(B0, 0);
                ConnectThread0.start();
            }

            if(IsConnect1) {
                System.out.println("IsConnect1 == true");
                if(ConnectThread1 != null) {
                    try {
                        ConnectThread1.cancel();

                        Message m = new Message();
                        m.what = 1;
                        m.arg1 = STATE_NONE;
                        mHandler.sendMessage(m);

                        ConnectThread1 = null;
                    } catch (IOException e) { }
                }
            }
            if(!IsConnect1) {
                System.out.println("IsConnect1 == false");
                ConnectThread1 = new ConnectThread(B1, 1);
                ConnectThread1.start();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class ConnectThread extends Thread {
        private BluetoothSocket BS;
        private BluetoothDevice BD;

        int bluetooth_index;

        ConnectedThread connectedThread;

        ConnectThread(BluetoothDevice device , int index) {
            BD = device;
            bluetooth_index = index;

        }

        @Override
        public void run() {
            BA.cancelDiscovery();
            // BluetoothSocket 연결 시도
            try {
                Log.d(TAG, bluetooth_index + " 시도");

                // BluetoothSocket 연결 시도에 대한 return 값은 succes 또는 exception이다.
                BS = BD.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
                BS.connect();

                connectedThread = new ConnectedThread(BS, bluetooth_index);
                connectedThread.start();

                Log.d(TAG, bluetooth_index+" Connect Success");
            } catch (IOException e) {
                connectionFailed();
                // 연결 실패시 불러오는 메소드
                Log.d(TAG, bluetooth_index + " Connect Fail");
                // socket을 닫는다.
                try {
                    BS.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
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

        }


        public void cancel() throws IOException{
            if(BS != null) {
                BS.close();
                BS = null;
            }

            if(connectedThread != null){
                connectedThread.cancel();
            }

            sendMessage(STATE_NONE);

        }

        public void sendMessage(int arg) {
            Message m = new Message();
            m.what = bluetooth_index;
            m.arg1 = STATE_CONNECTING;

            mHandler.sendMessage(m);
        }

    }

    private class ConnectedThread extends Thread {
        private InputStream in = null;


        int bluetooth_index;

        boolean is = false;

        public ConnectedThread(BluetoothSocket socket, int index) {

            bluetooth_index  = index;
            Log.d(TAG, index + " create ConnectedThread");
            // BluetoothSocket의 inputstream 과 outputstream을 얻는다.
            try {
                //tmpIn = socket.getInputStream();
                in = socket.getInputStream();
                is = true;

                if(bluetooth_index == 0) {
                    IsConnect0 = is;
                }
                else {
                    IsConnect1 = is;
                    //in = tmpIn;
                }
                sendMessage(STATE_CONNECTED);

            } catch (IOException e) {
                cancel();
                Log.e(TAG, "temp sockets not created", e);
            }


        }

        @Override
        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            BufferedReader Buffer_in = new BufferedReader(new InputStreamReader(in));

            // Keep listening to the InputStream while connected
            while (is) {
                try {
                        String s = Buffer_in.readLine();

                        if(!s.equals("")) {
                            sendMessage(TranslationFragment.MESSAGE_READ, s);
                            Log.i(TAG, "connected msg: "+ s);
                        }


                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        public void sendMessage(int arg) {
            Message m = new Message();
            m.what = bluetooth_index;
            m.arg1 = arg;
            mHandler.sendMessage(m);
        }

        public void sendMessage(int arg, String s) {
            Message m = new Message();
            m.what = bluetooth_index;
            m.arg1 = arg;
            m.obj = s;
            mHandler.sendMessage(m);
        }


        public void cancel() {
            is = false;

            if(bluetooth_index == 0) IsConnect0 = is;
            else IsConnect1 = is;

            if(in != null){
                try{
                    in.close();
                    in = null;
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

    public synchronized  void start() {
        if (D) Log.d(TAG, "start");

        if(mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        if(mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}


        if(mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }

        setState(STATE_LISTEN);

    }

    public synchronized void connect(BluetoothDevice device) {
        if (D) Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Create a new thread and attempt to connect to each UUID one-by-one.
        for (int i = 0; i < 7; i++) {
            try {
                mConnectThread = new ConnectThread(device, mUuids.get(i));
                mConnectThread.start();
                setState(STATE_CONNECTING);
            } catch (Exception e) {
            }
        }
    }

    public synchronized  void connected(BluetoothSocket socket, BluetoothDevice device) {

        if (D) Log.d(TAG, "connected");

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        // Add each connected thread to an array
        mConnThreads.add(mConnectedThread);

        setState(STATE_CONNECTED);
    }


    // 모든 thread stop
    public synchronized void stop() throws IOException {
        if(D) Log.d(TAG, "stop");

        if(mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if(mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}
        setState(STATE_NONE);
    }

    public void write(byte[] out) {
        for (int i = 0; i < mConnThreads.size(); i++) {
            try {
                // Create temporary object
                ConnectedThread r;
                // Synchronize a copy of the ConnectedThread
                synchronized (this) {
                    if (mState != STATE_CONNECTED) return;
                    r = mConnThreads.get(i);
                }
                // Perform the write unsynchronized
                r.write(out);
            } catch (Exception e) {
            }
        }
    }

    // 연결 실패했을때
    private void connectionFailed() {
        setState(STATE_LISTEN);
    }
    // 연결을 잃었을 때
    private void connectionLost() {
        setState(STATE_LISTEN);
    }


    private class AcceptThread extends Thread  {
        BluetoothServerSocket serverSocket = null;

        public AcceptThread() {}

        public void run() {
            if(D) Log.d(TAG, "BEGIN mAcceptThread" + this);
            setName("AcceptTHread");
            BluetoothSocket socket = null;

        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
