package com.example.bluertooth.BluetoothClass;

import android.bluetooth.BluetoothSocket;

public class ClientBlueTooth {

    private static  BluetoothSocket mSocket = null;
    private static String address;

    public static void setmSocket(BluetoothSocket mSocket) {
        ClientBlueTooth.mSocket = mSocket;
    }

    public static BluetoothSocket getmSocket() {
        if(mSocket!=null){
            return mSocket;
        }
        return null;
    }

    public static String getAddress() {
        return address;
    }

    public static void setAddress(String address) {
        ClientBlueTooth.address = address;
    }
}
