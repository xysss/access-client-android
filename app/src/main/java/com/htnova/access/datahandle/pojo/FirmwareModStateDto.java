package com.htnova.access.datahandle.pojo;

import lombok.Data;

/** 模块状态结构。 */
@Data
public class FirmwareModStateDto {
    private int module;
    private int state;
    private int operate;
}
