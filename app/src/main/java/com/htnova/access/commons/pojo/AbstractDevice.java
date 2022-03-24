package com.htnova.access.commons.pojo;

import java.util.Date;

import lombok.Data;

/**
 * <pre>
 *     设备的高层抽象，设备相关的实体都需要继承该抽象类。
 *     从实体组合的角度，应该是每个Device对象由两部分组成：
 *     （1）DeviceInfo的属性，这些属性是设备的基本信息。
 *     （2）DeviceData的属性，这些属性是设备的数据信息。
 *     在此，将两部分基础信息合成到一起，没有再进行综合和组合。
 * </pre>
 */
@Data
public class AbstractDevice {
    ////////////////////////////////////////////////////////////////////////////////
    // 每个设备都有的静态属性
    ////////////////////////////////////////////////////////////////////////////////
    protected String id; // 流水号：64位的字符串。
    protected String sn; // 编号：如2004020035。
    protected String outterSn; // 铭牌号：如09030019180008。
    protected String name; // 名称：西直门南E口监测设备。
    protected int runMode; // 产品运行方式（按监测、检测分）：0-未定义，1-监测类，2-检测类。
    protected int productCode; // 产品代码：如0x05对应308设备，0x08对应B02设备。
    protected String deviceTypeName; // 设备型号名称。
    protected String deviceType; // 设备型号编码：如FCBR-100、FCBR-100B、CR2500、BM3001等。
    protected String officeId; // 机构号。
    protected String officeName; // 机构名称。
    protected String taskId; // 任务号。
    protected String taskName; // 任务名称。
    protected Boolean heartBeat = false; // 是否心跳数据。

    ////////////////////////////////////////////////////////////////////////////////
    // 每个设备都有的动态属性
    ////////////////////////////////////////////////////////////////////////////////
    protected double latitude; // 纬度。
    protected double longitude; // 经度。
    protected double altitude; // 高度。
    protected int state = 0; // 报警状态：0-正常，1-预警，2-报警，3-恢复，4-采集基线，5-开始。
    protected int runstate = 1; // 运行状态：0-未运行，1-正常，2-维护中，3-故障，4-采集基线中。
    protected Long beginTime = System.currentTimeMillis(); // 起始时间：长整型。
    protected Long endTime = System.currentTimeMillis(); // 结束时间：长整型。
    protected Date beginTimeD = new Date(); // 起始时间：日期型。
    protected Date endTimeD = new Date(); // 结束时间：日期型。
    protected int code = 30801; // 全部推送默认code码。

    ////////////////////////////////////////////////////////////////////////////////
    // 协议解析用的额外属性
    ////////////////////////////////////////////////////////////////////////////////
    public static final int DATA_TYPE_HEARTBEAT = 1;
    public static final int DATA_TYPE_VERSION = 2;
    public static final int DATA_TYPE_INSTRUCT = 3;
    public static final int DATA_TYPE_DATA = 4;
    protected int msgId;
    protected int dataType;
}
