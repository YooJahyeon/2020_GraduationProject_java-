package com.example.signlanguagetranslator;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.Context;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;


public class BluetoothService extends Service {
    private static final String TAG = "BluetoothService: ";

    // Intent request code
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;



    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    private Activity bActivity;
    private Handler bHandler;

    BluetoothDevice B0,B1;

    Boolean B0_state = false;
    Boolean B1_state = false;

    ConnectThread connectThread_0;
    ConnectThread connectThread_1;

    final String B0MacAddress = "98:D3:41:FD:6A:4E"; //Bluetooth0 MacAddress
    final String B1MacAddress = "98:D3:91:FD:86:0E"; //Bluetooth1 MacAddress

    final String SPP_UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB"; //SPP UUID
    final UUID SPP_UUID = UUID.fromString(SPP_UUID_STRING);



    final int DISCONNECT = 0;
    final int CONNECTING = 1;
    final int CONNECTED = 2;
    final int INPUTDATA = 9999;

    private String s;



    public BluetoothService(Activity activity, Handler handler) {
        bActivity = activity;
        bHandler = handler;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!bluetoothAdapter.isEnabled()) {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            bActivity.startActivityForResult(i, 5000);

        }

        B0 = bluetoothAdapter.getRemoteDevice(B0MacAddress);
        B1 = bluetoothAdapter.getRemoteDevice(B1MacAddress);

        connectDevice();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Service 객체와 데이터를 주고받을 때 사용하는 메서드
        return null;
    }


    public boolean getDeviceState() {
        Log.d(TAG, "check the Bluetooth support");

        if(bluetoothAdapter == null) {
            Log.d(TAG, "Bluetooth is not available");

            return false;
        }
        else {
            Log.d(TAG, "Bluetooth is available");

            return true;
        }
    }
    public void enableBluetooth() {
        Log.i(TAG, "Check the enabled Bluetooth");
        if(bluetoothAdapter.isEnabled()) {
            // 기기의 블루투스 상태가 On인 경우
            Log.d(TAG, "Bluetooth Enable Now");
            // Next Step
            } else {
            // 기기의 블루투스 상태가 Off인 경우
            Log.d(TAG, "Bluetooth Enable Request");
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            bActivity.startActivityForResult(i, REQUEST_ENABLE_BT);
        }
    }

    public void connectDevice() {
        Log.d(TAG, "Connect Device");

        if(B0_state != true) {
            connectThread_0 = new ConnectThread(B0, 0);
            connectThread_0.start();
        }



        if(B1_state != true) {
            connectThread_1 = new ConnectThread(B1, 1);
            connectThread_1.start();
        }





    }

    class ConnectThread extends Thread{

        private BluetoothDevice bluetoothDevice;
        private BluetoothSocket bluetoothSocket;

        int bluetooth_index;

        ConnectedThread connectedThread;

        ConnectThread(BluetoothDevice device , int index){
            bluetoothDevice = device;
            bluetooth_index = index;
        }

        @Override
        public void run() {
            try {
                sendMessage(CONNECTING);

                bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
                bluetoothSocket.connect();

                connectedThread = new ConnectedThread(bluetoothSocket, bluetooth_index);
                connectedThread.start();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    cancel();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                if(connectedThread != null){
                    connectedThread.cancel();
                }
            }
        }

        public void cancel() throws IOException {
            if(bluetoothSocket != null) {
                bluetoothSocket.close();
                bluetoothSocket = null;
            }

            if(connectedThread != null){
                connectedThread.cancel();
            }

            sendMessage(DISCONNECT);
        }

        public void sendMessage(int arg){
            Message m = new Message();
            m.what = bluetooth_index;
            m.arg1 = CONNECTING;

            bHandler.sendMessage(m);
        }
    }

    class ConnectedThread extends Thread{

        InputStream in = null;

        int bluetooth_index;

        boolean is =false;

        public ConnectedThread(BluetoothSocket bluetoothsocket, int index) {
            bluetooth_index = index;

            try {
                in = bluetoothsocket.getInputStream();

                is = true;

                if(bluetooth_index == 0) B0_state = is;
                else B1_state = is;

                sendMessage(CONNECTED);

            } catch (IOException e) {
                cancel();
            }
        }

        @Override
        public void run() {
            BufferedReader Buffer_in = new BufferedReader(new InputStreamReader(in));

            while (is){
                try {
                    String s = Buffer_in.readLine();

                    if(!s.equals("")){
                        sendMessage(INPUTDATA,s);
                    }

                } catch (IOException e) { }
            }

        }

        public void sendMessage(int arg){
            Message m = new Message();
            m.what = bluetooth_index;
            m.arg1 = arg;

            bHandler.sendMessage(m);
        }

        public void sendMessage(int arg, String s){
            Message m = new Message();
            m.what = bluetooth_index;
            m.arg1 = arg;
            m.obj = s;

            bHandler.sendMessage(m);
        }

        public void cancel(){
            is = false;

            if(bluetooth_index == 0) B0_state = is;
            else B1_state = is;

            if(in != null){
                try {
                    in.close();
                    in=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            sendMessage(DISCONNECT);
        }
    }


    @Override
    public void onDestroy() {

    }
}




/* 버린 코드
   class LocalBinder extends Binder {
        BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Service 객체와 데이터를 주고받을 때 사용하는 메서드
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        start_main();
        // 가장 먼저 호출됨
        Log.d("test", "BluetoothService onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스가 호출될 때마다 실행
        Log.d("test", "서비스의 onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //stop_main();

        Log.d("test", "서비스 onDestroy");
    }

    public void start_main(){
        Intent i = new Intent(this, MainActivity.class);
        PendingIntent p = PendingIntent.getActivity(this, 0, i, 0);
        try {
            p.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public void stop_main(){


    }
 */