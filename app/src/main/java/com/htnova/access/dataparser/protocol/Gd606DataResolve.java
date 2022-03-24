package com.htnova.access.dataparser.protocol;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.commons.utils.NumberUtil;
import com.htnova.access.pojo.dto.DeviceConfig;
import com.htnova.access.pojo.dto.ModGd606Dto;
import com.htnova.access.pojo.dto.SensorConfig;
import com.htnova.access.pojo.dto.SensorData;
import com.htnova.access.sysconfig.constdef.ProductDef;
import com.htnova.access.sysconfig.constdef.UnitDictDef;

/** 说明：GD606数据解析公共实现。 */
class Gd606DataResolve {
    private static Logger log = LoggerFactory.getLogger(Gd606DataResolve.class);

    // 0x00：读取设备信息，MSG_GET_SYS_INFO。
    // Byte 0：硬件主版本号，uint8_t
    // Byte 1: 硬件次版本号，uint8_t
    // Byte 2：软件主版本号，uint8_t
    // Byte 3: 软件次版本号，uint8_t
    // Byte 4~23: 设备序列号，字符串
    // Byte 24~27:用户ID
    // Byte 28~ : 传感器信息，6个槽位的传感器按照结构SENS_INFO_t依次排列
    // Typedef struct
    // {
    // Uint16_t type; //类型编码见下表，type为0表示传感器不在位
    // Uint16_t ver; //高字节主版本号，低字节次版本号
    // Char name[20];
    // Uint8_t unit; //单位，0-PPM，1-vol%，2-LEL%
    // Uint8_t reserv;
    // Uint16_t mw; //分子量
    // Int full_scale;
    // Float sensibility;
    // }SENS_INFO_t;
    static DeviceConfig getGd606SensorConfig(int startPos, AbstractDevice deviceRawData, byte[] effectiveData,
        int msgId) throws Exception {
        String deviceType = deviceRawData.getDeviceType();
        String sn = deviceRawData.getSn();

        if (log.isDebugEnabled()) {
            log.debug("开始读取配置信息，sn={}，msgId={}", sn, Integer.toHexString(msgId));
        }

        DeviceConfig deviceConfig = HisDeviceConfig.getConfig(deviceType, sn);
        if (deviceConfig == null) {
            deviceConfig = new DeviceConfig();
        }
        deviceConfig.setDeviceType(deviceType);
        deviceConfig.setSn(sn);

        StringBuilder buf = new StringBuilder("");
        int configedCount = 0;
        int eachConfigLen = 36;

        for (byte i = 0; i < 6; i++) {
            SensorConfig sensorConfig = new SensorConfig();
            sensorConfig.setSensorSeq(i);

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
                    buf.append("第").append(i).append("个槽位type=").append(sensorConfig.getType()).append("，code=")
                        .append(sensorConfig.getCode()).append("，name=").append(sensorConfig.getName()).append("，unit=")
                        .append(sensorConfig.getUnit()).append("\n");
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("读取配置信息结束，sn={}，msgId={}，共配置{}个槽位，配置信息为{}", sn, Integer.toHexString(msgId), configedCount,
                    buf.toString());
        }

        HisDeviceConfig.putConfig(deviceType, sn, deviceConfig);

        return deviceConfig;
    }

    // 0xFE：实时数据上报，MSG_DATA_REPORT。
    // Byte 0：安装状态，每BIT指示一个传感器，0--未安装 1--安装，BIT0指示传感器0，BIT5指示传感器5
    // Byte 1：运行状态，每BIT指示一个传感器，0--停止 1--运行, BIT0指示传感器0，BIT5指示传感器5
    // Byte 2：故障状态，每BIT指示一个传感器，0--无故障 1--故障，BIT0指示传感器0，BIT5指示传感器5
    // Byte 3：报警状态，每BIT指示一个传感器，0--无报警 1--报警，BIT0指示传感器0，BIT5指示传感器5
    // Byte 4：温度，单位摄氏度
    // Byte 5：相对湿度，百分比
    // Byte 6：泵安装状态，0-未安装 1-安装
    // Byte 7：泵运行状态，0-停止 1-开启 2-故障。
    // Byte 8~11：传感器0数据,结构SENS_DATA_t;
    // Byte 12~15：传感器1数据,结构SENS_DATA_t;
    // Byte 16~19：传感器2数据,结构SENS_DATA_t;
    // Byte 20~23：传感器3数据,结构SENS_DATA_t;
    // Byte 24~27：传感器4数据,结构SENS_DATA_t;
    // Byte 28~31：传感器5数据,结构SENS_DATA_t;
    // typedef struct
    // {
    // uint16_t data;
    // uint8_t overflow;
    // uint8_t decimal_len; //小数点后位数
    // }SENS_DATA_t;
    static ModGd606Dto getGd606SensorData(int startPos, AbstractDevice deviceRawData, byte[] effectiveData, int msgId)
        throws Exception {
        String deviceType = deviceRawData.getDeviceType();
        String sn = deviceRawData.getSn();

        if (log.isDebugEnabled()) {
            log.debug("开始读取传感器数据，sn={}，msgId={}", sn, Integer.toHexString(msgId));
        }

        // 初始化整个GD606设备的数据。
        ModGd606Dto modGd606Dto = new ModGd606Dto();
        modGd606Dto.setDataType(3);
        modGd606Dto.setRecrodTime(System.currentTimeMillis());

        // 0-安装状态。
        byte installState = effectiveData[startPos];
        byte[] installStates = NumberUtil.getBits(installState, false);

        // 1-运行状态。
        byte runstate = effectiveData[startPos + 1];
        byte[] runstates = NumberUtil.getBits(runstate, false);

        // 2-故障状态。
        byte faultState = effectiveData[startPos + 2];
        byte[] faultStates = NumberUtil.getBits(faultState, false);

        // 3-报警状态。
        byte alarmState = effectiveData[startPos + 3];
        byte[] alarmStates = NumberUtil.getBits(alarmState, false);

        // 4-温度。
        modGd606Dto.setTemp(effectiveData[startPos + 4]);

        // 5-相对湿度。
        modGd606Dto.setHumidity(effectiveData[startPos + 5]);

        // 6-泵安装状态。
        modGd606Dto.setPumpInstallState(effectiveData[startPos + 6]);

        // 7-泵运行状态。
        modGd606Dto.setPumpRunstate(effectiveData[startPos + 7]);

        // 最多6组传感器数据，每组4字节，数据结构为SENS_DATA_t。
        List<SensorData> sensors = new ArrayList<>();
        int eachSensorLen = 4;

        for (byte i = 0; i < 6; i++) {
            byte[] currSensor = new byte[] {effectiveData[startPos + 8 + i * eachSensorLen],
                effectiveData[startPos + 8 + i * eachSensorLen + 1],
                effectiveData[startPos + 8 + i * eachSensorLen + 2],
                effectiveData[startPos + 8 + i * eachSensorLen + 3]};
            SensorData sensor =
                parseModGd606Sensor(i, installStates, runstates, faultStates, alarmStates, currSensor, deviceType, sn);
            if (sensor != null) {
                sensors.add(sensor);
            }
        }
        modGd606Dto.setSensors(sensors);

        if (log.isDebugEnabled()) {
            StringBuilder basicBuf = new StringBuilder();
            basicBuf.append("temp=").append(modGd606Dto.getTemp()).append("，humidity=")
                .append(modGd606Dto.getHumidity()).append("，pumpInstallState=")
                .append(modGd606Dto.getPumpInstallState()).append("，pumpRunstate=")
                .append(modGd606Dto.getPumpRunstate()).append("\n");

            StringBuilder sensorBuf = new StringBuilder();
            sensorBuf.append("共").append(sensors.size()).append("个传感器数据：\n");

            for (int i = 0; i < sensors.size(); i++) {
                SensorData sensor = sensors.get(i);
                sensorBuf.append("第").append(sensor.getSensorSeq()).append("个传感器，installState=")
                    .append(sensor.getInstallState()).append("，runstate=").append(sensor.getRunstate())
                    .append("，faultState=").append(sensor.getFaultState()).append("，alarmState=")
                    .append(sensor.getAlarmState()).append("，originReading=").append(sensor.getOriginReading())
                    .append("，dotLength=").append(sensor.getDotLength()).append("，value=").append(sensor.getValue())
                    .append("，type=").append(sensor.getType()).append("，code=").append(sensor.getCode())
                    .append("，name=").append(sensor.getName()).append("，unit=").append(sensor.getUnit())
                    .append("，molecularWeight=").append(sensor.getMolecularWeight()).append("，fullScall=")
                    .append(sensor.getFullScall()).append("，sensibility=").append(sensor.getSensibility()).append("\n");
            }

            log.debug("读取传感器数据结束，sn={}，msgId={}，基本信息：{}，传感器信息：{}", sn, Integer.toHexString(msgId), basicBuf.toString(),
                    sensorBuf.toString());
        }

        return modGd606Dto;
    }

    private static SensorData parseModGd606Sensor(byte currSeq, byte[] installStates, byte[] runstates,
        byte[] faultStates, byte[] alarmStates, byte[] sensorData, String deviceType, String sn) {
        // 协议约定，安装状态为0，则不含该传感器，直接返回。
        if (installStates[currSeq] == 0) {
            return null;
        }

        SensorData modSensorData = new SensorData();
        modSensorData.setSensorSeq(currSeq);
        modSensorData.setInstallState(installStates[currSeq]);
        modSensorData.setRunstate(runstates[currSeq]);
        modSensorData.setFaultState(faultStates[currSeq]);
        modSensorData.setAlarmState(alarmStates[currSeq]);

        int originReading = NumberUtil.bytes2Int(sensorData, 0, 2, false);
        byte dotLength = sensorData[3];
        modSensorData.setOriginReading(originReading);
        modSensorData.setDotLength(dotLength);

        if (dotLength > 0) {
            modSensorData
                .setValue(NumberUtil.getFloatPrecise(originReading / (float)Math.pow(10, dotLength), dotLength));
        } else {
            modSensorData.setValue(originReading);
        }
        modSensorData.setOriginValue(modSensorData.getValue());

        // 都格式化为两位小数。
        modSensorData.setValue(NumberUtil.getFloatPrecise(modSensorData.getValue(), 2));
        modSensorData.setOriginValue(NumberUtil.getFloatPrecise(modSensorData.getOriginValue(), 2));

        modSensorData.setBground(modSensorData.getValue());

        if (HisDeviceConfig.containsConfig(deviceType, sn)) {
            DeviceConfig deviceConfig = HisDeviceConfig.getConfig(deviceType, sn);

            // 此处暂时这样处理：GD606原有逻辑是只要有一个传感器配置获取不到，则需要重新清除配置，重新获取配置。
            // 但白盒子中使用的GM606模块，因为在测试阶段没有完全安装，导致部分配置获取不到频繁发送获取配置，与设备端形成了死循环。
            boolean isClearConfig = false;

            if (ProductDef.DEVICE_TYPE_GD606.equals(deviceType)) {
                // GD606模块非常灵活，随时可能会换模块，因此需要灵活更新配置。
                // 无配置或某个传感器配置为空，都需要重新清除配置，然后再次获取配置。
                isClearConfig = (deviceConfig == null) || (deviceConfig.getSensorSeqConfigs()
                    .get(modSensorData.getSensorType() + "_" + modSensorData.getSensorSeq()) == null);
            } else {
                // 其它的设备将GM606作为模块使用，一般设备确定了，模块本身和位置不会变化。
                // 所以只要有配置就行，不需要单独判断某个模块是否有配置。
                isClearConfig = (deviceConfig == null);
            }

            if (isClearConfig) {
                HisDeviceConfig.clearConfig(deviceType, sn);
            } else {
                SensorConfig sensorConfig = deviceConfig.getSensorSeqConfigs()
                    .get(modSensorData.getSensorType() + "_" + modSensorData.getSensorSeq());

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
                    log.error("设备型号{}，设备号{}的传感器{}配置信息为空，所有数据按0处理", deviceType, sn, currSeq);
                }
            }
        }

        return modSensorData;
    }
}
