package com.example.slt_ver2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.slt_ver2.TranslationFragment.MESSAGE_READ;


public class ListFragment extends Fragment  {
    View fragmentView;
    RecyclerView signLanguageListRecyclerView;
    SignLanguageListAdapter signLanguageListAdapter;
    List<SignLanguageList> signLanguageList;
    EditText et_meaning;
    static String readMessage0, readMessage1;


    //signLanguageName, iconImage, delete_btn    ==> itemView

    boolean startList = true;
    String server_list;

    public ListFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if(startList = true) {
//            new Thread() {
//                public void run() {
//                    MainActivity.out.println("load");
//                }
//            }.start();
//            startList = false;
//        }
//
//
//        Thread rev_server_list = new Thread() {    //worker 를 Thread 로 생성
//            public void run() { //스레드 실행구문
//                while (true) {
//                    try {
//                        server_list = MainActivity.in.readLine(); //signLanguageName, iconImage 로
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        rev_server_list.start();



        //Add dummy data in SignLanguage Fragment here
        signLanguageList = new ArrayList<>();
        signLanguageList.add(new SignLanguageList("ㄱ",R.drawable.fingerlanguage_icon));
        signLanguageList.add(new SignLanguageList("ㄴ",R.drawable.fingerlanguage_icon));
        signLanguageList.add(new SignLanguageList("ㄷ",R.drawable.fingerlanguage_icon));
        signLanguageList.add(new SignLanguageList("ㄹ",R.drawable.fingerlanguage_icon));
        signLanguageList.add(new SignLanguageList("ㅁ",R.drawable.fingerlanguage_icon));
        signLanguageList.add(new SignLanguageList("ㅂ",R.drawable.fingerlanguage_icon));
        signLanguageList.add(new SignLanguageList("ㅅ",R.drawable.fingerlanguage_icon));
        signLanguageList.add(new SignLanguageList("ㅇ",R.drawable.fingerlanguage_icon));
        signLanguageList.add(new SignLanguageList("ㅈ",R.drawable.fingerlanguage_icon));
        signLanguageList.add(new SignLanguageList("ㅊ",R.drawable.fingerlanguage_icon));
        signLanguageList.add(new SignLanguageList("ㅋ",R.drawable.fingerlanguage_icon));
        signLanguageList.add(new SignLanguageList("ㅌ",R.drawable.fingerlanguage_icon));
        signLanguageList.add(new SignLanguageList("ㅍ",R.drawable.fingerlanguage_icon));
        signLanguageList.add(new SignLanguageList("ㅎ",R.drawable.fingerlanguage_icon));
    }

    Button btn_Add;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_list, container, false);
        signLanguageListRecyclerView = fragmentView.findViewById(R.id.signlanguage_recycler);
        signLanguageListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        signLanguageListRecyclerView.setHasFixedSize(true);

        signLanguageListAdapter = new SignLanguageListAdapter(getActivity(), signLanguageList);
        signLanguageListRecyclerView.setAdapter(signLanguageListAdapter);


        btn_Add = fragmentView.findViewById(R.id.btn_Add);
        et_meaning =(EditText) fragmentView.findViewById(R.id.et_meaning);


        btn_Add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
//                new Thread(){
//                    public void run() {
//                        MainActivity.out.println("start " + et_meaning);
//                    }
//                }.start();
//
//                new Thread() {
//                    public void run() {
//                        if (readMessage0 != null && readMessage1 != null) {
//                            MainActivity.out.println(readMessage0); //data를   stream 형태로 변형하여 전송.  변환내용은 쓰레드에 담겨 있다.
//                            MainActivity.out.println(readMessage1); //data를   stream 형태로 변형하여 전송.  변환내용은 쓰레드에 담겨 있다.
//                            Log.d("=== in net0", readMessage0);
//                            Log.d("=== in net1", readMessage1);
//                            System.out.println();
//                        }
//                    }
//                }.start();

                new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("수화데이터 수집중")
                        .setContentText("지금부터 수화를 1회 사용해주세요.")
                        .setConfirmText("완료")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                new Thread(){
//                                    public void run() {
//                                        MainActivity.out.println("Done");
//                                    }
//                                }.start();

                                sweetAlertDialog.setTitleText("동작 완료!")
                                        .setContentText("수화 등록이 완료되었습니다.")
                                        .setConfirmText("완료")
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        })
                        .show();
            }
        });

        return fragmentView;

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
                    if (msg.arg1 == MESSAGE_READ) {
                        readMessage1 = (String) msg.obj;
                        break;
                    }
            }
            return true;
        }
    });
}
