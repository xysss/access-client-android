package com.htnova.access.pojo.dto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;

@Data
public class DeviceConfig {
    private String deviceType;// 设备型号。
    private String sn;// 设备号。
    private Byte sceneType;// 0-工作场景，1-公共场景。

    // 软硬件版本。
    private String hwVer;
    private String swVer;
    private String mainHwVer;
    private String mainSwVer;
    private String ticHwVer;
    private String ticSwVer;
    private String cwaHwVer;
    private String cwaSwVer;

    // 设备序列号：可能与sn相同，也可能不同。
    private String deviceSn;

    // 设备IP地址。
    private String ipAddr;
    private String ipMask;
    private String ipGateway;

    // 设备网络服务信息。
    private Byte netServType;
    private String netServName;
    private Integer netServPort;

    // 传感器数量。
    private int sensorCount;

    // 设备时间。
    private Long deviceTime;

    // 模块开关状态：0-待机，127-开机。
    private Byte modCtrlState;

    // 针对GD606设备，用以保存每台设备不同模块的配置信息，模块由0-5的数字组成。
    // 针对Fcbr100m设备，因为传感器比较多，分为两组，一组0-17（TVOC），另一组0-6（CWA）。
    // sensorSeqKey由"sensorType_sensorSeq"组合而成，sensorCodeKey由"sensorType_sensorCode"组合而成。
    private Map<String, SensorConfig> sensorSeqConfigs = new ConcurrentHashMap<>();
    private Map<String, SensorConfig> sensorCodeConfigs = new ConcurrentHashMap<>();

    // 包括0-工作场景和1-公共场景两种情况下的默认参数：hi、lo、stel、twa、mac。
    private Map<String, Map<String, Float>> default1Params = new ConcurrentHashMap<>();
    private Map<String, Map<String, Float>> default2Params = new ConcurrentHashMap<>();

    // 当前场景下的参数：hi、lo、stel、twa、mac。
    private Map<String, Map<String, Float>> currentParams = new ConcurrentHashMap<>();
}
