package com.example.bluertooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bluertooth.AdapterClass.Info;
import com.example.bluertooth.AdapterClass.MyAdapter;
import com.example.bluertooth.BluetoothClass.ClientBlueTooth;
import com.hb.dialog.dialog.LoadingDialog;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {
    private ListView Y_li;
    private ListView W_li;
    private Button fanhui;
    private Button shuaxin;
    private List<Info> y_list=new ArrayList<>();
    private MyAdapter y_hadapter;
    private List<Info> w_list=new ArrayList<>();
    private MyAdapter w_hadapter;
    private  Info info;
    //获取蓝牙对象
    private BluetoothAdapter hBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Y_li=findViewById(R.id.Y_li);
        W_li=findViewById(R.id.W_li);
        fanhui=findViewById(R.id.fanhui);
        shuaxin=findViewById(R.id.shuaxing);


        search();//搜索
        showYetbooth();//将已配对的设备添加到列表中
        //给listView添加适配器
        y_hadapter=new MyAdapter(this,y_list);
        Y_li.setAdapter(y_hadapter);

        w_hadapter=new MyAdapter(this,w_list);
        W_li.setAdapter(w_hadapter);

        // 监听
        Y_li.setOnItemClickListener(new Y_MyOnItemClickListener());
        W_li.setOnItemClickListener(new W_MyOnItemClickListener());
        fanhui.setOnClickListener(this);
        shuaxin.setOnClickListener(this);

    }

    /**
     * 将已配对的设备添加到列表中
     */
    public void showYetbooth(){
        // 将已配对的设备添加到列表中
        Set<BluetoothDevice> pairedDevices = hBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                info=new Info();
                info.setName(device.getName()==null? device.getAddress():device.getName());
                info.setAddress(device.getAddress());
                info.setImg(R.mipmap.shebei);
                y_list.add(info);

            }
        }
    }

    /**
     * 点击刷新与返回按钮
     * @param view
     */
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shuaxing://刷新
                search(); //搜索
                y_list.clear();
                w_list.clear();
                showYetbooth();
                y_hadapter.notifyDataSetChanged();
                break;
            case R.id.fanhui:
                finish();
                break;
        }
    }

    /**
     * 在instart 中注册广播
     */
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();//创建一个查找蓝牙设备的广播意图，也就是搜索到的设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);// 创建一个结束查找蓝牙设备结束的广播意图
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);//创建一个开始查找蓝牙设备结束的广播意图
        //注册一个广播接收者，开启查找蓝牙设备意图后将结果以广播的形式返回
        this.registerReceiver(mReceiver, filter);
    }
    /**
     * 搜索蓝牙方法
     */
    private void search(){
        if (hBluetoothAdapter.isDiscovering()) {
            //判断蓝牙是否正在扫描，如果是调用取消扫描方法；如果不是，则开始扫描
            hBluetoothAdapter.cancelDiscovery();
        }
        hBluetoothAdapter.startDiscovery();
        if(hBluetoothAdapter.startDiscovery()){

        }
    }

    /**
     * 定义广播 接收搜索到的蓝牙
     */
    private  final BroadcastReceiver mReceiver=new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device;
            switch (action){
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    // Toast.makeText(MainActivity.this,"搜索结束",Toast.LENGTH_SHORT) .show();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED://开始
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);//搜索到的蓝牙设备【未配对+已配对
                    //取得未配对设备集合
                    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                        info=new Info();
                        info.setName(device.getName()==null? device.getAddress():device.getName());
                        info.setAddress(device.getAddress());
                        info.setImg(R.mipmap.shebei);
                        w_list.add(info);
                        w_hadapter.notifyDataSetChanged();
                    }
                    break;
            }

        }
    };

    protected void onStop() {
        super.onStop();
        // 结束时销毁广播
        this.unregisterReceiver(mReceiver);
    }


    /**
     * 监听已配对的适配器 进行socket的连接
     */
    BluetoothSocket socket=null;
    BluetoothDevice device;
    public  class  Y_MyOnItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Info a=(Info) parent.getItemAtPosition(position);
            device = hBluetoothAdapter.getRemoteDevice(a.getAddress());
            //加载弹窗
            final LoadingDialog loadingDialog = new LoadingDialog(Main2Activity.this);
            loadingDialog.setMessage("连接中");
            loadingDialog.show();
            //连接线程
            new Thread(){
                public void run() {
                    try {
                        socket = device.createRfcommSocketToServiceRecord(UUID.fromString(Params.BLUETOOTH_UUID));
                        hBluetoothAdapter.cancelDiscovery();
                        socket.connect();
                        Log.d("cheshi01", "连接建立.");
                        ClientBlueTooth.setmSocket(socket);
                        ClientBlueTooth.setAddress(a.getName());
                        runOnUiThread(new Runnable() {
                            public void run() {
                                loadingDialog.dismiss();
                                Main2Activity.this.finish();
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("log", "连接失败");
                        try {
                            socket.close();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    loadingDialog.dismiss();
                                    Toast.makeText(Main2Activity.this,"连接失败",Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException closeException) { }
                    }

                }
            }.start();

        }
    }
    /**
     * 监听未配对的适配器 进行配对
     */
    public  class  W_MyOnItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Info a=(Info) parent.getItemAtPosition(position);
            device =  hBluetoothAdapter.getRemoteDevice(a.getAddress());
            // 如过未配对
            if (device.getBondState() == BluetoothDevice.BOND_NONE){
                try {
                    //此方法用于配对
                    Method createBondMethod = device.getClass().getMethod("createBond");
                    createBondMethod.invoke(device);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
