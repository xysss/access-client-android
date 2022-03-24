package com.htnova.access.pojo.dto;

import com.htnova.access.commons.pojo.AbstractModExt;
import com.htnova.access.sysconfig.constdef.AlarmDef;
import com.htnova.access.sysconfig.constdef.ModDef;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ModNuclearDto extends AbstractModExt {
    private boolean modAlarmState; // 模块报警状态。

    private int nuclearDetectionMode; // 核检测模式 1：αβ模式 2：γ模式 3：两种模式同时。

    private float radDoseBground; // 伽马剂量率本底数据。
    private float radBetaBground; // 贝塔剂量率本底数据。
    private float radDoseThreshold; // 伽马剂量率阈值。
    private float radBetaThreshold; // 贝塔剂量率阈值。
    private float radDose; // 伽马剂量率。
    private float radBeta; // 贝塔剂量率。
    private float radDoseAll; // 累积伽马剂量率。
    private float radBetaAll; // 累积贝塔剂量率。
    private float radDose2Bground; // 伽马剂量率2本底数据。
    private float radBeta2Bground; // 贝塔剂量率2本底数据。
    private float radDose2Threshold; // 伽马剂量率2阈值。
    private float radBeta2Threshold; // 贝塔剂量率2阈值。
    private float radDose2; // 伽马剂量率2。
    private float radBeta2; // 贝塔剂量率2。
    private float radDose2All; // 累积伽马剂量率2。
    private float radBeta2All; // 累积贝塔剂量率2。

    private int liveTime; // 活时间，20ms为单位。
    private int realTime; // 实时间，20ms为单位。

    private float[] powerScale; // 能量刻度，3个浮点数。
    private float[] peakScale; // 峰型刻度，3个浮点数。
    private float[] effectScale; // 效率刻度，6个浮点数。

    private float highVolt; // 高压。

    private int[] spectrumTracks; // 谱道计数，共1024道，占同样存储字节的在一起。
    private byte spectrumResultFlag; // 谱分析结果标记：0-无分析结果，1-有分析结果。
    private byte spectrumResultCount; // 谱分析结果个数。
    private float[] spectrumResults; // 前4个分别是：报警阈值、计数率、剂量率、累积剂量。

    private byte nuclideCount; // 识别的核素个数。
    private int nuclideTypeValue; // 识别的核素分类值：大于0则引起核素报警。
    private String nuclideName; // 核素名称【冗余属性，来自FitRm的nuclideName或nuclideNames的拼接】。
    private String[] nuclideNames; // 核素名称列表。
    private float[] nuclideDoses; // 核素剂量率列表。

    private int neutronCount; // 中子数【冗余属性，来自FitRm的neutronCount】。

    public ModNuclearDto() {
        super();
        this.modType = ModDef.TYPE_NUCLEAR;
        this.alarmType = AlarmDef.CONT_NONE;
    }
}
