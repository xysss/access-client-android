package com.htnova.accessdroid;

import android.Manifest;
import android.app.Activity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * <pre>
 *     动态权限设置。
 * </pre>
 */
public class MyRxPermission {
    private static MyRxPermission instance;
    private Activity activity;

    private MyRxPermission(){

    }

    public static MyRxPermission getInstance(Activity activity){
        if(instance == null){
            instance = new MyRxPermission();
            instance.activity = activity;
        }
        return instance;
    }

    public void initRxPermission(){
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }
}
