package com.htnova.access.dataparser.protocol;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.commons.pojo.AbstractModExt;
import com.htnova.access.commons.utils.NumberUtil;
import com.htnova.access.pojo.dto.DeviceDataDto;
import com.htnova.access.pojo.dto.ModBioDto;

/** BIO模块的协议解析实现类。 */
class ModBioImpl extends AbstractModProt {
    public static final int BUSITYPE_308 = 1;
    public static final int BUSITYPE_b02 = 2;

    private static final long PROT_MAX_VALUE = 100000;

    @Override
    void protAnalysis(AbstractDevice deviceRawData, byte[] sensorData, int msgId, int protVer, int busiType)
        throws Exception {
        if (BUSITYPE_308 == busiType) {
            ModBioDto bioMod = (ModBioDto)getMod(deviceRawData);
            switch (msgId) {
                // BUSITYPE_FCBR_V1 = 1; FCBR-V1版本，FCBR设备使用。有通道1-5的数据，由FCBR内部的klv协议号0x41-0x41标识。
                case 0x41:
                    bioMod.setBusiType(ModBioDto.BUSITYPE_FCBR_V1);
                    analysisFcbrV1Data(deviceRawData.getSn(), bioMod, sensorData);
                    return;
                // BUSITYPE_FCBR_V2 = 2; FCBR-V2版本，FCBR设备使用。有通道1-13的数据，由FCBR内部的klv协议号0x41-0x42标识。
                case 0x42:
                    bioMod.setBusiType(ModBioDto.BUSITYPE_FCBR_V2);
                    analysisFcbrV2Data(deviceRawData.getSn(), bioMod, sensorData);
                    return;
                // BUSITYPE_FCBR_VE = 3; FCBR-VE版本，没有设备使用。有电化学数据，与V2属于同一个版本，与V2一起按功能分组发送，由FCBR内部的klv协议号0x41-0x43标识。
                case 0x43:
                    bioMod.setBusiType(ModBioDto.BUSITYPE_FCBR_VE);
                    analysisFcbrVEData(deviceRawData.getSn(), bioMod, sensorData);
                    return;
                default:
                    return;
            }
        } else if (BUSITYPE_b02 == busiType) {
            ModBioDto bioMod = (ModBioDto)getMod(deviceRawData);
            switch (msgId) {
                // BUSITYPE_BM3001_V1 = 11; BM3001-V1版本，BM3001设备使用。有通道1-13的数据，由BM3001内部的klv协议号0x41-0x04标识。
                case 0x04:
                    bioMod.setBusiType(ModBioDto.BUSITYPE_BM3001_V1);
                    analysisBM3001V1Data(deviceRawData.getSn(), bioMod, sensorData);
                    return;
                // BUSITYPE_BM3001_V2 = 12; BM3001-V2版本，BM3001设备使用。有通道1-23的数据，由BM3001内部的klv协议号0x41-（0x07+0x03+0x05）标识。
                // case 0x03:（内容包含在0x07和0x05中）
                case 0x05:
                case 0x07:
                    bioMod.setBusiType(ModBioDto.BUSITYPE_BM3001_V2);
                    analysisBM3001V2Data(deviceRawData.getSn(), bioMod, sensorData, msgId);
                    return;
                default:
                    return;
            }
        }
    }

    @Override
    AbstractModExt getMod(AbstractDevice deviceRawData) {
        DeviceDataDto deviceDataDto = (DeviceDataDto)deviceRawData;
        if (deviceDataDto.getBioMod() != null) {
            return deviceDataDto.getBioMod();
        }
        ModBioDto bioMod = new ModBioDto();
        deviceDataDto.setBioMod(bioMod);
        return bioMod;
    }

    // FcbrV1生物模块数据解析：有通道1-5的数据。
    private void analysisFcbrV1Data(String sn, ModBioDto bioMod, byte[] sensorData) throws Exception {
        String strIbac0 = new String(sensorData, StandardCharsets.UTF_8);
        if (strIbac0 != null && !"".equals(strIbac0.trim()) && strIbac0.contains("N99") && strIbac0.contains("*")) {
            int n99Index = strIbac0.indexOf("N99") + 3;
            int starIndex = strIbac0.indexOf("*");
            if (n99Index < starIndex) {
                String strIbac2 = strIbac0.substring(n99Index, starIndex);
                String[] strContent = strIbac2.split(",");
                if (strContent.length < 8) {
                    log.error("sn={}的生物模块长度小于8，以全0代替：{}", sn, strIbac0);
                } else {
                    try {
                        long par1 = Long.parseLong(strContent[0]);
                        long par2 = Long.parseLong(strContent[1]);
                        long par3 = Long.parseLong(strContent[2]);
                        long par4 = Long.parseLong(strContent[3]);
                        long bio1 = Long.parseLong(strContent[4]);
                        long bio2 = Long.parseLong(strContent[5]);
                        long bio3 = Long.parseLong(strContent[6]);
                        long bio4 = Long.parseLong(strContent[7]);
                        long par5 = 0/*par1 + par2 + par3 + par4*/;
                        long bio5 = 0/*bio1 + bio2 + bio3 + bio4*/;

                        // 此种生物传感器，bio5表示所有的荧光颗粒之和。
                        if (strContent.length > 8) {
                            bio5 = Long.parseLong(strContent[8]);

                            // 发往海南的100PRO的生物模块，该值都为0，为了兼容，做如下处理
                            if (bio5 > 0) {
                                bio5 = bio5 - bio1 - bio2 - bio3 - bio4;
                                if (bio5 < 0) {
                                    bio5 = 0;
                                }
                            }
                        }
                        bioMod.setFcbrV1Data(new long[] {par1, par2, par3, par4, par5},
                            new long[] {bio1, bio2, bio3, bio4, bio5});
                    } catch (Exception ex) {
                        log.error("sn={}的生物模块数据转换异常，以全0代替：{}", sn, strIbac0, ex);
                    }
                }
            } else {
                log.error("sn={}的生物模块N99在*之后，以全0代替：{}", sn, strIbac0);
            }
        }
    }

    // FcbrV2生物模块数据解析：有通道1-13的数据。
    private void analysisFcbrV2Data(String sn, ModBioDto bioMod, byte[] sensorData) throws Exception {
        if (sensorData.length != 106) {
            log.error("sn={}的新生物模块，数据长度非法", sn);
            return;
        }

        long[] parArr = new long[13];
        long[] bioArr = new long[13];
        int pressure = 0;
        int k = 0;
        for (int j = 0; j < sensorData.length; j++) {
            if (k < 13) {
                if (j % 4 == 0) {
                    calcFcbrV2Data(sn, sensorData, j, k, parArr);
                    k++;
                }
            } else if (k < 26) {
                if (j % 4 == 0) {
                    calcFcbrV2Data(sn, sensorData, j, k - 13, bioArr);
                    k++;
                }
            } else {
                pressure = NumberUtil.bytes2Int(new byte[] {sensorData[j], sensorData[j + 1]}, true);
                break;
            }
        }
        bioMod.setFcbrV2Data(parArr, bioArr, pressure);
    }

    // FcbrVE生物模块数据解析：有电化学数据，与V2属于同一个版本，与V2一起按功能分组发送。
    private void analysisFcbrVEData(String sn, ModBioDto bioMod, byte[] sensorData) throws Exception {
        if (sensorData.length != 6) {
            log.error("sn={}的新生物模块，电化学数据长度非法", sn);
        }
        int co2Value = NumberUtil.bytes2Int(new byte[] {sensorData[0], sensorData[1]}, true);
        int coValue1 = NumberUtil.bytes2Int(new byte[] {sensorData[2], sensorData[3]}, true);
        int coValue2 = NumberUtil.bytes2Int(new byte[] {sensorData[4], sensorData[5]}, true);
        bioMod.setFcbrVEData(new int[] {co2Value, coValue1, coValue2});
    }

    // BM3001V1生物模块数据解析：有通道1-13的数据。
    private void analysisBM3001V1Data(String sn, ModBioDto bioMod, byte[] sensorData) {
        if (sensorData.length != 130) {
            log.error("sn={}的新生物模块，数据长度非法", sn);
            return;
        }

        // 总体按大端，即大类顺序与协议的顺序一致，但里面的数据是小端，因此需要与之前的处理相反。
        // 时间戳（4）+ 粒子数（4x13=52，个/秒）+ 荧光数（4x13=52，个/秒）+ 生物气溶胶浓度（4，个/升）+ 气溶胶荧光背景（4，个/升）+ 噪声本底（4，个/升）+ 设备准备状态（1）+ 报警状态（1）+
        // 故障代码（4）+ 真实流量（4，个/升.秒）
        Map<String, Number> otherDataMap = new HashMap<>();
        int offset = 0;
        long timestamp = NumberUtil.bytes2Long(sensorData, offset, 4, false);
        otherDataMap.put("timestamp", timestamp);

        offset = 108;
        long bioConcentration = NumberUtil.bytes2Long(sensorData, offset, 4, false);
        otherDataMap.put("bioConcentration", bioConcentration);

        offset = 112;
        long bioBground = NumberUtil.bytes2Long(sensorData, offset, 4, false);
        otherDataMap.put("bioBground", bioBground);

        offset = 116;
        long noiseBground = NumberUtil.bytes2Long(sensorData, offset, 4, false);
        otherDataMap.put("noiseBground", noiseBground);

        offset = 120;
        int readyState = sensorData[offset];
        otherDataMap.put("readyState", readyState);

        offset = 121;
        int alarmState = sensorData[offset];
        otherDataMap.put("alarmState", alarmState);

        offset = 122;
        int fault = NumberUtil.bytes2Int(sensorData, offset, 4, false);
        otherDataMap.put("fault", fault);

        offset = 126;
        float rateOfFlow = NumberUtil.bytes2Float(sensorData, offset, false);
        rateOfFlow = NumberUtil.getFloatPrecise(rateOfFlow, 4);
        otherDataMap.put("rateOfFlow", rateOfFlow);

        long[] parArr = new long[13];
        long[] bioArr = new long[13];

        int k = 0;
        for (int j = 4; j < 108; j++) {
            if (k < 13) {
                if (j % 4 == 0) {
                    k++;
                    Long par = NumberUtil.bytes2Long(sensorData, j, 4, false);
                    // 解析数据会造成值过大，从而报警。所以添加判断，过滤错误数据。
                    if (par > PROT_MAX_VALUE) {
                        // 气溶胶粒子超过最大值日志太多，间隔一段时间再输出。
                        printFullScaleErrorLog(sn, "气溶胶粒子数", par, PROT_MAX_VALUE);
                        parArr[k - 1] = PROT_MAX_VALUE;
                    } else {
                        // BM3001以前1秒一次数据，现改为2秒一次数据，单位个/秒，因此需要除以2，为了与设备显示一致。
                        parArr[k - 1] = par / 2;
                    }
                }
            } else if (k < 26) {
                if (j % 4 == 0) {
                    k++;
                    Long bio = NumberUtil.bytes2Long(sensorData, j, 4, false);
                    if (bio > PROT_MAX_VALUE) {
                        printFullScaleErrorLog(sn, "荧光粒子数", bio, PROT_MAX_VALUE);
                        bioArr[k - 13 - 1] = PROT_MAX_VALUE;
                    } else {
                        // BM3001以前1秒一次数据，现改为2秒一次数据，单位个/秒，因此需要除以2，为了与设备显示一致。
                        bioArr[k - 13 - 1] = bio / 2;
                    }
                }
            }
        }
        bioMod.setBM3001V1Data(parArr, bioArr, otherDataMap);
    }

    // BM3001V2生物模块数据解析：有通道1-23的数据。
    private void analysisBM3001V2Data(String sn, ModBioDto bioMod, byte[] sensorData, int msgId) {
        if (msgId == 0x07) {
            if (sensorData.length != 189) {
                log.error("sn={}的新生物模块，数据长度非法", sn);
                return;
            }

            // 总体按大端，即大类顺序与协议的顺序一致，但里面的数据是小端，因此需要与之前的处理相反。
            // 粒子数（4x23=92，个/秒）+ 荧光数（4x23=92，个/秒）+ 生物粒子数符号位（1）+ 故障代码（4）
            long[] parArr = new long[23];
            long[] bioArr = new long[23];

            int k = 0;
            for (int j = 0; j < 184; j++) {
                if (k < 23) {
                    if (j % 4 == 0) {
                        k++;
                        Long par = NumberUtil.bytes2Long(sensorData, j, 4, false);
                        // 解析数据会造成值过大，从而报警。所以添加判断，过滤错误数据。
                        if (par > PROT_MAX_VALUE) {
                            // 气溶胶粒子超过最大值日志太多，间隔一段时间再输出。
                            printFullScaleErrorLog(sn, "气溶胶粒子数", par, PROT_MAX_VALUE);
                            parArr[k - 1] = PROT_MAX_VALUE;
                        } else {
                            // BM3001重新改回1秒一次数据，单位个/秒，为了与上位机显示一致。
                            parArr[k - 1] = par;
                        }
                    }
                } else if (k < 46) {
                    if (j % 4 == 0) {
                        k++;
                        Long bio = NumberUtil.bytes2Long(sensorData, j, 4, false);
                        if (bio > PROT_MAX_VALUE) {
                            printFullScaleErrorLog(sn, "荧光粒子数", bio, PROT_MAX_VALUE);
                            bioArr[k - 23 - 1] = PROT_MAX_VALUE;
                        } else {
                            // BM3001重新改回1秒一次数据，单位个/秒，为了与上位机显示一致。
                            bioArr[k - 23 - 1] = bio;
                        }
                    }
                }
            }
            bioMod.setBM3001V2OrData(parArr, bioArr);
        }

        // 内容包含在0x07和0x05中。
        // if (msgId == 0x03) {
        // if (sensorData.length != 22) {
        // log.error("sn={}的新生物模块，数据长度非法：{}", sn, JsonUtil.toJson(sensorData));
        // return;
        // }
        //
        // // 总体按大端，即大类顺序与协议的顺序一致，但里面的数据是小端，因此需要与之前的处理相反。
        // // 设备准备状态（1）+ 报警状态（1）+ 散射光0.5-1um粒子浓度（4，个/秒）+ 散射光1-10um粒子浓度（4，个/秒）+ 荧光0.5-1um粒子浓度（4，个/秒）+
        // // 荧光1-10um粒子浓度（4，个/秒）+ 生物粒子浓度（4，个/升）
        // Map<String, Number> otherDataMap = new HashMap<>();
        // int offset = 0;
        // int readyState = sensorData[offset];
        // otherDataMap.put("readyState", readyState);
        //
        // offset = 1;
        // int alarmState = sensorData[offset];
        // otherDataMap.put("alarmState", alarmState);
        //
        // offset = 2;
        // int s05_1_data = NumberUtil.bytes2Int(sensorData, offset, 4, false);
        // otherDataMap.put("s05_1_data", s05_1_data);
        //
        // offset = 6;
        // int s1_10_data = NumberUtil.bytes2Int(sensorData, offset, 4, false);
        // otherDataMap.put("s1_10_data", s1_10_data);
        //
        // offset = 10;
        // int f05_1_data = NumberUtil.bytes2Int(sensorData, offset, 4, false);
        // otherDataMap.put("f05_1_data", f05_1_data);
        //
        // offset = 14;
        // int f1_10_data = NumberUtil.bytes2Int(sensorData, offset, 4, false);
        // otherDataMap.put("f1_10_data", f1_10_data);
        //
        // offset = 18;
        // int bp_data = NumberUtil.bytes2Int(sensorData, offset, 4, false);
        // otherDataMap.put("bp_data", bp_data);
        // }

        if (msgId == 0x05) {
            if (sensorData.length != 22) {
                log.error("sn={}的新生物模块，数据长度非法", sn);
                return;
            }

            // 总体按大端，即大类顺序与协议的顺序一致，但里面的数据是小端，因此需要与之前的处理相反。
            // 生物气溶胶浓度（4，个/升）+ 气溶胶荧光背景（4，个/升）+ 噪声本底（4，个/升）+ 设备准备状态（1）+ 报警状态（1）+ 故障代码（4）+ 真实流量（4，个/升.秒）
            Map<String, Number> otherDataMap = new HashMap<>();
            int offset = 0;
            long bioConcentration = NumberUtil.bytes2Long(sensorData, offset, 4, false);
            otherDataMap.put("bioConcentration", bioConcentration);

            offset = 4;
            long bioBground = NumberUtil.bytes2Long(sensorData, offset, 4, false);
            otherDataMap.put("bioBground", bioBground);

            offset = 8;
            long noiseBground = NumberUtil.bytes2Long(sensorData, offset, 4, false);
            otherDataMap.put("noiseBground", noiseBground);

            offset = 12;
            int readyState = sensorData[offset];
            otherDataMap.put("readyState", readyState);

            offset = 13;
            int alarmState = sensorData[offset];
            otherDataMap.put("alarmState", alarmState);

            offset = 14;
            int fault = NumberUtil.bytes2Int(sensorData, offset, 4, false);
            otherDataMap.put("fault", fault);

            offset = 18;
            float rateOfFlow = NumberUtil.bytes2Float(sensorData, offset, false);
            rateOfFlow = NumberUtil.getFloatPrecise(rateOfFlow, 4);
            otherDataMap.put("rateOfFlow", rateOfFlow);

            bioMod.setBM3001V2OtherData(otherDataMap);
        }
    }

    private void calcFcbrV2Data(String sn, byte[] sensorData, int sensorDataIndex, int retDataIndex, long[] retArr) {
        long sensorValue = NumberUtil.bytes2Long(sensorData, sensorDataIndex, 4, true);
        if (sensorValue > PROT_MAX_VALUE) {
            printFullScaleErrorLog(sn, "生物模块数值", sensorValue, PROT_MAX_VALUE);
            retArr[retDataIndex] = PROT_MAX_VALUE;
        } else {
            retArr[retDataIndex] = sensorValue;
        }
    }

    private static final Map<String, Long> lastFullScaleCache = new ConcurrentHashMap<>();
    private static final long fullScaleErrorInterval = 120000;

    // 减少超过最大值的日志输出。
    private void printFullScaleErrorLog(String sn, String msg, long currValue, long maxValue) {
        Long currTimeInMillis = System.currentTimeMillis();
        if (lastFullScaleCache.containsKey(sn)) {
            Long lastTimeInMillis = lastFullScaleCache.get(sn);
            if ((currTimeInMillis - lastTimeInMillis) > fullScaleErrorInterval) {
                log.error("sn={}的{}为{}，超过最大值{}，以最大值代替。", sn, msg, currValue, maxValue);
                lastFullScaleCache.put(sn, currTimeInMillis);
            }
        } else {
            log.error("sn={}的{}为{}，超过最大值{}，以最大值代替。", sn, msg, currValue, maxValue);
            lastFullScaleCache.put(sn, currTimeInMillis);
        }
    }
}
