package com.example.bluetoothconnect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class translateactivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private TextToSpeech tts;

    TextView tran_Bluetoothtext0; //Bluetooth0
    TextView tran_Bluetoothtext1; //Bluetooth1

    ListView tran_Bluetoothvalue0; //Bluetooth0 출력부
    ListView tran_Bluetoothvalue1;  //Bluetooth1 출력부

    RelativeLayout tran_Bluetoothlayout0;
    RelativeLayout tran_Bluetoothlayout1;

    boolean IsConnect0 = false, IsConnect1 = false;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice0,bluetoothDevice1;

    MainActivity.ConnectThread connectThread0;
    MainActivity.ConnectThread connectThread1;

    ArrayList array0; //bluetooth0의 출력을 위한
    ArrayList array1;  //bluetooth1의 출력을 위한

    final int DISCONNECT = 0;
    final int CONNECTED = 2;
    final int INPUTDATA = 9999;

    MainActivity.MyView M0;
    MainActivity.MyView M1;
    private ArrayAdapter<String> mConversationArrayAdapter0;  //bluetooth0의 리스트뷰 출력을 위한 adapter
    private ArrayAdapter<String> mConversationArrayAdapter1;  //bluetooth1의 리스트뷰 출력을 위한 adapter
    private String s;   //message
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        tran_Bluetoothtext0 = (TextView)findViewById(R.id.tran_bluetoothtext0);
        tran_Bluetoothtext1 = (TextView)findViewById(R.id.tran_bluetoothtext1);

    }

    //Bluetooth state -> View Change
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == 0){
                switch (msg.arg1){
                    case INPUTDATA:
                        s = (String)msg.obj;
                        mConversationArrayAdapter0.insert(s, 0);
                        if(!s.equals("")) {
                            array0.add(s);
                            speakOutNow();
                            M0.invalidate();
                        }
                        break;

                }

            }
            else{
                switch (msg.arg1){
                    case INPUTDATA:
                        s = (String)msg.obj;
                        mConversationArrayAdapter1.insert(s, 0);
                        if(!s.equals("")){
                            array1.add(s);
                            speakOutNow();
                            M1.invalidate();
                        }
                        break;

                }
            }
            return true;
        }
    });

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
    private void speakOutNow() {
        String text = (String)s;
        //tts.setPitch((float) 0.1); //음량
        tts.setSpeechRate((float) 0.7); //재생속도
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

}
