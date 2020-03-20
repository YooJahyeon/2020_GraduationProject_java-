package com.example.bluetoothconnect;

//아직 테스트를 못해본 버전
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

public class TranslateActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener{

    private static TextToSpeech tts;

    Button previous_button; //이전 버튼

    TextView tran_Bluetoothtext0; //Bluetooth0
    TextView tran_Bluetoothtext1; //Bluetooth1

    ListView tran_Bluetoothvalue0; //Bluetooth0 출력부
    ListView tran_Bluetoothvalue1;  //Bluetooth1 출력부

    RelativeLayout tran_Bluetoothlayout0;
    RelativeLayout tran_Bluetoothlayout1;

    MainActivity.ConnectThread connectThread0;
    MainActivity.ConnectThread connectThread1;

    static boolean IsConnect0 = false;
    static boolean IsConnect1 = false;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice0,bluetoothDevice1;


    static ArrayList array0; //bluetooth0의 출력을 위한
    static ArrayList array1;  //bluetooth1의 출력을 위한

    final String B0MA = "98:D3:41:FD:6A:4E"; //Bluetooth0 Mac주소
    final String B1MA = "98:D3:91:FD:86:0E"; //Bluetooth1 Mac주소

    static final int DISCONNECT = 0;
    static final int CONNECTED = 2;
    static final int INPUTDATA = 9999;

    static MainActivity.MyView M0;
    static MainActivity.MyView M1;
    private static ArrayAdapter<String> mConversationArrayAdapter0;  //bluetooth0의 리스트뷰 출력을 위한 adapter
    private static ArrayAdapter<String> mConversationArrayAdapter1;  //bluetooth1의 리스트뷰 출력을 위한 adapter
    private static String s;   //message
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        tran_Bluetoothtext0 = (TextView)findViewById(R.id.tran_bluetoothtext0);
        tran_Bluetoothtext1 = (TextView)findViewById(R.id.tran_bluetoothtext1);

        tran_Bluetoothvalue0 = (ListView) findViewById(R.id.value0);
        tran_Bluetoothvalue1 = (ListView)findViewById(R.id.value1);

        tran_Bluetoothlayout0 = (RelativeLayout)findViewById(R.id.tran_bluetoothlayout0);
        tran_Bluetoothlayout1 = (RelativeLayout)findViewById(R.id.tran_bluetoothlayout1);

        previous_button = (Button)findViewById(R.id.previous);

        bluetoothDevice0 = bluetoothAdapter.getRemoteDevice(B0MA);
        bluetoothDevice1 = bluetoothAdapter.getRemoteDevice(B1MA);


        mConversationArrayAdapter0 = new ArrayAdapter<>( this,
                android.R.layout.simple_list_item_1);
        mConversationArrayAdapter1 = new ArrayAdapter<>( this,
                android.R.layout.simple_list_item_1);
        tran_Bluetoothvalue0.setAdapter(mConversationArrayAdapter0);
        tran_Bluetoothvalue1.setAdapter(mConversationArrayAdapter1);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.previous) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    //Bluetooth state -> View Change
    static Handler handler = new Handler(new Handler.Callback() {
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
    public static class ConnectedThread extends Thread{
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
    private static void speakOutNow() {
        String text = s;
        //tts.setPitch((float) 0.1); //음량
        tts.setSpeechRate((float) 0.7); //재생속도 조절하기
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
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
