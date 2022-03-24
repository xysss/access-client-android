package com.htnova.access.pojo.dto;

import java.util.ArrayList;
import java.util.List;

import com.htnova.access.commons.pojo.AbstractModExt;
import com.htnova.access.sysconfig.constdef.ModDef;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ModGd606Dto extends AbstractModExt {
    private byte temp; // 温度：摄氏度。
    private byte humidity; // 相对湿度：百分比。
    private byte pumpInstallState; // 泵安装状态：0-未安装，1-安装。
    private byte pumpRunstate; // 泵运行状态：0-停止，1-开启，2-故障。
    private int dataType;// 数据类型：1-写入内存，2-跌倒报警，3-实时传感器数据，4-连上网LTEUP。
    private long recrodTime;// 事件发生的时间。
    private boolean eventFlag;// 是否是事件。
    private int eventId;// 事件号。
    private int eventSensorSeq;// 事件对应的传感器序号。
    private int eventDataSize;// 事件对应的数据长度。
    private List<SensorData> sensors = new ArrayList<>(); // 传感器列表。

    public ModGd606Dto() {
        super();
        this.modType = ModDef.TYPE_GD606;
    }
}
