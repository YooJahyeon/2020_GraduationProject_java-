package com.example.socket_test2;


import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.mbms.StreamingServiceInfo;
import android.util.Log;
import android.view.View;
import android.os.AsyncTask;
import android.widget.Button;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    TCP_Client tc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT>22){
            requestPermissions(new String[] {"android.permission.READ_EXTERNAL_STORAGE"}, 1);
        }
        Button But = (Button)findViewById(R.id.Button01);
        But.setOnClickListener(new onClick());
    }
    public class onClick implements View.OnClickListener{
        public void onClick(View v){
            tc = new TCP_Client();
            tc.execute(this);
        }
    }

    public static class TCP_Client extends AsyncTask {
        protected static String SERV_IP = "119.195.232.145"; //서버의 ip주소를 작성하면 됩니다.
        protected static int PORT = 9999; //서버의 Port번호를 작성하면 됩니다.

        @Override
        protected Object doInBackground(Object... params) {

            try {
                Log.d("TCP", "server connecting");
                InetAddress serverAddr = InetAddress.getByName(SERV_IP);
                Socket sock = new Socket(serverAddr, PORT);

                try{
                    System.out.println("데이터찾는중");

                    File file = new File(Environment.getExternalStorageDirectory().getPath()+"/내장메모리\\DCIM\\Screenshots/", "Screenshot_20200214-125113.png"); //읽을 파일 경로 적어 주시면 됩니다.

                    DataInputStream dis = new DataInputStream(new FileInputStream(file));
                    DataOutputStream dos = new DataOutputStream(sock.getOutputStream());

                    long fileSize = file.length();
                    byte[] buf = new byte[1024];

                    long totalReadBytes = 0;
                    int readBytes;
                    System.out.println("데이터찾기 끝");

                    while ((readBytes = dis.read(buf)) > 0) { //길이 정해주고 서버로 보냅니다.
                        System.out.println("while");
                        dos.write(buf, 0, readBytes);
                        totalReadBytes += readBytes;
                    }

                    System.out.println("데이터보내기 끝 직전");
                    dos.close();
                    System.out.println("데이터끝");

                } catch(IOException e){
                    Log.d("TCP", "don't send message");
                    e.printStackTrace();
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch(IOException    e){
                e.printStackTrace();
            }
            return null;
        }
    }
}