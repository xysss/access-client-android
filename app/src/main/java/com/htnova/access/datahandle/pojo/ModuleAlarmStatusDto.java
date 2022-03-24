package com.htnova.access.datahandle.pojo;

import com.htnova.access.sysconfig.constdef.AlarmDef;
import com.htnova.access.sysconfig.constdef.ModDef;

import lombok.Data;

@Data
public class ModuleAlarmStatusDto {
    // 是否报警
    private Boolean alarm;

    /**
     * 报警状态
     *
     * @see AlarmDef#STATE_ALARM 系列。
     */
    private int state;

    /**
     * 报警模块
     *
     * @see ModDef#TYPE_SMOKE 系列。
     */
    private String alarmModule;

    // 报警内容
    private String alarmContent;

    // 溶度
    private String concentration;
}
