package com.htnova.access.pojo.param;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DeviceHisFaultParam extends PageParam {
    private Long beginTime;
    private Long endTime;
    private List<String> sn;
    private List<String> officeId;
    private List<String> taskId;
    private List<String> deviceModel;
    private String message;

    public DeviceHisFaultParam(){
        super();
    }
}
