package com.example.slt_ver2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity implements View.OnClickListener {
    BluetoothSocket bluetoothSocket;

    Button connectbtn0; //연결 버튼(connect/disconnect)
    Button connectbtn1; //연결 버튼(connect/disconnect)
    Button nextbutton; //다음 액티비티로 넘어가기 위한 버튼

    ImageView lefthand; //왼손
    ImageView righthand; //오른손

    private BluetoothService bluetoothService = null;

    static boolean IsConnect0 = false;
    static boolean IsConnect1 = false;

    BluetoothAdapter bluetoothAdapter;
    static BluetoothDevice bluetoothDevice0,bluetoothDevice1;

    static ConnectThread connectThread0;
    static ConnectThread connectThread1;

    final String B0MA = "98:D3:41:FD:6A:4E"; //Bluetooth0 Mac주소
    final String B1MA = "98:D3:C1:FD:69:59"; //Bluetooth1 Mac주소

    final String SPP_UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB"; //SPP UUID
    final UUID SPP_UUID = UUID.fromString(SPP_UUID_STRING);

    final int DISCONNECT = 0;
    final static int CONNECTING = 1;
    final int CONNECTED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        //----------------------Find VIEW---------------------------------//
        connectbtn0 = (Button)findViewById(R.id.LeftConnectButton);
        connectbtn1 = (Button)findViewById(R.id.RightConnectButton);
        nextbutton = (Button)findViewById(R.id.NextButton);


        lefthand = (ImageView)findViewById(R.id.LeftHand);
        righthand = (ImageView)findViewById(R.id.RightHand);

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
                        connectbtn0.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.unconnected_button));
                        righthand.setImageResource(R.drawable.ic_left_red);
                        break;
                    case CONNECTING:
                        connectbtn0.setText("CONNECTING");
                        lefthand.setImageResource(R.drawable.ic_left);
                        break;
                    case CONNECTED:
                        IsConnect0 = true;
                        connectbtn0.setEnabled(true);
                        connectbtn0.setText("DISCONNECT");
                        connectbtn0.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.connected_button));
                        lefthand.setImageResource(R.drawable.ic_left_green);

                        break;

                }

            }
            else {
                switch (msg.arg1){
                    case DISCONNECT:
                        IsConnect1 = false;
                        connectbtn1.setText("CONNECT");
                        connectbtn1.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.unconnected_button));
                        righthand.setImageResource(R.drawable.ic_right_red);
                        break;
                    case CONNECTING:
                        connectbtn1.setText("CONNECTING");
                        righthand.setImageResource(R.drawable.ic_right);
                        break;
                    case CONNECTED:
                        IsConnect1 = true;
                        connectbtn1.setEnabled(true);
                        connectbtn1.setText("DISCONNECT");
                        connectbtn1.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.connected_button));
                        righthand.setImageResource(R.drawable.ic_right_green);
                        break;

                }
            }
            return true;
        }
    });

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.NextButton)
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
//            connectThread0.connectedThread.cancel();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        else if(v.getId() == R.id.LeftConnectButton){
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
        //BluetoothSocket bluetoothSocket;

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

                connectedThread = new BluetoothActivity.ConnectedThread(bluetoothSocket, bluetooth_index);
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
                } catch (IOException e) { }
            }
        }

        public void sendMessage(int arg){
            Message m = new Message();
            m.what = bluetooth_index;
            m.arg1 = arg;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(connectThread0 != null || connectThread1 != null) {
//            connectThread0.connectedThread.cancel();
//            connectThread1.connectedThread.cancel();
//        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        try {
//            bluetoothSocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}