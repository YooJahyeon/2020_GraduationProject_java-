package com.example.signlanguagetranslator;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class TranslationFragment extends Fragment {

    private Socket clientSocket;
    private BufferedReader socketIn;
    private PrintWriter socketOut;
    private int port = 9999;
    private final String ip = "223.195.222.138";
    private MyHandler myHandler;
    private MyThread myThread;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            clientSocket = new Socket(ip, port);
            socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            socketOut = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        myHandler = new MyHandler();
        myThread = new MyThread();
        myThread.start();

        //btn = (Button) findViewById(R.id.btn);
        //tv = (TextView) findViewById(R.id.tv);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socketOut.println(123);
            }
        });

        class MyThread extends Thread {
            @Override
            public void run() {
                while (true) {
                    try {
                        // InputStream의 값을 읽어와서 data에 저장
                        String data = socketIn.readLine();
                        // Message 객체를 생성, 핸들러에 정보를 보낼 땐 이 메세지 객체를 이용
                        Message msg = myHandler.obtainMessage();
                        msg.obj = data;
                        myHandler.sendMessage(msg);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        class MyHandler extends Handler {
            @Override
            public void handleMessage(Message msg) {
                //tv.setText(msg.obj.toString());
            }
        }





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_translation, container, false);
    }


}
