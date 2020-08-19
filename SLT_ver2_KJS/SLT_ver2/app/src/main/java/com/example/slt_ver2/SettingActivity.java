package com.example.slt_ver2;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;


import static com.example.slt_ver2.TranslateActivity.CONNECTED;
import static com.example.slt_ver2.TranslateActivity.DISCONNECT;
import static com.example.slt_ver2.TranslateActivity.IsConnect0;
import static com.example.slt_ver2.TranslateActivity.IsConnect1;
import static com.example.slt_ver2.utils.Constants.STATE_CONNECTED;
import static com.example.slt_ver2.utils.Constants.STATE_CONNECTING;
import static com.example.slt_ver2.utils.Constants.STATE_DISCONNECTED;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    RadioButton button_050;
    RadioButton button_075;
    RadioButton button_100;
    RadioButton button_125;
    RadioButton button_150;

    CheckBox total;
    CheckBox individual;

    SeekBar seekbar_volume;

    Button left_connect;
    Button right_connect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        button_050 = (RadioButton)findViewById(R.id.button_050);
        button_075 = (RadioButton)findViewById(R.id.button_075);
        button_100 = (RadioButton)findViewById(R.id.button_100);
        button_125 = (RadioButton)findViewById(R.id.button_125);
        button_150 = (RadioButton)findViewById(R.id.button_150);

        total = (CheckBox)findViewById(R.id.total);
        individual = (CheckBox)findViewById(R.id.individual);

        left_connect = (Button)findViewById(R.id.left_connect);
        right_connect = (Button)findViewById(R.id.right_connect);

        //seekBar로 음량조절하기
        seekbar_volume = (SeekBar)findViewById(R.id.seekbar_volume);

        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int nMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int nCurrentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        seekbar_volume.setMax(nMax);
        seekbar_volume.setProgress(nCurrentVolume);

        seekbar_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }
        });
    }


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == 0){
                switch (msg.arg1){
                    case STATE_DISCONNECTED:
                        BluetoothService.state_right = STATE_DISCONNECTED;
                        right_connect.setText("CONNECT");
                        break;
                    case STATE_CONNECTING:
                        right_connect.setText("CONNECTING");
                        break;
                    case STATE_CONNECTED:
                        BluetoothService.state_right = STATE_CONNECTED;
                        right_connect.setEnabled(true);
                        right_connect.setText("DISCONNECT");

                        break;

                }

            }
            else{
                switch (msg.arg1){
                    case STATE_DISCONNECTED:
                        BluetoothService.state_left = STATE_DISCONNECTED;
                        left_connect.setText("CONNECT");
                        break;
                    case STATE_CONNECTING:
                        break;
                    case STATE_CONNECTED:
                        BluetoothService.state_left = STATE_CONNECTED;
                        left_connect.setEnabled(true);
                        left_connect.setText("DISCONNECT");
                        break;

                }
            }
            return true;
        }
    });


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.button_050)
        {
            TranslateActivity.speed = (float)0.5;
        }
        else if(v.getId()==R.id.button_075)
        {
            TranslateActivity.speed = (float)0.75;
        }
        else if(v.getId()==R.id.button_100)
        {
            TranslateActivity.speed = (float)1.00;
        }
        else if(v.getId()==R.id.button_125)
        {
            TranslateActivity.speed = (float)1.25;
        }
        else if(v.getId()==R.id.button_150)
        {
            TranslateActivity.speed = (float)1.50;
        }

        else if(v.getId()==R.id.individual)
        {

        }
        else if(v.getId()==R.id.total)
        {

        }

        else if(v.getId()==R.id.left_connect)
        {
            if(IsConnect0){
                //블루투스 연결된 상태
                if(MainActivity.bs.connectThread_right != null){
                    try {
                        MainActivity.bs.connectThread_right.cancel();

                        Message m = new Message();
                        m.what = 0;
                        m.arg1 = DISCONNECT;
                        handler.sendMessage(m);

                        MainActivity.bs.connectThread_right = null;
                    } catch (IOException e) { }
                }
            }
            else {
                //블루투스 끊어진 상태
                v.setEnabled(false);
//                connectThread0 = new MainActivity.ConnectThread(bluetoothDevice0,0);
                MainActivity.bs.connectThread_right.start();

            }
        }

        else if(v.getId()==R.id.right_connect)
        {
            if(IsConnect1){
                //블루투스 연결된 상태
                if(MainActivity.bs.connectThread_left != null){
                    try {
                        MainActivity.bs.connectThread_left.cancel();

                        Message m = new Message();
                        m.what = 1;
                        m.arg1 = DISCONNECT;
                        handler.sendMessage(m);

                        MainActivity.bs.connectThread_left = null;
                    } catch (IOException e) { }
                }
            }else{
                //블루투스 끊어지면
                v.setEnabled(false);

//                connectThread1 = new MainActivity.ConnectThread(bluetoothDevice1,1);
                MainActivity.bs.connectThread_left.start();
            }
        }

    }
}
