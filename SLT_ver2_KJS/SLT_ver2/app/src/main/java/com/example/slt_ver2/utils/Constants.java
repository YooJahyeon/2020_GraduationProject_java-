package com.example.slt_ver2.utils;

import java.util.UUID;

public class Constants {


    //MAC, UUID
    public final static String MAC_ADDR_right= "4C:11:AE:C9:68:4A";   // 오른손 테스트 Mac address
    public final static String MAC_ADDR_left = "4C:11:AE:C9:67:4A";
    public final static String MAC_ADDR_testRight = "4C:11:AE:C9:68:4A";    // 배터리 테스트용 오른손
    public static String SERVICE_STRING = "19b10000-e8f2-537e-4f6c-d104768a1214";
    public static UUID UUID_SERVICE = UUID.fromString(SERVICE_STRING);
    public static String CHARACTERISTIC_STRING = "d8756bd2-7efc-4b48-b53c-47ebca8c2300";
    public static UUID UUID_CHAR = UUID.fromString(CHARACTERISTIC_STRING);
    public static UUID UUID_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    // used to identify adding bluetooth names
    public final static int REQUEST_ENABLE_BT= 1;
    // used to request fine location permission
    public final static int REQUEST_FINE_LOCATION= 2;
    // scan period in milliseconds
    public final static int SCAN_PERIOD= 5000;

    public final static int RIGHT = 0;
    public final static int LEFT = 1;

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

}
