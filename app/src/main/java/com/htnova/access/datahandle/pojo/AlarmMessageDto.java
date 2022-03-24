package com.htnova.access.datahandle.pojo;

import lombok.Data;

@Data
public class AlarmMessageDto<T> {
    private int code; // 300-报警事件，301-新增报警事件，302-升级报警，303-结束报警事件，200-阈值修改时间。
    private String name;
    private T eventData;
    private String type; // 报警的设备类型
    private String deviceName;
}
