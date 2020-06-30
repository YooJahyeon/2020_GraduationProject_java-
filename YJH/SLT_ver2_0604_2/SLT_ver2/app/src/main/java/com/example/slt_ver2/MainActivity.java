package com.example.slt_ver2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    static BluetoothService bluetoothService = null;

    private BluetoothAdapter mBluetoothAdapter = null;
    private FragmentManager fragmentManager = getSupportFragmentManager();

        private  ListViewFragment listViewFragment = new ListViewFragment();
    private TranslationFragment translationFragment = new TranslationFragment();
    private ListFragment listFragment = new ListFragment();
    private SettingFragment settingFragment = new SettingFragment();



    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if(bluetoothService == null) {
//            bluetoothService = new BluetoothService(this, handler);
//        }
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
