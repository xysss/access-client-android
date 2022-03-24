package com.htnova.access.pojo.dto;

import com.htnova.access.commons.pojo.AbstractModExt;
import com.htnova.access.sysconfig.constdef.AlarmDef;
import com.htnova.access.sysconfig.constdef.ModDef;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ModGcameraDto extends AbstractModExt {
    private String radPicId; // 伽马相机辐射图像ID号，往前端只传送一个最近的ID值。
    private Float[] radDoses; // 剂量率列表。
    private float radDoseMax; // 剂量率最大值，报警时显示最大值。

    public ModGcameraDto() {
        super();
        this.modType = ModDef.TYPE_GCAMERA;
        this.alarmType = AlarmDef.CONT_NONE;
    }
}
