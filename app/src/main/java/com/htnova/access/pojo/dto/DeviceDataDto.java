package com.htnova.access.pojo.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.commons.pojo.AbstractModExt;
import com.htnova.access.sysconfig.constdef.ModDef;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 中间转换设备数据（DTO）。
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class DeviceDataDto extends AbstractDevice {
    private Map<String, AbstractModExt> mods = new ConcurrentHashMap<>();

    /** 用以表示当前所有的故障列表。 */
    private List<FaultData> faults = null;

    /** 用以表示当前所有的报警列表。 */
    private List<AlarmData> alarms = null;

    public DeviceDataDto() {
        super();
        this.dataType = DATA_TYPE_DATA;
    }

    @JsonIgnore
    public ModSmokeDto getSmokeMod() {
        return (ModSmokeDto)mods.get(ModDef.TYPE_SMOKE);
    }

    @JsonIgnore
    public ModBioDto getBioMod() {
        return (ModBioDto)mods.get(ModDef.TYPE_BIO);
    }

    @JsonIgnore
    public ModTvocDto getTvocMod() {
        return (ModTvocDto)mods.get(ModDef.TYPE_TVOC);
    }

    @JsonIgnore
    public ModCwaDto getCwaMod() {
        return (ModCwaDto)mods.get(ModDef.TYPE_CWA);
    }

    @JsonIgnore
    public ModSysDto getSysMod() {
        return (ModSysDto)mods.get(ModDef.TYPE_SYS);
    }

    @JsonIgnore
    public ModWeatherDto getWeatherMod() {
        return (ModWeatherDto)mods.get(ModDef.TYPE_WEATHER);
    }

    @JsonIgnore
    public ModNuclearDto getNuclearMod() {
        return (ModNuclearDto)mods.get(ModDef.TYPE_NUCLEAR);
    }

    @JsonIgnore
    public ModGcameraDto getGcameraMod() {
        return (ModGcameraDto)mods.get(ModDef.TYPE_GCAMERA);
    }

    @JsonIgnore
    public ModGd606Dto getGd606Mod() {
        return (ModGd606Dto)mods.get(ModDef.TYPE_GD606);
    }

    @JsonIgnore
    public ModEcDto getEcMod() {
        return (ModEcDto)mods.get(ModDef.TYPE_EC);
    }

    public void setSmokeMod(AbstractModExt currMod) {
        mods.put(ModDef.TYPE_SMOKE, currMod);
    }

    public void setBioMod(AbstractModExt currMod) {
        mods.put(ModDef.TYPE_BIO, currMod);
    }

    public void setTvocMod(AbstractModExt currMod) {
        mods.put(ModDef.TYPE_TVOC, currMod);
    }

    public void setCwaMod(AbstractModExt currMod) {
        mods.put(ModDef.TYPE_CWA, currMod);
    }

    public void setSysMod(AbstractModExt currMod) {
        mods.put(ModDef.TYPE_SYS, currMod);
    }

    public void setWeatherMod(AbstractModExt currMod) {
        mods.put(ModDef.TYPE_WEATHER, currMod);
    }

    public void setNuclearMod(AbstractModExt currMod) {
        mods.put(ModDef.TYPE_NUCLEAR, currMod);
    }

    public void setGcameraMod(AbstractModExt currMod) {
        mods.put(ModDef.TYPE_GCAMERA, currMod);
    }

    public void setGd606Mod(AbstractModExt currMod) {
        mods.put(ModDef.TYPE_GD606, currMod);
    }

    public void setEcMod(AbstractModExt currMod) {
        mods.put(ModDef.TYPE_EC, currMod);
    }

    public void clearMods(String[] modTypes) {
        if (modTypes != null && modTypes.length > 0) {
            for (int i = 0; i < modTypes.length; i++) {
                String modType = modTypes[i];
                if (mods.containsKey(modType)) {
                    mods.remove(modType);
                }
            }
        }
    }

    public void initEmptyMods(String[] modTypes) {
        if (modTypes != null && modTypes.length > 0) {
            for (String modType : modTypes) {
                switch (modType) {
                    case ModDef.TYPE_SMOKE:
                        if (getSmokeMod() == null) {
                            setSmokeMod(new ModSmokeDto());
                        }
                        break;
                    case ModDef.TYPE_BIO:
                        if (getBioMod() == null) {
                            setBioMod(new ModBioDto());
                        }
                        break;
                    case ModDef.TYPE_TVOC:
                        if (getTvocMod() == null) {
                            setTvocMod(new ModTvocDto());
                        }
                        break;
                    case ModDef.TYPE_CWA:
                        if (getCwaMod() == null) {
                            setCwaMod(new ModCwaDto());
                        }
                        break;
                    case ModDef.TYPE_SYS:
                        if (getSysMod() == null) {
                            setSysMod(new ModSysDto());
                        }
                        break;
                    case ModDef.TYPE_WEATHER:
                        if (getWeatherMod() == null) {
                            setWeatherMod(new ModWeatherDto());
                        }
                        break;
                    case ModDef.TYPE_NUCLEAR:
                        if (getNuclearMod() == null) {
                            setNuclearMod(new ModNuclearDto());
                        }
                        break;
                    case ModDef.TYPE_GCAMERA:
                        if (getGcameraMod() == null) {
                            setGcameraMod(new ModGcameraDto());
                        }
                        break;
                    case ModDef.TYPE_GD606:
                        if (getGd606Mod() == null) {
                            setGd606Mod(new ModGd606Dto());
                        }
                        break;
                    case ModDef.TYPE_EC:
                        if (getEcMod() == null) {
                            setEcMod(new ModEcDto());
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void addFault(FaultData faultData) {
        if (faults == null) {
            faults = new ArrayList<>();
        }
        faults.add(faultData);
    }

    public void addAlarm(AlarmData alarmData) {
        if (alarms == null) {
            alarms = new ArrayList<>();
        }
        alarms.add(alarmData);
    }
}
