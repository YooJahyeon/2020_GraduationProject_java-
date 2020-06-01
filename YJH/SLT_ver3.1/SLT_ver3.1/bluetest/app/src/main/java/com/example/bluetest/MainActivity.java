package com.example.bluetest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    private TextToSpeech tts;

    Button connectbtn0; //연결버튼(connect/disconnect)
    Button connectbtn1;

    TextView Bluetoothtext0;  //Bluetooth0
    TextView Bluetoothtext1;   //bluetooth1


    ListView Bluetoothvalue0;   //Bluetooth0 출력부
    ListView Bluetoothvalue1;   //Bluetooth1 출력부

    RelativeLayout Bluetoothlayout0;
    RelativeLayout Bluetoothlayout1;

    boolean IsConnect0 = false, IsConnect1 = false;


    ArrayList array0; //bluetooth0의 출력을 위한
    ArrayList array1;   //bluetooth1의 출력을 위한



    final int DISCONNECT = 0;
    final int CONNECTING = 1;
    final int CONNECTED = 2;
    final int INPUTDATA = 9999;

    MyView M0;
    MyView M1;

    private ArrayAdapter<String> mConversationArrayAdapter0;   //bluetooth0의 리스트뷰 출력을 위한 adapter
    private ArrayAdapter<String> mConversationArrayAdapter1;    //bluetooth1의 리스트뷰 출력을 위한 adapter
    private String s;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //----------------------Find VIEW---------------------------------//
        connectbtn0 = (Button)findViewById(R.id.connect0btn);
        connectbtn1 = (Button)findViewById(R.id.connect1btn);


        Bluetoothtext0 = (TextView)findViewById(R.id.bluetoothtext0);
        Bluetoothtext1 = (TextView)findViewById(R.id.bluetoothtext1);


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

        //----------------------SET Listener---------------------------------//
        connectbtn0.setOnClickListener(this);
        connectbtn1.setOnClickListener(this);

        //----------------------Bluetooth init---------------------------------//



        M0 = new MyView(this,0);
        M1 = new MyView(this,1);
        Bluetoothlayout0.addView(M0);
        Bluetoothlayout1.addView(M1);

        tts = new TextToSpeech(this, this); //첫번째는 Context 두번째는 리스너

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
                        mConversationArrayAdapter0.insert("-", 0);
                        IsConnect0 = false;
                        connectbtn0.setText("CONNECT");
                        Bluetoothtext0.setTextColor(Color.parseColor("#FF0000"));
                        Bluetoothtext0.setText("DISCONNECT");
                        break;
                    case CONNECTING:
                        Bluetoothtext0.setTextColor(Color.parseColor("#FF0000"));
                        Bluetoothtext0.setText("CONNECTING");
                        break;
                    case CONNECTED:
                        IsConnect0 = true;
                        connectbtn0.setEnabled(true);
                        connectbtn0.setText("DISCONNECT");
                        Bluetoothtext0.setTextColor(Color.parseColor("#00FF00"));
                        Bluetoothtext0.setText("CONNECTED");
                        break;
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
                    case DISCONNECT:
                        IsConnect1 = false;
                        mConversationArrayAdapter1.insert("-", 0);
                        connectbtn1.setText("CONNECT");
                        Bluetoothtext1.setTextColor(Color.parseColor("#FF0000"));
                        Bluetoothtext1.setText("DISCONNECT");
                        break;
                    case CONNECTING:
                        Bluetoothtext1.setTextColor(Color.parseColor("#FF0000"));
                        Bluetoothtext1.setText("CONNECTING");
                        break;
                    case CONNECTED:
                        IsConnect1 = true;
                        connectbtn1.setEnabled(true);
                        connectbtn1.setText("DISCONNECT");
                        Bluetoothtext1.setTextColor(Color.parseColor("#00FF00"));
                        Bluetoothtext1.setText("CONNECTED");
                        break;
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
    public void onClick(View v) {
        if(v.getId() == R.id.connect0btn){
            if(IsConnect0){
                //블루투스 연결된 상태
                if(BC0 != null){
                    try {
                        BC0.cancel();

                        Message m = new Message();
                        m.what = 0;
                        m.arg1 = DISCONNECT;
                        handler.sendMessage(m);

                        BC0 = null;
                    } catch (IOException e) { }
                }
            }
            else {
                //블루투스 끊어진 상태
                v.setEnabled(false);
                array0 = new ArrayList();
                Intent intent = new Intent(getApplicationContext(), BluetoothService.class);

                startService(intent);


            }
        }

        else{
            if(IsConnect1){
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
                //블루투스 끊어진
                v.setEnabled(false);
                array1 = new ArrayList();
                BC1 = new ConnectThread(B1,1);
                BC1.start();
            }
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
    

    class MyView extends View{
        int Bluetooth_index = 0;
        Paint p;
        int dp = 3;

        public MyView(Context context, int i) {
            super(context);

            Bluetooth_index = i;
            p = new Paint();
            p.setStrokeWidth(10f);
            p.setStyle(Paint.Style.STROKE);
        }
    }

    private void speakOutNow() {
        String text = (String)s;
        //tts.setPitch((float) 0.1); //음량
        //tts.setSpeechRate((float) 0.5); //재생속도
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
