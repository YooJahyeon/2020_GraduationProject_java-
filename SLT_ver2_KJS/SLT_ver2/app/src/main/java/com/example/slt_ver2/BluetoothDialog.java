package com.example.slt_ver2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;
<<<<<<< HEAD
import java.util.UUID;
=======

import static com.example.slt_ver2.BluetoothService.state_left;
import static com.example.slt_ver2.BluetoothService.state_right;
import static com.example.slt_ver2.utils.Constants.LEFT;
import static com.example.slt_ver2.utils.Constants.RIGHT;
import static com.example.slt_ver2.utils.Constants.STATE_CONNECTED;
import static com.example.slt_ver2.utils.Constants.STATE_CONNECTING;
import static com.example.slt_ver2.utils.Constants.STATE_DISCONNECTED;
>>>>>>> parent of ab5bef1... Revert "200821 YJH SLT_ble"

public class BluetoothDialog extends AppCompatActivity implements View.OnClickListener {
    private static Context context;

    private static final String TAG = "BluetoothDialog";

    static Button connectbtn_left; //연결 버튼(connect/disconnect)
    static Button connectbtn_right; //연결 버튼(connect/disconnect)
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
        connectbtn_left = (Button) findViewById(R.id.LeftConnectButton);
        connectbtn_right = (Button) findViewById(R.id.RightConnectButton);
        nextbutton = (Button) findViewById(R.id.NextButton);
        lefthand = (ImageView) findViewById(R.id.LeftHand);
        righthand = (ImageView) findViewById(R.id.RightHand);

        //----------------------SET Listener---------------------------------//
        connectbtn_left.setOnClickListener(this);
        connectbtn_right.setOnClickListener(this);

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
            if (msg.what == LEFT) {
                switch (msg.arg1) {
<<<<<<< HEAD
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
=======
                    case STATE_DISCONNECTED:
                        state_right = STATE_DISCONNECTED;
                        connectbtn_left.setText("CONNECT");
                        connectbtn_left.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.unconnected_button));
                        lefthand.setImageResource(R.drawable.ic_left_red);
                        break;
                    case STATE_CONNECTING:
                        connectbtn_left.setText("CONNECTING");
                        break;
                    case STATE_CONNECTED:
//                        connectbtn0.setEnabled(true);
                        connectbtn_left.setText("DISCONNECT");
                        connectbtn_left.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.connected_button));
>>>>>>> parent of ab5bef1... Revert "200821 YJH SLT_ble"
                        lefthand.setImageResource(R.drawable.ic_left_green);

                        break;

                }

            }
            else {
                switch (msg.arg1) {
<<<<<<< HEAD
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
=======
                    case STATE_DISCONNECTED:
                        state_left = STATE_DISCONNECTED;
                        connectbtn_right.setText("CONNECT");
                        connectbtn_right.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.unconnected_button));
                        righthand.setImageResource(R.drawable.ic_right_red);
                        break;
                    case STATE_CONNECTING:
                        connectbtn_right.setText("CONNECTING");
                        break;
                    case STATE_CONNECTED:
//                        state_left = STATE_CONNECTED;
//                        connectbtn1.setEnabled(true);
                        System.out.println("Right DISCONNECT : " + state_right);
                        connectbtn_right.setText("DISCONNECT");
                        connectbtn_right.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.connected_button));
>>>>>>> parent of ab5bef1... Revert "200821 YJH SLT_ble"
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
<<<<<<< HEAD
                if (IsConnect0) {
                    try {
                        MainActivity.bluetoothService.DIsconnectThread(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Message m = new Message();
                    m.what = 0;
                    m.arg1 = DISCONNECT;
=======
                if ( MainActivity.bs.isConnected_left()) {
                    MainActivity.bs.disconnect(LEFT);

                    Message m = new Message();
                    m.what = LEFT;
                    m.arg1 = STATE_DISCONNECTED;
>>>>>>> parent of ab5bef1... Revert "200821 YJH SLT_ble"
                    handler.sendMessage(m);
                    break;
                } else {
                    //블루투스 끊어진 상태
                    Message m = new Message();
<<<<<<< HEAD
                    m.what = 0;
                    m.arg1 = CONNECTING;
=======
                    m.what = LEFT;
                    m.arg1 = STATE_CONNECTING;
>>>>>>> parent of ab5bef1... Revert "200821 YJH SLT_ble"
                    handler.sendMessage(m);
                    MainActivity.bluetoothService.getDeviceInfo(B0MA, 0);
                    break;
                }

            case R.id.RightConnectButton:
<<<<<<< HEAD
                if (IsConnect1) {
                    try {
                        MainActivity.bluetoothService.DIsconnectThread(1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Message m = new Message();
                    m.what = 1;
                    m.arg1 = DISCONNECT;
=======
                if (MainActivity.bs.isConnected_right()) {
                    MainActivity.bs.disconnect(RIGHT);

                    Message m = new Message();
                    m.what = RIGHT;
                    m.arg1 = STATE_DISCONNECTED;
>>>>>>> parent of ab5bef1... Revert "200821 YJH SLT_ble"
                    handler.sendMessage(m);
                    break;
                } else {
                    //블루투스 끊어진 상태
                    Message m = new Message();
<<<<<<< HEAD
                    m.what = 1;
                    m.arg1 = CONNECTING;
=======
                    m.what = RIGHT;
                    m.arg1 = STATE_CONNECTING;
>>>>>>> parent of ab5bef1... Revert "200821 YJH SLT_ble"
                    handler.sendMessage(m);
                    MainActivity.bluetoothService.getDeviceInfo_right(B1MA, 1);
                    break;
                }
        }
    }
<<<<<<< HEAD
=======

    public static void startConnect_right() {
        if(!MainActivity.bs.isScanning_right()) {
            MainActivity.bs.scanLeDevice(true, RIGHT);
        }
    }
    public static void startConnect_left() {
        if(!MainActivity.bs.isScanning_left()) {
            MainActivity.bs.scanLeDevice(true, LEFT);
        }
    }
>>>>>>> parent of ab5bef1... Revert "200821 YJH SLT_ble"
}