package com.example.slt_ver2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class BluetoothDialog extends AppCompatActivity implements View.OnClickListener {
    private static Context context;

    static Button connectbtn0; //연결 버튼(connect/disconnect)
    static Button connectbtn1; //연결 버튼(connect/disconnect)
    Button nextbutton; //다음 액티비티로 넘어가기 위한 버튼

    static ImageView lefthand; //왼손
    static ImageView righthand; //오른손

    static boolean IsConnect0 = false;
    static boolean IsConnect1 = false;

    final String B0MA = "98:D3:71:FD:9D:1F"; //Bluetooth0 Mac주소
    final String B1MA = "98:D3:C1:FD:69:59";

    final String SPP_UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB"; //SPP UUID
    final UUID SPP_UUID = UUID.fromString(SPP_UUID_STRING);

    final static int DISCONNECT = 0;
    final static int CONNECTING = 1;
    final static int CONNECTED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_dialog);
        context = getApplicationContext();

        //----------------------Find VIEW---------------------------------//
        connectbtn0 = (Button) findViewById(R.id.LeftConnectButton);
        connectbtn1 = (Button) findViewById(R.id.RightConnectButton);
        nextbutton = (Button) findViewById(R.id.NextButton);
        lefthand = (ImageView) findViewById(R.id.LeftHand);
        righthand = (ImageView) findViewById(R.id.RightHand);

        //----------------------SET Listener---------------------------------//
        connectbtn0.setOnClickListener(this);
        connectbtn1.setOnClickListener(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    // 바깥 레이어 클릭시 안 닫힘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    //뒤로가기 버튼 막기
//    @Override
//    public void onBackPressed() {
//        return;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 5000) {
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    //Bluetooth state -> View Change
    static Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                switch (msg.arg1) {
                    case DISCONNECT:
                        IsConnect0 = false;
                        connectbtn0.setText("CONNECT");
                        connectbtn0.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.unconnected_button));
                        lefthand.setImageResource(R.drawable.ic_left_red);
                        break;
                    case CONNECTING:
                        connectbtn0.setText("CONNECTING");
                        break;
                    case CONNECTED:
                        IsConnect0 = true;
//                        connectbtn0.setEnabled(true);
                        System.out.println("0 DISCONNECT : " + IsConnect0);
                        connectbtn0.setText("DISCONNECT");
                        connectbtn0.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.connected_button));
                        lefthand.setImageResource(R.drawable.ic_left_green);

                        break;

                }

            } else {
                switch (msg.arg1) {
                    case DISCONNECT:
                        IsConnect1 = false;
                        connectbtn1.setText("CONNECT");
                        connectbtn1.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.unconnected_button));
                        righthand.setImageResource(R.drawable.ic_right_red);
                        break;
                    case CONNECTING:
                        connectbtn1.setText("CONNECTING");
                        break;
                    case CONNECTED:
                        IsConnect1 = true;
//                        connectbtn1.setEnabled(true);
                        System.out.println("1 DISCONNECT : " + IsConnect1);
                        connectbtn1.setText("DISCONNECT");
                        connectbtn1.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.connected_button));
                        righthand.setImageResource(R.drawable.ic_right_green);
                        break;

                }
            }
            return true;
        }
    });

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.NextButton:
                TranslationFragment.startTrans = true;
                onBackPressed();
                break;

            case R.id.LeftConnectButton:
                if (IsConnect0) {
                    try {
                        TranslationFragment.bluetoothService.DIsconnectThread(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Message m = new Message();
                    m.what = 0;
                    m.arg1 = DISCONNECT;
                    handler.sendMessage(m);
                    break;
                } else {
                    //블루투스 끊어진 상태
                    Message m = new Message();
                    m.what = 0;
                    m.arg1 = CONNECTING;
                    handler.sendMessage(m);
                    TranslationFragment.bluetoothService.getDeviceInfo(B0MA, 0);
                    break;
                }

            case R.id.RightConnectButton:
                if (IsConnect1) {
                    try {
                        TranslationFragment.bluetoothService.DIsconnectThread(1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Message m = new Message();
                    m.what = 1;
                    m.arg1 = DISCONNECT;
                    handler.sendMessage(m);
                    break;
                } else {
                    //블루투스 끊어진 상태
                    Message m = new Message();
                    m.what = 1;
                    m.arg1 = CONNECTING;
                    handler.sendMessage(m);
                    TranslationFragment.bluetoothService.getDeviceInfo_right(B1MA, 1);
                    break;
                }
        }
    }
}