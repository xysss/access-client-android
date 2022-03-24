package com.htnova.access.pojo.po;

import org.apache.commons.lang3.StringUtils;

import com.htnova.access.commons.pojo.AbstractDevice;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** 设备信息实体。 */
@Data
@EqualsAndHashCode(callSuper=false)
public class DeviceInfo extends AbstractDevice {
    private String busiName;
    private String pointCoordinate;
    private String areaCoordinate;

    public DeviceInfo(){
        super();
    }

    @Override
    public String getName() {
        if (StringUtils.isNoneBlank(busiName)) {
            return busiName;
        }
        return name;
    }

    public String getBusiName() {
        if (StringUtils.isNoneBlank(busiName)) {
            return busiName;
        }
        return name;
    }
}
