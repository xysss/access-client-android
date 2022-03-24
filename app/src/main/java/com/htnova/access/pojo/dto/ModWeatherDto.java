package com.htnova.access.pojo.dto;

import com.htnova.access.commons.pojo.AbstractModExt;
import com.htnova.access.sysconfig.constdef.AlarmDef;
import com.htnova.access.sysconfig.constdef.ModDef;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ModWeatherDto extends AbstractModExt {
    private float windSpeed;
    private int windDirection;
    private int windScale;
    private String windSpeedValue;
    private String windDirectionValue;

    public ModWeatherDto() {
        super();
        this.modType = ModDef.TYPE_WEATHER;
        this.alarmType = AlarmDef.CONT_NONE;
    }
}
