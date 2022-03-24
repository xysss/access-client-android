package com.htnova.access.pojo.dto;

import java.util.ArrayList;
import java.util.List;

import com.htnova.access.commons.pojo.AbstractModExt;
import com.htnova.access.sysconfig.constdef.ModDef;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ModSmokeDto extends AbstractModExt {
    private int smokeData; // 烟感AD采样值
    private int smokeFire1; // 烟感火灾1
    private int smokeFire2; // 烟感火灾2
    private int smokeAlarm; // 烟雾预警
    private int smokeInspect; // 烟感警告
    private int smokeFault; // 烟感故障
    private double smokeBground; // 烟感本底

    private List<SensorData> sensors = new ArrayList<>(); // 传感器列表。

    public ModSmokeDto() {
        super();
        this.modType = ModDef.TYPE_SMOKE;
    }

    public void addSensor(SensorData sensorData) {
        sensors.add(sensorData);
    }
}
