package com.htnova.access.pojo.param;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DeviceHisAlarmParam extends PageParam {
    private Long beginTime;
    private Long endTime;
    private List<String> sn;
    private List<String> officeId;
    private List<String> taskId;
    private String alarmType;
    private String message;
    private List<String> deviceModel;

    public DeviceHisAlarmParam(){
        super();
    }
}
