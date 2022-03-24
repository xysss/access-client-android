package com.htnova.access.commons.pojo;

import java.util.Objects;

import com.htnova.access.sysconfig.constdef.AlarmDef;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class AbstractModExt extends AbstractMod {
    @Builder.Default
    protected String alarmType = AlarmDef.CONT_NONE;

    @Builder.Default
    protected String alarmContent = AlarmDef.CONT_NONE;

    protected String alarmConcentrate;

    @Builder.Default
    protected String trend = AlarmDef.CONT_SMOOTH;

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getAlarmContent() {
        return alarmContent;
    }

    public void setAlarmContent(String alarmContent) {
        this.alarmContent = alarmContent;
    }

    public String getAlarmConcentrate() {
        return alarmConcentrate;
    }

    public void setAlarmConcentrate(String alarmConcentrate) {
        this.alarmConcentrate = alarmConcentrate;
    }

    public String getTrend() {
        if (this.state != 0 && Objects.equals(this.trend, AlarmDef.CONT_SMOOTH)) {
            return AlarmDef.CONT_ASCEND;
        }
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }
}
