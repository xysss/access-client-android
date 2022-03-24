package com.htnova.access.pojo.dto;

import java.util.Map;

import com.htnova.access.commons.pojo.AbstractModExt;
import com.htnova.access.sysconfig.constdef.ModDef;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ModSysDto extends AbstractModExt {
    private int mainTemp; // 主控温度
    private int mainHumidity; // 主控湿度
    private int doorState; // 门状态：0-关闭，1-打开（维修）
    private int lvState; // 滤毒罐状态
    private int bioGasState; // 生物采集的状态
    private int bioGasHave; // 生物采集的是否在线

    private byte batteryChargeState;// 电池充电状态，0-未充电，1-充电。
    private byte batteryCapacity;// 电池电量0-100。
    private byte mobileEnableState;// 4G（移动通信）启用状态，0-禁用，1-启用。
    private byte mobileSignalStrength;// 4G（移动通信）信号强度。
    private byte mobileWorkingState;// 4G（移动通信）工作状态。

    private float temp; // 温度：摄氏度。
    private float humidity; // 相对湿度：百分比。
    private byte pumpInstallState; // 泵安装状态：0-未安装，1-安装。
    private byte pumpRunstate; // 泵运行状态：0-停止，1-开启，2-故障。
    private float pumpFlowRate; // 泵流速。

    private Long dataDateTime; // 精确到秒的数据产生时间。

    private int totalMemSize;// 内存总空间。
    private int freeMemSize;// 内存可用空间。

    private Map<String, String> faults;// 故障列表。

    public ModSysDto() {
        super();
        this.modType = ModDef.TYPE_SYS;
    }
}
