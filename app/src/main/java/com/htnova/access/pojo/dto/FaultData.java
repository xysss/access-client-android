package com.htnova.access.pojo.dto;

import java.util.Date;

import lombok.Data;

@Data
public class FaultData {
    private String faultCode;// 故障编号。
    private String faultName;// 故障名称。
    private String sensorCode;// 传感器编号。
    private String sensorName;// 传感器名称。
    private Long beginTime = System.currentTimeMillis(); // 起始时间：长整型。
    private Long endTime = System.currentTimeMillis(); // 结束时间：长整型。
    private Date beginTimeD = new Date(); // 起始时间：日期型。
    private Date endTimeD = new Date(); // 结束时间：日期型。
}
