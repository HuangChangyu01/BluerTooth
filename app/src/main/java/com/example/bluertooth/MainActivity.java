package com.example.bluertooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.bluertooth.BluetoothClass.ClientBlueTooth;
import com.example.bluertooth.BluetoothClass.ReceiveAndDispatchThread;
import com.example.bluertooth.ReadXingGeMessage.ServerDate;
import com.google.zxing.util.QrCodeGenerator;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.bluertooth.R.id.Lianjie;
import static com.example.bluertooth.R.id.cheshi_tuisong;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView li;
    private ImageView Erweima;
    private Button Lianjie;
    private TextView zhuantai;
    public  ArrayList<String> arrayList5=new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter5;
    private BluetoothAdapter  bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();//获取适配器
    private SharedPreferences sp;
    private BluetoothSocket socket = null;
    private BluetoothDevice device;
    private ReceiveAndDispatchThread hrecceive; //接收蓝牙信息线程
    private TextView cheshi_tuisong;
    private  OkHttpClient httpClient=new OkHttpClient();
    private String xianshi=new String("xx");
    private Bitmap  bb;//机器二维码
    private final Handler uiHandler = new Handler() {
        String xianshi = "xx";
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Params.MESSAGE_READ:
                    String boothmes = msg.obj.toString();
                    arrayList5.add(boothmes);
                    arrayAdapter5.notifyDataSetChanged();

                    Log.d("cheshi01", "新 " + boothmes);
                    if (boothmes.length() > 29) {
                        String[] boothli = boothmes.split(",");
                        if (boothli[3].equals("1") && boothli[4].equals("0") || boothli[5].equals("2")) {//判断门是否打开
                            Log.d("cheshi01", "进入if" + boothmes);
                            abc(ServerDate.getId(), ServerDate.getU_register(), boothmes);
                        }
                        if(boothli[5].equals("2")){
                            MediaPlayer mediaPlayer2;
                            mediaPlayer2 = MediaPlayer.create(MainActivity.this, R.raw.thanks);
                            mediaPlayer2.start();
                            Erweima.setImageBitmap(bb);
                        }
                        if(boothli[5].equals("3")){
                            abc(ServerDate.getId(), ServerDate.getU_register(), boothmes);
                            MediaPlayer mediaPlayer2;
                            mediaPlayer2 = MediaPlayer.create(MainActivity.this, R.raw.thanks);
                            mediaPlayer2.start();
                            Erweima.setImageBitmap(bb);
                        }
                    }

                    break;
                case Params.MESSAGE_XINGGE:
                    String ms = msg.obj.toString();
                    cheshi_tuisong.setText(ms);
                    Erweima.setImageResource(R.mipmap.zhenzai);
                    String[] meli = ms.split(",");
                    if (meli[1].equals("open") && hrecceive != null) {
                        String str = "R";
                        byte[] flush = str.getBytes();
                        hrecceive.write(flush);
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                    MediaPlayer mediaPlayer;
                                    mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.huangying);
                                    mediaPlayer.start();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
//                    ServerDate.setXinGeMessage(null);
                    break;
                case 110:
                    String  er=msg.obj.toString();
                    Bitmap bitmap = QrCodeGenerator.getQrCodeImage(er, Erweima.getWidth(), Erweima.getHeight());
                    if (bitmap == null) {
                        Toast.makeText(MainActivity.this, "生成二维码出错", Toast.LENGTH_SHORT).show();
                        Erweima.setImageBitmap(null);
                    } else {
                        bb=bitmap;
                        Erweima.setImageBitmap(bitmap);
                    }
                    break;

            }
        }
    };



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Lianjie=findViewById(R.id.Lianjie);
        li=findViewById(R.id.li);
        Erweima=findViewById(R.id.Erweima);
        zhuantai=findViewById(R.id.zhuantai);
        cheshi_tuisong=findViewById(R.id.cheshi_tuisong);

        // 生成二维码方法
//        makeRegisterEr();
        MakeRegister makeRegister=new MakeRegister();
        makeRegister.start();
        //动态获取蓝牙权限
        applypermission();
        //打开蓝牙
        openBluetooth();
        Lianjie.setOnClickListener(this);
        arrayAdapter5= new ArrayAdapter<String>
                (MainActivity.this, android.R.layout.simple_list_item_1,arrayList5);
        li.setAdapter(arrayAdapter5);
        MyReceiver.handlerMessage(uiHandler);

    }

    /**
     * 按钮点击
     * @param view
     */
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Lianjie:
                if(Lianjie.getText().equals("连接")){
                    Intent intent=new Intent(this,Main2Activity.class);
                    startActivity(intent);
                }
                else{
                    if (hrecceive != null) {
                        hrecceive.cancel();
                        hrecceive = null;
                        Lianjie.setText("连接");
                    }
                }
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //回到主界面后检查是否已成功连接蓝牙设备
        if (ClientBlueTooth.getmSocket() == null || hrecceive != null ) {
            zhuantai.setText("未连接");
            return;
        }
        else{
            zhuantai.setText("已连接："+ClientBlueTooth.getAddress());
            Lianjie.setText("断开");
            Log.d("cheshi01", "返回主页面是否已经在连接状态");
            hrecceive = new  ReceiveAndDispatchThread(ClientBlueTooth.getmSocket(), uiHandler);
            hrecceive.start();
        }
    }

    /**
     * 动态获取蓝牙权限
     */
    public void applypermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            int permissionCheck = 0;
            permissionCheck = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionCheck += this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                //未获得权限
                this.requestPermissions( // 请求授权
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            }
        }
    }

    /**
     *  权限回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // 如果请求被取消，则结果数组为空。
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"同意",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,"拒绝",Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    /**
     * 打开蓝牙方法
     */
    public void openBluetooth(){
        if(bluetoothAdapter==null){ //代表设备不支持蓝牙、
            Toast.makeText(this,"当前设备不支持蓝牙功能",Toast.LENGTH_SHORT).show();
        }
        if(!bluetoothAdapter.isEnabled()){
            bluetoothAdapter.enable();//打开蓝牙
        }
        //蓝牙可见操作
//        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
//            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//            //可见性的时间由EXTRA_DISCOVERABLE_DURATION设定
//            i.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//            startActivity(i);
//        }
    }

    /**
     * 获取极光注册码，并将注册码生成为二维码
     */
    public void makeRegisterEr(){
          String registrationID=JPushInterface.getRegistrationID(this);// 获取极光推送id码
          Log.d("jgtscheshi02", "获取码: "+registrationID);
          final String rest="XRYH01"+registrationID;
            runOnUiThread(new Runnable() {
            public void run() {
                        Bitmap bitmap = QrCodeGenerator.getQrCodeImage(rest, Erweima.getWidth(), Erweima.getHeight());
                        if (bitmap == null) {
                            Toast.makeText(MainActivity.this, "生成二维码出错", Toast.LENGTH_SHORT).show();
                            Erweima.setImageBitmap(null);
                        } else {
                            bb=bitmap;
                            Erweima.setImageBitmap(bitmap);
                        }
                    }
                });
    }

    class  MakeRegister extends Thread{
        @Override
        public void run() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String registrationID=JPushInterface.getRegistrationID(MainActivity.this);// 获取极光推送id码
            Log.d("jgtscheshi02", "获取码: "+registrationID);
            String rest="XRYH01"+registrationID;
            Message mesg=new Message();
            mesg.what=110;
            mesg.obj=rest;
            uiHandler.sendMessage(mesg);



        }
    }

    /**
     * 进行网络请求
     * @param aa
     * @param bb
     * @param cc
     */
    public  void abc(String aa,String bb,String cc){
        Log.d("cheshi02", "onResponse: "+"进入");
        FormBody formBody=new FormBody.Builder()
                                .addEncoded("u_id", aa)
                                .addEncoded("u_token",bb)
                                .addEncoded("data",cc)
                                .addEncoded("type","shopping")
                                .build();
                        Request request=new Request.Builder().post(formBody)
                                .url("http://47.95.234.172:8080/test/ReturnServlet").build();
                        httpClient.newCall(request).enqueue(new Callback() {
                            public void onFailure(Call call,  IOException e) {
                                Log.d("cheshi02", "onResponse: "+"失败");
                            }

                            public void onResponse(Call call, Response response) throws IOException {
                                String repone=response.body().string();
                                Log.d("cheshi02", "onResponse: "+repone);
                            }
                        });
    }

    class abcde extends  Thread{
        private  String aa;
        private String bb;
        private String cc;
        public  abcde(){}
        public abcde(String aa,String bb,String cc){
            this.aa=aa;
            this.bb=bb;
            this.cc=cc;
        }

        public void run() {
            FormBody formBody=new FormBody.Builder()
                    .addEncoded("u_id", aa)
                    .addEncoded("u_token",bb)
                    .addEncoded("data",cc)
                    .addEncoded("type","shopping")
                    .build();
            Request request = new Request
                    .Builder().post(formBody).url("http://47.95.234.172:8080/test/ReturnServlet")
                    .build();
            try {
                Response response = httpClient.newCall(request).execute();
//                response.body().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


