package com.htnova.access.pojo.param;

import lombok.Data;

@Data
public class Fcbr100mParamSetting<T> {
    private String sn;
    private String deviceType;
    private T requestData;
}
