package com.example.slt_ver2;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import static com.example.slt_ver2.TranslationFragment.handler;

public class MainActivity extends AppCompatActivity {
    static BluetoothService bluetoothService = null;

    private BluetoothAdapter mBluetoothAdapter = null;
    private FragmentManager fragmentManager = getSupportFragmentManager();

//    private  ListViewFragment listViewFragment = new ListViewFragment();
    private TranslationFragment translationFragment = new TranslationFragment();
    private ListFragment listFragment = new ListFragment();
    private SettingFragment settingFragment = new SettingFragment();

    private Socket socket;  //소켓생성
    static BufferedReader in;      //서버로부터 온 데이터를 읽는다.
    static PrintWriter out;        //서버에 데이터를 전송한다.

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread main_socket = new Thread() {    //worker 를 Thread 로 생성
            public void run() { //스레드 실행구문
                try {
                    //소켓을 생성하고 입출력 스트립을 소켓에 연결한다.
                    socket = new Socket("115.85.173.148", 9999); //소켓생성
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true); //데이터를 전송시 stream 형태로 변환하여 전송한다.
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //데이터 수신시 stream을 받아들인다.

                } catch (
                        IOException e) {
                    e.printStackTrace();
                }
            }
        };
        main_socket.start();

        if(bluetoothService == null) {
            bluetoothService = new BluetoothService(this, handler);
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.layout_container, translationFragment).commitAllowingStateLoss();

        bottomNavigationView = findViewById(R.id.bottomnavigationview_main);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());
        bottomNavigationView.getMenu().findItem(R.id.navigation_translator).setChecked(true);

    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (menuItem.getItemId())
            {
                case R.id.navigation_translator:
                    transaction.replace(R.id.layout_container, translationFragment).commitAllowingStateLoss();
                    break;

                case R.id.navigation_list:
                    transaction.replace(R.id.layout_container, listFragment).commitAllowingStateLoss();
                    break;

                case R.id.navigation_setting:
                    transaction.replace(R.id.layout_container, settingFragment).commitAllowingStateLoss();
                    break;
            }

            return true;
        }
    }
}
