package com.example.slt_ver2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.slt_ver2.interfaces.BleCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

import butterknife.ButterKnife;

import static com.example.slt_ver2.BluetoothService.device_left;
import static com.example.slt_ver2.BluetoothService.device_right;
import static com.example.slt_ver2.TranslationFragment.handler;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    static BluetoothService bs;
    private BluetoothAdapter mBluetoothAdapter = null;

    private FragmentManager fragmentManager;
    private Fragment fa, fb, fc;

    private Socket socket;  //소켓생성
    static BufferedReader in;      //서버로부터 온 데이터를 읽는다.
    static PrintWriter out;        //서버에 데이터를 전송한다.
    static float speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if(bs == null) {
            bs = new BluetoothService(this, handler);
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        fragmentManager = getSupportFragmentManager();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomnavigationview_main);
        bottomNavigationView.setSelectedItemId(R.id.navigation_translator);

        fa = new TranslationFragment();
        fragmentManager.beginTransaction().replace(R.id.layout_container,fa).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@androidx.annotation.NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_translator:
//                        getFragmentManager().beginTransaction().replace(R.id.layout_container, new TranslationFragment()).commit();

                        if(fa == null) {
                            fa = new TranslationFragment();
                            fragmentManager.beginTransaction().add(R.id.layout_container, fa).commit();
                        }

                        if(fa != null) fragmentManager.beginTransaction().show(fa).commit();
                        if(fb != null) fragmentManager.beginTransaction().hide(fb).commit();
                        if(fc != null) fragmentManager.beginTransaction().hide(fc).commit();
                        ListFragment.startList = false;

                        return true;
                    case R.id.navigation_list:
//                        getFragmentManager().beginTransaction().replace(R.id.layout_container, new ListFragment()).commit();

                        if(fb == null) {
                            fb = new ListFragment();
                            fragmentManager.beginTransaction().add(R.id.layout_container, fb).commit();
                        }

                        if(fa != null) fragmentManager.beginTransaction().hide(fa).commit();
                        if(fb != null) fragmentManager.beginTransaction().show(fb).commit();
                        if(fc != null) fragmentManager.beginTransaction().hide(fc).commit();
                        ListFragment.startList = true;

                        return true;
                    case R.id.navigation_setting:
//                        getFragmentManager().beginTransaction().replace(R.id.layout_container, new SettingFragment()).commit();

                        if(fc == null) {
                            fc = new SettingFragment();
                            fragmentManager.beginTransaction().add(R.id.layout_container, fc).commit();
                        }

                        if(fa != null) fragmentManager.beginTransaction().hide(fa).commit();
                        if(fb != null) fragmentManager.beginTransaction().hide(fb).commit();
                        if(fc != null) fragmentManager.beginTransaction().show(fc).commit();
                        ListFragment.startList = false;

                        return true;
                }
                return false;
            }
        });

        Thread main_socket = new Thread() {
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

    }

}
