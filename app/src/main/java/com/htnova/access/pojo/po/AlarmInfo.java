package com.htnova.access.pojo.po;

import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/** 设备报警基本信息。 */
@Data
public class AlarmInfo {
    private String id;
    private String testId;
    private String runMode;
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
    private Long beginTime; // 报警开始时间。
    private Long endTime; // 报警结束时间。

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date beginTimeD; // 报警开始时间。

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTimeD; // 报警结束时间。

    private int state; // 预警类型。
    private String typeName; // 报警类型。
    private String typeCode; // 报警信息码
    private String message; // 报警信息摘要。
    private String dataFile; // data上传路径

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
