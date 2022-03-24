package com.htnova.access.pojo.dto;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.htnova.access.commons.pojo.AbstractModExt;
import com.htnova.access.commons.utils.NumberUtil;
import com.htnova.access.sysconfig.constdef.AlarmDef;
import com.htnova.access.sysconfig.constdef.ModDef;
import com.htnova.access.sysconfig.constdef.RunStateDef;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ModBioDto extends AbstractModExt implements Cloneable {
    // 生物模块版本比较多，ModBioDto综合了各个版本的数据。外部引用时通过BUSITYPE来区分不同的版本。
    public static final int BUSITYPE_FCBR_V1 = 1; // FCBR-V1版本，FCBR设备使用。有通道1-5的数据，由FCBR内部的klv协议号0x41-0x41标识。
    public static final int BUSITYPE_FCBR_V2 = 2; // FCBR-V2版本，FCBR设备使用。有通道1-13的数据，由FCBR内部的klv协议号0x41-0x42标识。
    public static final int BUSITYPE_FCBR_VE = 3; // FCBR-VE版本，没有设备使用。有电化学数据，与V2属于同一个版本，与V2一起按功能分组发送，由FCBR内部的klv协议号0x41-0x43标识。
    public static final int BUSITYPE_BM3001_V1 = 11; // BM3001-V1版本，BM3001设备使用。有通道1-13的数据，由BM3001内部的klv协议号0x41-0x04标识。
    public static final int BUSITYPE_BM3001_V2 = 12; // BM3001-V2版本，BM3001设备使用。有通道1-23的数据，由BM3001内部的klv协议号0x41-（0x07+0x03+0x05）标识。

    // BM3001的报警状态。
    public static final int STATE_NORMAL = 0;
    public static final int STATE_BIOALARM = 1;
    public static final int STATE_SMOKEALARM = 2;
    public static final int STATE_BIOWARNING = 3;
    public static final int STATE_UNKNOW = 9;

    // 业务类型。
    private int busiType; // 与BUSITYPE_开头的值对应。

    // 用于保存原始气溶胶各粒径值，原始值，不做额外处理，兼容13通道以内的版本。
    private long parOr1; // 13通道：0.5-1.0粒子数
    private long parOr2; // 13通道：1.0-1.5粒子数
    private long parOr3; // 13通道：1.5-2.0粒子数
    private long parOr4; // 13通道：2.0-2.5粒子数
    private long parOr5; // 13通道：2.5-3.0粒子数
    private long parOr6; // 13通道：3.0-3.5粒子数
    private long parOr7; // 13通道：3.5-4.0粒子数
    private long parOr8; // 13通道：4.0-4.5粒子数
    private long parOr9; // 13通道：4.5-5.0粒子数
    private long parOr10; // 13通道：5.0-5.5粒子数
    private long parOr11; // 13通道：5.5-6.0粒子数
    private long parOr12; // 13通道：6.0-10粒子数
    private long parOr13; // 13通道：10以上粒子数

    // // 用于保存原始气溶胶19通道的原始值，导出上位机使用。
    // private long parOr1V2; // 19通道：0.25-0.3粒子数，只用以保存原始数据，实际显示按13通道
    // private long parOr2V2; // 19通道：0.3-0.35粒子数，只用以保存原始数据，实际显示按13通道
    // private long parOr3V2; // 19通道：0.35-0.4粒子数，只用以保存原始数据，实际显示按13通道
    // private long parOr4V2; // 19通道：0.4-0.45粒子数，只用以保存原始数据，实际显示按13通道
    // private long parOr5V2; // 19通道：0.45-0.5粒子数，只用以保存原始数据，实际显示按13通道
    // private long parOr6V2; // 19通道：0.5-0.58粒子数，只用以保存原始数据，实际显示按13通道
    // private long parOr7V2; // 19通道：0.58-0.7粒子数，只用以保存原始数据，实际显示按13通道
    // private long parOr8V2; // 19通道：0.7-0.8粒子数，只用以保存原始数据，实际显示按13通道
    // private long parOr9V2; // 19通道：0.8-1粒子数，只用以保存原始数据，实际显示按13通道
    // private long parOr10V2; // 19通道：1.0-1.3粒子数，只用以保存原始数据，实际显示按13通道
    // private long parOr11V2; // 19通道：1.3-2.0粒子数，只用以保存原始数据，实际显示按13通道
    // private long parOr12V2; // 19通道：2.0-2.5粒子数，只用以保存原始数据，实际显示按13通道
    // private long parOr13V2; // 19通道：2.5-3.0粒子数，只用以保存原始数据，实际显示按13通道
    // private long parOr14V2; // 19通道：3.0-3.5粒子数，只用以保存原始数据，实际显示按13通道
    // private long parOr15V2; // 19通道：3.5-4.0粒子数，只用以保存原始数据，实际显示按13通道
    // private long parOr16V2; // 19通道：4.0-5.0粒子数，只用以保存原始数据，实际显示按13通道
    // private long parOr17V2; // 19通道：5.0-6.5粒子数，只用以保存原始数据，实际显示按13通道
    // private long parOr18V2; // 19通道：6.5-10粒子数，只用以保存原始数据，实际显示按13通道
    // private long parOr19V2; // 19通道：10以上粒子数，只用以保存原始数据，实际显示按13通道

    // 用于保存原始气溶胶23通道的原始值，后台内部使用，前端使用综合后的13通道的数据。
    private long parOr1V2; // 23通道：0.25-0.3粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr2V2; // 23通道：0.3-0.35粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr3V2; // 23通道：0.35-0.4粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr4V2; // 23通道：0.4-0.45粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr5V2; // 23通道：0.45-0.5粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr6V2; // 23通道：0.5-0.58粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr7V2; // 23通道：0.58-0.7粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr8V2; // 23通道：0.7-0.8粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr9V2; // 23通道：0.8-1粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr10V2; // 23通道：1.0-1.3粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr11V2; // 23通道：1.3-1.5粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr12V2; // 23通道：1.5-2.0粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr13V2; // 23通道：2.0-2.5粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr14V2; // 23通道：2.5-3.0粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr15V2; // 23通道：3.0-3.5粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr16V2; // 23通道：3.5-4.0粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr17V2; // 23通道：4.0-4.5粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr18V2; // 23通道：4.5-5.0粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr19V2; // 23通道：5.0-5.5粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr20V2; // 23通道：5.5-6.0粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr21V2; // 23通道：6.0-6.5粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr22V2; // 23通道：6.5-10粒子数，只用以保存原始数据，实际显示按13通道
    private long parOr23V2; // 23通道：10以上粒子数，只用以保存原始数据，实际显示按13通道

    // 用于保存原始荧光各粒径值，原始值，不做额外处理，兼容13通道以内的版本。
    private long bioOr1; // 13通道：0.5-1.0荧光数
    private long bioOr2; // 13通道：1.0-1.5荧光数
    private long bioOr3; // 13通道：1.5-2.0荧光数
    private long bioOr4; // 13通道：2.0-2.5荧光数
    private long bioOr5; // 13通道：2.5-3.0荧光数
    private long bioOr6; // 13通道：3.0-3.5荧光数
    private long bioOr7; // 13通道：3.5-4.0荧光数
    private long bioOr8; // 13通道：4.0-4.5荧光数
    private long bioOr9; // 13通道：4.5-5.0荧光数
    private long bioOr10; // 13通道：5.0-5.5荧光数
    private long bioOr11; // 13通道：5.5-6.0荧光数
    private long bioOr12; // 13通道：6.0-10荧光数
    private long bioOr13; // 13通道：10以上荧光数

    // // 用于保存原始荧光19通道的原始值，导出上位机使用。
    // private long bioOr1V2; // 19通道：0.25-0.3荧光数，只用以保存原始数据，实际显示按13通道
    // private long bioOr2V2; // 19通道：0.3-0.35荧光数，只用以保存原始数据，实际显示按13通道
    // private long bioOr3V2; // 19通道：0.35-0.4荧光数，只用以保存原始数据，实际显示按13通道
    // private long bioOr4V2; // 19通道：0.4-0.45荧光数，只用以保存原始数据，实际显示按13通道
    // private long bioOr5V2; // 19通道：0.45-0.5荧光数，只用以保存原始数据，实际显示按13通道
    // private long bioOr6V2; // 19通道：0.5-0.58荧光数，只用以保存原始数据，实际显示按13通道
    // private long bioOr7V2; // 19通道：0.58-0.7荧光数，只用以保存原始数据，实际显示按13通道
    // private long bioOr8V2; // 19通道：0.7-0.8荧光数，只用以保存原始数据，实际显示按13通道
    // private long bioOr9V2; // 19通道：0.8-1荧光数，只用以保存原始数据，实际显示按13通道
    // private long bioOr10V2; // 19通道：1.0-1.3荧光数，只用以保存原始数据，实际显示按13通道
    // private long bioOr11V2; // 19通道：1.3-2.0荧光数，只用以保存原始数据，实际显示按13通道
    // private long bioOr12V2; // 19通道：2.0-2.5荧光数，只用以保存原始数据，实际显示按13通道
    // private long bioOr13V2; // 19通道：2.5-3.0荧光数，只用以保存原始数据，实际显示按13通道
    // private long bioOr14V2; // 19通道：3.0-3.5荧光数，只用以保存原始数据，实际显示按13通道
    // private long bioOr15V2; // 19通道：3.5-4.0荧光数，只用以保存原始数据，实际显示按13通道
    // private long bioOr16V2; // 19通道：4.0-5.0荧光数，只用以保存原始数据，实际显示按13通道
    // private long bioOr17V2; // 19通道：5.0-6.5荧光数，只用以保存原始数据，实际显示按13通道
    // private long bioOr18V2; // 19通道：6.5-10荧光数，只用以保存原始数据，实际显示按13通道
    // private long bioOr19V2; // 19通道：10以上荧光数，只用以保存原始数据，实际显示按13通道

    // 用于保存原始荧光23通道的原始值，后台内部使用，前端使用综合后的13通道的数据。
    private long bioOr1V2; // 23通道：0.25-0.3荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr2V2; // 23通道：0.3-0.35荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr3V2; // 23通道：0.35-0.4荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr4V2; // 23通道：0.4-0.45荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr5V2; // 23通道：0.45-0.5荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr6V2; // 23通道：0.5-0.58荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr7V2; // 23通道：0.58-0.7荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr8V2; // 23通道：0.7-0.8荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr9V2; // 23通道：0.8-1荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr10V2; // 23通道：1.0-1.3荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr11V2; // 23通道：1.3-1.5荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr12V2; // 23通道：1.5-2.0荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr13V2; // 23通道：2.0-2.5荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr14V2; // 23通道：2.5-3.0荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr15V2; // 23通道：3.0-3.5荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr16V2; // 23通道：3.5-4.0荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr17V2; // 23通道：4.0-4.5荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr18V2; // 23通道：4.5-5.0荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr19V2; // 23通道：5.0-5.5荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr20V2; // 23通道：5.5-6.0荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr21V2; // 23通道：6.0-6.5荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr22V2; // 23通道：6.5-10荧光数，只用以保存原始数据，实际显示按13通道
    private long bioOr23V2; // 23通道：10以上荧光数，只用以保存原始数据，实际显示按13通道

    // 用于保存原始通道合并后的值，原始值，除了合并外，不做额外处理：按0.5-1.0、1.0-2.5、2.5-5.0、5.0-10、10以上划分。
    private long parOrSum1; // 0.5-1.0粒子数
    private long parOrSum2; // 1.0-2.5粒子数
    private long parOrSum3; // 2.5-5.0粒子数
    private long parOrSum4; // 5.0-10粒子数
    private long parOrSum5; // 10以上粒子数
    private long parOrSmall; // 1.0以下粒子数
    private long parOrLarge; // 1.0以上粒子数
    private long parOrEffective; // 1.0-10粒子数（原有算法为1.0-10，现有算法为0.5-10以上，与parOrAll相同）
    private long parOrAll; // 0.5-10以上粒子数
    private long bioOrSum1; // 0.5-1.0荧光数
    private long bioOrSum2; // 1.0-2.5荧光数
    private long bioOrSum3; // 2.5-5.0荧光数
    private long bioOrSum4; // 5.0-10荧光数
    private long bioOrSum5; // 10以上荧光数
    private long bioOrSmall; // 1.0以下荧光数
    private long bioOrLarge; // 1.0以上荧光数
    private long bioOrEffective; // 1.0-10荧光数（原有算法为1.0-10，现有算法为0.5-10以上，与bioOrAll相同）
    private long bioOrAll; // 0.5-10以上荧光数

    // v2版压力值：没有使用。
    private int pressure; // 压力值。

    // v2版电化学相关值：没有使用。
    private int co2Value; // co2值。
    private int coValue1; // co_1值。
    private int coValue2; // co_2值。

    // 用于保存约化后的值，在原始数据的基础上加工而成，由个/秒转为个/升。
    private long par1; // 0.5-1.0粒子数
    private long par2; // 1.0-2.5粒子数
    private long par3; // 2.5-5.0粒子数
    private long par4; // 5.0-10粒子数
    private long par5; // 10以上粒子数
    private long parSmall; // 1.0以下粒子数
    private long parLarge; // 1.0以上粒子数
    private long parEffective; // 1.0-10粒子数（原有算法为1.0-10，现有算法为0.5-10以上，与parAll相同）
    private long parAll; // 0.5-10以上粒子数，代表所有粒子数
    private long bio1; // 0.5-1.0荧光数
    private long bio2; // 1.0-2.5荧光数
    private long bio3; // 2.5-5.0荧光数
    private long bio4; // 5.0-10荧光数
    private long bio5; // 10以上荧光数
    private long bioSmall; // 1.0以下荧光数
    private long bioLarge; // 1.0以上荧光数
    private long bioEffective; // 1.0-10荧光数（原有算法为1.0-10，现有算法为0.5-10以上，与bioAll相同）
    private long bioAll; // 0.5-10以上荧光数，代表所有荧光数
    private double bioParRatioSmall; // 0.5-1.0荧光数与粒子数的比值
    private double bioParRatioLarge; // 1.0-10以上荧光数与粒子数的比值
    private long bioBaseLine; // 100B的生物采集基线
    private long bioEffectiveBground; // 荧光数本底值，与bioEffective相对应
    private String bioChannel;// 记录粒径通道情况
    private String bioAlgo; // 记录采用的算法
    private Boolean sendToDevice = false; // 报警是否下发到设备

    // BM3001新协议增加的信息项。
    private long timestamp; // 时间戳
    private long bioConcentration; // 生物浓度(与算法相关)
    private long bioConcentrationDevice; // 生物浓度（设备原始值）
    private long bioBground; // 生物本底(与算法相关)
    private long bioBgroundDevice; // 生物本底（设备原始值）
    private long noiseBground; // 噪声本底
    private int readyState; // 准备状态
    private int alarmState; // 报警状态，可能会被修改，前端需要使用来判断是否报警和显示
    private int modAlarmState; // 未经修改的设备端原始报警状态，用于存储到数仓
    private int fault; // 故障码
    private float rateOfFlow; // 流量率
    private String faultContent = AlarmDef.CONT_NONE;

    public ModBioDto() {
        super();
        this.modType = ModDef.TYPE_BIO;
    }

    public void setFcbrV1Data(long[] parArr, long[] bioArr) {
        // 将协议数据映射到原始数据：粒径映射。
        mappingParBioOrdata525(parArr, bioArr);

        // 特有计算：原始数据映射到约化后的数据，可能会有粒径合并计算。
        calcParBioData525();

        // 通用计算：大粒径、小粒径、全部粒径、有效粒径（当前与全部粒径相同）、大荧光粒子比、小荧光粒子比。
        calcParBioDataCommon();
    }

    public void setFcbrV2Data(long[] parArr, long[] bioArr, int pressValue) {
        // 将协议数据映射到原始数据：粒径映射。
        mappingParBioOrdata13213(parArr, bioArr);

        // 压力是独有数据，单独处理。
        pressure = pressValue;

        // 特有计算：原始数据映射到约化后的数据，可能会有粒径合并计算。
        calcParBioData1325();

        // 通用计算：大粒径、小粒径、全部粒径、有效粒径（当前与全部粒径相同）、大荧光粒子比、小荧光粒子比。
        calcParBioDataCommon();
    }

    public void setFcbrVEData(int[] elecArr) {
        co2Value = elecArr[0];
        coValue1 = elecArr[1];
        coValue2 = elecArr[2];
    }

    public void setBM3001V1Data(long[] parArr, long[] bioArr, Map<String, Number> otherDataMap) {
        // 将协议数据映射到原始数据：粒径映射。
        mappingParBioOrdata13213(parArr, bioArr);

        // 特有计算：原始数据映射到约化后的数据，可能会有粒径合并计算。
        calcParBioData1325();

        // BM3001特有的其它属性和计算逻辑：BM3001V1协议，其它属性和粒径属性放到一个协议中，BM3001V2分开两个协议。
        calcBM3001OtherData(otherDataMap);

        // 通用计算：大粒径、小粒径、全部粒径、有效粒径（当前与全部粒径相同）、大荧光粒子比、小荧光粒子比。
        calcParBioDataCommon();
    }

    public void setBM3001V2OrData(long[] parArr, long[] bioArr) {
        // 将协议数据映射到原始数据：粒径映射。
        mappingParBioOrdata23213(parArr, bioArr);

        // 特有计算：原始数据映射到约化后的数据，可能会有粒径合并计算。
        calcParBioData1325();

        // 通用计算：大粒径、小粒径、全部粒径、有效粒径（当前与全部粒径相同）、大荧光粒子比、小荧光粒子比。
        calcParBioDataCommon();
    }

    public void setBM3001V2OtherData(Map<String, Number> otherDataMap) {
        calcBM3001OtherData(otherDataMap);
    }

    private void calcBM3001OtherData(Map<String, Number> otherDataMap) {
        // BM3001特有的其它属性和计算逻辑：V1协议有timestamp，V2协议没有，因此有非空判断，其它属性都有，不需要判断。
        timestamp = otherDataMap.get("timestamp") == null ? 0 : otherDataMap.get("timestamp").longValue();
        bioConcentrationDevice = otherDataMap.get("bioConcentration").longValue();
        bioBgroundDevice = otherDataMap.get("bioBground").longValue();
        bioConcentration = otherDataMap.get("bioConcentration").longValue();
        bioBground = otherDataMap.get("bioBground").longValue();
        noiseBground = otherDataMap.get("noiseBground").longValue();
        readyState = otherDataMap.get("readyState").intValue();
        alarmState = otherDataMap.get("alarmState").intValue();
        fault = otherDataMap.get("fault").intValue();
        rateOfFlow = otherDataMap.get("rateOfFlow").floatValue();
        rateOfFlow = NumberUtil.getFloatPrecise(rateOfFlow, 4);

        // alarmState状态根据报警的情况，可能会被修改，为了保存原始状态到数仓，使用modAlarmState属性。
        modAlarmState = alarmState;

        alarmContent = "";
        faultContent = "";

        // alarmState与alarmContent的转换。
        if (alarmState == STATE_NORMAL) {
            alarmContent = "正常";
        } else {
            if (alarmState == STATE_BIOALARM) {
                alarmContent = "生物报警";
            } else if (alarmState == STATE_SMOKEALARM) {
                alarmContent = "烟雾报警";
            } else if (alarmState == STATE_BIOWARNING) {
                alarmContent = "生物警告";
            } else {
                alarmContent = "未知报警" + alarmState;
            }
        }

        // fault与runstate的转换。
        if (isFault(fault)) {
            fault = RunStateDef.FAULT;
            faultContent = "有故障";
        } else {
            fault = RunStateDef.NORMAL;
            faultContent = "无故障";
        }
        if (readyState == 0) {
            fault = RunStateDef.BASELINE;
            faultContent = "准备中";
        }
    }

    private void calcParBioData525() {
        parOrSum1 = par1 = parOr1;
        parOrSum2 = par2 = parOr2;
        parOrSum3 = par3 = parOr3;
        parOrSum4 = par4 = parOr4;
        parOrSum5 = par5 = parOr5;

        bioOrSum1 = bio1 = bioOr1;
        bioOrSum2 = bio2 = bioOr2;
        bioOrSum3 = bio3 = bioOr3;
        bioOrSum4 = bio4 = bioOr4;
        bioOrSum5 = bio5 = bioOr5;
    }

    private void calcParBioData1325() {
        parOrSum1 = par1 = parOr1;
        parOrSum2 = par2 = parOr2 + parOr3 + parOr4;
        parOrSum3 = par3 = parOr5 + parOr6 + parOr7 + parOr8 + parOr9;
        parOrSum4 = par4 = parOr10 + parOr11 + parOr12;
        parOrSum5 = par5 = parOr13;

        bioOrSum1 = bio1 = bioOr1;
        bioOrSum2 = bio2 = bioOr2 + bioOr3 + bioOr4;
        bioOrSum3 = bio3 = bioOr5 + bioOr6 + bioOr7 + bioOr8 + bioOr9;
        bioOrSum4 = bio4 = bioOr10 + bioOr11 + bioOr12;
        bioOrSum5 = bio5 = bioOr13;
    }

    /**
     * 协议解析完成之后，执行通用计算：大粒径、小粒径、全部粒径、有效粒径（当前与全部粒径相同）、大荧光粒子比、小荧光粒子比。<br>
     * （1）对原始数据（个/秒）进行计算，变量名包含Or字符。<br>
     * （2）对约化后的浓度数据（PPL，个/升）计算，变量名不含Or字符。<br>
     * 此处原始数据和约化后的浓度数据没有区别，只是为了分开保存，方便后面的程序会对两类数据进行不同的处理。<br>
     */
    private void calcParBioDataCommon() {
        // 原有算法：1-10之间才算有效粒子，参与算法计算，太小的颗粒太多，参与算法计算不容易报警。
        // 现有算法：0.5-10以上都算有效粒子。
        parOrSmall = parSmall = par1;
        parOrLarge = parLarge = par2 + par3 + par4 + par5;
        parOrAll = parAll = parSmall + parLarge;
        parOrEffective = parEffective = parAll;

        bioOrSmall = bioSmall = bio1;
        bioOrLarge = bioLarge = bio2 + bio3 + bio4 + bio5;
        bioOrAll = bioAll = bioSmall + bioLarge;
        bioOrEffective = bioEffective = bioAll;

        // 防止被0除。
        if (parSmall == 0) {
            parSmall = 1;
        }
        if (parLarge == 0) {
            parLarge = 1;
        }
        bioParRatioSmall = (bioSmall * 1.0) / parSmall;
        bioParRatioLarge = (bioLarge * 1.0) / parLarge;
        bioParRatioSmall = NumberUtil.getDoublePrecise(bioParRatioSmall, 6);
        bioParRatioLarge = NumberUtil.getDoublePrecise(bioParRatioLarge, 6);
    }

    /**
     * 协议解析出的原始数据映射，映射到原始各粒径值：不同通道数的映射方式不同。
     *
     * @param parArr
     *            散射光粒子协议解析结果。
     * @param bioArr
     *            荧光粒子协议解析结果。
     */
    private void mappingParBioOrdata525(long[] parArr, long[] bioArr) {
        if (parArr.length == 5 && bioArr.length == 5) {
            parOr1 = parArr[0];
            parOr2 = parArr[1];
            parOr3 = parArr[2];
            parOr4 = parArr[3];
            parOr5 = parArr[4];

            bioOr1 = bioArr[0];
            bioOr2 = bioArr[1];
            bioOr3 = bioArr[2];
            bioOr4 = bioArr[3];
            bioOr5 = bioArr[4];

            bioChannel = "5,5";
        }
    }

    private void mappingParBioOrdata13213(long[] parArr, long[] bioArr) {
        if (parArr.length == 13 && bioArr.length == 13) {
            parOr1 = parArr[0];
            parOr2 = parArr[1];
            parOr3 = parArr[2];
            parOr4 = parArr[3];
            parOr5 = parArr[4];
            parOr6 = parArr[5];
            parOr7 = parArr[6];
            parOr8 = parArr[7];
            parOr9 = parArr[8];
            parOr10 = parArr[9];
            parOr11 = parArr[10];
            parOr12 = parArr[11];
            parOr13 = parArr[12];

            bioOr1 = bioArr[0];
            bioOr2 = bioArr[1];
            bioOr3 = bioArr[2];
            bioOr4 = bioArr[3];
            bioOr5 = bioArr[4];
            bioOr6 = bioArr[5];
            bioOr7 = bioArr[6];
            bioOr8 = bioArr[7];
            bioOr9 = bioArr[8];
            bioOr10 = bioArr[9];
            bioOr11 = bioArr[10];
            bioOr12 = bioArr[11];
            bioOr13 = bioArr[12];

            bioChannel = "13,19";
        }
    }

    private void mappingParBioOrdata23213(long[] parArr, long[] bioArr) {
        if (parArr.length == 23 && bioArr.length == 23) {
            parOr1V2 = parArr[0];
            parOr2V2 = parArr[1];
            parOr3V2 = parArr[2];
            parOr4V2 = parArr[3];
            parOr5V2 = parArr[4];
            parOr6V2 = parArr[5];
            parOr7V2 = parArr[6];
            parOr8V2 = parArr[7];
            parOr9V2 = parArr[8];
            parOr10V2 = parArr[9];
            parOr11V2 = parArr[10];
            parOr12V2 = parArr[11];
            parOr13V2 = parArr[12];
            parOr14V2 = parArr[13];
            parOr15V2 = parArr[14];
            parOr16V2 = parArr[15];
            parOr17V2 = parArr[16];
            parOr18V2 = parArr[17];
            parOr19V2 = parArr[18];
            parOr20V2 = parArr[19];
            parOr21V2 = parArr[20];
            parOr22V2 = parArr[21];
            parOr23V2 = parArr[22];

            parOr1 = parOr6V2 + parOr7V2 + parOr8V2 + parOr9V2;
            parOr2 = parOr10V2 + parOr11V2;
            parOr3 = parOr12V2;
            parOr4 = parOr13V2;
            parOr5 = parOr14V2;
            parOr6 = parOr15V2;
            parOr7 = parOr16V2;
            parOr8 = parOr17V2;
            parOr9 = parOr18V2;
            parOr10 = parOr19V2;
            parOr11 = parOr20V2;
            parOr12 = parOr21V2 + parOr22V2;
            parOr13 = parOr23V2;

            bioOr1V2 = bioArr[0];
            bioOr2V2 = bioArr[1];
            bioOr3V2 = bioArr[2];
            bioOr4V2 = bioArr[3];
            bioOr5V2 = bioArr[4];
            bioOr6V2 = bioArr[5];
            bioOr7V2 = bioArr[6];
            bioOr8V2 = bioArr[7];
            bioOr9V2 = bioArr[8];
            bioOr10V2 = bioArr[9];
            bioOr11V2 = bioArr[10];
            bioOr12V2 = bioArr[11];
            bioOr13V2 = bioArr[12];
            bioOr14V2 = bioArr[13];
            bioOr15V2 = bioArr[14];
            bioOr16V2 = bioArr[15];
            bioOr17V2 = bioArr[16];
            bioOr18V2 = bioArr[17];
            bioOr19V2 = bioArr[18];
            bioOr20V2 = bioArr[19];
            bioOr21V2 = bioArr[20];
            bioOr22V2 = bioArr[21];
            bioOr23V2 = bioArr[22];

            bioOr1 = bioOr6V2 + bioOr7V2 + bioOr8V2 + bioOr9V2;
            bioOr2 = bioOr10V2 + bioOr11V2;
            bioOr3 = bioOr12V2;
            bioOr4 = bioOr13V2;
            bioOr5 = bioOr14V2;
            bioOr6 = bioOr15V2;
            bioOr7 = bioOr16V2;
            bioOr8 = bioOr17V2;
            bioOr9 = bioOr18V2;
            bioOr10 = bioOr19V2;
            bioOr11 = bioOr20V2;
            bioOr12 = bioOr21V2 + bioOr22V2;
            bioOr13 = bioOr23V2;

            bioChannel = "23,19";
        }
    }

    private boolean isFault(int faultValue) {
        String faultBinary = Integer.toString(faultValue, 2);
        return faultBinary.charAt(faultBinary.length() - 1) != '0';
    }

    @Override
    public ModBioDto clone() throws CloneNotSupportedException {
        return (ModBioDto)super.clone();
    }

    /**
     * 更新设备可以监测到的最小粒子数组
     *
     * @param minParticle
     */
    public void updateMinParticleGroup(String minParticle) {
        if (BUSITYPE_BM3001_V2 == busiType && StringUtils.isNotBlank(minParticle)) {
            switch (minParticle) {
                case "0.25":
                    parOr1 = parOr1V2 + parOr2V2 + parOr3V2 + parOr4V2 + parOr5V2 + parOr6V2 + parOr7V2 + parOr8V2
                        + parOr9V2;
                    bioOr1 = bioOr1V2 + bioOr2V2 + bioOr3V2 + bioOr4V2 + bioOr5V2 + bioOr6V2 + bioOr7V2 + bioOr8V2
                        + bioOr9V2;
                    break;
                case "0.3":
                    parOr1 = parOr2V2 + parOr3V2 + parOr4V2 + parOr5V2 + parOr6V2 + parOr7V2 + parOr8V2 + parOr9V2;
                    bioOr1 = bioOr2V2 + bioOr3V2 + bioOr4V2 + bioOr5V2 + bioOr6V2 + bioOr7V2 + bioOr8V2 + bioOr9V2;
                    break;
                case "0.35":
                    parOr1 = parOr3V2 + parOr4V2 + parOr5V2 + parOr6V2 + parOr7V2 + parOr8V2 + parOr9V2;
                    bioOr1 = bioOr3V2 + bioOr4V2 + bioOr5V2 + bioOr6V2 + bioOr7V2 + bioOr8V2 + bioOr9V2;
                    break;
                case "0.4":
                    parOr1 = parOr4V2 + parOr5V2 + parOr6V2 + parOr7V2 + parOr8V2 + parOr9V2;
                    bioOr1 = bioOr4V2 + bioOr5V2 + bioOr6V2 + bioOr7V2 + bioOr8V2 + bioOr9V2;
                    break;
                case "0.45":
                    parOr1 = parOr5V2 + parOr6V2 + parOr7V2 + parOr8V2 + parOr9V2;
                    bioOr1 = bioOr5V2 + bioOr6V2 + bioOr7V2 + bioOr8V2 + bioOr9V2;
                    break;
                default:
                    break;
            }
        }
    }
}
