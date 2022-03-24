package com.htnova.access.pojo.po;

import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/** 设备故障基本信息。 */
@Data
public class FaultInfo {
    private String id;
    private String sn;
    private String outterSn;
    private String deviceName;
    private String deviceType;
    private String deviceTypeName;
    private String officeId; // 中控室ID。
    private String officeName; // 中控室名称。
    private String pointId; // 监测点ID。
    private String pointName; // 监测点名称。
    private String taskId; // 任务ID
    private String taskName; // 任务名称
    private Long beginTime; // 故障开始时间。
    private Long endTime; // 故障结束时间。

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date beginTimeD; // 故障开始时间。

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTimeD; // 故障结束时间。

    private String sensorCode; // 传感器编码。
    private String sensorName; // 传感器名称。

    private String faultCode; // 故障编码。
    private String faultName; // 故障名称。
    private String message; // 故障信息摘要：一般与faultName一致，预留保存更多与faultName不同的信息。

    public Long getBeginTime() {
        if (Objects.nonNull(beginTimeD)) {
            return beginTimeD.getTime();
        }
        return this.beginTime;
    }

    public Long getEndTime() {
        if (Objects.nonNull(endTimeD)) {
            return endTimeD.getTime();
        }
        return this.endTime;
    }
}
