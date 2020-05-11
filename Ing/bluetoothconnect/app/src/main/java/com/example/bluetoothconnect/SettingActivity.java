package com.example.bluetoothconnect;

import android.graphics.Color;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SeekBar;

import static com.example.bluetoothconnect.TranslateActivity.CONNECTED;
import static com.example.bluetoothconnect.TranslateActivity.DISCONNECT;

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
                    case DISCONNECT:
                        MainActivity.IsConnect0 = false;
                        left_connect.setText("CONNECT");
                        Bluetoothtext0.setTextColor(Color.parseColor("#FF0000"));
                        Bluetoothtext0.setText("DISCONNECT");
                        break;
                    case CONNECTING:
                        Bluetoothtext0.setTextColor(Color.parseColor("#FF0000"));
                        Bluetoothtext0.setText("CONNECTING");
                        break;
                    case CONNECTED:
                        MainActivity.IsConnect0 = true;
                        left_connect.setEnabled(true);
                        right_connect.setText("DISCONNECT");
                        Bluetoothtext0.setTextColor(Color.parseColor("#00FF00"));
                        Bluetoothtext0.setText("CONNECTED");
                        break;

                }

            }
            else{
                switch (msg.arg1){
                    case DISCONNECT:
                        MainActivity.IsConnect1 = false;
                        right_connect.setText("CONNECT");
                        Bluetoothtext1.setTextColor(Color.parseColor("#FF0000"));
                        Bluetoothtext1.setText("DISCONNECT");
                        break;
                    case CONNECTING:
                        Bluetoothtext1.setTextColor(Color.parseColor("#FF0000"));
                        Bluetoothtext1.setText("CONNECTING");
                        break;
                    case CONNECTED:
                        MainActivity.IsConnect1 = true;
                        right_connect.setEnabled(true);
                        right_connect.setText("DISCONNECT");
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

        }

        else if(v.getId()==R.id.right_connect)
        {

        }

    }
}
