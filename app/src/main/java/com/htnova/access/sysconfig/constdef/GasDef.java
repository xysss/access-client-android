package com.htnova.access.sysconfig.constdef;

/** ChemPro检测到的毒害气体的定义。 */
public interface GasDef {
    /** 气体类型，包括对应的Key和Desc。 */
    String[] GASTYPE_NERVE = {"Nerve", "神经性毒剂"};

    String[] GASTYPE_BLISTER = {"Blister", "糜烂性毒剂"};
    String[] GASTYPE_BLOOD = {"Blood", "血液性毒剂"};
    String[] GASTYPE_CHOKING = {"Choking", "窒息性毒剂"};
    String[] GASTYPE_INCAPACITATING = {"Incapacitating", "失能性毒剂"};
    String[] GASTYPE_TIC = {"TIC", "工业毒气"};
    String[] GASTYPE_CHEMICAL = {"Chemical", "化学气体"};
    String[] GASTYPE_TOXIC = {"Toxic", "工业毒气"};
    String[] GASTYPE_AIR = {"Air", "空气"};
}
