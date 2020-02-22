package com.example.bluetoothconnect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextToSpeech tts;

    Button connectbtn0; //연결 버튼(connect/disconnect)
    Button connectbtn1; //연결 버튼(connect/disconnect)
    Button nextbutton; //다음 액티비티로 넘어가기 위한 버튼

    TextView Bluetoothtext0; //Bluetooth0
    TextView Bluetoothtext1; //Bluetooth1

    boolean IsConnect0 = false, IsConnect1 = false;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice0,bluetoothDevice1;

    ConnectThread connectThread0;
    ConnectThread connectThread1;

    final String B0MA = "98:D3:41:FD:6A:4E"; //Bluetooth0 Mac주소
    final String B1MA = "98:D3:91:FD:86:0E"; //Bluetooth1 Mac주소

    final String SPP_UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB"; //SPP UUID
    final UUID SPP_UUID = UUID.fromString(SPP_UUID_STRING);

    final int DISCONNECT = 0;
    final int CONNECTING = 1;
    final int CONNECTED = 2;
    final int INPUTDATA = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //----------------------Find VIEW---------------------------------//
        connectbtn0 = (Button)findViewById(R.id.connect0btn);
        connectbtn1 = (Button)findViewById(R.id.connect1btn);
        nextbutton = (Button)findViewById(R.id.nextbutton);

        Bluetoothtext0 = (TextView)findViewById(R.id.bluetoothtext0);
        Bluetoothtext1 = (TextView)findViewById(R.id.bluetoothtext1);

        //----------------------SET Listener---------------------------------//
        connectbtn0.setOnClickListener(this);
        connectbtn1.setOnClickListener(this);

        //----------------------Bluetooth init---------------------------------//

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!bluetoothAdapter.isEnabled()){
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i,5000);
        }

        bluetoothDevice0 = bluetoothAdapter.getRemoteDevice(B0MA);
        bluetoothDevice1 = bluetoothAdapter.getRemoteDevice(B1MA);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
                        IsConnect0 = false;
                        connectbtn0.setText("CONNECT");
                        Bluetoothtext0.setTextColor(Color.parseColor("#FF0000"));
                        Bluetoothtext0.setText("DISCONNECT");
                        break;
                    case CONNECTING:
                        Bluetoothtext0.setTextColor(Color.parseColor("#FF0000"));
                        Bluetoothtext0.setText("CONNECTING");
                        break;
                    case CONNECTED:
                        IsConnect0 = true;
                        connectbtn0.setEnabled(true);
                        connectbtn0.setText("DISCONNECT");
                        Bluetoothtext0.setTextColor(Color.parseColor("#00FF00"));
                        Bluetoothtext0.setText("CONNECTED");
                        break;

                }

            }
            else{
                switch (msg.arg1){
                    case DISCONNECT:
                        IsConnect1 = false;
                        connectbtn1.setText("CONNECT");
                        Bluetoothtext1.setTextColor(Color.parseColor("#FF0000"));
                        Bluetoothtext1.setText("DISCONNECT");
                        break;
                    case CONNECTING:
                        Bluetoothtext1.setTextColor(Color.parseColor("#FF0000"));
                        Bluetoothtext1.setText("CONNECTING");
                        break;
                    case CONNECTED:
                        IsConnect1 = true;
                        connectbtn1.setEnabled(true);
                        connectbtn1.setText("DISCONNECT");
                        Bluetoothtext1.setTextColor(Color.parseColor("#00FF00"));
                        Bluetoothtext1.setText("CONNECTED");
                        break;

                }
            }
            return true;
        }
    });

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.nextbutton)
        {
            Intent intent = new Intent(getApplicationContext(), translateactivity.class);
            startActivity(intent);
        }
        if(v.getId() == R.id.connect0btn){
            if(IsConnect0){
                //블루투스 연결된 상태
                if(connectThread0 != null){
                    try {
                        connectThread0.cancel();

                        Message m = new Message();
                        m.what = 0;
                        m.arg1 = DISCONNECT;
                        handler.sendMessage(m);

                        connectThread0 = null;
                    } catch (IOException e) { }
                }
            }
            else {
                //블루투스 끊어진 상태
                v.setEnabled(false);
                connectThread0 = new ConnectThread(bluetoothDevice0,0);
                connectThread0.start();

            }
        }

        else{
            if(IsConnect1){
                //블루투스 연결된 상태
                if(connectThread1 != null){
                    try {
                        connectThread1.cancel();

                        Message m = new Message();
                        m.what = 1;
                        m.arg1 = DISCONNECT;
                        handler.sendMessage(m);

                        connectThread1 = null;
                    } catch (IOException e) { }
                }
            }else{
                //블루투스 끊어지면
                v.setEnabled(false);

                connectThread1 = new ConnectThread(bluetoothDevice1,1);
                connectThread1.start();
            }
        }
    }

    //connect bluetooth
    class ConnectThread extends Thread{

        BluetoothDevice bluetoothDevice;
        BluetoothSocket bluetoothSocket;

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

            handler.sendMessage(m);
        }
    }

    //connected bluetooth - communication
    public class ConnectedThread extends Thread{
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
        public MyView(Context context, int i) {
            super(context);
            Bluetooth_index = i;
            p = new Paint();
            p.setStrokeWidth(10f);
            p.setStyle(Paint.Style.STROKE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if(connectThread0 != null || connectThread1 != null) {
            connectThread0.connectedThread.cancel();
            connectThread1.connectedThread.cancel();
        }
    }

}
