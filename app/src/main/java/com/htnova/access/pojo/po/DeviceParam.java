package com.htnova.access.pojo.po;

import java.util.ArrayList;
import java.util.List;

import com.htnova.access.commons.pojo.AbstractParam;
import com.htnova.access.pojo.param.DeviceHisAlarmParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** 参数实体。 */
@Data
@EqualsAndHashCode(callSuper=false)
public class DeviceParam extends AbstractParam {
    private String id;
    private int batchId;
    private int defaultFlag;
    private List<ParamItem> items = new ArrayList<>();

    public DeviceParam(){
        super();
    }
}
