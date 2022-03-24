package com.htnova.access.commons.pojo;

import lombok.Data;

/** 设备参数的高层抽象，设备参数相关的实体都需要继承该抽象类。 */
@Data
public class AbstractParam {
    public static final int LEVEL_LOW = 1;
    public static final int LEVEL_MID = 2;
    public static final int LEVEL_HIGH = 3;

    ////////////////////////////////////////////////////////////////////////////////
    // 以下是每个参数都有的公共属性，这些公共属性与算法、协议解析、业务处理相关。
    ////////////////////////////////////////////////////////////////////////////////
    protected String sn; // 编号：如2004020035。
    protected int productCode; // 产品代码：如0x05对应308设备，0x08对应B02设备。
    protected String deviceType; // 设备型号编码：如FCBR-100、FCBR-100B、CR2500、BM3001等。
    // todo by junzai：基本废弃了，以paramLevel代替。
    protected int sceneLevel = 1; // 参数场景等级。
    protected int paramLevel = 1; // 参数灵敏度等级。
    protected int preventFlag = 0; // 屏蔽故障或报警：1-屏蔽故障，2-屏蔽报警，3-两者都屏蔽。
    protected int localFlag = 0; // 是否使用本地参数：0-服务器参数，1-本地参数。
    protected int code = 30804; // 设备灵敏度推送默认code码。
    ////////////////////////////////////////////////////////////////////////////////
}
