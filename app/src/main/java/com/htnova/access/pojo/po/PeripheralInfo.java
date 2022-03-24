package com.htnova.access.pojo.po;

import java.util.Date;

import lombok.Data;

@Data
public class PeripheralInfo {
    private static final long serialVersionUID = 1L;

    private String id;

    /** 设备编号：外设所属的设备编号。 */
    private String sn;

    /** 外设编号：每一个外设都有自己的编号，用以区分每次更换的外设。 */
    private String peripheralsn;

    /** 外设类型：1-滤毒罐，2-生物采集器，用以区分不同的外设，同一设备可能有多个外设。 */
    private Integer peripheraltype;

    /** 安装时间：安装时设定，中间不会发生变化。 */
    private Date installTime;

    /** 过期时间：安装时根据外设类型设定，中间不会发生变化。 */
    private Date expireTime;

    /** 建议更换时间：后台计算后确定，需要定期根据使用情况更新。 */
    private Date replaceTime;

    /** 累计使用时长：从安装开始计算，不开机也算。单位秒。 */
    private Integer useTime;

    /** 累计工作时长：含报警时的工作时间。单位秒。 */
    private Integer workTime;

    /** 累计报警时长：报警或异常情况下的工作时间。单位秒。 */
    private Integer alarmTime;

    /** 使用时长限制（从安装开始，不开机也算）。单位秒。 */
    private Integer limitUseTime;

    /** 工作时长限制（开机才算）。单位秒。 */
    private Integer limitWorkTime;

    /** 报警时长限制（报警才算）。单位秒。 */
    private Integer limitAlarmTime;

    /** 创建时间 */
    private Date createDate;

    /** 更新时间 */
    private Date updateDate;
}
