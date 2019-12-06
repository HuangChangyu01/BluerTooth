package com.example.bluertooth.BluetoothClass;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.bluertooth.Params;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ReceiveAndDispatchThread extends Thread {
    private final BluetoothSocket hSocker;
    private final InputStream is;
    private final OutputStream os;
    private Handler hHandler;
//    private Message message;
    private String xianshi="xx";
    public ReceiveAndDispatchThread(BluetoothSocket socket, Handler handler){
         this.hSocker=socket;
         this.hHandler=handler;
         InputStream  ris=null;
         OutputStream ros=null;
//         message=Message.obtain();
        try {
            ris=socket.getInputStream();
            ros=socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        is=ris;
        os=ros;
    }
    public void run() {
        int bytes;
        int ch;
        while (true) {
            //无限循环读取数据
            byte[] buffer = new byte[1024];
            try {
                bytes = 0;
                while ((ch = is.read()) != '>') {
                    if (ch != -1) {
                        buffer[bytes] = (byte) ch;
                        bytes++;
                    }
                }
                String str = new String(buffer, 0,bytes);
//                str = str.substring(0, bytes);
                if(xianshi.equals(str)==false){
                    xianshi=str;
                    hHandler.obtainMessage(Params.MESSAGE_READ,xianshi).sendToTarget();
                }

            }catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
    public void write(byte[] bytes) {
        try {
            os.write(bytes);
            os.flush();
            Log.d("cheshi01", "write: "+"成功写出");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        try {
            hSocker.close();
            ClientBlueTooth.setmSocket(null);
        } catch (IOException e) { }
    }

}
