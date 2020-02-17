package com.example.signlanguagetranslator;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class BluetoothActivity extends AppCompatActivity {

    boolean scanning  = false;

    Button button_Lrefresh;
    Button button_Rrefresh;
    Button button_Next;

    TextView textView_leftState;
    TextView textView_rightState;

    private static final String TAG = "BluetoothActivity: ";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    final int DISCONNECT = 0;
    final int CONNECTING = 1;
    final int CONNECTED = 2;

    Boolean B0_state = false;
    Boolean B1_state = false;

    private BluetoothService bluetoothService = null;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    Handler handler;

    private final Handler bHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        button_Next =(Button)findViewById(R.id.button_next);

        Log.d("test", "BluetoothActivity.java onCreate");

        if(bluetoothService == null) {
            bluetoothService = new BluetoothService(this, bHandler);
        }

        if(bluetoothService.getDeviceState()) {
            bluetoothService.enableBluetooth();
        }



    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                }
                else {
                    Toast.makeText(this, "Bluetooth is not enable", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected  void onStart() {
        super.onStart();
        Log.d("test","BluetoothActivity.java onStart");
        if(!bluetoothAdapter.isEnabled()){
            if (!bluetoothAdapter.isEnabled()){
                Intent enableBtIntent= new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,1000);
            }
        }

    }



}
