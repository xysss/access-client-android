package com.htnova.access.pojo.dto;

import java.util.ArrayList;
import java.util.List;

import com.htnova.access.commons.pojo.AbstractModExt;
import com.htnova.access.sysconfig.constdef.AlarmDef;
import com.htnova.access.sysconfig.constdef.ModDef;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ModCwaDto extends AbstractModExt {
    private float temp; // 温度
    private float humidity; // 湿度
    private int[] ims; // 离子迁移值，16个整型值对应的数组
    private String imsContent; // 离子迁移值的内容，以逗号隔开
    private float sccell1RData; // 相对电压1
    private float sccell1Data; // 电压1
    private float sccell2RData; // 相对电压2
    private float sccell2Data; // 电压2
    private float sccellData; // 电压1 + 电压2
    private float sccellRData; // 相对电压1 + 相对电压2
    private float sccellBground; // 电压本底【1 + 2的均值】
    private float sccellRBground; // 相对电压本底【1 + 2的均值】
    private int concentration; // 浓度
    private float flow; // 气流
    private String gasName; // 气体名称
    private int htFault; // ht故障报警
    private int htAlarm; // ht报警
    private int htState; // ht状态
    private List<SensorData> sensors = new ArrayList<>(); // 传感器列表。

    public ModCwaDto() {
        super();
        this.modType = ModDef.TYPE_CWA;
        this.alarmType = AlarmDef.CONT_AIR;
    }

    public void addSensor(SensorData sensorData) {
        sensors.add(sensorData);
    }
}
