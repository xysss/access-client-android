package com.htnova.access.pojo.dto;

import com.htnova.access.commons.pojo.AbstractModExt;
import com.htnova.access.sysconfig.constdef.ModDef;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ModEcDto extends AbstractModExt {
    // 单独一类，分富氧和缺氧，单位vol%。
    private SensorData o2; // 氧气

    // 这两个一类，属于TIC，单位PPM。
    private SensorData no2; // 二氧化氮
    private SensorData h2s; // 硫化氢

    // 这两个一类，属于CWA，单位PPM。
    private SensorData hcn; // 氰化氢
    private SensorData cocl2; // 光气

    public ModEcDto() {
        super();
        this.modType = ModDef.TYPE_EC;
    }
}
