package com.htnova.access.sysconfig.constdef;

/**
 * <pre>
 *     与设备接入相关的常量：
 * 
 * </pre>
 */
public interface DeviceEntryDef {
    // 数据传输方式（1-串口，2-MQTT，3-TCP，4-UDP）。
    int TRANSPORT_SERIAL = 1;
    int TRANSPORT_MQTT = 2;
    int TRANSPORT_TCP = 3;
    int TRANSPORT_UDP = 4;

    // 心跳发送标志（0-不发送，1-发送）。
    int HEARTBEAT_SEND_NO = 0;
    int HEARTBEAT_SEND_YES = 1;

    // 服务提供方：针对TCP/UDP，1-应用程序提供（云端或本地应用），2-设备提供。
    int SERVICE_PROVIDER_APP = 1;
    int SERVICE_PROVIDER_DEVICE = 2;

    // 转发到MQTT：0-不转发，1-转发。
    int MQTT_SEND_NO = 0;
    int MQTT_SEND_YES = 1;

    // MQTT提供方：1-内部，2-外部。
    int MQTT_PROVIDER_INTERNAL = 1;
    int MQTT_PROVIDER_EXTERNAL = 2;

    // 数据接入及读取常量。
    int READ_BUFSIZE = 2048;
    int READ_EMPTY_CYCLE_INTERVAL = 2000;
    int RECREATE_INTERVAL = 30000;
    int SERVER_PORT_FCBR100M = 19001;
}
