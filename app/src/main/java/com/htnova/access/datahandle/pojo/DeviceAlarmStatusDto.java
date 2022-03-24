package com.htnova.access.datahandle.pojo;

import java.util.HashMap;
import java.util.Map;

import com.htnova.access.commons.pojo.AbstractStatus;
import com.htnova.access.sysconfig.constdef.AlarmDef;
import com.htnova.access.sysconfig.constdef.ModDef;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** 通过将AbstractStatus抽象为顶层接口，所有的相关系统都需要，解除此依赖。 */
@Data
@EqualsAndHashCode(callSuper=false)
public class DeviceAlarmStatusDto extends AbstractStatus {
    private Boolean alarm = false;

    public DeviceAlarmStatusDto(){
        super();
    }

    /**
     * 报警状态
     *
     * @see AlarmDef#STATE_ALARM 系列。
     */
    private int state;

    private Map<String, ModuleAlarmStatusDto> moduleAlarmMap = new HashMap<>();

    public void putModuleAlarmStatus(String modType, ModuleAlarmStatusDto moduleAlarmStatus) {
        moduleAlarmMap.put(modType, moduleAlarmStatus);
    }

    public void removeModuleAlarmStatus(String modType) {
        if (moduleAlarmMap.containsKey(modType)) {
            moduleAlarmMap.remove(modType);
        }
    }

    public ModuleAlarmStatusDto getModuleAlarmStatus(String modType) {
        if (moduleAlarmMap.containsKey(modType)) {
            return moduleAlarmMap.get(modType);
        }
        return null;
    }

    public boolean containsModule(String modType) {
        return moduleAlarmMap.containsKey(modType);
    }

    public boolean isAbgas() {
        if (containsModule(ModDef.TYPE_TVOC)) {
            ModuleAlarmStatusDto alarmModDto = getModuleAlarmStatus(ModDef.TYPE_TVOC);
            if (!moduleAlarmMap.isEmpty() && moduleAlarmMap.size() == 1
                && AlarmDef.CONT_ABGAS.equals(alarmModDto.getAlarmContent())) {
                return true;
            }
        }
        return false;
    }
}
