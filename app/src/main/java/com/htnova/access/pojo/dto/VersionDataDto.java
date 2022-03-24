package com.htnova.access.pojo.dto;

import com.htnova.access.commons.pojo.AbstractDevice;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** 产品版本信息数据结构。 */
@Data
@EqualsAndHashCode(callSuper=false)
public class VersionDataDto extends AbstractDevice {
    private double mainVer;
    private double vocVer;
    private double hwMainVer;
    private double hwVocVer;
    private double ticVer;
    private double hwTicVer;
    private double cwaVer;
    private double hwCwaVer;

    public VersionDataDto() {
        super();
        this.dataType = DATA_TYPE_VERSION;
    }
}
