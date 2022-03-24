package com.htnova.access.pojo.po;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonNaming(value = PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public class DeviceEntryConf {
    private String id;
    private String sn;
    private String originSn;
    private String deviceType;
    private int transferMethod;
    private int sendHeartBeat;
    private int heartBeatInterval;
    private String deviceIpAddr;
    private String devicePortName;
    private int deviceBaudRate;
    private int serviceProvider;
    private String serviceIpAddr;
    private int servicePort;
    private int sendMqtt;
    private int mqttProvider;
    private String createBy;
    private LocalDateTime createDate;
    private String updateBy;
    private LocalDateTime updateDate;
    private String remarks;
    private String delFlag;
}
