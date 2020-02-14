package com.example.socket_test2;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {    //메인 activity 시작!

    private Socket socket;  //소켓생성
    BufferedReader in;      //서버로부터 온 데이터를 읽는다.
    PrintWriter out;        //서버에 데이터를 전송한다.
    EditText input;         //화면구성
    Button button;          //화면구성
    TextView output;        //화면구성
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {   //앱 시작시 초기화설정
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//start
        input = (EditText) findViewById(R.id.input); // 글자입력칸을 찾는다.
        button = (Button) findViewById(R.id.button); // 버튼을 찾는다.
        output = (TextView) findViewById(R.id.output); // 글자출력칸을 찾는다.
// 버튼을 누르는 이벤트 발생, 이벤트 제어문이기 때문에 이벤트 발생 때마다 발동된다. 시스템이 처리하는 부분이 무한루프문에
//있더라도 이벤트가 발생하면 자동으로 실행된다.
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
//버튼이 클릭되면 소켓에 데이터를 출력한다.
                String data = input.getText().toString(); //글자입력칸에 있는 글자를 String 형태로 받아서 data에 저장
                Log.w("NETWORK", " " + data);
                if (data != null) {
                    out.println(data); //data를   stream 형태로 변형하여 전송.  변환내용은 쓰레드에 담겨 있다.
                }
            }
        });

        Thread worker = new Thread() {    //worker 를 Thread 로 생성
            public void run() { //스레드 실행구문
                try {
//소켓을 생성하고 입출력 스트립을 소켓에 연결한다.
                    socket = new Socket("119.195.232.145", 9998); //소켓생성
                    out = new PrintWriter(socket.getOutputStream(), true); //데이터를 전송시 stream 형태로 변환하여                                                                                                                       //전송한다.
                    in = new BufferedReader(new InputStreamReader(
                            socket.getInputStream())); //데이터 수신시 stream을 받아들인다.

                } catch (IOException e) {
                    e.printStackTrace();
                }

//소켓에서 데이터를 읽어서 화면에 표시한다.
                try {
                    while (true) {
                        data = in.readLine(); // in으로 받은 데이타를 String 형태로 읽어 data 에 저장
                        output.post(new Runnable() {
                            public void run() {
                                output.setText(data); //글자출력칸에 서버가 보낸 메시지를 받는다.
                            }
                        });
                    }
                } catch (Exception e) {
                }
            }
        };
        worker.start();  //onResume()에서 실행.
    }

    @Override
    protected void onStop() {  //앱 종료시
        super.onStop();
        try {
            if(socket != null) socket.close(); //소켓을 닫는다.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}