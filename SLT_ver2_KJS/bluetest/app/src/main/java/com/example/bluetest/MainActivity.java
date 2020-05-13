package com.example.bluetest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
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

import com.example.bluetest.BluetoothService.ConnectThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.bluetest.BluetoothService.B0;
import static com.example.bluetest.BluetoothService.B1;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private static final String TAG = "bluetest";
    private static final boolean D = true;
    private BluetoothService bluetoothService = null;

    Button connectbtn0; //연결버튼(connect/disconnect)
    Button connectbtn1;

    TextView Bluetoothtext0;  //Bluetooth0
    TextView Bluetoothtext1;   //bluetooth1


    ListView Bluetoothvalue0;   //Bluetooth0 출력부
    ListView Bluetoothvalue1;   //Bluetooth1 출력부

    RelativeLayout Bluetoothlayout0;
    RelativeLayout Bluetoothlayout1;

    ConnectThread BC0;
    ConnectThread BC1;

    final String B0MA = "98:D3:41:FD:6A:4E"; //Bluetooth0 MacAddress
    final String B1MA = "98:D3:91:FD:86:0E";

    int blue_index0 = -1;
    int blue_index1 = -1;

    ArrayList array0; //bluetooth0의 출력을 위한
    ArrayList array1;   //bluetooth1의 출력을 위한

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 9999;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    MyView M0;
    MyView M1;
    private ArrayAdapter<String> mConversationArrayAdapter0;   //bluetooth0의 리스트뷰 출력을 위한 adapter
    private ArrayAdapter<String> mConversationArrayAdapter1;    //bluetooth1의 리스트뷰 출력을 위한 adapter
    private String readMessage;

    private BluetoothAdapter mBluetoothAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null){
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }
        //BluetoothService 클래스 생성
        if(bluetoothService == null) {
            bluetoothService = new BluetoothService(this, handler);
        }
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
        connectbtn0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bluetoothService.getDeviceInfo(B0MA);
                blue_index0 = 0;
            }
        });
        connectbtn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bluetoothService.getDeviceInfo(B1MA);
                blue_index1 = 1;
            }
        });

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
    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(blue_index0 == 0) {
                switch (msg.what) {
                    case MESSAGE_STATE_CHANGE:
                        if (D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                        switch (msg.arg1) {
                            case BluetoothService.STATE_NONE:
                                mConversationArrayAdapter0.insert("-", 0);
                                bluetoothService.IsConnect0 = false;
                                connectbtn0.setText("CONNECT");
                                Bluetoothtext0.setTextColor(Color.parseColor("#FF0000"));
                                Bluetoothtext0.setText("DISCONNECT");
                                break;
                            case BluetoothService.STATE_CONNECTING:
                                Bluetoothtext0.setTextColor(Color.parseColor("#FF0000"));
                                Bluetoothtext0.setText("CONNECTING");
                                break;
                            case BluetoothService.STATE_CONNECTED:
                                bluetoothService.IsConnect0 = true;
                                connectbtn0.setEnabled(true);
                                connectbtn0.setText("DISCONNECT");
                                Bluetoothtext0.setTextColor(Color.parseColor("#00FF00"));
                                Bluetoothtext0.setText("CONNECTED");
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        readMessage = (String)msg.obj;
                        Log.d("=== 왔어!== ", readMessage);

                        mConversationArrayAdapter0.add(readMessage);
//                        mConversationArrayAdapter0.insert(readMessage, 0);
                        speakOutNow();
                        break;
//                        s = (String) msg.obj;
//                        mConversationArrayAdapter0.insert(s, 0);
//                        if (!s.equals("")) {
//                            array0.add(s);
//                            speakOutNow();
//                            M0.invalidate();
//                        }
//                        break;

                }
            }
            if(blue_index1 == 1){
                switch (msg.what) {
                    case MESSAGE_STATE_CHANGE:
                        if (D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                        switch (msg.arg1) {
                            case BluetoothService.STATE_NONE:
                                bluetoothService.IsConnect1 = false;
                                mConversationArrayAdapter1.insert("-", 0);
                                connectbtn1.setText("CONNECT");
                                Bluetoothtext1.setTextColor(Color.parseColor("#FF0000"));
                                Bluetoothtext1.setText("DISCONNECT");
                                break;
                            case BluetoothService.STATE_CONNECTING:
                                Bluetoothtext1.setTextColor(Color.parseColor("#FF0000"));
                                Bluetoothtext1.setText("CONNECTING");
                                break;
                            case BluetoothService.STATE_CONNECTED:
                                bluetoothService.IsConnect1 = true;
                                connectbtn1.setEnabled(true);
                                connectbtn1.setText("DISCONNECT");
                                Bluetoothtext1.setTextColor(Color.parseColor("#00FF00"));
                                Bluetoothtext1.setText("CONNECTED");
                                break;
                        }break;
                    case MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        readMessage = new String(readBuf, 0, msg.arg1);
                        mConversationArrayAdapter1.add(readMessage);
                        speakOutNow();
                        break;
                }
            }
            return true;
        }
    });

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.connect0btn){
            if(bluetoothService.IsConnect0){
                //블루투스 연결된 상태
                if(BC0 != null){
                    try {
                        BC0.cancel();

                        Message m = new Message();
                        m.what = 0;
                        m.arg1 = BluetoothService.STATE_NONE;
                        handler.sendMessage(m);

                        BC0 = null;
                    } catch (IOException e) { }
                }
            }
            else {
                //블루투스 끊어진 상태
                v.setEnabled(false);
                array0 = new ArrayList();
//                BC0 = new ConnectThread(B0,0);
//                BC0.start();

            }
        }

        else{
            if(bluetoothService.IsConnect1){
                //블루투스 연결된 상태
                if(BC1 != null){
                    try {
                        BC1.cancel();

                        Message m = new Message();
                        m.what = 1;
                        m.arg1 = BluetoothService.STATE_NONE;
                        handler.sendMessage(m);

                        BC1 = null;
                    } catch (IOException e) { }
                }
            }else{
                //블루투스 끊어진
                v.setEnabled(false);
                array1 = new ArrayList();
//                BC1 = new ConnectThread(B1,1);
//                BC1.start();
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
        String text = (String)readMessage;
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
//        if(connectThread0 != null || connectThread1 != null) {
//            connectThread0.connectedThread.cancel();
//            connectThread1.connectedThread.cancel();
//        }
    }

}
