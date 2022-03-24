package com.htnova.access.pojo.po;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RunstateInfo implements java.io.Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    private String id;

    // 设备sn号
    private String sn;

    // 上线时间
    private Date startTime;

    // 下线时间
    private Date endTime;

    private Integer busiType;

    private String runstate;

    /** 上下线：0，上线、1，下线 * */
    private String currFlag;

    private String officeId; // 机构号。

    private String taskId; // 任务号。

    private String deviceTypeCode; // 设备型号名称。

    @Override
    public RunstateInfo clone() throws CloneNotSupportedException {
        return (RunstateInfo)super.clone();
    }
}
