package com.htnova.access.pojo.dto;

import com.htnova.access.commons.pojo.AbstractDevice;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** 产品版本信息数据结构。 */
@Data
@EqualsAndHashCode(callSuper=false)
public class HeartBeatDataDto extends AbstractDevice {
    public HeartBeatDataDto() {
        super();
        this.dataType = DATA_TYPE_HEARTBEAT;
    }
}
