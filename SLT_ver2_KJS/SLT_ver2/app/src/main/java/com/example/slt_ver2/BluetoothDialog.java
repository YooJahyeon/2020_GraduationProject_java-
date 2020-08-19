package com.example.slt_ver2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.UUID;


import static com.example.slt_ver2.BluetoothService.state_left;
import static com.example.slt_ver2.BluetoothService.state_right;
import static com.example.slt_ver2.TranslateActivity.DISCONNECT;
import static com.example.slt_ver2.TranslateActivity.IsConnect1;
import static com.example.slt_ver2.utils.Constants.LEFT;
import static com.example.slt_ver2.utils.Constants.RIGHT;
import static com.example.slt_ver2.utils.Constants.STATE_CONNECTED;
import static com.example.slt_ver2.utils.Constants.STATE_CONNECTING;
import static com.example.slt_ver2.utils.Constants.STATE_DISCONNECTED;

public class BluetoothDialog extends AppCompatActivity implements View.OnClickListener {
    private static Context context;
    static BluetoothService bs;

    static Button connectbtn0; //연결 버튼(connect/disconnect)
    static Button connectbtn1; //연결 버튼(connect/disconnect)
    Button nextbutton; //다음 액티비티로 넘어가기 위한 버튼

    static ImageView lefthand; //왼손
    static ImageView righthand; //오른손

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
        super.onActivityResult(requestCode, resultCode, data);
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
                    case STATE_DISCONNECTED:
                        state_right = STATE_DISCONNECTED;
                        connectbtn0.setText("CONNECT");
                        connectbtn0.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.unconnected_button));
                        lefthand.setImageResource(R.drawable.ic_left_red);
                        break;
                    case STATE_CONNECTING:
                        connectbtn0.setText("CONNECTING");
                        break;
                    case STATE_CONNECTED:
                        state_right = STATE_CONNECTED;
//                        connectbtn0.setEnabled(true);
                        System.out.println("Right DISCONNECT : " + state_right);
                        connectbtn0.setText("DISCONNECT");
                        connectbtn0.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.connected_button));
                        lefthand.setImageResource(R.drawable.ic_left_green);

                        break;

                }

            } else {
                switch (msg.arg1) {
                    case STATE_DISCONNECTED:
                        state_left = STATE_DISCONNECTED;
                        connectbtn1.setText("CONNECT");
                        connectbtn1.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.unconnected_button));
                        righthand.setImageResource(R.drawable.ic_right_red);
                        break;
                    case STATE_CONNECTING:
                        connectbtn1.setText("CONNECTING");
                        break;
                    case STATE_CONNECTED:
                        state_left = STATE_CONNECTED;
//                        connectbtn1.setEnabled(true);
                        System.out.println("Left DISCONNECT : " + state_left);
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
                if ( MainActivity.bs.isConnected_left()) {
                    try {
                       MainActivity.bs.disconnectThread(LEFT);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Message m = new Message();
                    m.what = 0;
                    m.arg1 = STATE_DISCONNECTED;
                    handler.sendMessage(m);
                    break;
                } else {
                    //블루투스 끊어진 상태
                    Message m = new Message();
                    m.what = 0;
                    m.arg1 = STATE_CONNECTING;
                    handler.sendMessage(m);
                    startConnect_left();
                    break;
                }

            case R.id.RightConnectButton:
                if (IsConnect1) {
                    try {
                        MainActivity.bs.disconnectThread(RIGHT);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Message m = new Message();
                    m.what = 1;
                    m.arg1 = STATE_DISCONNECTED;
                    handler.sendMessage(m);
                    break;
                } else {
                    //블루투스 끊어진 상태
                    Message m = new Message();
                    m.what = 1;
                    m.arg1 = STATE_CONNECTING;
                    handler.sendMessage(m);
                    startConnect_right();
                    break;
                }
        }
    }

    private void startConnect_right() {
        if(!MainActivity.bs.isScanning_right()) {
            MainActivity.bs.scanLeDevice(true, RIGHT);
        }
    }
    private void startConnect_left() {
        if(!MainActivity.bs.isScanning_left()) {
            MainActivity.bs.scanLeDevice(true, LEFT);
        }
    }
}