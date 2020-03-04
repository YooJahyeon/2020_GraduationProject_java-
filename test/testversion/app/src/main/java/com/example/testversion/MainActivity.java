package com.example.testversion;

import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;
    public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

        private TextToSpeech tts;

        Button connectbtn0;
        Button connectbtn1;
        @ -36,8 +44,8 @@ public class MainActivity extends AppCompatActivity implements View.OnClickListe
    TextView Bluetoothtext1;


        TextView Bluetoothvalue0;
        TextView Bluetoothvalue1;
        ListView Bluetoothvalue0;
        ListView Bluetoothvalue1;

        RelativeLayout Bluetoothlayout0;
        RelativeLayout Bluetoothlayout1;
        @ -66,6 +74,9 @@ public class MainActivity extends AppCompatActivity implements View.OnClickListe

    MyView M0;
        MyView M1;
        private ArrayAdapter<String> mConversationArrayAdapter0;
        private ArrayAdapter<String> mConversationArrayAdapter1;
        private String s;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            @ -80,8 +91,16 @@ public class MainActivity extends AppCompatActivity implements View.OnClickListe
        Bluetoothtext1 = (TextView)findViewById(R.id.bluetoothtext1);


            Bluetoothvalue0 = (TextView)findViewById(R.id.value0);
            Bluetoothvalue1 = (TextView)findViewById(R.id.value1);
            Bluetoothvalue0 = (ListView)findViewById(R.id.value0);
            Bluetoothvalue1 = (ListView)findViewById(R.id.value1);

            mConversationArrayAdapter0 = new ArrayAdapter<>( this,
                    android.R.layout.simple_list_item_1);
            mConversationArrayAdapter1 = new ArrayAdapter<>( this,
                    android.R.layout.simple_list_item_1);
            Bluetoothvalue0.setAdapter(mConversationArrayAdapter0);
            Bluetoothvalue1.setAdapter(mConversationArrayAdapter1);


            Bluetoothlayout0 = (RelativeLayout)findViewById(R.id.bluetoothlayout0);
            Bluetoothlayout1 = (RelativeLayout)findViewById(R.id.bluetoothlayout1);
            @ -106,6 +125,10 @@ public class MainActivity extends AppCompatActivity implements View.OnClickListe
        M1 = new MyView(this,1);
            Bluetoothlayout0.addView(M0);
            Bluetoothlayout1.addView(M1);

            tts = new TextToSpeech(this, this); //첫번째는 Context 두번째는 리스너

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        @Override
        @ -124,7 +147,7 @@ public class MainActivity extends AppCompatActivity implements View.OnClickListe
            if(msg.what == 0){
            switch (msg.arg1){
                case DISCONNECT:
                    Bluetoothvalue0.setText("-");
                    mConversationArrayAdapter0.insert("-", 0);
                    IsConnect0 = false;
                    connectbtn0.setText("CONNECT");
                    Bluetoothtext0.setText("DISCONNECT");
                    @ -139,10 +162,11 @@ public class MainActivity extends AppCompatActivity implements View.OnClickListe
                        Bluetoothtext0.setText("CONNECTED");
                    break;
                case INPUTDATA:
                    String s = (String)msg.obj;
                    Bluetoothvalue0.setText(s);
                    s = (String)msg.obj;
                    mConversationArrayAdapter0.insert(s, 0);
                    if(!s.equals("")) {
                        array0.add(s);
                        speakOutNow();
                        M0.invalidate();
                    }
                    break;
                @ -154,7 +178,7 @@ public class MainActivity extends AppCompatActivity implements View.OnClickListe
                switch (msg.arg1){
                    case DISCONNECT:
                        IsConnect1 = false;
                        Bluetoothvalue1.setText("-");
                        mConversationArrayAdapter1.insert("-", 0);
                        connectbtn1.setText("CONNECT");
                        Bluetoothtext1.setText("DISCONNECT");
                        break;
                    @ -168,10 +192,11 @@ public class MainActivity extends AppCompatActivity implements View.OnClickListe
                        Bluetoothtext1.setText("CONNECTED");
                    break;
                    case INPUTDATA:
                        String s = (String)msg.obj;
                        Bluetoothvalue1.setText(s);
                        s = (String)msg.obj;
                        mConversationArrayAdapter1.insert(s, 0);
                        if(!s.equals("")){
                            array1.add(s);
                            speakOutNow();
                            M1.invalidate();
                        }
                        break;
                    @ -235,6 +260,21 @@ public class MainActivity extends AppCompatActivity implements View.OnClickListe
                }
            }

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int language = tts.setLanguage(Locale.KOREAN);

                    if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(this, "지원하지 않는 언어입니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        speakOutNow();
                    }
                } else {
                    Toast.makeText(this, "TTS 실패!", Toast.LENGTH_SHORT).show();
                }
            }

            //connect bluetooth
            class ConnectThread extends Thread{
                public class MainActivity extends AppCompatActivity implements View.OnClickListe
            p.setStyle(Paint.Style.STROKE);
            }
        }
        private void speakOutNow() {
            String text = (String)s;
            //tts.setPitch((float) 0.1); //음량
            //tts.setSpeechRate((float) 0.5); //재생속도
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }

    }
