package com.htnova.access.pojo.dto;

import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRunstateDto implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    // 设备sn号
    private String sn;

    // 上线时间
    private Long beginTime;

    // 下线时间
    private Long endTime;

    // 上线时间
    @JsonIgnore
    private Date beginTimeD;

    // 下线时间
    @JsonIgnore
    private Date endTimeD;

    private Integer runstate;

    private String deviceName;

    private String deviceType;

    // 在线、离线、故障时间，统计用。
    private long onlineTime;
    private long offlineTime;
    private long faultTime;

    public Long getBeginTime() {
        if (Objects.nonNull(beginTimeD)) {
            return beginTimeD.getTime();
        }
        return this.beginTime;
    }

    public Long getEndTime() {
        if (Objects.nonNull(endTimeD)) {
            return endTimeD.getTime();
        }
        return this.endTime;
    }
}
