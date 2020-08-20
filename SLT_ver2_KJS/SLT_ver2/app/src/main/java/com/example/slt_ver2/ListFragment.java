package com.example.slt_ver2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ListFragment extends Fragment  {
    View fragmentView;
    RecyclerView signLanguageListRecyclerView;
    SignLanguageListAdapter signLanguageListAdapter;
    List<SignLanguageList> signLanguageList;
    EditText et_meaning;
    static String readMessage0;
    static String readMessage1;


    //signLanguageName, iconImage, delete_btn    ==> itemView

    static boolean startList = false;
    boolean check_List = false;
    String server_list = "";

    public ListFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        new Thread(){
//            public void run(){
//                try {
//                        sleep(300);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//            }
//        }.start();

        if(startList == true) {
            new Thread() {
                public void run() {
//                    try {
//                        sleep(300);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    MainActivity.out.println("load");
                    Log.d("=========load", "보냈는디요");
                }
            }.start();
            startList = false;
        }

        Thread rev_server_list = new Thread() {    //worker 를 Thread 로 생성
            public void run() { //스레드 실행구문
                while (true) {
                    try {
                        server_list = MainActivity.in.readLine(); //signLanguageName, iconImage를 리스트 형태로 받아옴.
                        Log.d("===by surim", server_list);

                        String[] server_list_2 = server_list.split("\\s");
                        Log.d("===by server_list2", String.valueOf(server_list_2));
                        for(int i=0; i<server_list_2.length; i+=2)
                        {
                            System.out.println("data["+i+"] : " + server_list_2[i] + "data["+(i+1)+"] : " + server_list_2[i+1]);

                            if(server_list_2[i].equals("지화")){
                                signLanguageList.add(new SignLanguageList(server_list_2[i+1],R.drawable.fingerlanguage_icon));
                            }
                            else
                            {
                                signLanguageList.add(new SignLanguageList(server_list_2[i+1],R.drawable.ic_su));
                            }
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                signLanguageListAdapter.notifyDataSetChanged();
                                System.out.println(signLanguageList.get(0).getSignLanguageName());
                                System.out.println(signLanguageList.get(2).getSignLanguageName());
                                System.out.println(signLanguageList.get(4).getSignLanguageName());
                                System.out.println(signLanguageList.get(5).getSignLanguageName());
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        rev_server_list.start();


        //Add dummy data in SignLanguage Fragment here
        signLanguageList = new ArrayList<>();
//        signLanguageList.add(new SignLanguageList("ㄱ",R.drawable.fingerlanguage_icon));
//        signLanguageList.add(new SignLanguageList("ㄴ",R.drawable.fingerlanguage_icon));
//        signLanguageList.add(new SignLanguageList("ㄷ",R.drawable.fingerlanguage_icon));
//        signLanguageList.add(new SignLanguageList("ㄹ",R.drawable.fingerlanguage_icon));
//        signLanguageList.add(new SignLanguageList("ㅁ",R.drawable.fingerlanguage_icon));
//        signLanguageList.add(new SignLanguageList("ㅂ",R.drawable.fingerlanguage_icon));
//        signLanguageList.add(new SignLanguageList("ㅅ",R.drawable.fingerlanguage_icon));
//        signLanguageList.add(new SignLanguageList("ㅇ",R.drawable.fingerlanguage_icon));
//        signLanguageList.add(new SignLanguageList("ㅈ",R.drawable.fingerlanguage_icon));
//        signLanguageList.add(new SignLanguageList("ㅊ",R.drawable.fingerlanguage_icon));
//        signLanguageList.add(new SignLanguageList("ㅋ",R.drawable.fingerlanguage_icon));
//        signLanguageList.add(new SignLanguageList("ㅌ",R.drawable.fingerlanguage_icon));
//        signLanguageList.add(new SignLanguageList("ㅍ",R.drawable.fingerlanguage_icon));
//        signLanguageList.add(new SignLanguageList("ㅎ",R.drawable.fingerlanguage_icon));
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


        btn_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (int i = 0; i < signLanguageList.size(); i++) {
                    if (et_meaning.getText().toString().equals(signLanguageList.get(i).getSignLanguageName())) {

                        check_List = true;
                    }
                    System.out.println(signLanguageList.get(i).getSignLanguageName());

                }

                if(check_List = true) {
                    if (et_meaning.getText().length() != 0) {
                        new Thread() {
                            public void run() {
                                if (et_meaning.getText().toString().equals("ㄱ") || et_meaning.getText().toString().equals("ㄴ") || et_meaning.getText().toString().equals("ㄷ")
                                        || et_meaning.getText().toString().equals("ㄹ") || et_meaning.getText().toString().equals("ㅁ") || et_meaning.getText().toString().equals("ㅂ")
                                        || et_meaning.getText().toString().equals("ㅅ") || et_meaning.getText().toString().equals("ㅇ") || et_meaning.getText().toString().equals("ㅈ")
                                        || et_meaning.getText().toString().equals("ㅊ") || et_meaning.getText().toString().equals("ㅋ") || et_meaning.getText().toString().equals("ㅌ")
                                        || et_meaning.getText().toString().equals("ㅍ") || et_meaning.getText().toString().equals("ㅎ") || et_meaning.getText().toString().equals("ㅏ")
                                        || et_meaning.getText().toString().equals("ㅑ") || et_meaning.getText().toString().equals("ㅓ") || et_meaning.getText().toString().equals("ㅕ")
                                        || et_meaning.getText().toString().equals("ㅗ") || et_meaning.getText().toString().equals("ㅛ") || et_meaning.getText().toString().equals("ㅜ")
                                        || et_meaning.getText().toString().equals("ㅠ") || et_meaning.getText().toString().equals("ㅡ") || et_meaning.getText().toString().equals("ㅣ")) {
                                    MainActivity.out.println("start " + et_meaning.getText().toString() + " 지화");
                                    Log.d("===start", et_meaning.getText().toString() + " 지화");
                                } else {
                                    MainActivity.out.println("start " + et_meaning.getText().toString() + " 수화");
                                    Log.d("===start", et_meaning.getText().toString() + " 수화");
                                }
                            }
                        }.start();


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

                        new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("수화데이터 수집중")
                                .setContentText("지금부터 수화를 1회 사용해주세요.")
                                .setConfirmText("완료")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        new Thread() {
                                            public void run() {
                                                MainActivity.out.println("done");
                                                Log.d("==done", "끝났다고요.");
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        et_meaning.setText(null);
                                                    }
                                                });
                                            }
                                        }.start();

                                        sweetAlertDialog.setTitleText("동작 완료!")
                                                .setContentText("수화 등록이 완료되었습니다.")
                                                .setConfirmText("완료")
                                                .setConfirmClickListener(null)
                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                                        if (et_meaning.getText().toString().equals("ㄱ") || et_meaning.getText().toString().equals("ㄴ") || et_meaning.getText().toString().equals("ㄷ")
                                                || et_meaning.getText().toString().equals("ㄹ") || et_meaning.getText().toString().equals("ㅁ") || et_meaning.getText().toString().equals("ㅂ")
                                                || et_meaning.getText().toString().equals("ㅅ") || et_meaning.getText().toString().equals("ㅇ") || et_meaning.getText().toString().equals("ㅈ")
                                                || et_meaning.getText().toString().equals("ㅊ") || et_meaning.getText().toString().equals("ㅋ") || et_meaning.getText().toString().equals("ㅌ")
                                                || et_meaning.getText().toString().equals("ㅍ") || et_meaning.getText().toString().equals("ㅎ") || et_meaning.getText().toString().equals("ㅏ")
                                                || et_meaning.getText().toString().equals("ㅑ") || et_meaning.getText().toString().equals("ㅓ") || et_meaning.getText().toString().equals("ㅕ")
                                                || et_meaning.getText().toString().equals("ㅗ") || et_meaning.getText().toString().equals("ㅛ") || et_meaning.getText().toString().equals("ㅜ")
                                                || et_meaning.getText().toString().equals("ㅠ") || et_meaning.getText().toString().equals("ㅡ") || et_meaning.getText().toString().equals("ㅣ")) {
                                            signLanguageList.add(new SignLanguageList(et_meaning.getText().toString(), R.drawable.fingerlanguage_icon));
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    signLanguageListAdapter.notifyDataSetChanged();
                                                }
                                            });
                                        } else {
                                            signLanguageList.add(new SignLanguageList(et_meaning.getText().toString(), R.drawable.ic_su));
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    signLanguageListAdapter.notifyDataSetChanged();
                                                }
                                            });
                                        }
                                    }
                                })
                                .show();
                    } else {
                        Toast.makeText(getContext(), "수화의 의미를 입력하세요", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getContext(), "이미 존재하는 수화입니다.", Toast.LENGTH_SHORT).show();
                }


//                if (et_meaning.getText().length() != 0) {
//                    new Thread() {
//                        public void run() {
//                            if (et_meaning.getText().toString().equals("ㄱ") || et_meaning.getText().toString().equals("ㄴ") || et_meaning.getText().toString().equals("ㄷ")
//                                    || et_meaning.getText().toString().equals("ㄹ") || et_meaning.getText().toString().equals("ㅁ") || et_meaning.getText().toString().equals("ㅂ")
//                                    || et_meaning.getText().toString().equals("ㅅ") || et_meaning.getText().toString().equals("ㅇ") || et_meaning.getText().toString().equals("ㅈ")
//                                    || et_meaning.getText().toString().equals("ㅊ") || et_meaning.getText().toString().equals("ㅋ") || et_meaning.getText().toString().equals("ㅌ")
//                                    || et_meaning.getText().toString().equals("ㅍ") || et_meaning.getText().toString().equals("ㅎ") || et_meaning.getText().toString().equals("ㅏ")
//                                    || et_meaning.getText().toString().equals("ㅑ") || et_meaning.getText().toString().equals("ㅓ") || et_meaning.getText().toString().equals("ㅕ")
//                                    || et_meaning.getText().toString().equals("ㅗ") || et_meaning.getText().toString().equals("ㅛ") || et_meaning.getText().toString().equals("ㅜ")
//                                    || et_meaning.getText().toString().equals("ㅠ") || et_meaning.getText().toString().equals("ㅡ") || et_meaning.getText().toString().equals("ㅣ")) {
//                                MainActivity.out.println("start " + et_meaning.getText().toString() + " 지화");
//                                Log.d("===start", et_meaning.getText().toString() + " 지화");
//                            } else {
//                                MainActivity.out.println("start " + et_meaning.getText().toString() + " 수화");
//                                Log.d("===start", et_meaning.getText().toString() + " 수화");
//                            }
//                        }
//                    }.start();
//
//
//                    new Thread() {
//                        public void run() {
//                            if (readMessage0 != null && readMessage1 != null) {
//                                MainActivity.out.println(readMessage0); //data를   stream 형태로 변형하여 전송.  변환내용은 쓰레드에 담겨 있다.
//                                MainActivity.out.println(readMessage1); //data를   stream 형태로 변형하여 전송.  변환내용은 쓰레드에 담겨 있다.
//                                Log.d("=== in net0", readMessage0);
//                                Log.d("=== in net1", readMessage1);
//                                System.out.println();
//                            }
//                        }
//                    }.start();
//
//                    new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
//                            .setTitleText("수화데이터 수집중")
//                            .setContentText("지금부터 수화를 1회 사용해주세요.")
//                            .setConfirmText("완료")
//                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                @Override
//                                public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                    new Thread() {
//                                        public void run() {
//                                            MainActivity.out.println("done");
//                                            Log.d("==done", "끝났다고요.");
//                                            getActivity().runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    et_meaning.setText(null);
//                                                }
//                                            });
//                                        }
//                                    }.start();
//
//                                    sweetAlertDialog.setTitleText("동작 완료!")
//                                            .setContentText("수화 등록이 완료되었습니다.")
//                                            .setConfirmText("완료")
//                                            .setConfirmClickListener(null)
//                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
//
//                                    if (et_meaning.getText().toString().equals("ㄱ") || et_meaning.getText().toString().equals("ㄴ") || et_meaning.getText().toString().equals("ㄷ")
//                                            || et_meaning.getText().toString().equals("ㄹ") || et_meaning.getText().toString().equals("ㅁ") || et_meaning.getText().toString().equals("ㅂ")
//                                            || et_meaning.getText().toString().equals("ㅅ") || et_meaning.getText().toString().equals("ㅇ") || et_meaning.getText().toString().equals("ㅈ")
//                                            || et_meaning.getText().toString().equals("ㅊ") || et_meaning.getText().toString().equals("ㅋ") || et_meaning.getText().toString().equals("ㅌ")
//                                            || et_meaning.getText().toString().equals("ㅍ") || et_meaning.getText().toString().equals("ㅎ") || et_meaning.getText().toString().equals("ㅏ")
//                                            || et_meaning.getText().toString().equals("ㅑ") || et_meaning.getText().toString().equals("ㅓ") || et_meaning.getText().toString().equals("ㅕ")
//                                            || et_meaning.getText().toString().equals("ㅗ") || et_meaning.getText().toString().equals("ㅛ") || et_meaning.getText().toString().equals("ㅜ")
//                                            || et_meaning.getText().toString().equals("ㅠ") || et_meaning.getText().toString().equals("ㅡ") || et_meaning.getText().toString().equals("ㅣ")) {
//                                        signLanguageList.add(new SignLanguageList(et_meaning.getText().toString(), R.drawable.fingerlanguage_icon));
//                                        getActivity().runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                signLanguageListAdapter.notifyDataSetChanged();
//                                            }
//                                        });
//                                    } else {
//                                        signLanguageList.add(new SignLanguageList(et_meaning.getText().toString(), R.drawable.ic_su));
//                                        getActivity().runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                signLanguageListAdapter.notifyDataSetChanged();
//                                            }
//                                        });
//                                    }
//                                }
//                            })
//                            .show();
//                }
//                else
//                {
//                    Toast.makeText(getContext(), "수화의 의미를 입력하세요",Toast.LENGTH_SHORT).show();
//                }
            }
        });

        return fragmentView;

    }

//    public static final Handler handler2 = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    if (msg.arg1 == MESSAGE_READ) {
//                        readMessage0 = (String) msg.obj;
//                    }
//                    break;
//                case 1:
//                    if(msg.arg1 == MESSAGE_READ) {
//                        readMessage1 = (String) msg.obj;
//                        break;
//                    }
//            }

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

//            return true;
//        }
//    });


    //Bluetooth state -> View Change
//    public static final Handler handler2 = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    if (msg.arg1 == MESSAGE_READ) {
//                        readMessage0 = (String) msg.obj;
//                    }
//                    break;
//                case 1:
//                    if (msg.arg1 == MESSAGE_READ) {
//                        readMessage1 = (String) msg.obj;
//                        break;
//                    }
//            }
//            return true;
//        }
//    });
}
