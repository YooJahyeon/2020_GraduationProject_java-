package com.ederdoski.appexample;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ederdoski.appexample.interfaces.BleCallback;
import com.ederdoski.appexample.models.BluetoothLE;
import com.ederdoski.appexample.utils.BluetoothLEHelper;
import com.ederdoski.appexample.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import static android.support.constraint.Constraints.TAG;
import static com.ederdoski.appexample.utils.BluetoothLEHelper.device_left;
import static com.ederdoski.appexample.utils.BluetoothLEHelper.device_right;
import static com.ederdoski.appexample.utils.Constants.LEFT;
import static com.ederdoski.appexample.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE;
import static com.ederdoski.appexample.utils.Constants.RIGHT;
import static com.ederdoski.appexample.utils.Constants.SCAN_PERIOD;

public class MainActivity extends AppCompatActivity {

    private boolean isPermissionAllowed = false;

    static BluetoothLEHelper ble;

//    ListView listBle;
    Button btnRight;
    Button btnLeft;
    static TextView txtRight;
    static TextView txtLeft;

    private static String recv_RightData;
    private static String recv_LeftData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //--- Initialize BLE Helper
        ble = new BluetoothLEHelper(this);

        btnRight = findViewById(R.id.connectRight);
        btnLeft = findViewById(R.id.connectLeft);
        txtRight = findViewById(R.id.RightValue);
        txtLeft = findViewById(R.id.LeftValue);

        txtRight.setText("오른쪽");
        txtLeft.setText("왼쪽");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //위치 권한 없으면 -1 반환
            int permission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

            if (permission == PackageManager.PERMISSION_DENIED) {
                String[] permissions = new String[1];
                permissions[0] = Manifest.permission.ACCESS_COARSE_LOCATION;  // 요청할 권한
                requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
            // 권한이 있을 때
            else {
                isPermissionAllowed = true;
            }
        }

        listenerButtons();

        //--- Delete this line to do a search of all the devices
        ble.setFilterService(Constants.SERVICE_STRING);
    }

    private void listenerButtons(){

        btnRight.setOnClickListener(v -> {
            if(ble.isConnected_right()) {
                ble.disconnect(RIGHT);
            }
            if(ble.isReadyForScan()) {
                startConnect_right();
            }
            else {
                Log.d(TAG,"오른손 연결 오류");
            }
        });

        btnLeft.setOnClickListener(v -> {
            if(ble.isConnected_left()) {
                ble.disconnect(LEFT);
            }
            if(ble.isReadyForScan()) {
                startConnect_left();
            }
            else {
                Log.d(TAG,"왼손 연결 오류");
            }
        });


    }


//private void startScan(View v, int index) {
//        ble.scanHandler = new Handler();
//        ble.startScan(index);
//        ble.scanHandler.postDelayed(()-> {
//            ble.stopScan(index);
//        },SCAN_PERIOD);
//}

private void startConnect_right() {
        if(!ble.isScanning_right()) {
            ble.scanLeDevice(true, RIGHT);
        }
}
private void startConnect_left() {
        if(!ble.isScanning_left()) {
            ble.scanLeDevice(true, LEFT);
        }
}



    public static BleCallback bleCallbacks(){

        return new BleCallback(){

            @Override
            public void onBleConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onBleConnectionStateChange(gatt, status, newState);

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    if(gatt.getDevice().equals(device_right)) {
                        //오른쪽 버튼 연결로 변경
                    }
                    else {
                        //왼쪾 버튼 연결로 변경
                    }

                }
                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    if(gatt.getDevice().equals(device_right)) {
                        //오른쪽 버튼 연결 해제로 변경
                    }
                    else {
                        //왼쪾 버튼 연결 해제로 변경
                    }
                }
            }

            @Override
            public void onBleServiceDiscovered(BluetoothGatt gatt, int status) {
                super.onBleServiceDiscovered(gatt, status);
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    Log.e("Ble ServiceDiscovered","onServicesDiscovered received: " + status);
                }
            }

            @Override
            public void onBleCharacteristicChange(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onBleCharacteristicChange(gatt, characteristic);
                Log.i("BluetoothLEHelper","onCharacteristicChanged Value: " + Arrays.toString(characteristic.getValue()));
                Log.i(TAG, "Main의 onBleCharacteristicChange");

            }

            @Override
            public void onBleRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onBleRead(gatt, characteristic, status);

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.i(TAG, "Main의 onBleRead");
                    Log.i("TAG", Arrays.toString(characteristic.getValue()));
                    byte[] readByte = characteristic.getValue();

                    if(gatt.getDevice() == device_right) {
                        recv_RightData = new String(readByte);
                        txtRight.setText(recv_RightData);
                        Log.i("TAG", "오른손 데이터: " + recv_RightData);
                    }

                    else if(gatt.getDevice() == device_left) {
                        recv_LeftData = new String(readByte);
                        txtLeft.setText(recv_LeftData);
                        Log.i("TAG", "왼손 데이터: " + recv_LeftData);
                    }


//                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "onBleRead : " + recv_RightData, Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onBleWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onBleWrite(gatt, characteristic, status);
            }
        };
    }


    // 사용자에게 권한을 요청했을 때 결과가 들어오는 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // BLE 스캐닝 넣을까 말까..
            }
        }
        // 권한 획득 실패했을 때
        else {
            Toast.makeText(this, R.string.error_permission_denied, Toast.LENGTH_SHORT);
            requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ble.disconnect(RIGHT);
        ble.disconnect(LEFT);
    }
}
