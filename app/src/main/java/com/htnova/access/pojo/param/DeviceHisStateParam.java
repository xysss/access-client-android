package com.htnova.access.pojo.param;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DeviceHisStateParam extends PageParam {
    private Long beginTime;
    private Long endTime;
    private List<String> sn;
    private List<String> officeId;
    private List<String> taskId;
    private List<Integer> runstate;
    private List<String> deviceModel;

    public DeviceHisStateParam(){
        super();
    }
}
