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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kimkevin.hangulparser.HangulParser;
import com.github.kimkevin.hangulparser.HangulParserException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Integer.parseInt;

public class TranslationFragment extends Fragment implements TextToSpeech.OnInitListener {

    private static final String TAG = "Translation";
    static BluetoothService bluetoothService = null;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 9999;

    private ArrayAdapter<String> mConversationArrayAdapter;   //리스트뷰 출력을 위한 adapter
    private String recv_data = "";

    private BluetoothAdapter mBluetoothAdapter = null;

    private TextToSpeech tts;

    ListView listview;
    CheckBox check_comb;

    //소켓 코드
    private Socket socket;  //소켓생성
    BufferedReader in;      //서버로부터 온 데이터를 읽는다.
    static PrintWriter out;        //서버에 데이터를 전송한다.
    static String data;
    static String readMessage0, readMessage1;
    static float speed;
    float pitch;
    ByteBuffer message_buffer;

    long start = 0;
    long end = 0;

    Boolean runTimer = false;

    static boolean startTrans = false;

    List<String> jasoList = new ArrayList<>();
    List<String> jasoList_fin = new ArrayList<>();
    String comb_message = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!startTrans) {
            startActivity(new Intent(getContext(),BluetoothDialog.class));
        }

        message_buffer = ByteBuffer.allocate(1024);

        Thread worker = new Thread() {    //worker 를 Thread 로 생성
            public void run() { //스레드 실행구문
                try {
                    //소켓을 생성하고 입출력 스트립을 소켓에 연결한다.
                    socket = new Socket("220.64.200.73", 9999); //소켓생성
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true); //데이터를 전송시 stream 형태로 변환하여 전송한다.
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //데이터 수신시 stream을 받아들인다.

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //소켓에서 데이터를 읽어서 화면에 표시한다.
                try {
                    while (true) {
                        data = in.readLine(); // in으로 받은 데이타를 String 형태로 읽어 data에 저장

                        start = System.currentTimeMillis();
                        Log.d("==start: ", Long.toString(start));
                        runTimer = false;

                        recv_data = data;
                        if(check_comb.isChecked()){  //데이터 버퍼에 넣기
                            jasoList.add(recv_data);
                            System.out.print(jasoList);
                            System.out.println( jasoList.size());
                        }
                        listview.post(new Runnable() {
                            public void run() {
                                mConversationArrayAdapter.add(recv_data);
                                speakOutNow(recv_data);
                                Log.d("========  ", recv_data);
                            }
                        });
                    }
                } catch (Exception e) {
                }
            }
        };
        worker.start();  //onResume()에서 실행.

        Thread timer = new Thread() {
            public void run() {
                try {
                    while(true) {
                        end = System.currentTimeMillis();
//                        System.out.println("END: "  + end);

                        if((end - start) > 3000 && !recv_data.equals("") && !runTimer) {
                            Log.d("3초 경과", "YES");
                            runTimer = true;

                            if( !jasoList.isEmpty() && check_comb.isChecked()) {
                                try {
                                    Log.d("==", "pass1");
                                    if(jasoList.size() > 2) {
                                        Log.d("==", "pass2");
                                        jasoList_fin = ssang(jasoList);
                                    }
                                    else
                                        jasoList_fin = jasoList;

                                    comb_message = HangulParser.assemble(jasoList_fin);
                                    Log.d("========조합 :  ", comb_message);
                                } catch (HangulParserException e) {
//                                    e.printStackTrace();
                                    comb_message = "다시 입력하세요.";
                                    jasoList.clear();
                                    comb_message = "";
                                }

                                if(comb_message != "") {
                                    listview.post(new Runnable() {
                                        public void run() {
                                            mConversationArrayAdapter.add(comb_message);
                                            speakOutNow(comb_message);
//                                        Log.d("========  ", comb_message);
                                            jasoList.clear();
                                            comb_message = "";
                                        }
                                    });
                                }
                            }
                            else{
                                jasoList.clear();
                                comb_message = "";
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }
        };
        timer.start();

        //BluetoothService 클래스 생성
        if(bluetoothService == null) {
            bluetoothService = new BluetoothService(this, handler);
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
        check_comb = (CheckBox)view.findViewById(R.id.check_comb);


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
            switch (msg.what) {
                case 0:
                    if (msg.arg1 == MESSAGE_READ) {
                        readMessage0 = (String) msg.obj;
                    }
                    break;
                case 1:
                    if(msg.arg1 == MESSAGE_READ) {
                        readMessage1 = (String) msg.obj;
                        break;
                    }
            }

            if(startTrans) {
                new Thread() {
                    public void run() {
                        if (readMessage0 != null && readMessage1 != null) {
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
            }
        } else {
            Toast.makeText(getActivity(), "TTS 실패!", Toast.LENGTH_SHORT).show();
        }

    }

    private void speakOutNow(String tts_data) {
        String text = (String)tts_data;
        tts.setPitch(pitch); //음량
        tts.setSpeechRate(speed); //재생속도
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private List<String> ssang(List<String> jasoList){
        for(int i=0; i < jasoList.size()-2; i++){
            String element1 = jasoList.get(i);
            String element2 = jasoList.get(i+1);
            String element3 = jasoList.get(i+2);

            char c1 = element1.charAt(0);
            char c2 = element2.charAt(0);
            char c3 = element3.charAt(0);
            Log.d("==", "pass3");

            //if(i == 0 && parseInt(element1,16)<0x314F && parseInt(element2,16)<0x314F){
            if( i == 0 && c1 < 12623 && c2 < 12623) {
                Log.d("==", "pass4");
                if(element1.equals("ㄱ")){
                    jasoList.set(0, "ㄲ");
                    jasoList.remove(1);
                }
                else if(element1.equals("ㄷ")){
                    jasoList.set(0, "ㄸ");
                    jasoList.remove(1);
                }
                else if(element1.equals("ㅂ")){
                    jasoList.set(0, "ㅃ");
                    jasoList.remove(1);
                }
                else if(element1.equals("ㅅ")){
                    jasoList.set(0, "ㅆ");
                    jasoList.remove(1);
                }
                else if(element1.equals("ㅈ")){
                    jasoList.set(0, "ㅉ");
                    jasoList.remove(1);
                }
                System.out.println("ssang : "+jasoList);
            }
//            else if(parseInt(element1,16)<12623 && parseInt(element2,16)<12623 && parseInt(element3,16)<12623){
            else if(c1 < 12623 && c2 < 12623 && c3 < 12623) {
                if(element1.equals("ㄱ")&&element2.equals("ㅅ")) {
                    jasoList.set(i, "ㄳ");
                    jasoList.remove(i+1);
                }
                else if(element1.equals("ㄴ")&&element2.equals("ㅈ")) {
                    jasoList.set(i, "ㄵ");
                    jasoList.remove(i+1);
                }
                else if(element1.equals("ㄴ")&&element2.equals("ㅎ")) {
                    jasoList.set(i, "ㄶ");
                    jasoList.remove(i+1);
                }
                else if(element1.equals("ㄹ")&&element2.equals("ㄱ")) {
                    jasoList.set(i, "ㄺ");
                    jasoList.remove(i+1);
                }
                else if(element1.equals("ㄹ")&&element2.equals("ㅁ")) {
                    jasoList.set(i, "ㄻ");
                    jasoList.remove(i+1);
                }
                else if(element1.equals("ㄹ")&&element2.equals("ㅂ")) {
                    jasoList.set(i, "ㄼ");
                    jasoList.remove(i+1);
                }
                else if(element1.equals("ㄹ")&&element2.equals("ㅅ")) {
                    jasoList.set(i, "ㄽ");
                    jasoList.remove(i+1);
                }
                else if(element1.equals("ㄹ")&&element2.equals("ㅌ")) {
                    jasoList.set(i, "ㄾ");
                    jasoList.remove(i+1);
                }
                else if(element1.equals("ㄹ")&&element2.equals("ㅍ")) {
                    jasoList.set(i, "ㄿ");
                    jasoList.remove(i+1);
                }
                else if(element1.equals("ㄹ")&&element2.equals("ㅎ")) {
                    jasoList.set(i, "ㅀ");
                    jasoList.remove(i+1);
                }
                else if(element1.equals("ㅂ")&&element2.equals("ㅅ")) {
                    jasoList.set(i, "ㅄ");
                    jasoList.remove(i+1);
                }
                else if(element1.equals("ㄱ")&&element2.equals("ㄱ")) {
                    jasoList.set(i, "ㄲ");
                    jasoList.remove(i+1);
                }
                else if(element1.equals("ㅅ")&&element2.equals("ㅅ")) {
                    jasoList.set(i, "ㅆ");
                    jasoList.remove(i+1);
                }
            }
            else if(i==jasoList.size() -3 && c2<12623 && c3 < 12623) {
//            else if(i == jasoList.size() - 3 && parseInt(element2,16)<12623 && parseInt(element3,16)<12623){
                if(element1.equals("ㄱ")&&element2.equals("ㅅ")) {
                    jasoList.set(i, "ㄳ");
                    jasoList.remove(i+1);
                }
                else if(element2.equals("ㄴ")&&element3.equals("ㅈ")) {
                    jasoList.set(i, "ㄵ");
                    jasoList.remove(i+1);
                }
                else if(element2.equals("ㄴ")&&element3.equals("ㅎ")) {
                    jasoList.set(i, "ㄶ");
                    jasoList.remove(i+1);
                }
                else if(element2.equals("ㄹ")&&element3.equals("ㄱ")) {
                    jasoList.set(i, "ㄺ");
                    jasoList.remove(i+1);
                }
                else if(element2.equals("ㄹ")&&element3.equals("ㅁ")) {
                    jasoList.set(i, "ㄻ");
                    jasoList.remove(i+1);
                }
                else if(element2.equals("ㄹ")&&element3.equals("ㅂ")) {
                    jasoList.set(i, "ㄼ");
                    jasoList.remove(i+1);
                }
                else if(element2.equals("ㄹ")&&element3.equals("ㅅ")) {
                    jasoList.set(i, "ㄽ");
                    jasoList.remove(i+1);
                }
                else if(element2.equals("ㄹ")&&element3.equals("ㅌ")) {
                    jasoList.set(i, "ㄾ");
                    jasoList.remove(i+1);
                }
                else if(element2.equals("ㄹ")&&element3.equals("ㅍ")) {
                    jasoList.set(i, "ㄿ");
                    jasoList.remove(i+1);
                }
                else if(element2.equals("ㄹ")&&element3.equals("ㅎ")) {
                    jasoList.set(i, "ㅀ");
                    jasoList.remove(i+1);
                }
                else if(element2.equals("ㅂ")&&element3.equals("ㅅ")) {
                    jasoList.set(i, "ㅄ");
                    jasoList.remove(i+1);
                }
                else if(element2.equals("ㄱ")&&element3.equals("ㄱ")) {
                    jasoList.set(i, "ㄲ");
                    jasoList.remove(i+1);
                }
                else if(element2.equals("ㅅ")&&element3.equals("ㅅ")) {
                    jasoList.set(i, "ㅆ");
                    jasoList.remove(i+1);
                }
            }
        }
        return jasoList;
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