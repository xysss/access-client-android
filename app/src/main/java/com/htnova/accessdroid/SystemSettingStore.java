package com.htnova.accessdroid;

import android.os.Handler;

/**
 * 该类用来保存系统设置以及系统间数据交换的基本参数。
 */
public class SystemSettingStore {
    private static String deviceType = "FCBR-100M";
    private static String deviceName = "执勤设备";
    private static String sn = "2004020039";
    private static String ipAddr = "192.168.1.100";
    private static int port = 1908;
    private static Handler handler = null;

    public static void setDeviceType(String deviceType) {
        SystemSettingStore.deviceType = deviceType;
    }

    public static String getDeviceType() {
        return deviceType;
    }

    public static void setSn(String sn) {
        SystemSettingStore.sn = sn;
    }

    public static String getSn() {
        return sn;
    }

    public static void setIpAddr(String ipAddr) {
        SystemSettingStore.ipAddr = ipAddr;
    }

    public static String getIpAddr() {
        return ipAddr;
    }

    public static void setPort(int port) {
        SystemSettingStore.port = port;
    }

    public static int getPort() {
        return port;
    }

    public static void setHandler(Handler handler) {
        SystemSettingStore.handler = handler;
    }

    public static Handler getHandler() {
        return handler;
    }
}
