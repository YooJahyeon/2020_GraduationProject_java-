package com.example.socket_test2;

import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button connect_btn;                // ip 받아오는 버튼
    Button send_btn;                   // 통신 시작 버튼
    Button stop_btn;                   // 통신 정지 버튼

    EditText ip_edit;                    // ip 에디트
    EditText send_edit;                 // 서버에 송신할 데이터 입력부
    TextView receive_text;             // 서버에서 수신 받은 데이터 보여주는 TextView
    TextView status_text;              // 연결 상태 보여주는 TextView

    private Handler mHandler;
    private DataOutputStream dos;
    private DataInputStream dis;

    //  TCP연결 관련
    private Socket socket;
    private BufferedReader socketIn;
    private PrintWriter socketOut;
    private int port = 9999;
    private String ip = "115.85.173.148";
    String data;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        send_btn = (Button) findViewById(R.id.send_btn);
        stop_btn = (Button) findViewById(R.id.stop_btn);


        ip_edit = (EditText) findViewById(R.id.ip_edit);
        send_edit = (EditText)findViewById(R.id.send_edit);
        receive_text = (TextView) findViewById(R.id.receive_text);
        status_text = (TextView) findViewById(R.id.satus_text);

        connect_btn = (Button) findViewById(R.id.connect_btn);
        connect_btn.setOnClickListener(this);


//        send_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String data = send_edit.getText().toString();
//                Log.w("SEND", " " + data);
//                if(data != null) {
//                    socketOut.println(data);
//                }
//            }
//        });

//        Thread worker = new Thread() {
//            public void run() {
//                try {
//
//                    // StrictMode는 개발자가 실수하는 것을 감지하고 해결할 수 있도록 돕는 일종의 개발 툴
//                    // - 메인 스레드에서 디스크 접근, 네트워크 접근 등 비효율적 작업을 하려는 것을 감지하여
//                    //   프로그램이 부드럽게 작동하도록 돕고 빠른 응답을 갖도록 함, 즉  Android Not Responding 방지에 도움
//                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                    StrictMode.setThreadPolicy(policy);
//
//                    //ip = ip_edit.getText().toString();
//                    socket = new Socket(ip, port);
//                    socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    socketOut = new PrintWriter(socket.getOutputStream(), true);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                try {
//                    while(true) {
//                        data = socketIn.readLine();
//                    }
//                } catch (Exception e) {
//
//                }
//            }
//        };
//        worker.start();


    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.connect_btn:
                connect();
            case R.id.send_btn:
                sendMSG();


        }
    }
    void connect() {
        mHandler = new Handler();
        Log.w("connect", "연결하는 중");

        Thread checkUpdate = new Thread() {
            public void run() {
                String newip = String.valueOf(ip_edit.getText());

                try {
                    socket = new Socket(newip, port);
                    Log.w("서버 접속됨", "서버 접속됨");
                } catch (IOException e1) {
                    Log.w("서버 접속 실패", "서버 접속 실패");
                    e1.printStackTrace();
                }

                try{
                    dos = new DataOutputStream(socket.getOutputStream());
                    dis = new DataInputStream(socket.getInputStream());

                    dos.writeUTF("안드로이드에서 서버로 연결요청");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.w("버퍼", "버퍼생성 실패");
                }
                Log.w("버퍼", "버퍼생성 성공");




            }
        };
        checkUpdate.start();
    }

    private void sendMSG() {
        socketOut = new PrintWriter(dos, true);
        String msg =send_edit.getText().toString();

        socketOut.println(msg);
    }


    @Override
    protected  void onStop() {
        super.onStop();

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
}


}

