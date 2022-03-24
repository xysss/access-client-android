package com.htnova.android.accessapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.htnova.access.pojo.dto.DeviceDataDto;
import com.htnova.accessdroid.LocalAppProcess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainActivity extends AppCompatActivity {
    private Logger log = null;
    private MainViewHandler mainViewHandler = null;
    private Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 默认界面初始化。
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图更新对象。
        mainViewHandler = new MainViewHandler(this);

        // 初始用于与后台进行数据交互的Handler。
        handler = new Handler(getMainLooper()){
            public void handleMessage(Message msg){
                try{
                    super.handleMessage(msg);

                    // 这里处理与界面的交互，将数据取出来，更新到界面。
                    // deviceDataDto提供了所有的数据。
                    if(msg.obj != null || msg.obj instanceof  DeviceDataDto){
                        DeviceDataDto deviceDataDto = (DeviceDataDto) msg.obj;
                        mainViewHandler.updateView(deviceDataDto);
                    }
                }catch (Exception e){
                    if(log != null){
                        log.error("接收到设备数据，处理消息循环时异常", e);
                    }
                }
            }
        };

        // 整个系统的初始化工作：如设置日志记录路径，设置日志权限，初始化Socket连接，注册监听器。
        // 没有采用服务的方式，采用子线程的方式与设备进行数据交互，用Handler与视图进行更新。
        LocalAppProcess.init(this, handler);

        // 初始化之后再使用log，否则由于路径等没有，log保存会有问题。
        log = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        LocalAppProcess.close();
    }
}