package com.htnova.access.dataparser.protocol;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.commons.utils.NumberUtil;
import com.htnova.access.pojo.dto.DeviceConfig;
import com.htnova.access.pojo.dto.SensorConfig;
import com.htnova.access.pojo.dto.SensorData;
import com.htnova.access.sysconfig.constdef.AlarmTypeDictDef;
import com.htnova.access.sysconfig.constdef.SensorTypeDictDef;
import com.htnova.access.sysconfig.constdef.UnitDictDef;

/** 说明：GD606数据解析公共实现。 */
class Fcbr100mDataResolve {
    private static Logger log = LoggerFactory.getLogger(Fcbr100mDataResolve.class);

    // 0x53-设置生物采样器状态响应（FCBR-100M，获取设备时间响应）。
    // Byte0-Byte5：日期、时间，6个字节，YYMMDDHHMMSS组织，YY代表从2020年算起的年份。
    static DeviceConfig getFcbr100mTimeConfig(int startPos, AbstractDevice deviceRawData, byte[] effectiveData,
        int msgId) throws Exception {
        String deviceType = deviceRawData.getDeviceType();
        String sn = deviceRawData.getSn();

        if (log.isDebugEnabled()) {
            log.debug("开始读取设备时间，sn={}，msgId={}", sn, Integer.toHexString(msgId));
        }

        // 传感器配置信息的优先级最高，因此，在解析设备时间时，一定存在设备的配置信息。
        DeviceConfig deviceConfig = HisDeviceConfig.getConfig(deviceType, sn);
        if (deviceConfig == null) {
            return null;
        }

        // 设备端日期格式为BCD码，使用时需要转换。
        // 年份只取后面两位，忽略前面两位，因此需要根据当前的时间补齐。
        int yearBcd = effectiveData[startPos];
        int monthBcd = effectiveData[startPos + 1];
        int dateBcd = effectiveData[startPos + 2];
        int hourBcd = effectiveData[startPos + 3];
        int minuteBcd = effectiveData[startPos + 4];
        int secondBcd = effectiveData[startPos + 5];

        LocalDateTime currLocalDateTime = LocalDateTime.now();
        int currYearAditional = currLocalDateTime.getYear() / 100 * 100;
        int year = currYearAditional + NumberUtil.bcd2Byte((byte)yearBcd);
        int month = NumberUtil.bcd2Byte((byte)monthBcd);
        int date = NumberUtil.bcd2Byte((byte)dateBcd);
        int hour = NumberUtil.bcd2Byte((byte)hourBcd);
        int minute = NumberUtil.bcd2Byte((byte)minuteBcd);
        int second = NumberUtil.bcd2Byte((byte)secondBcd);

        Long dataDateTime = null;
        try {
            if (month == 0 && date == 0) {
                dataDateTime = currLocalDateTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
            } else {
                LocalDateTime localDateTime = LocalDateTime.of(year, month, date, hour, minute, second);
                dataDateTime = localDateTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
            }
        } catch (Exception e) {
            log.error("设备{}获取日期异常", sn, e);
            dataDateTime = System.currentTimeMillis();
        }

        // 系统时间没有单独的协议来处理，是从数据中直接获取到，只要获取到就更新到配置中。
        if (deviceConfig != null) {
            deviceConfig.setDeviceTime(dataDateTime);
        }

        return deviceConfig;
    }

    // 0x57-传感器信息读取响应。
    // Byte0-Byte1：传感器数量，uint16_t
    //
    // Byte2-Byte39：传感器信息，按照结构SENS_DATA_INFO_t依次排列
    //
    // typedef struct
    // {
    // uint8_t sen_type; //1-TID传感器，2-CWA传感器
    // uint8_t sen_index; //传感器索引号，TID传感器范围0-17，CWA传感器范围0-6
    // SENS_INFO_t data; //传感器信息
    // }SENS_DATA_INFO_t；
    //
    //
    // typedef struct
    // {
    // uint16_t type;//类型编码见下表，0表示无传感器
    // uint16_t ver;//高字节主版本号，低字节次版本号
    // char name[20];
    // uint8_t unit;//单位，0-PPM，1-vol%，2-LEL%，3-PPB
    // uint8_t reserv;
    // uint16_t mw;//分子量
    // int full_scale;
    // float sensibility;
    // }SENS_INFO_t;
    static DeviceConfig getFcbr100mSensorConfig(int startPos, AbstractDevice deviceRawData, byte[] effectiveData,
        int msgId) throws Exception {
        String deviceType = deviceRawData.getDeviceType();
        String sn = deviceRawData.getSn();

        if (log.isDebugEnabled()) {
            log.debug("开始读取传感器配置信息，sn={}，msgId={}", sn, Integer.toHexString(msgId));
        }

        // 传感器配置优先级最高，其它配置都获取该配置对象。
        // 传感器配置信息的优先级最高，因此，在解析设备时间时，一定存在设备的配置信息。
        DeviceConfig deviceConfig = HisDeviceConfig.getConfig(deviceType, sn);
        if (deviceConfig == null) {
            deviceConfig = new DeviceConfig();
        }
        deviceConfig.setDeviceType(deviceType);
        deviceConfig.setSn(sn);

        StringBuilder buf = new StringBuilder("");
        int configedCount = 0;
        // 本来每组数据长度为38，但解析时让startPos偏移了2个字节，因此，这里仍然保持36，与GD606解析一致。
        int eachConfigLen = 36;

        int sensorCount = NumberUtil.bytes2Int(effectiveData, startPos, 2, false);
        startPos += 2;

        for (byte i = 0; i < sensorCount; i++) {
            SensorConfig sensorConfig = new SensorConfig();

            byte sensorType = effectiveData[startPos + i * eachConfigLen];
            byte sensorIndex = effectiveData[startPos + 1 + i * eachConfigLen];

            sensorConfig.setSensorSeq(sensorIndex);
            sensorConfig.setSensorType(sensorType);

            startPos += 2;

            int type = NumberUtil.bytes2Int(new byte[] {effectiveData[startPos + i * eachConfigLen],
                effectiveData[startPos + i * eachConfigLen + 1]}, 0, 2, false);
            sensorConfig.setType(type);

            int version = NumberUtil.bytes2Int(new byte[] {effectiveData[startPos + i * eachConfigLen + 2],
                effectiveData[startPos + i * eachConfigLen + 2 + 1]}, 0, 2, false);
            sensorConfig.setVersion(version);

            byte[] nameBytes = new byte[20];
            System.arraycopy(effectiveData, startPos + i * eachConfigLen + 2 + 2, nameBytes, 0, nameBytes.length);
            sensorConfig.parseAndSetNameCodeOrder(nameBytes);

            byte unitCode = effectiveData[startPos + i * eachConfigLen + 2 + 2 + 20];
            sensorConfig.setOriginUnit(unitCode);
            sensorConfig.setUnit(unitCode);
            sensorConfig.setUnitName(UnitDictDef.getUnitName(unitCode));

            byte reserv = effectiveData[startPos + i * eachConfigLen + 2 + 2 + 20 + 1];
            sensorConfig.setReserv(reserv);

            int molecularWeight =
                NumberUtil.bytes2Int(new byte[] {effectiveData[startPos + i * eachConfigLen + 2 + 2 + 20 + 1 + 1],
                    effectiveData[startPos + i * eachConfigLen + 2 + 2 + 20 + 1 + 1 + 1]}, 0, 2, false);
            sensorConfig.setMolecularWeight(molecularWeight);

            int fullScale =
                NumberUtil.bytes2Int(effectiveData, startPos + i * eachConfigLen + 2 + 2 + 20 + 1 + 1 + 2, 4, false);
            sensorConfig.setFullScall(fullScale);

            float sensibility =
                NumberUtil.bytes2Float(effectiveData, startPos + i * eachConfigLen + 2 + 2 + 20 + 1 + 1 + 2 + 4, false);
            sensorConfig.setSensibility(sensibility);

            // 不再判断是否有效的标识才决定是否保存。只要读到传感器配置信息，就放到缓存中。
            // 协议约定，type大于0，表示传感器有效。
            // if (type > 0) {
            deviceConfig.getSensorSeqConfigs().put(sensorConfig.getSensorType() + "_" + sensorConfig.getSensorSeq(),
                sensorConfig);
            deviceConfig.getSensorCodeConfigs().put(sensorConfig.getSensorType() + "_" + sensorConfig.getCode(),
                sensorConfig);
            // }

            if (log.isDebugEnabled()) {
                if (type > 0) {
                    configedCount++;
                    buf.append("第").append(sensorIndex).append("个槽位type=").append(sensorConfig.getType())
                        .append("，code=").append(sensorConfig.getCode()).append("，name=").append(sensorConfig.getName())
                        .append("，unit=").append(sensorConfig.getUnit()).append("\n");
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("读取传感器配置信息结束，sn={}，msgId={}，共配置{}个槽位，配置信息为{}", sn, Integer.toHexString(msgId), configedCount,
                    buf.toString());
        }

        HisDeviceConfig.putConfig(deviceType, sn, deviceConfig);

        return deviceConfig;
    }

    // 0x5D-获取传感器报警阈值响应。
    // Byte0-Byte1：传感器数量，uint16_t
    //
    // Byte2-Byte23：传感器序列号，按照结构SENS_ALARM_THRESH_t依次排列
    //
    // typedef struct
    // {
    // uint8_t sen_type; //1-TIC传感器，2-CWA传感器
    // uint8_t sen_index; //传感器索引号，TIC传感器范围0-17，CWA传感器范围0-6
    // SENSOR_ALARM_THRESH_t sen_thresh;
    // }SENS_ALARM_THRESH_t；
    //
    // typedef struct
    // {
    // float thresh_hi;
    // float thresh_lo;
    // float thresh_stel;
    // float thresh_twa;
    // float thresh_mac;
    // }SENSOR_ALARM_THRESH_t;
    //
    // 说明：对于CWA传感器，SENSOR_ALARM_THRESH_t结构中仅thresh_hi字段表示阈值，其余字段保留。
    static DeviceConfig getFcbr100mAlarmConfig(int startPos, AbstractDevice deviceRawData, byte[] effectiveData,
        int msgId) throws Exception {
        String deviceType = deviceRawData.getDeviceType();
        String sn = deviceRawData.getSn();

        if (log.isDebugEnabled()) {
            log.debug("开始读取传感器报警阈值信息，sn={}，msgId={}", sn, Integer.toHexString(msgId));
        }

        // 传感器配置信息的优先级最高，因此，在解析报警阈值时，一定存在设备的配置信息。
        DeviceConfig deviceConfig = HisDeviceConfig.getConfig(deviceType, sn);
        if (deviceConfig == null) {
            return null;
        }

        StringBuilder buf = new StringBuilder("");
        int configedCount = 0;
        // 本来每组数据长度为22，但解析时让startPos偏移了2个字节，因此，这里为20。
        int eachConfigLen = 20;

        int sensorCount = NumberUtil.bytes2Int(effectiveData, startPos, 2, false);
        startPos += 2;

        for (byte i = 0; i < sensorCount; i++) {
            byte sensorType = effectiveData[startPos + i * eachConfigLen];
            byte sensorIndex = effectiveData[startPos + 1 + i * eachConfigLen];

            startPos += 2;

            Map<String, Float> currentParam = new HashMap<>();
            if (sensorType == SensorTypeDictDef.SENSOR_TIC) {
                float highParam =
                    NumberUtil.bytes2Float(effectiveData, startPos + (4 * 0) + i * eachConfigLen, 4, false);
                float lowParam =
                    NumberUtil.bytes2Float(effectiveData, startPos + (4 * 1) + i * eachConfigLen, 4, false);
                float stelParam =
                    NumberUtil.bytes2Float(effectiveData, startPos + (4 * 2) + i * eachConfigLen, 4, false);
                float twaParam =
                    NumberUtil.bytes2Float(effectiveData, startPos + (4 * 3) + i * eachConfigLen, 4, false);
                float macParam =
                    NumberUtil.bytes2Float(effectiveData, startPos + (4 * 4) + i * eachConfigLen, 4, false);

                currentParam.put(SensorTypeDictDef.PARAM_VALUE_HIGH, highParam);
                currentParam.put(SensorTypeDictDef.PARAM_VALUE_LOW, lowParam);
                currentParam.put(SensorTypeDictDef.PARAM_VALUE_STEL, stelParam);
                currentParam.put(SensorTypeDictDef.PARAM_VALUE_TWA, twaParam);
                currentParam.put(SensorTypeDictDef.PARAM_VALUE_MAC, macParam);
            } else {
                float highParam =
                    NumberUtil.bytes2Float(effectiveData, startPos + (4 * 0) + i * eachConfigLen, 4, false);

                currentParam.put(SensorTypeDictDef.PARAM_VALUE_HIGH, highParam);
            }

            deviceConfig.getCurrentParams().put(sensorType + "_" + sensorIndex, currentParam);

            if (log.isDebugEnabled()) {
                configedCount++;

                buf.append("第").append(sensorIndex).append("个槽位读取到传感器报警阈值").append("\n");
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("读取传感器报警阈值结束，sn={}，msgId={}，共读取到{}个槽位，具体信息为{}", sn, Integer.toHexString(msgId), configedCount,
                    buf.toString());
        }

        return deviceConfig;
    }

    // 0x61-获取传感器报警阈值场景缺省值响应。
    // Byte0-Byte1：传感器数量，uint16_t
    //
    // Byte2-Byte24：传感器序列号，按照结构SENS_ALARM_THRESH_DEF_t依次排列
    //
    // typedef struct
    // {
    // uint8_t sen_type; //1-TIC传感器，2-CWA传感器
    // uint8_t sen_index; //传感器索引号，TIC传感器范围0-17，CWA传感器范围0-6
    // uint8_t sen_work_type; //场景代码，0-工作场景 1-公共场景
    // SENSOR_ALARM_THRESH_t sen_thresh;
    // }SENS_ALARM_THRESH_DEF_t；
    //
    // typedef struct
    // {
    // float thresh_hi;
    // float thresh_lo;
    // float thresh_stel;
    // float thresh_twa;
    // float thresh_mac;
    // }SENSOR_ALARM_THRESH_t;
    //
    // 说明：对于CWA传感器，SENSOR_ALARM_THRESH_t结构中仅thresh_hi字段表示阈值，其余字段保留。
    static DeviceConfig getFcbr100mAlarmSceneConfig(int startPos, AbstractDevice deviceRawData, byte[] effectiveData,
        int msgId) throws Exception {
        String deviceType = deviceRawData.getDeviceType();
        String sn = deviceRawData.getSn();

        if (log.isDebugEnabled()) {
            log.debug("开始读取报警阈值场景缺省值信息，sn={}，msgId={}", sn, Integer.toHexString(msgId));
        }

        // 传感器配置信息的优先级最高，因此，在解析报警阈值场景缺省值时，一定存在设备的配置信息。
        DeviceConfig deviceConfig = HisDeviceConfig.getConfig(deviceType, sn);
        if (deviceConfig == null) {
            return null;
        }

        StringBuilder buf = new StringBuilder("");
        int configedCount = 0;
        // 本来每组数据长度为23，但解析时让startPos偏移了3个字节，因此，这里为20。
        int eachConfigLen = 20;

        int sensorCount = NumberUtil.bytes2Int(effectiveData, startPos, 2, false);
        startPos += 2;

        for (byte i = 0; i < sensorCount; i++) {
            byte sensorType = effectiveData[startPos + i * eachConfigLen];
            byte sensorIndex = effectiveData[startPos + 1 + i * eachConfigLen];
            byte sceneType = effectiveData[startPos + 2 + i * eachConfigLen];

            startPos += 3;

            Map<String, Float> currentParam = new HashMap<>();
            if (sensorType == SensorTypeDictDef.SENSOR_TIC) {
                float highParam =
                    NumberUtil.bytes2Float(effectiveData, startPos + (4 * 0) + i * eachConfigLen, 4, false);
                float lowParam =
                    NumberUtil.bytes2Float(effectiveData, startPos + (4 * 1) + i * eachConfigLen, 4, false);
                float stelParam =
                    NumberUtil.bytes2Float(effectiveData, startPos + (4 * 2) + i * eachConfigLen, 4, false);
                float twaParam =
                    NumberUtil.bytes2Float(effectiveData, startPos + (4 * 3) + i * eachConfigLen, 4, false);
                float macParam =
                    NumberUtil.bytes2Float(effectiveData, startPos + (4 * 4) + i * eachConfigLen, 4, false);

                currentParam.put(SensorTypeDictDef.PARAM_VALUE_HIGH, highParam);
                currentParam.put(SensorTypeDictDef.PARAM_VALUE_LOW, lowParam);
                currentParam.put(SensorTypeDictDef.PARAM_VALUE_STEL, stelParam);
                currentParam.put(SensorTypeDictDef.PARAM_VALUE_TWA, twaParam);
                currentParam.put(SensorTypeDictDef.PARAM_VALUE_MAC, macParam);
            } else {
                float highParam =
                    NumberUtil.bytes2Float(effectiveData, startPos + (4 * 0) + i * eachConfigLen, 4, false);

                currentParam.put(SensorTypeDictDef.PARAM_VALUE_HIGH, highParam);
            }

            if (sceneType == SensorTypeDictDef.SCENE_WORK) {
                deviceConfig.getDefault1Params().put(sensorType + "_" + sensorIndex, currentParam);
            } else {
                deviceConfig.getDefault2Params().put(sensorType + "_" + sensorIndex, currentParam);
            }

            if (log.isDebugEnabled()) {
                configedCount++;

                buf.append("第").append(sensorIndex).append("个槽位读取到传感器报警阈值场景缺省值").append("\n");
            }

            if (log.isDebugEnabled()) {
                log.debug("读取传感器报警阈值场景缺省值结束，sn={}，msgId={}，共读取到{}个槽位，具体信息为{}", sn, Integer.toHexString(msgId),
                        configedCount, buf.toString());
            }
        }

        return deviceConfig;
    }

    // 0x67-获取设备IP地址响应。
    // Byte0-Byte3：IP地址；
    // Byte4-Byte7：IP地址掩码；
    // Byte8-Byte11：网关地址；
    //
    // typedef struct
    // {
    // uint8_t ip_addr[4]; //IP地址
    // uint8_t ip_mask[4]; //IP地址掩码
    // uint8_t gateway_addr[4]; //网关地址
    // }IP_ADDR_t；
    //
    // 说明：
    // IP地址：192.168.1.100，ip_addr[0]=192，ip_addr[1]=168，ip_addr[2]=1，ip_addr[3]=100；
    static DeviceConfig getFcbr100mIpAddrConfig(int startPos, AbstractDevice deviceRawData, byte[] effectiveData,
        int msgId) throws Exception {
        String deviceType = deviceRawData.getDeviceType();
        String sn = deviceRawData.getSn();

        if (log.isDebugEnabled()) {
            log.debug("开始读取IP地址信息，sn={}，msgId={}", sn, Integer.toHexString(msgId));
        }

        // 传感器配置信息的优先级最高，因此，在解析IP地址信息时，一定存在设备的配置信息。
        DeviceConfig deviceConfig = HisDeviceConfig.getConfig(deviceType, sn);
        if (deviceConfig == null) {
            return null;
        }

        String ipAddr = NumberUtil.byte2Int(effectiveData[startPos]) + "."
            + NumberUtil.byte2Int(effectiveData[startPos + 1]) + "." + NumberUtil.byte2Int(effectiveData[startPos + 2])
            + "." + NumberUtil.byte2Int(effectiveData[startPos + 3]);

        String ipMask = NumberUtil.byte2Int(effectiveData[startPos + 4]) + "."
            + NumberUtil.byte2Int(effectiveData[startPos + 5]) + "." + NumberUtil.byte2Int(effectiveData[startPos + 6])
            + "." + NumberUtil.byte2Int(effectiveData[startPos + 7]);

        String ipGateway = NumberUtil.byte2Int(effectiveData[startPos + 8]) + "."
            + NumberUtil.byte2Int(effectiveData[startPos + 9]) + "." + NumberUtil.byte2Int(effectiveData[startPos + 10])
            + "." + NumberUtil.byte2Int(effectiveData[startPos + 11]);

        deviceConfig.setIpAddr(ipAddr);
        deviceConfig.setIpMask(ipMask);
        deviceConfig.setIpGateway(ipGateway);

        return deviceConfig;
    }

    // 0x6B-获取网络服务参数响应。
    // typedef struct
    // {
    // uint8_t server_type; //1-TCP Server,2-UDP Server
    // uint8_t reserve; //保留
    // uint16_t server_port; //服务端口号
    // }NET_SERVER_PARA_t；
    static DeviceConfig getFcbr100mNetServConfig(int startPos, AbstractDevice deviceRawData, byte[] effectiveData,
        int msgId) throws Exception {
        String deviceType = deviceRawData.getDeviceType();
        String sn = deviceRawData.getSn();

        if (log.isDebugEnabled()) {
            log.debug("开始读取网络服务参数信息，sn={}，msgId={}", sn, Integer.toHexString(msgId));
        }

        // 传感器配置信息的优先级最高，因此，在解析网络服务参数信息时，一定存在设备的配置信息。
        DeviceConfig deviceConfig = HisDeviceConfig.getConfig(deviceType, sn);
        if (deviceConfig == null) {
            return null;
        }

        byte serverType = effectiveData[startPos];
        int serverPort = NumberUtil.bytes2Int(effectiveData, startPos + 2, 2, false);
        deviceConfig.setNetServType(serverType);
        deviceConfig.setNetServPort(serverPort);
        // 根据协议约定，1-TCP，2-UDP。
        if (serverType == 1) {
            deviceConfig.setNetServName("TCP," + serverPort);
        } else {
            deviceConfig.setNetServName("UDP," + serverPort);
        }

        return deviceConfig;
    }

    // 0x6F-获取设备场景响应。
    // Byte0：场景代码，0-工作场景 1-公共场景
    static DeviceConfig getFcbr100mSceneTypeConfig(int startPos, AbstractDevice deviceRawData, byte[] effectiveData,
        int msgId) throws Exception {
        String deviceType = deviceRawData.getDeviceType();
        String sn = deviceRawData.getSn();

        if (log.isDebugEnabled()) {
            log.debug("开始读取设备场景信息，sn={}，msgId={}", sn, Integer.toHexString(msgId));
        }

        // 传感器配置信息的优先级最高，因此，在解析设备场景时，一定存在设备的配置信息。
        DeviceConfig deviceConfig = HisDeviceConfig.getConfig(deviceType, sn);
        if (deviceConfig == null) {
            return null;
        }

        byte sceneType = effectiveData[startPos];
        deviceConfig.setSceneType(sceneType);

        return deviceConfig;
    }

    // 0x79-获取模块开关状态响应。
    // Byte 0：共8位，项目中只用0-待机，127-开机。
    // 第0位-TIC传感器，第1位-CWA传感器，第2位-4G模块，第3位-烟感模块，第4位-气象模块，
    // 第5位-气泵模块，第6位-LCD模块，其余保留。每1位0表示关闭，1表示开启。
    // 示例如下：
    // 0（0000 0000）：全部都关闭，断电状态
    // 1（0000 0001）：TIC开启
    // 2（0000 0010）：CWA开启
    // 3（0000 0011）：CWA/TIC开启
    // 127（0111 1111）：全部开启
    static DeviceConfig getFcbr100mModCtrlStateConfig(int startPos, AbstractDevice deviceRawData, byte[] effectiveData,
        int msgId) throws Exception {
        String deviceType = deviceRawData.getDeviceType();
        String sn = deviceRawData.getSn();

        if (log.isDebugEnabled()) {
            log.debug("开始读取模块开关状态信息，sn={}，msgId={}", sn, Integer.toHexString(msgId));
        }

        // 传感器配置信息的优先级最高，因此，在解析设备场景时，一定存在设备的配置信息。
        DeviceConfig deviceConfig = HisDeviceConfig.getConfig(deviceType, sn);
        if (deviceConfig == null) {
            return null;
        }

        byte modCtrlState = effectiveData[startPos];
        deviceConfig.setModCtrlState(modCtrlState);

        return deviceConfig;
    }

    /**
     * 0x41-0x26，0x41-0x91：传感器信息读取响应。 获取传感器的信息，填充好单位和名称等信息。
     * 
     * @param sensorData
     *            已有的传感器数据。
     * @param sensorType
     *            1-TIC（TVOC）传感器，2-CWA传感器。
     * @return 填充好单位的传感器数据。
     */
    static SensorData getFcbr100mSensorData(byte[] sensorData, int sensorType, String deviceType, String sn) {
        if (sensorData == null || sensorData.length < 7) {
            return null;
        }

        SensorData modSensorData = new SensorData();
        modSensorData.setSensorSeq(sensorData[0]);
        modSensorData.setSensorType((byte)sensorType);

        byte[] sensorStates = NumberUtil.getBits(sensorData[1], false);
        modSensorData.setInstallState(sensorStates[0]);
        modSensorData.setRunstate(sensorStates[1]);
        modSensorData.setFaultState(sensorStates[2]);

        byte[] alarmStates = NumberUtil.getBits(sensorData[2], false);
        // 预告警。
        if (alarmStates[5] == 1) {
            modSensorData.setAlarmState(AlarmTypeDictDef.STATE_PREALARM);
        }
        // 低报。
        if (alarmStates[0] == 1) {
            modSensorData.addAlarmTypeLevel(SensorTypeDictDef.PARAM_VALUE_LOW);
            modSensorData.setAlarmState(AlarmTypeDictDef.STATE_ALARM_L);
        }
        if (alarmStates[2] == 1) {
            modSensorData.addAlarmTypeLevel(SensorTypeDictDef.PARAM_VALUE_STEL);
            modSensorData.setAlarmState(AlarmTypeDictDef.STATE_ALARM_L);
        }
        if (alarmStates[3] == 1) {
            modSensorData.addAlarmTypeLevel(SensorTypeDictDef.PARAM_VALUE_TWA);
            modSensorData.setAlarmState(AlarmTypeDictDef.STATE_ALARM_L);
        }
        if (alarmStates[4] == 1) {
            modSensorData.addAlarmTypeLevel(SensorTypeDictDef.PARAM_VALUE_MAC);
            modSensorData.setAlarmState(AlarmTypeDictDef.STATE_ALARM_L);
        }
        // 高报。
        if (alarmStates[1] == 1) {
            modSensorData.addAlarmTypeLevel(SensorTypeDictDef.PARAM_VALUE_HIGH);
            modSensorData.setAlarmState(AlarmTypeDictDef.STATE_ALARM_H);
        }

        // 根据协议约定，TIC需要进行转换，CWA直接获取读数。
        if (sensorType == SensorTypeDictDef.SENSOR_TIC) {
            int originReading = NumberUtil.bytes2Int(sensorData, 3, 2, false);
            byte overflow = sensorData[5];
            byte dotLength = sensorData[6];
            modSensorData.setOriginReading(originReading);
            modSensorData.setDotLength(dotLength);
            if (dotLength > 0) {
                modSensorData
                    .setValue(NumberUtil.getFloatPrecise(originReading / (float)Math.pow(10, dotLength), dotLength));
            } else {
                modSensorData.setValue(originReading);
            }
        } else {
            modSensorData.setValue(NumberUtil.bytes2Float(sensorData, 3, 4, false));
        }
        modSensorData.setOriginValue(modSensorData.getValue());

        // 都格式化为两位小数。
        modSensorData.setValue(NumberUtil.getFloatPrecise(modSensorData.getValue(), 2));
        modSensorData.setOriginValue(NumberUtil.getFloatPrecise(modSensorData.getOriginValue(), 2));

        modSensorData.setBground(modSensorData.getValue());

        // 填充传感器名称，单位等信息。
        if (HisDeviceConfig.containsConfig(deviceType, sn)) {
            DeviceConfig deviceConfig = HisDeviceConfig.getConfig(deviceType, sn);
            SensorConfig sensorConfig =
                deviceConfig.getSensorSeqConfigs().get(sensorType + "_" + modSensorData.getSensorSeq());
            if (sensorConfig != null) {
                modSensorData.setType(sensorConfig.getType());
                modSensorData.setCode(sensorConfig.getCode());
                modSensorData.setName(sensorConfig.getName());
                modSensorData.setOriginUnit(sensorConfig.getOriginUnit());
                modSensorData.setUnit(sensorConfig.getUnit());
                modSensorData.setUnitName(sensorConfig.getUnitName());
                modSensorData.setMolecularWeight(sensorConfig.getMolecularWeight());
                modSensorData.setFullScall(sensorConfig.getFullScall());
                modSensorData.setSensibility(sensorConfig.getSensibility());
                modSensorData.setDisplayOrder(sensorConfig.getDisplayOrder());
            } else {
                log.error("设备型号{}，设备号{}的传感器{}配置信息为空，所有数据按0处理", deviceType, sn, modSensorData.getSensorSeq());
            }
        }

        return modSensorData;
    }
}
