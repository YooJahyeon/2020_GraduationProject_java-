package com.example.socket;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button connect_btn;                 // ip 받아오는 버튼
    Button send_btn;                   // 통신 시작 버튼
    Button stop_btn;                    // 통신 정지 버튼

    EditText ip_edit;               // ip 에디트
    EditText send_edit;                 // 서버에 송신할 데이터 입력부
    TextView receive_text;             // 서버에서 수신 받은 데이터 보여주는 TextView
    TextView status_text;              // 연결 상태 보여주는 TextView

    boolean isConnected = false;       // 연결 상황

    // 소켓통신에 필요한것
    private String html = "";
    private Handler mHandler;

    private Socket socket;

    private DataOutputStream dos;
    private DataInputStream dis;

    private int port = 9999;                          // port 번호

    String recv_msg = "";               // 받은 메세지
    String send_msg = "";               // 보낸 메세지


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connect_btn = (Button) findViewById(R.id.connect_btn);
        connect_btn.setOnClickListener(this);

        send_btn = (Button) findViewById(R.id.send_btn);
        send_btn.setOnClickListener(this);

        stop_btn = (Button) findViewById(R.id.stop_btn);


        ip_edit = (EditText) findViewById(R.id.ip_edit);
        send_edit = (EditText)findViewById(R.id.send_edit);
        receive_text = (TextView) findViewById(R.id.receive_text);
        status_text = (TextView) findViewById(R.id.satus_text);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect_btn:     // ip 받아오는 버튼
                connect();
                break;

            case R.id.send_btn:        // 통신 시작
                sendMessage();


            case R.id.stop_btn:         // 통신 정지
                break;
        }
    }
    // 로그인 정보 db에 넣어주고 연결시켜야 함.
    public void connect() {
        mHandler = new Handler();
        Log.w("connect", "연결 하는중");

        Thread checkUpdate = new Thread() {
            public void run() {
                // ip받기
                String newip = String.valueOf(ip_edit.getText());

                // 서버 접속
                try {
                    socket = new Socket(newip, port);
                    Log.w("서버 접속됨", "서버 접속됨");
                    isConnected = true;
                    status_text.setText("서버 접속 성공");

                } catch (IOException e1) {
                    Log.w("서버접속못함", "서버접속못함");
                    isConnected = false;
                    status_text.setText("서버 접속 실패");
                    e1.printStackTrace();
                }


                try {
                    dos = new DataOutputStream(socket.getOutputStream());   // 보내기 위한 통로
                    dis = new DataInputStream(socket.getInputStream());     // 받기 위한 통로
                    dos.writeUTF("안드로이드에서 서버로 연결요청");

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.w("버퍼", "버퍼생성 잘못됨");
                }
                Log.w("버퍼", "버퍼생성 잘됨");


                // 서버에서 받아온 메세지 TextView에 표시

                while (isConnected) {
                    try {
                        recv_msg = dis.readUTF();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    receive_text.setText(recv_msg);
                    Log.w("------서버에서 받아온 값 ", "" + recv_msg);
                }
            }
        };

        // 소켓 접속 시도, 버퍼생성
        checkUpdate.start();
    }

    /// EditText에서 서버로 메세지 보내기
    public void sendMessage() {
        if(dos==null)
            return;

        send_msg = send_edit.getText().toString();

        Thread send = new Thread() {
            public void run() {
                try {

                    dos.writeUTF(send_msg);
                    dos.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        send.start();
    }


}
