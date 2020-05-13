package com.example.slt_ver2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;
import static android.media.session.PlaybackState.STATE_CONNECTING;
import static android.media.session.PlaybackState.STATE_NONE;

public class BluetoothActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BluetoothActivity: ";

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // 블루투스 어댑터
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothService bluetoothService = null;

    BluetoothDevice bluetoothDevice0,bluetoothDevice1;

    BluetoothService.ConnectThread BC1;
    BluetoothService.ConnectThread BC0;

    //0 = 왼손, 1 = 오른손
    final String B0MA = "98:D3:41:FD:6A:4E"; //Bluetooth0 Mac주소
    final String B1MA = "98:D3:91:FD:86:0E"; //Bluetooth1 Mac주소


    // 블루투스 연결 상태
    final int DISCONNECT = 0;
    final int CONNECTING = 1;
    final int CONNECTED = 2;
    final int INPUTDATA = 9999;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 9999;

    Button leftButton;
    Button rightButton;
    Button nextButton;

    ImageView rightHand;
    ImageView leftHand;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        rightHand = (ImageView)findViewById(R.id.RightHand);
        rightButton = (Button)findViewById(R.id.RightConnectButton);
        rightButton.setOnClickListener(this);

        leftHand = (ImageView)findViewById(R.id.LeftHand);
        leftButton = (Button)findViewById(R.id.LeftConnectButton);
        leftButton.setOnClickListener(this);

        // Bluetooth Init
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i, 5000);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    public void onStart()
    {
        super.onStart();

        // 블루투스 꺼져 있을 때
        if(mBluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 5000:
                if (resultCode == RESULT_CANCELED)
                {
                    finish();
                }
                break;
        }
    }

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                switch (msg.arg1) {
                    case BluetoothService.STATE_NONE:
                        rightButton.setText("DISCONNECT");
                        rightHand.setImageResource(R.drawable.ic_right_red);
                        leftButton.setText("DISCONNECT");
                        leftHand.setImageResource(R.drawable.ic_left_red);
                        break;

                    case BluetoothService.STATE_CONNECTING:
                        rightButton.setText("CONNECTING");
                        rightHand.setImageResource(R.drawable.ic_right_red);
                        break;

                    case BluetoothService.STATE_CONNECTED:
                        rightButton.setText("CONNECT");
                        rightHand.setImageResource(R.drawable.ic_right_green);
                        break;

                }
            }
            return false;
        }
    });

    @Override
    public void onClick(View v)
    {
        // nextButton
        if(v.getId() == R.id.nextbutton)
        {
            Intent intent = new Intent(getApplicationContext(), TranslationFragment.class);
            startActivity(intent);
        }

        //왼손 연결 버튼
        if(v.getId() == R.id.LeftConnectButton){
//            if(B0_state){
//                //블루투스 연결된 상태
//                if(connectThread0 != null){
//                    try {
//                        connectThread0.cancel();
//
//                        Message m = new Message();
//                        m.what = 0;
//                        m.arg1 = DISCONNECT;
//                        handler.sendMessage(m);
//
//                        connectThread0 = null;
//                    } catch (IOException e) { }
//                }
//            }
//            else {
//                //블루투스 끊어진 상태
//                v.setEnabled(false);
//                connectThread0 = new ConnectThread(bluetoothDevice0,0);
//                connectThread0.start();
//
//            }
        }

        //오른손 연결 버튼
        else{
            if(bluetoothService.IsConnect_R){
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
                //블루투스 끊어지면
                v.setEnabled(false);

                BC1 = new BluetoothService.ConnectThread(bluetoothDevice1,1);
                BC1.start();
            }
        }
    }

//    @Override
//    protected  void onStart() {
//        super.onStart();
//        Log.d("test","BluetoothActivity.java onStart");
//        if(!bluetoothAdapter.isEnabled()){
//            if (!bluetoothAdapter.isEnabled()){
//                Intent enableBtIntent= new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent,1000);
//            }
//        }
//
//    }



}
