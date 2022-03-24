package com.htnova.access.sysconfig.constdef;

/** 应用常量类，除了一些专门的常量（如：Mqtt、Mail、Datahub），都放到该类中。 */
public interface AlarmTransferDef {
    // 报警状态迁移：1-新增，2-持续，3-结束，4-新增226。
    int ALARM_TRANSFER_NEW = 1; // 新增报警。
    int ALARM_TRANSFER_CONTINUE = 2; // 持续报警。
    int ALARM_TRANSFER_END = 3; // 结束报警。
    int ALARM_TRANSFER_NEWDETECT = 4; // 新增检测类报警。
}
