package com.example.bluertooth;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.bluertooth.ReadXingGeMessage.ServerDate;

import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.service.JPushMessageReceiver;


public class MyReceiver extends JPushMessageReceiver {
    private static Handler uhandler;
    public static void  handlerMessage(Handler handler){
        MyReceiver.uhandler=handler;
    }

    /**
     * 第一次安装注册的极光码
     * @param context
     * @param s
     */
    public void onRegister(Context context, String s) {
        super.onRegister(context, s);
        Log.d("jgtscheshi01", "ID码: "+s);
        ServerDate.setRegistrationID(s);

    }

    /**
     * 接收推送的消息
     * @param context
     * @param customMessage
     */
    public void onMessage(Context context, CustomMessage customMessage) {
        super.onMessage(context, customMessage);
        String data = customMessage.message;
        Log.d("jgtscheshi01", "推送收到的信息: " + customMessage.message);
        if (customMessage.message != null) {
            String[] datali = data.split(",");
            ServerDate.setId(datali[0]);
            ServerDate.setOp(datali[1]);
            ServerDate.setU_register(datali[2]);
            uhandler.obtainMessage(Params.MESSAGE_XINGGE, data).sendToTarget();
        }
    }
}
