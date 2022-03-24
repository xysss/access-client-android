package com.htnova.access.datahandle.pojo;

import com.htnova.access.pojo.dto.VersionDataDto;

import lombok.Data;

/** 设备状态，模块状态小于0表示指令下发，等待回应。 */
@Data
public class FirmwareUpdateStateDto {
    private String sn;
    private VersionDataDto versionModel;
    private FirmwareModStateDto updateModuleState;
    private FirmwareModStateDto restartModuleState;
    private String message;
}
