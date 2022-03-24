package com.htnova.access.sysconfig.constdef;

/** 运行状态定义。 */
public interface RunStateDef {
    // 是否屏蔽报警或故障。
    int PREVENT_NONE = 0; // 不屏蔽。
    int PREVENT_FAULT = 1; // 屏蔽故障。
    int PREVENT_ALARM = 2; // 屏蔽报警。
    int PREVENT_BOTH = 3; // 屏蔽故障屏蔽报警。

    // 设备运行状态：0-未运行，1-正常，2-维护中，3-故障，4-采集基线中。
    int NOTRUN = 0; // 未运行。
    int NORMAL = 1; // 正常。
    int MAINTANCE = 2; // 维护中。
    int FAULT = 3; // 故障。
    int BASELINE = 4; // 采集基线中。
}
