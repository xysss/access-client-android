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
public class ModTvocDto extends AbstractModExt {
    private boolean modAlarmState;
    private double nh3;
    private double cl2;
    private double co;
    private float pidValue;

    /** PID本底 */
    private double pidBground;
    /** PID本底 */
    private double nh3Bground;
    /** PID本底 */
    private double cl2Bground;
    /** PID本底 */
    private double coBground;

    private List<SensorData> sensors = new ArrayList<>(); // 传感器列表。

    public ModTvocDto() {
        super();
        this.modType = ModDef.TYPE_TVOC;
        this.alarmType = AlarmDef.CONT_AIR;
    }

    public void addSensor(SensorData sensorData) {
        sensors.add(sensorData);
    }
}
