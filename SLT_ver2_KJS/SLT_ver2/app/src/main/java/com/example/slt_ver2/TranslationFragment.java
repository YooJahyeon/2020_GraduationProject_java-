package com.example.slt_ver2;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.ListFragment;

import com.github.kimkevin.hangulparser.HangulParser;
import com.github.kimkevin.hangulparser.HangulParserException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TranslationFragment extends ListFragment implements TextToSpeech.OnInitListener {

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 9999;

    static ListViewAdapter adapter;
    private String recv_data = "";
    private String pre_data = "";

    private TextToSpeech tts;

    ListView listview;
    Switch switch_comb, switch_print;
    TextView first_print;
    ImageButton image_mute;

    static String data;
    static String readMessage0, readMessage1;
    ByteBuffer message_buffer;

    long start = 0;
    long end = 0;

    Boolean runTimer = false;
    Boolean check_tts = true;
    Boolean check_mute = false;
    Boolean check_jo = false;
    Boolean check_server_list = false;
    static boolean check_finished = false;

    static boolean startTrans = false;

    List<String> jasoList = new ArrayList<>();
    List<String> jasoList_ver1 = new ArrayList<>();
    List<String> jasoList_ver2 = new ArrayList<>();
    String comb_message = "";
    static String[] server_list_2;


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!startTrans) {
            startActivity(new Intent(getContext(),BluetoothDialog.class));
        }

        message_buffer = ByteBuffer.allocate(1024);



        Thread worker = new Thread() {    //worker 를 Thread 로 생성
            public void run() { //스레드 실행구문
                //소켓에서 데이터를 읽어서 화면에 표시한다.
                try {
                    while (true) {
                        data = MainActivity.in.readLine(); // in으로 받은 데이타를 String 형태로 읽어 data에 저장

                        System.out.println("데이터 :" + data);
                        if(data.equals("finish")){
                            check_finished = true;
                            System.out.println("translation " + check_finished);
                            data = "";
                        }
                        server_list_2 = data.split("\\s");
                        System.out.println("serverList :" + Arrays.toString(server_list_2));
                        if(server_list_2[0].equals("list")){
                            data = "";
                            check_server_list = true;
                            com.example.slt_ver2.ListFragment.server_list = server_list_2;
                            System.out.println("갔나요???" + Arrays.toString(com.example.slt_ver2.ListFragment.server_list));
                        }

                        start = System.currentTimeMillis();
                        Log.d("==start: ", Long.toString(start));
                        runTimer = false;

                        recv_data = data;
                        if (switch_comb.isChecked() && (recv_data.equals("ㄱ") || recv_data.equals("ㄴ") || recv_data.equals("ㄷ") || recv_data.equals("ㄹ") || recv_data.equals("ㅁ")
                                || recv_data.equals("ㅂ") || recv_data.equals("ㅅ") || recv_data.equals("ㅇ") || recv_data.equals("ㅈ") || recv_data.equals("ㅊ") || recv_data.equals("ㅋ")
                                || recv_data.equals("ㅌ") || recv_data.equals("ㅍ") || recv_data.equals("ㅎ") || recv_data.equals("ㅏ") || recv_data.equals("ㅑ") || recv_data.equals("ㅓ")
                                || recv_data.equals("ㅕ") || recv_data.equals("ㅗ") || recv_data.equals("ㅛ") || recv_data.equals("ㅜ") || recv_data.equals("ㅠ") || recv_data.equals("ㅡ")
                                || recv_data.equals("ㅣ") || recv_data.equals("ㅐ") || recv_data.equals("ㅒ") || recv_data.equals("ㅔ") || recv_data.equals("ㅖ") || recv_data.equals("ㅚ")
                                || recv_data.equals("ㅟ") || recv_data.equals("ㅢ"))) {  //데이터 버퍼에 넣기
                            jasoList.add(recv_data);
                            System.out.print(jasoList);
                            System.out.println(jasoList.size());
                        }

                        Log.d("========처음  ", recv_data);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("========1  ", recv_data);
                                if (switch_print.isChecked()) {
                                    first_print.setText(recv_data);  //텍스트뷰에 출력
                                } else if (!switch_print.isChecked() && !recv_data.equals("ㄱ") && !recv_data.equals("ㄴ") && !recv_data.equals("ㄷ") && !recv_data.equals("ㄹ") && !recv_data.equals("ㅁ")
                                        && !recv_data.equals("ㅂ") && !recv_data.equals("ㅅ") && !recv_data.equals("ㅇ") && !recv_data.equals("ㅈ") && !recv_data.equals("ㅊ") && !recv_data.equals("ㅋ")
                                        && !recv_data.equals("ㅌ") && !recv_data.equals("ㅍ") && !recv_data.equals("ㅎ") && !recv_data.equals("ㅏ") && !recv_data.equals("ㅑ") && !recv_data.equals("ㅓ")
                                        && !recv_data.equals("ㅕ") && !recv_data.equals("ㅗ") && !recv_data.equals("ㅛ") && !recv_data.equals("ㅜ") && !recv_data.equals("ㅠ") && !recv_data.equals("ㅡ")
                                        && !recv_data.equals("ㅣ") && !recv_data.equals("ㅐ") && !recv_data.equals("ㅒ") && !recv_data.equals("ㅔ") && !recv_data.equals("ㅖ") && !recv_data.equals("ㅚ")
                                        && !recv_data.equals("ㅟ") && !recv_data.equals("ㅢ")) {
                                    first_print.setText(recv_data);  //텍스트뷰에 출력
                                }

                                first_print.post(new Runnable() {
                                    public void run() {
                                        if (check_tts && !switch_print.isChecked() && !recv_data.equals("ㄱ") && !recv_data.equals("ㄴ") && !recv_data.equals("ㄷ") && !recv_data.equals("ㄹ") && !recv_data.equals("ㅁ")
                                                && !recv_data.equals("ㅂ") && !recv_data.equals("ㅅ") && !recv_data.equals("ㅇ") && !recv_data.equals("ㅈ") && !recv_data.equals("ㅊ") && !recv_data.equals("ㅋ")
                                                && !recv_data.equals("ㅌ") && !recv_data.equals("ㅍ") && !recv_data.equals("ㅎ") && !recv_data.equals("ㅏ") && !recv_data.equals("ㅑ") && !recv_data.equals("ㅓ")
                                                && !recv_data.equals("ㅕ") && !recv_data.equals("ㅗ") && !recv_data.equals("ㅛ") && !recv_data.equals("ㅜ") && !recv_data.equals("ㅠ") && !recv_data.equals("ㅡ")
                                                && !recv_data.equals("ㅣ") && !recv_data.equals("ㅐ") && !recv_data.equals("ㅒ") && !recv_data.equals("ㅔ") && !recv_data.equals("ㅖ") && !recv_data.equals("ㅚ")
                                                && !recv_data.equals("ㅟ") && !recv_data.equals("ㅢ")) {
                                            speakOutNow(recv_data); //tts 출력
                                        } else if (check_tts && switch_print.isChecked()) {
                                            speakOutNow(recv_data);
                                        }
                                        Log.d("========2  ", recv_data);

                                        if (!pre_data.equals("")) {
                                            listview.post(new Runnable() { //리스트뷰에 있는거 출력하기
                                                public void run() {
                                                    Log.d("==check_jo", String.valueOf(check_jo));
                                                    if (switch_print.isChecked()) {
                                                        if (!check_jo && !pre_data.equals("ㄱ") && !pre_data.equals("ㄴ") && !pre_data.equals("ㄷ") && !pre_data.equals("ㄹ") && !pre_data.equals("ㅁ")
                                                                && !pre_data.equals("ㅂ") && !pre_data.equals("ㅅ") && !pre_data.equals("ㅇ") && !pre_data.equals("ㅈ") && !pre_data.equals("ㅊ") && !pre_data.equals("ㅋ")
                                                                && !pre_data.equals("ㅌ") && !pre_data.equals("ㅍ") && !pre_data.equals("ㅎ") && !pre_data.equals("ㅏ") && !pre_data.equals("ㅑ") && !pre_data.equals("ㅓ")
                                                                && !pre_data.equals("ㅕ") && !pre_data.equals("ㅗ") && !pre_data.equals("ㅛ") && !pre_data.equals("ㅜ") && !pre_data.equals("ㅠ") && !pre_data.equals("ㅡ")
                                                                && !pre_data.equals("ㅣ") && !pre_data.equals("ㅐ") && !pre_data.equals("ㅒ") && !pre_data.equals("ㅔ") && !pre_data.equals("ㅖ") && !pre_data.equals("ㅚ")
                                                                && !pre_data.equals("ㅟ") && !pre_data.equals("ㅢ")) {
                                                            Log.d("==check_jo 1", String.valueOf(check_jo));
                                                            adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_su),
                                                                    pre_data, 0);
                                                            adapter.notifyDataSetChanged();
                                                        } else {
                                                            if (!check_jo) {
                                                                Log.d("==check_jo 2", String.valueOf(check_jo));
                                                                adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ji),
                                                                        pre_data, 0);
                                                                adapter.notifyDataSetChanged();
                                                            } else {
                                                                Log.d("==check_jo 3", String.valueOf(check_jo));
                                                                adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_jo),
                                                                        pre_data, 0);
                                                                adapter.notifyDataSetChanged();
                                                                check_jo = false;
                                                            }
                                                        }
                                                    } else if (!check_jo && !switch_print.isChecked() && !pre_data.equals("ㄱ") && !pre_data.equals("ㄴ") && !pre_data.equals("ㄷ") && !pre_data.equals("ㄹ") && !pre_data.equals("ㅁ")
                                                            && !pre_data.equals("ㅂ") && !pre_data.equals("ㅅ") && !pre_data.equals("ㅇ") && !pre_data.equals("ㅈ") && !pre_data.equals("ㅊ") && !pre_data.equals("ㅋ")
                                                            && !pre_data.equals("ㅌ") && !pre_data.equals("ㅍ") && !pre_data.equals("ㅎ") && !pre_data.equals("ㅏ") && !pre_data.equals("ㅑ") && !pre_data.equals("ㅓ")
                                                            && !pre_data.equals("ㅕ") && !pre_data.equals("ㅗ") && !pre_data.equals("ㅛ") && !pre_data.equals("ㅜ") && !pre_data.equals("ㅠ") && !pre_data.equals("ㅡ")
                                                            && !pre_data.equals("ㅣ") && !pre_data.equals("ㅐ") && !pre_data.equals("ㅒ") && !pre_data.equals("ㅔ") && !pre_data.equals("ㅖ") && !pre_data.equals("ㅚ")
                                                            && !pre_data.equals("ㅟ") && !pre_data.equals("ㅢ")) {
                                                        Log.d("==check_jo 4", String.valueOf(check_jo));
                                                        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_su),
                                                                pre_data, 0);
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                    pre_data = recv_data;
                                                }
                                            });
                                        } else {
                                            pre_data = recv_data; //이전의 데이터를 리스트뷰에 출력하기 위해서
                                        }
                                        Log.d("======pre_data: ", pre_data);
                                    }
                                });
                            }
                        });
                    }
                }catch (Exception ignored) {
                }
            }
        };
        worker.start();  //onResume()에서 실행.

        Thread timer = new Thread() {
            public void run() {
                try {
                    while(true) {
                        end = System.currentTimeMillis();

                        if((end - start) > 3000 && !recv_data.equals("") && !runTimer) {
                            Log.d("3초 경과", "YES");
                            runTimer = true;

                            if( !jasoList.isEmpty() && switch_comb.isChecked()) {  //3초 시간이 경과되면 출력
                                try {
                                    Log.d("==", "pass1");
                                    if(jasoList.size() > 2) {
                                        Log.d("==", "pass2");
                                        jasoList_ver1 = ssang(jasoList);
                                        jasoList_ver2 = mo_ssang(jasoList_ver1);
                                    }
                                    else
                                        jasoList_ver2 = jasoList;

                                    comb_message = HangulParser.assemble(jasoList_ver2);
                                    Log.d("========조합 :  ", comb_message);
                                    Log.d("====pre_미친놈: ", pre_data);
                                    //adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ji),
//                                            pre_data, 0);
                                } catch (HangulParserException e) {
//                                    e.printStackTrace();
                                    comb_message = "다시 입력하세요.";
                                    jasoList.clear();
                                    comb_message = "";
                                }
                                if(!comb_message.equals("")) {
                                    listview.post(new Runnable() {
                                        public void run() {
                                            Log.d("====pre: ", pre_data);
                                            if(switch_print.isChecked()) {
                                                adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ji),
                                                        pre_data, 0);
                                                adapter.notifyDataSetChanged();
                                            }
                                            first_print.setText(comb_message);
                                            pre_data = comb_message;
                                            check_jo = true;
                                            Log.d("==check_jo 9", String.valueOf(check_jo));
                                            if(check_tts){
                                                speakOutNow(comb_message);
                                            }
                                            jasoList.clear();
                                            comb_message = "";
                                        }
                                    });
                                }

                                if(check_jo && switch_print.isChecked())
                                {
                                    Log.d("==check_jo 5", String.valueOf(check_jo));
                                    adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_jo),
                                            pre_data, 0);
                                    adapter.notifyDataSetChanged();
                                    check_jo = false;
                                    Log.d("==check_jo 6", String.valueOf(check_jo));
                                }
                                else if(check_jo && !switch_print.isChecked())
                                {
                                    Log.d("==check_jo 7", String.valueOf(check_jo));
                                    adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_jo),
                                            pre_data, 0);
                                    adapter.notifyDataSetChanged();
                                    check_jo = false;
                                    Log.d("==check_jo 8", String.valueOf(check_jo));
                                }
                            }
                            else{
                                jasoList.clear();
                                comb_message = "";
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        };
        timer.start();

        tts = new TextToSpeech(getContext(), this); //첫번째는 Context 두번째는 리스너

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void addItem(Drawable icon, String desc, int pos) {
        adapter.addItem(icon, desc, pos) ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_translation, container, false);

        //리스트뷰
        listview = (ListView)view.findViewById(android.R.id.list);
        switch_comb = (Switch)view.findViewById(R.id.switch_comb);
        switch_print = (Switch)view.findViewById(R.id.switch_print);
        first_print = (TextView)view.findViewById(R.id.first_print);
        image_mute = (ImageButton)view.findViewById(R.id.image_mute);
        image_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!check_mute) {
                    image_mute.setImageResource(R.drawable.ic_mute);
                    check_tts = false;
                    check_mute = true;
                }
                else{
                    image_mute.setImageResource(R.drawable.ic_unmute);
                    check_tts = true;
                    check_mute = false;
                }

            }
        });

        // Adapter 생성 및 Adapter 지정.
        adapter = new ListViewAdapter() ;
        setListAdapter(adapter);
//        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_su),
//                "안녕하세요", 0);
//        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_su),
//                "글씨 크기 예시", 0);

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
                            MainActivity.out.println(readMessage0); //data를   stream 형태로 변형하여 전송.  변환내용은 쓰레드에 담겨 있다.
                            MainActivity.out.println(readMessage1); //data를   stream 형태로 변형하여 전송.  변환내용은 쓰레드에 담겨 있다.
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
//        tts.setPitch(pitch); //음량
        tts.setSpeechRate(SettingFragment.speed); //재생속도
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

            if( i == 0 && c1 < 12623 && c2 < 12623) {
                Log.d("==", "pass4");
                if(element1.equals("ㄱ") && element2.equals("ㄱ")){
                    jasoList.set(0, "ㄲ");
                    jasoList.remove(1);
                }
                else if(element1.equals("ㄷ") && element2.equals("ㄷ")){
                    jasoList.set(0, "ㄸ");
                    jasoList.remove(1);
                }
                else if(element1.equals("ㅂ") && element2.equals("ㅂ")){
                    jasoList.set(0, "ㅃ");
                    jasoList.remove(1);
                }
                else if(element1.equals("ㅅ") && element2.equals("ㅅ")){
                    jasoList.set(0, "ㅆ");
                    jasoList.remove(1);
                }
                else if(element1.equals("ㅈ") && element2.equals("ㅈ")){
                    jasoList.set(0, "ㅉ");
                    jasoList.remove(1);
                }
//                System.out.println("ssang : "+jasoList);
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
                else if(element2.equals("ㄱ")&&element3.equals("ㄱ")) {
                    jasoList.set(i+1, "ㄲ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㅅ")&&element3.equals("ㅅ")) {
                    jasoList.set(i+1, "ㅆ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㄷ")&&element3.equals("ㄷ")) {
                    jasoList.set(i+1, "ㄸ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㅂ")&&element3.equals("ㅂ")) {
                    jasoList.set(i+1, "ㅃ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㅈ")&&element3.equals("ㅈ")) {
                    jasoList.set(i+1, "ㅉ");
                    jasoList.remove(i+2);
                }

            }
            else if(i==jasoList.size() -3 && c2<12623 && c3 < 12623) {
//            else if(i == jasoList.size() - 3 && parseInt(element2,16)<12623 && parseInt(element3,16)<12623){
                if(element2.equals("ㄱ")&&element3.equals("ㅅ")) {
                    jasoList.set(i+1, "ㄳ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㄴ")&&element3.equals("ㅈ")) {
                    jasoList.set(i+1, "ㄵ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㄴ")&&element3.equals("ㅎ")) {
                    jasoList.set(i+1, "ㄶ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㄹ")&&element3.equals("ㄱ")) {
                    jasoList.set(i+1, "ㄺ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㄹ")&&element3.equals("ㅁ")) {
                    jasoList.set(i+1, "ㄻ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㄹ")&&element3.equals("ㅂ")) {
                    jasoList.set(i+1, "ㄼ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㄹ")&&element3.equals("ㅅ")) {
                    jasoList.set(i+1, "ㄽ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㄹ")&&element3.equals("ㅌ")) {
                    jasoList.set(i+1, "ㄾ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㄹ")&&element3.equals("ㅍ")) {
                    jasoList.set(i+1, "ㄿ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㄹ")&&element3.equals("ㅎ")) {
                    jasoList.set(i+1, "ㅀ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㅂ")&&element3.equals("ㅅ")) {
                    jasoList.set(i+1, "ㅄ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㄱ")&&element3.equals("ㄱ")) {
                    jasoList.set(i+1, "ㄲ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㅅ")&&element3.equals("ㅅ")) {
                    jasoList.set(i+1, "ㅆ");
                    jasoList.remove(i+2);
                }
            }
        }
        System.out.println("ver_1 : " + jasoList);
        return jasoList;
    }

    private List<String> mo_ssang(List<String> jasoList) {
        for (int i = 0; i < jasoList.size() - 3; i++) {
            String element1 = jasoList.get(i);
            String element2 = jasoList.get(i + 1);
            String element3 = jasoList.get(i + 2);
            String element4 = jasoList.get(i + 3);

            char c1 = element1.charAt(0);
            char c2 = element2.charAt(0);
            char c3 = element3.charAt(0);
            char c4 = element4.charAt(0);

            if (c1 < 12623 && c2 >= 12623 && c3 >= 12623 && c4 >= 12623) {
                if (element2.equals("ㅗ") && element3.equals("ㅏ") && element4.equals("ㅣ")) {
                    jasoList.set(i+1, "ㅙ");
                    jasoList.remove(i + 2);
                    jasoList.remove(i + 2);
                } else if (element2.equals("ㅜ") && element3.equals("ㅓ") && element4.equals("ㅣ")) {
                    jasoList.set(i+1, "ㅞ");
                    jasoList.remove(i + 2);
                    jasoList.remove(i + 2);
                }
            }
            else if (c1 <12623 && c2 >= 12623 && c3 >= 12623 && c4 < 12623) {
                if(element2.equals("ㅗ")&&element3.equals("ㅏ")) {
                    jasoList.set(i+1, "ㅘ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㅗ")&&element3.equals("ㅐ")) {
                    jasoList.set(i+1, "ㅙ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㅜ")&&element3.equals("ㅓ")) {
                    jasoList.set(i+1, "ㅝ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㅜ")&&element3.equals("ㅔ")) {
                    jasoList.set(i+1, "ㅞ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㅏ")&&element3.equals("ㅣ")) {
                    jasoList.set(i+1, "ㅐ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㅑ")&&element3.equals("ㅣ")) {
                    jasoList.set(i+1, "ㅒ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㅓ")&&element3.equals("ㅣ")) {
                    jasoList.set(i+1, "ㅔ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㅕ")&&element3.equals("ㅣ")) {
                    jasoList.set(i+1, "ㅖ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㅗ")&&element3.equals("ㅣ")) {
                    jasoList.set(i+1, "ㅚ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㅜ")&&element3.equals("ㅣ")) {
                    jasoList.set(i+1, "ㅟ");
                    jasoList.remove(i+2);
                }
                else if(element2.equals("ㅡ")&&element3.equals("ㅣ")) {
                    jasoList.set(i+1, "ㅢ");
                    jasoList.remove(i+2);
                }
            }
            else if (c2 < 12623 && c3 >= 12623 && c4 >= 12623) {
                if(element3.equals("ㅗ")&&element4.equals("ㅏ")) {
                    jasoList.set(i+2, "ㅘ");
                    jasoList.remove(i+3);
                }
                else if(element3.equals("ㅗ")&&element4.equals("ㅐ")) {
                    jasoList.set(i+2, "ㅙ");
                    jasoList.remove(i+3);
                }
                else if(element3.equals("ㅜ")&&element4.equals("ㅓ")) {
                    jasoList.set(i+2, "ㅝ");
                    jasoList.remove(i+3);
                }
                else if(element3.equals("ㅜ")&&element4.equals("ㅔ")) {
                    jasoList.set(i+2, "ㅞ");
                    jasoList.remove(i+3);
                }
                else if(element3.equals("ㅏ")&&element4.equals("ㅣ")) {
                    jasoList.set(i+2, "ㅐ");
                    jasoList.remove(i+3);
                }
                else if(element3.equals("ㅑ")&&element4.equals("ㅣ")) {
                    jasoList.set(i+2, "ㅒ");
                    jasoList.remove(i+3);
                }
                else if(element3.equals("ㅓ")&&element4.equals("ㅣ")) {
                    jasoList.set(i+2, "ㅔ");
                    jasoList.remove(i+3);
                }
                else if(element3.equals("ㅕ")&&element4.equals("ㅣ")) {
                    jasoList.set(i+2, "ㅖ");
                    jasoList.remove(i+3);
                }
                else if(element3.equals("ㅗ")&&element4.equals("ㅣ")) {
                    jasoList.set(i+2, "ㅚ");
                    jasoList.remove(i+3);
                }
                else if(element3.equals("ㅜ")&&element4.equals("ㅣ")) {
                    jasoList.set(i+2, "ㅟ");
                    jasoList.remove(i+3);
                }
                else if(element3.equals("ㅡ")&&element4.equals("ㅣ")) {
                    jasoList.set(i+2, "ㅢ");
                    jasoList.remove(i+3);
                }
            }
        }
        System.out.println("ver_2 : " + jasoList);
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