package com.htnova.access.sysconfig.constdef;

/** 报警相关的常量：报警类型、报警显示的内容。 */
public interface AlarmDef {
    /** 报警显示的内容。 */
    String CONT_NONE = "无";
    String CONT_AIR = "空气";
    String CONT_ABGAS = "异常气体";
    String CONT_TOXIC = "工业毒气";
    String CONT_CHEMICAL = "化学气体";
    String CONT_NERVE = "神经性毒剂";
    String CONT_BLISTER = "糜烂性毒剂";
    String CONT_BLOOD = "血液性毒剂";
    String CONT_CHOKING = "窒息性毒剂";
    String CONT_INCAPACITATING = "失能性毒剂";
    String CONT_HAZARDOUS_GAS = "有毒有害气体";
    String CONT_BIO = "生物因子报警";
    String CONT_PID10 = "PID超10%";
    String CONT_PREALARM = "预警";
    String CONT_WARNING = "警告";
    String CONT_FIRE1 = "烟雾1";
    String CONT_FIRE2 = "烟雾2";
    String CONT_WEATHER = "气象环境报警";
    String CONT_NUCLEAR = "核辐射剂量报警";
    String CONT_GD606 = "复合气体报警";
    String CONT_EC = "电化学报警";
    String CONT_NORMAL = "正常";

    String CONT_SMOOTH = "平稳";
    String CONT_ASCEND = "上升";
    String CONT_DESCEND = "下降";
    String CONT_HIGH = "高";
    String CONT_MID = "中";
    String CONT_LOW = "低";

    /** 报警状态：包括正常、预警、报警、恢复、采集基线、开始。 */
    int STATE_NORMAL = 0;

    int STATE_PREALARM = 1;
    int STATE_ALARM = 2;
    int STATE_RECOVERY = 3;
    int STATE_BASELINE = 4;
    int STATE_STARTUP = 5;

    /**
     * 报警类型：到传感器级别，按模块编号排序。 包括烟雾、生物、PID、氨气、氯气、一氧化碳、战剂、核。
     * 其中烟雾-1000、生物-2000、TVOC-3000、CWA-4000、SYS-5000、WEATHER-6000、NUCLEAR-7000、D226-21000。
     */
    int TYPE_SMOKE = 1101;

    int TYPE_BIO = 2101;
    int TYPE_PID = 3101;
    int TYPE_PID10 = 3102;
    int TYPE_NH3 = 3201;
    int TYPE_CL2 = 3202;
    int TYPE_CO = 3203;
    int TYPE_HT = 4101;
    int TYPE_NUCLEAR = 7101;
    int TYPE_GCAMERA = 8101;
    int TYPE_EC_O2 = 9101;
    int TYPE_EC_NO2 = 9102;
    int TYPE_EC_H2S = 9103;
    int TYPE_EC_HCN = 9104;
    int TYPE_EC_COCL2 = 9105;
    String TYPE_SMOKE_CONT = "烟雾";
    String TYPE_BIO_CONT = "生物因子";
    String TYPE_GD606_CONT = "复合气体";
    String TYPE_EC_CONT = "电化学";

    // 报警模式：包括相对滑动、绝对、相对固定、动态基线、采用硬件的报警结果。
    int MODE_RELATIVE = 1;
    int MODE_ABSOLUTE = 2;
    int MODE_NEWRELATIVE = 3;
    int MODE_DYNABASELINE = 4;
    int MODE_HARDWARE_CONSISTENCY = 5;
}
