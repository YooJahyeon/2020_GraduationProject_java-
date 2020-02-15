package com.example.bluetoothconnect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button connectbtn0;
    Button connectbtn1;

    TextView Bluetoothtext0;
    TextView Bluetoothtext1;


    TextView Bluetoothvalue0;
    TextView Bluetoothvalue1;

    RelativeLayout Bluetoothlayout0;
    RelativeLayout Bluetoothlayout1;

    boolean IsConnect0 = false, IsConnect1 = false;

    BluetoothAdapter BA;
    BluetoothDevice B0,B1;

    ConnectThread BC0;
    ConnectThread BC1;

    ArrayList array0;
    ArrayList array1;

    final String B0MA = "98:D3:41:FD:6A:4E"; //Bluetooth0 MacAddress
    final String B1MA = "98:D3:91:FD:86:0E"; //Bluetooth1 MacAddress

    final String SPP_UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB"; //SPP UUID
    final UUID SPP_UUID = UUID.fromString(SPP_UUID_STRING);

    final int DISCONNECT = 0;
    final int CONNECTING = 1;
    final int CONNECTED = 2;
    final int INPUTDATA = 9999;

    MyView M0;
    MyView M1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //----------------------Find VIEW---------------------------------//
        connectbtn0 = (Button)findViewById(R.id.connect0btn);
        connectbtn1 = (Button)findViewById(R.id.connect1btn);


        Bluetoothtext0 = (TextView)findViewById(R.id.bluetoothtext0);
        Bluetoothtext1 = (TextView)findViewById(R.id.bluetoothtext1);


        Bluetoothvalue0 = (TextView)findViewById(R.id.value0);
        Bluetoothvalue1 = (TextView)findViewById(R.id.value1);

        Bluetoothlayout0 = (RelativeLayout)findViewById(R.id.bluetoothlayout0);
        Bluetoothlayout1 = (RelativeLayout)findViewById(R.id.bluetoothlayout1);

        //----------------------SET Listener---------------------------------//
        connectbtn0.setOnClickListener(this);
        connectbtn1.setOnClickListener(this);

        //----------------------Bluetooth init---------------------------------//

        BA = BluetoothAdapter.getDefaultAdapter();

        if(!BA.isEnabled()){
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i,5000);
        }

        B0 = BA.getRemoteDevice(B0MA);
        B1 = BA.getRemoteDevice(B1MA);

        M0 = new MyView(this,0);
        M1 = new MyView(this,1);
        Bluetoothlayout0.addView(M0);
        Bluetoothlayout1.addView(M1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 5000){
            if(resultCode == RESULT_CANCELED){
                finish();
            }
        }
    }

    //Bluetooth state -> View Change
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == 0){
                switch (msg.arg1){
                    case DISCONNECT:
                        Bluetoothvalue0.setText("-");
                        IsConnect0 = false;
                        connectbtn0.setText("CONNECT");
                        Bluetoothtext0.setText("DISCONNECT");
                        break;
                    case CONNECTING:
                        Bluetoothtext0.setText("CONNECTING");
                        break;
                    case CONNECTED:
                        IsConnect0 = true;
                        connectbtn0.setEnabled(true);
                        connectbtn0.setText("DISCONNECT");
                        Bluetoothtext0.setText("CONNECTED");
                        break;
                    case INPUTDATA:
                        String s = (String)msg.obj;
                        Bluetoothvalue0.setText(s);
                        if(!s.equals("")) {
                            array0.add(s);
                            M0.invalidate();
                        }
                        break;

                }

            }
            else{
                switch (msg.arg1){
                    case DISCONNECT:
                        IsConnect1 = false;
                        Bluetoothvalue1.setText("-");
                        connectbtn1.setText("CONNECT");
                        Bluetoothtext1.setText("DISCONNECT");
                        break;
                    case CONNECTING:
                        Bluetoothtext1.setText("CONNECTING");
                        break;
                    case CONNECTED:
                        IsConnect1 = true;
                        connectbtn1.setEnabled(true);
                        connectbtn1.setText("DISCONNECT");
                        Bluetoothtext1.setText("CONNECTED");
                        break;
                    case INPUTDATA:
                        String s = (String)msg.obj;
                        Bluetoothvalue1.setText(s);
                        if(!s.equals("")){
                            array1.add(s);
                            M1.invalidate();
                        }
                        break;

                }
            }
            return true;
        }
    });

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.connect0btn){
            if(IsConnect0){
                //블루투스 연결된 상태
                if(BC0 != null){
                    try {
                        BC0.cancel();

                        Message m = new Message();
                        m.what = 0;
                        m.arg1 = DISCONNECT;
                        handler.sendMessage(m);

                        BC0 = null;
                    } catch (IOException e) { }
                }
            }
            else {
                //블루투스 끈어진 상태
                v.setEnabled(false);
                array0 = new ArrayList();
                BC0 = new ConnectThread(B0,0);
                BC0.start();

            }
        }

        else{
            if(IsConnect1){
                //블루투스 연결된 상태
                if(BC1 != null){
                    try {
                        BC1.cancel();

                        Message m = new Message();
                        m.what = 1;
                        m.arg1 = DISCONNECT;
                        handler.sendMessage(m);

                        BC1 = null;
                    } catch (IOException e) { }
                }
            }else{
                //블루투스 끈어진
                v.setEnabled(false);
                array1 = new ArrayList();
                BC1 = new ConnectThread(B1,1);
                BC1.start();
            }
        }
    }

    //connect bluetooth
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

            handler.sendMessage(m);
        }
    }

    //connected bluetooth - communication
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
                        sendMessage(INPUTDATA,s);
                    }

                } catch (IOException e) { }
            }

        }

        public void sendMessage(int arg){
            Message m = new Message();
            m.what = bluetooth_index;
            m.arg1 = arg;

            handler.sendMessage(m);
        }

        public void sendMessage(int arg, String s){
            Message m = new Message();
            m.what = bluetooth_index;
            m.arg1 = arg;
            m.obj = s;

            handler.sendMessage(m);
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

    class MyView extends View{
        int Bluetooth_index = 0;
        Paint p;
        int dp = 3;

        public MyView(Context context, int i) {
            super(context);

            Bluetooth_index = i;
            p = new Paint();
            p.setStrokeWidth(10f);
            p.setStyle(Paint.Style.STROKE);
        }
    }
}
