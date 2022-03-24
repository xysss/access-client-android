package com.htnova.accessdroid;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;

import com.htnova.access.socket.CommonSocketService;
import com.htnova.access.socket.Fcbr100SocketClient;

import java.io.File;

/**
 * <pre>
 *     该类用来实现本地程序的初始化流程方面的工作，或者修改完配置需要处理的业务逻辑。
 *     作为后台服务与Android前端的一个门面。
 * </pre>
 */
public class LocalAppProcess {
    public static void init(Activity activity, Handler handler) {
        // 设置日志文件存储路径。
        File file = activity.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        String rootLogPath = file.getPath();
        System.setProperty("logPath", rootLogPath + "/accessapp");

        // 保存初始化的handler。
        SystemSettingStore.setHandler(handler);

        // 初始化动态权限设置。
        MyRxPermission.getInstance(activity).initRxPermission();

        // 初始化Socket的心跳和数据收发。
        CommonSocketService.getInstance().initHeartBeat();
        Fcbr100SocketClient.getInstance().initConn();

        // 注册数据监听。
        EventPublisher.getInstance().addListener(new DataRecvEventListener());
    }

    public static void close() {
        // 释放连接信息。
        CommonSocketService.getInstance().closeAll();
        Fcbr100SocketClient.getInstance().closeAll();
    }
}
