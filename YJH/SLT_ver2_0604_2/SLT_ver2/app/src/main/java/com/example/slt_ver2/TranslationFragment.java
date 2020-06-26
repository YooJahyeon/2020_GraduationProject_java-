package com.example.slt_ver2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.slt_ver2.BluetoothService.B1;


public class TranslationFragment extends Fragment implements TextToSpeech.OnInitListener {

    private static final String TAG = "Translation";
    static BluetoothService bluetoothService = null;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 9999;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

//    final String B0MA = "98:D3:71:FD:9D:1F"; //Bluetooth0 MacAddress
//    final String B1MA = "98:D3:C1:FD:69:59";

    private ArrayAdapter<String> mConversationArrayAdapter;   //리스트뷰 출력을 위한 adapter
    //    private String readMessage1, readMessage0;
    private String recv_data;

    private BluetoothAdapter mBluetoothAdapter = null;

    private TextToSpeech tts;

    ListView listview;

    //소켓 코드
    private Socket socket;  //소켓생성
    BufferedReader in;      //서버로부터 온 데이터를 읽는다.
    static PrintWriter out;        //서버에 데이터를 전송한다.
    static String data;
    static String readMessage0, readMessage1;
    static String buffer0;
    static String buffer1;

    static boolean startTrans = false;

    //tts 속도 조절
    static double ttsSpeed = 1.0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!startTrans) {
            startActivity(new Intent(getContext(),BluetoothDialog.class));
        }
//        else
//            bluetoothService.startConnectedThread();
//
//        if(startTrans) {
//            bluetoothService.startConnectedThread();
//        }

        Thread worker = new Thread() {    //worker 를 Thread 로 생성
            public void run() { //스레드 실행구문
                try {
                    //소켓을 생성하고 입출력 스트립을 소켓에 연결한다.
                    socket = new Socket("115.85.173.148", 9999); //소켓생성
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true); //데이터를 전송시 stream 형태로 변환하여 전송한다.
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //데이터 수신시 stream을 받아들인다.

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //소켓에서 데이터를 읽어서 화면에 표시한다.
                try {
                    while (true) {
                        data = in.readLine(); // in으로 받은 데이타를 String 형태로 읽어 data 에 저장
                        recv_data = data;
                        listview.post(new Runnable() {
                            public void run() {
                                mConversationArrayAdapter.add(recv_data);
                                speakOutNow();
                                Log.d("========  ", recv_data);
                            }
                        });
                    }
                } catch (Exception e) {
                }
            }
        };
        worker.start();  //onResume()에서 실행.

        //BluetoothService 클래스 생성
        if(bluetoothService == null) {
            bluetoothService = new BluetoothService(this, handler);
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        bluetoothService.getDeviceInfo(B0MA, 0);
//        bluetoothService.getDeviceInfo_right(B1MA, 1);

        tts = new TextToSpeech(getContext(), this); //첫번째는 Context 두번째는 리스너

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_translation, container, false);

        //리스트뷰
        listview = (ListView)view.findViewById(R.id.listview_translator);
        List<String> list = new ArrayList<>();
        mConversationArrayAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1, list);

        listview.setAdapter(mConversationArrayAdapter);
        listview.setSelection(mConversationArrayAdapter.getCount() - 1);
        return view;
    }


    //Bluetooth state -> View Change
    public static final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
//            if(blue_index1 == 1){
            switch (msg.what) {
                case 0:
                    if (msg.arg1 == MESSAGE_READ) {
                        readMessage0 = (String) msg.obj;
//                            readMessage0 = data;
                    }
                    break;
                case 1:
                    if(msg.arg1 == MESSAGE_READ) {
                        readMessage1 = (String) msg.obj;
//                            readMessage1 = data;
                        break;
                    }
            }

            if(startTrans) {
                new Thread() {
                    public void run() {
//                    if (readMessage0 != null && readMessage1 != null) {
////                        if(readMessage1 != buffer1 && readMessage0 != buffer0) {
////                            Log.d("=== in net0", readMessage0);
////                            Log.d("=== in net1", readMessage1);
////                            out.println(readMessage0); //data를   stream 형태로 변형하여 전송.  변환내용은 쓰레드에 담겨 있다.
////                            buffer0 = readMessage0;
////                            out.println(readMessage1);
////                            buffer1 = readMessage1;
//////                            Log.d("=== in net0 OK", readMessage0);
//////                            Log.d("=== in net1 OK", readMessage1);
////                        }
////                    }

//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

//                    if (readMessage1 != null && readMessage1 != buffer1) {
//                        Log.d("=== in net1", readMessage1);
//                        out.println(readMessage1); //data를   stream 형태로 변형하여 전송.  변환내용은 쓰레드에 담겨 있다.
//                        buffer1 = readMessage1;
//                        Log.d("=== in net1 OK", readMessage1);
//                    }
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (readMessage1 != null) {
//                                Log.d("=== in net1", readMessage1);
//                                out.println(readMessage1); //data를   stream 형태로 변형하여 전송.  변환내용은 쓰레드에 담겨 있다.
//                                Log.d("=== in net1 OK", readMessage1);
//                            }
//                        }
//                    }, 50);

//                    new Handler().postDelayed(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            if (readMessage1 != null) {
//                                Log.d("=== in net1", readMessage1);
//                                out.println(readMessage1); //data를   stream 형태로 변형하여 전송.  변환내용은 쓰레드에 담겨 있다.
//                                Log.d("=== in net1 OK", readMessage1);
//                            }
//                        }
//                    }, 500);

                        if (readMessage0 != null && readMessage1 != null) {
//                        Log.d("=== in net0", readMessage0);
//                        Log.d("=== in net1", readMessage1);
                            out.println(readMessage0); //data를   stream 형태로 변형하여 전송.  변환내용은 쓰레드에 담겨 있다.
                            out.println(readMessage1); //data를   stream 형태로 변형하여 전송.  변환내용은 쓰레드에 담겨 있다.
                            Log.d("=== in net0", readMessage0);
                            Log.d("=== in net1", readMessage1);
                            System.out.println();
                        }
                    }
                }.start();
            }
            return true;
        }
    });
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int language = tts.setLanguage(Locale.KOREAN);

            if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(getActivity(), "지원하지 않는 언어입니다.", Toast.LENGTH_SHORT).show();
            } else {
                speakOutNow();
            }
        } else {
            Toast.makeText(getActivity(), "TTS 실패!", Toast.LENGTH_SHORT).show();
        }

    }

    private void speakOutNow() {
        float speed = (float)ttsSpeed;
        String text = (String)recv_data;

        //tts.setPitch((float) 0.1); //음량
        tts.setSpeechRate(speed); //재생속도
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

    }
}