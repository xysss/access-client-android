package com.htnova.android.accessapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 临时测试用后台服务。
 */
public class MyService extends Service {
    public void onCreate(){
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public  IBinder onBind(Intent var1){
        return null;
    }

    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
