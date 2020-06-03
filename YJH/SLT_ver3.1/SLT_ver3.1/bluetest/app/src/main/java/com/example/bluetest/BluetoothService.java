package com.example.bluetest;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

public class BluetoothService extends Service {
    boolean IsConnect0 = false, IsConnect1 = false;

    private Context _context;
    private BluetoothAdapter BA;
    BluetoothDevice B0,B1;

    ConnectThread BC0;
    ConnectThread BC1;

    MainActivity.ConnectThread connectThread0;
    MainActivity.ConnectThread connectThread1;

    final String B0MA = "98:D3:41:FD:6A:4E"; //Bluetooth0 MacAddress
    final String B1MA = "98:D3:91:FD:86:0E"; //Bluetooth1 MacAddress

    final String SPP_UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB"; //SPP UUID
    final UUID SPP_UUID = UUID.fromString(SPP_UUID_STRING);

    final int DISCONNECT = 0;
    final int CONNECTING = 1;
    final int CONNECTED = 2;
    final int INPUTDATA = 9999;



    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BA = BluetoothAdapter.getDefaultAdapter();

        BC1 = new ConnectThread(B1,0);
        BC1.start();
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId)
//    {
//
//    }




    class ConnectThread extends Thread{

        BluetoothDevice BD;
        BluetoothSocket BS;

        int bluetooth_index;

        ConnectedThread connectedThread;

        ConnectThread(BluetoothDevice device , int index){
            BD = device;
            bluetooth_index = index;
        }

        @Override
        public void run() {
            try {
                sendMessage(CONNECTING);

                BS = BD.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
                BS.connect();

                connectedThread = new ConnectedThread(BS, bluetooth_index);
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
            if(BS != null) {
                BS.close();
                BS = null;
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

                if(bluetooth_index == 0) IsConnect0 = is;
                else IsConnect1 = is;

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
                        Intent dataIntent = new Intent(getApplicationContext(), MainActivity.class);

                        dataIntent.putExtra("data", s);

                        startActivity(dataIntent);

                    }

                } catch (IOException e) { }
            }

        }

        public void sendMessage(int arg){
            Message m = new Message();
            m.what = bluetooth_index;
            m.arg1 = arg;

        }

        public void sendMessage(int arg, String s){
            Message m = new Message();
            m.what = bluetooth_index;
            m.arg1 = arg;
            m.obj = s;

        }

        public void cancel(){
            is = false;

            if(bluetooth_index == 0) IsConnect0 = is;
            else IsConnect1 = is;

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
        super.onDestroy();

        if(connectThread0 != null || connectThread1 != null) {
            connectThread0.connectedThread.cancel();
            connectThread1.connectedThread.cancel();
        }
    }

}
