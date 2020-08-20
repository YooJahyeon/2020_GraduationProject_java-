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

import java.util.ArrayList;
import java.util.Arrays;
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
    static String[] server_list;
    static boolean is_finished = false;

    public ListFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(startList == true) {
            new Thread() {
                public void run() {
                    MainActivity.out.println("load");
                    Log.d("=========load", "보냈는디요");
                }
            }.start();
            startList = false;
        }

        Thread serverList = new Thread() {    //worker 를 Thread 로 생성
            public void run() { //스레드 실행구문
                //소켓에서 데이터를 읽어서 화면에 표시한다.
                try {
                    while (true) {
                        if (server_list != null) {
                            server_list = TranslationFragment.server_list_2;

                            Log.d("===by server_list2", Arrays.toString(server_list));

                            for (int i = 1; i < server_list.length; i += 2) {
//                            System.out.println("data["+i+"] : " + server_list_2[i] + "data["+(i+1)+"] : " + server_list_2[i+1]);

                                if (server_list[i].equals("지화")) {
                                    signLanguageList.add(new SignLanguageList(server_list[i + 1], R.drawable.fingerlanguage_icon));
                                } else {
                                    signLanguageList.add(new SignLanguageList(server_list[i + 1], R.drawable.ic_su));
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

                            break;
                        }
                    }
                }catch (Exception ignored) {
                }
            }
        };
        serverList.start();  //onResume()에서 실행.


        //Add dummy data in SignLanguage Fragment here
        signLanguageList = new ArrayList<>();
//        signLanguageList.add(new SignLanguageList("ㄱ",R.drawable.fingerlanguage_icon));
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

                if(!check_List) {
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

//                        is_finished = TranslationFragment.check_finished;
//
//                        System.out.println("finish의 정체는....." + is_finished);

                        final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                                .setTitleText("수화 인식중");
                        pDialog.setCancelable(false);
                        pDialog.getProgressHelper()
                                .setBarColor(getResources().getColor(R.color.blue_btn_bg_color));
                        pDialog.show();

                        new Thread() {
                            public void run() {
                                while (true) {
                                    if(TranslationFragment.check_finished == true) {
                                        is_finished = TranslationFragment.check_finished;
                                        System.out.println("finish의 정체는....." + is_finished);

                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(is_finished == true){
                                                    System.out.println("성공시켜주세요.....");
                                                    pDialog.setTitleText("Success!")
                                                            .setConfirmText("확인")
                                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                                }
                                            }
                                        });

                                        break;
                                    }
                                }
                            }
                        }.start();

                        if (et_meaning.getText().toString().equals("ㄱ") || et_meaning.getText().toString().equals("ㄴ") || et_meaning.getText().toString().equals("ㄷ")
                                || et_meaning.getText().toString().equals("ㄹ") || et_meaning.getText().toString().equals("ㅁ") || et_meaning.getText().toString().equals("ㅂ")
                                || et_meaning.getText().toString().equals("ㅅ") || et_meaning.getText().toString().equals("ㅇ") || et_meaning.getText().toString().equals("ㅈ")
                                || et_meaning.getText().toString().equals("ㅊ") || et_meaning.getText().toString().equals("ㅋ") || et_meaning.getText().toString().equals("ㅌ")
                                || et_meaning.getText().toString().equals("ㅍ") || et_meaning.getText().toString().equals("ㅎ") || et_meaning.getText().toString().equals("ㅏ")
                                || et_meaning.getText().toString().equals("ㅑ") || et_meaning.getText().toString().equals("ㅓ") || et_meaning.getText().toString().equals("ㅕ")
                                || et_meaning.getText().toString().equals("ㅗ") || et_meaning.getText().toString().equals("ㅛ") || et_meaning.getText().toString().equals("ㅜ")
                                || et_meaning.getText().toString().equals("ㅠ") || et_meaning.getText().toString().equals("ㅡ") || et_meaning.getText().toString().equals("ㅣ")) {
                            signLanguageList.add(new SignLanguageList(et_meaning.getText().toString(), R.drawable.fingerlanguage_icon));
                            et_meaning.setText(null);
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
                    } else {
                        Toast.makeText(getContext(), "수화의 의미를 입력하세요", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getContext(), "이미 존재하는 수화입니다.", Toast.LENGTH_SHORT).show();
                    et_meaning.setText(null);
                }
            }
        });

        return fragmentView;

    }
}