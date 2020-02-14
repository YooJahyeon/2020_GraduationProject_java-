package com.example.socket_test;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button connect_btn;                 // ip 받아오는 버튼

    EditText ip_edit;               // ip 에디트
    TextView show_text;             // 서버에서온거 보여주는 에디트
    // 소켓통신에 필요한것
    private String html = "";
    private Handler mHandler;

    private Socket socket;

    private BufferedReader networkReader;
    private PrintWriter networkWriter;

    private DataOutputStream dos;
    private DataInputStream dis;

    private String ip = "220.149.216.90";            // IP 번호
    private int port = 9999;                          // port 번호

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connect_btn = (Button)findViewById(R.id.connect_btn);
        connect_btn.setOnClickListener(this);

        ip_edit = (EditText)findViewById(R.id.ip_edit);
        show_text = (TextView)findViewById(R.id.show_text);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.connect_btn:     // ip 받아오는 버튼
                connect();
        }
    }

    // 로그인 정보 db에 넣어주고 연결시켜야 함.
    void connect(){
        mHandler = new Handler();
        Log.w("connect","연결 하는중");
        // 받아오는거
        Thread checkUpdate = new Thread() {
            public void run() {
                // ip받기
                String newip = String.valueOf(ip_edit.getText());

                // 서버 접속
                try {
                    socket = new Socket(newip, port);
                    Log.w("서버 접속됨", "서버 접속됨");
                } catch (IOException e1) {
                    Log.w("서버접속못함", "서버접속못함");
                    e1.printStackTrace();
                }

                Log.w("edit 넘어가야 할 값 : ","안드로이드에서 서버로 연결요청");

                // Buffered가 잘못된듯.
                try {
                    dos = new DataOutputStream(socket.getOutputStream());   // output에 보낼꺼 넣음
                    dis = new DataInputStream(socket.getInputStream());     // input에 받을꺼 넣어짐
                    dos.writeUTF("안드로이드에서 서버로 연결요청");

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.w("버퍼", "버퍼생성 잘못됨");
                }
                Log.w("버퍼","버퍼생성 잘됨");

                while(true) {
                    // 서버에서 받아옴
                    try {
                        String line = "";
                        int line2;
                        while (true) {
                            //line = (String) dis.readUTF();
                            line2 = (int) dis.read();
                            //Log.w("서버에서 받아온 값 ", "" + line);
                            //Log.w("서버에서 받아온 값 ", "" + line2);

                            if(line2 > 0) {
                                Log.w("------서버에서 받아온 값 ", "" + line2);
                                dos.writeUTF("하나 받았습니다. : " + line2);
                                dos.flush();
                            }
                            if(line2 == 99) {
                                Log.w("------서버에서 받아온 값 ", "" + line2);
                                socket.close();
                                break;
                            }
                        }
                    } catch (Exception e) {

                    }
                }

            }
        };
        // 소켓 접속 시도, 버퍼생성
        checkUpdate.start();
    }
}