package com.htnova.access.socket;

import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htnova.access.dataparser.utils.ProtocolUtil;

public class DeviceControlService {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private static DeviceControlService instance;

    private DeviceControlService(){

    }

    public static DeviceControlService getInstance(){
        if(instance == null){
            instance = new DeviceControlService();
        }
        return instance;
    }

    public void resetSmokeSensor(String deviceType, String sn) {
        byte[] bytes =
                ProtocolUtil.buildFrame308v2WithCrcAndEncode(Long.parseLong(sn), (byte)0x05, (byte)0x44, new byte[0]);

        if (null == bytes) {
            log.error("重置烟感异常，发送字节为null，[deviceType={}，sn={}]", deviceType, sn);

            return;
        }

        sendToDevice(deviceType, sn, bytes, 0);
    }

    public boolean sendToDevice(String deviceType, String sn, byte[] sendBytes, int qos) {
        CommonSocketService commonSocketService = CommonSocketService.getInstance();
        if (commonSocketService.containsClientSocket(sn)) {
            Socket socket = commonSocketService.getClientSocket(sn);
            if (socket != null) {
                commonSocketService.send(deviceType, sn, socket, sendBytes);
                log.info("发送设备控制指令，deviceType={}，sn={}", deviceType, sn);
            }
        }

        return true;
    }
}
