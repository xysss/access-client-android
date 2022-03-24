package com.htnova.access.socket;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import com.htnova.access.commons.pojo.AbstractMod;
import com.htnova.access.commons.utils.NumberUtil;
import com.htnova.access.commons.utils.RandomUtil;
import com.htnova.access.dataparser.protocol.ProtocolService;
import com.htnova.access.dataparser.utils.ProtocolUtil;
import com.htnova.access.pojo.dto.*;
import com.htnova.access.sysconfig.constdef.AlarmDef;
import com.htnova.access.sysconfig.constdef.RunStateDef;
import com.htnova.access.sysconfig.constdef.SensorTypeDictDef;
import com.htnova.access.sysconfig.constdef.UnitDictDef;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 *     与FCBR-100M相关的业务逻辑。
 *     FCBR-100M有很多特殊的控制及业务逻辑，因此单独设定Service来处理。
 * </pre>
 */
@Slf4j
public class Fcbr100mService {
    private DeviceControlService deviceControlService = DeviceControlService.getInstance();
    private static Fcbr100mService instance;

    private Fcbr100mService(){

    }

    public static Fcbr100mService getInstance(){
        if(instance == null){
            instance = new Fcbr100mService();
        }
        return instance;
    }

    /**
     * <pre>
     *     FCBR-100M每10秒一条心跳，收到心跳时，判断配置是否已经获取完成。
     *     如未获取到，则重新发送获取指令。
     *     因为都是异步操作，一次不应发送过多指令，因此一条条逐步发送。
     * </pre>
     * 
     * @param deviceType
     *            设备型号。
     * @param sn
     *            设备号。
     * @param isForceSend
     *            是否强制全部刷新。
     */
    public void sendDeviceInfoReq(String deviceType, String sn, boolean isForceSend) {
        // 强制刷新获取所有的配置。
        if (isForceSend) {
            // 先设置一个较长的时间，这样避免实时数据发送，导致配置收发异常。
            // 为增加成功的概率，连续发送三次。同时也等待实时数据不再发送后再进行后面的操作。
            boolean isNofitySuccess = sendSetDataNotifyIntervalReq(deviceType, sn, 3600);
            if (!isNofitySuccess) {
                isNofitySuccess = sendSetDataNotifyIntervalReq(deviceType, sn, 3600);
            }
            if (!isNofitySuccess) {
                sendSetDataNotifyIntervalReq(deviceType, sn, 3600);
            }

            sendToDevice(deviceType, sn, (byte)0x56, null, "发送获取传感器配置指令(0x56)");

            sendToDevice(deviceType, sn, (byte)0x02, null, "发送获取设备基本信息指令(0x02)");

            sendToDevice(deviceType, sn, (byte)0x5C, null, "发送获取传感器报警阈值指令(0x5C)");

            sendToDevice(deviceType, sn, (byte)0x66, null, "发送获取IP地址指令(0x66)");

            sendToDevice(deviceType, sn, (byte)0x52, null, "发送获取设备时间指令(0x52)");

            Map<String, Object> params = new HashMap<>();
            params.put("sceneType", SensorTypeDictDef.SCENE_WORK);
            sendToDevice(deviceType, sn, (byte)0x60, params, "发送获取传感器报警阈值场景缺省值指令(0x60-0)");

            sendToDevice(deviceType, sn, (byte)0x6E, null, "发送获取设备场景指令(0x6E)");

            sendToDevice(deviceType, sn, (byte)0x6A, null, "发送获取网络服务参数指令(0x6A)");

            params = new HashMap<>();
            params.put("sceneType", SensorTypeDictDef.SCENE_PUBLIC);
            sendToDevice(deviceType, sn, (byte)0x60, params, "发送获取传感器报警阈值场景缺省值指令(0x60-1)");

            sendToDevice(deviceType, sn, (byte)0x78, null, "发送获取模块开关状态指令(0x78)");

            // 手工获取参数时，校准一遍时间。
            sendSetTimeReq(deviceType, sn, System.currentTimeMillis());

            // 将通知时间设置回2秒一条。为增加成功的概率，连续发送三次。
            isNofitySuccess = sendSetDataNotifyIntervalReq(deviceType, sn, 2);
            if (!isNofitySuccess) {
                sendSetDataNotifyIntervalReq(deviceType, sn, 2);
            }
            if (!isNofitySuccess) {
                sendSetDataNotifyIntervalReq(deviceType, sn, 2);
            }
        } else {
            // 如果有设备配置信息，其它信息没有，则也支持，此时允许实时数据收发操作。
            // 其它配置可以逐渐得到，这样不影响实时数据的接收。
            DeviceConfig deviceConfig = ProtocolService.getConfig(deviceType, sn);
            if (deviceConfig == null || deviceConfig.getSensorSeqConfigs().size() == 0) {
                // 先设置一个较长的时间，这样避免实时数据发送，导致配置收发异常。
                // 为增加成功的概率，连续发送三次。同时也等待实时数据不再发送后再进行后面的操作。
                boolean isNofitySuccess = sendSetDataNotifyIntervalReq(deviceType, sn, 3600);
                if (!isNofitySuccess) {
                    isNofitySuccess = sendSetDataNotifyIntervalReq(deviceType, sn, 3600);
                }
                if (!isNofitySuccess) {
                    sendSetDataNotifyIntervalReq(deviceType, sn, 3600);
                }

                sendToDevice(deviceType, sn, (byte)0x56, null, "发送获取传感器配置指令(0x56)");

                // 开机时，校准一遍时间。
                sendSetTimeReq(deviceType, sn, System.currentTimeMillis());

                // 将通知时间设置回2秒一条。为增加成功的概率，连续发送三次。
                isNofitySuccess = sendSetDataNotifyIntervalReq(deviceType, sn, 2);
                if (!isNofitySuccess) {
                    sendSetDataNotifyIntervalReq(deviceType, sn, 2);
                }
                if (!isNofitySuccess) {
                    sendSetDataNotifyIntervalReq(deviceType, sn, 2);
                }
            } else {
                if (deviceConfig.getCurrentParams().size() == 0) {
                    sendToDevice(deviceType, sn, (byte)0x5C, null, "发送获取传感器报警阈值指令(0x5C)");
                }

                if (deviceConfig.getHwVer() == null) {
                    sendToDevice(deviceType, sn, (byte)0x02, null, "发送获取设备基本信息指令(0x02)");
                }

                if (deviceConfig.getIpAddr() == null) {
                    sendToDevice(deviceType, sn, (byte)0x66, null, "发送获取IP地址指令(0x66)");
                }

                if (deviceConfig.getDefault1Params().size() == 0) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("sceneType", SensorTypeDictDef.SCENE_WORK);
                    sendToDevice(deviceType, sn, (byte)0x60, params, "发送获取传感器报警阈值场景缺省值指令(0x60-0)");
                }

                if (deviceConfig.getDeviceTime() == null) {
                    sendToDevice(deviceType, sn, (byte)0x52, null, "发送获取设备时间指令(0x52)");
                }

                if (deviceConfig.getSceneType() == null) {
                    sendToDevice(deviceType, sn, (byte)0x6E, null, "发送获取设备场景指令(0x6E)");
                }

                if (deviceConfig.getDefault2Params().size() == 0) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("sceneType", SensorTypeDictDef.SCENE_PUBLIC);
                    sendToDevice(deviceType, sn, (byte)0x60, params, "发送获取传感器报警阈值场景缺省值指令(0x60-1)");
                }

                if (deviceConfig.getNetServName() == null) {
                    sendToDevice(deviceType, sn, (byte)0x6A, null, "发送获取网络服务参数指令(0x6A)");
                }

                if (deviceConfig.getModCtrlState() == null) {
                    sendToDevice(deviceType, sn, (byte)0x78, null, "发送获取模块开关状态指令(0x78)");
                }
            }
        }
    }

    private void sendToDevice(String deviceType, String sn, byte msgId, Map<String, Object> params, String msgInfo) {
        try {
            byte[] configReq = ProtocolService.buildReq(msgId, deviceType, sn, params);
            deviceControlService.sendToDevice(deviceType, sn, configReq, 0);
            log.info("{}完成，deviceType={}，sn={}", msgInfo, deviceType, sn);
        } catch (Exception e) {
            log.error("{}异常，deviceType={}，sn={}", msgInfo, deviceType, sn, e);
        }

        try {
            // 这几项要回复的数据较多，预留多一些时间。
            if (msgId == 0x56 || msgId == 0x5C || msgId == 0x60) {
                Thread.sleep(4000);
            } else {
                Thread.sleep(2000);
            }
        } catch (Exception e) {
        }
    }

    /**
     * <pre>
     *     为设备设置通知时间间隔，下发到设备。设置之后，实时数据会按设置后的间隔发送。
     * </pre>
     *
     * @param deviceType
     *            设备型号。
     * @param sn
     *            设备号。
     * @param currInterval
     *            要设置的间隔。
     */
    public boolean sendSetDataNotifyIntervalReq(String deviceType, String sn, Integer currInterval) {
        boolean success = false;

        try {
            // 0x4C-设置设备通知时间间隔请求。
            byte[] crcAndEncodeBytes = ProtocolUtil.buildFrame308v2WithCrcAndEncode(0, (byte)0x05, (byte)0x4C,
                NumberUtil.int2Bytes2(currInterval, false));

            // 由于异步操作和网络的可靠性，结果不具有确定性，只是尽可能获取到响应结果。
            // 1.先设置响应结果为false。
            // 2.再接收响应结果。
            // 3.根据响应结果做处理。
            ProtocolService.setLastInstructResult(deviceType, sn, (byte)0x4D, false);
            deviceControlService.sendToDevice(deviceType, sn, crcAndEncodeBytes, 0);
            try {
                // 此处只是预估一个网络的开销，预估这个时间应该有响应。
                Thread.sleep(2000);
            } catch (Exception e) {
            }
            success = ProtocolService.isLastInstructSuccess(deviceType, sn, (byte)0x4D);

            if (success) {
                log.info("发送设置通知时间间隔请求成功，deviceType={}，sn={}", deviceType, sn);
            } else {
                log.info("发送设置通知时间间隔请求失败，deviceType={}，sn={}", deviceType, sn);
            }
        } catch (Exception e) {
            log.error("发送设置通知时间间隔请求异常，deviceType={}，sn={}", deviceType, sn, e);
        }

        return success;
    }

    /**
     * <pre>
     *     为设备设置场景，下发到设备。
     * </pre>
     * 
     * @param deviceType
     *            设备型号。
     * @param sn
     *            设备号。
     * @param currScene
     *            要设置的场景，0-工作场景，1-公共场景。
     */
    public boolean sendSetSceneTypeReq(String deviceType, String sn, Integer currScene) {
        boolean success = false;

        try {
            // 0x70-设置设备场景请求。
            byte[] crcAndEncodeBytes = ProtocolUtil.buildFrame308v2WithCrcAndEncode(0, (byte)0x05, (byte)0x70,
                new byte[] {currScene.byteValue()});

            // 由于异步操作和网络的可靠性，结果不具有确定性，只是尽可能获取到响应结果。
            // 1.先设置响应结果为false。
            // 2.再接收响应结果。
            // 3.根据响应结果做处理。
            ProtocolService.setLastInstructResult(deviceType, sn, (byte)0x71, false);
            deviceControlService.sendToDevice(deviceType, sn, crcAndEncodeBytes, 0);
            try {
                // 此处只是预估一个网络的开销，预估这个时间应该有响应。
                Thread.sleep(2000);
            } catch (Exception e) {
            }
            success = ProtocolService.isLastInstructSuccess(deviceType, sn, (byte)0x71);

            if (success) {
                // 场景更新到设备配置中。
                DeviceConfig deviceConfig = ProtocolService.getConfig(deviceType, sn);
                if (deviceConfig != null) {
                    deviceConfig.setSceneType(currScene.byteValue());
                }

                log.info("发送设置场景请求成功，deviceType={}，sn={}", deviceType, sn);
            } else {
                log.info("发送设置场景请求失败，deviceType={}，sn={}", deviceType, sn);
            }
        } catch (Exception e) {
            log.error("发送设置场景请求异常，deviceType={}，sn={}", deviceType, sn, e);
        }

        return success;
    }

    /**
     * <pre>
     *     为设备设置时间，下发到设备。
     * </pre>
     *
     * @param deviceType
     *            设备型号。
     * @param sn
     *            设备号。
     * @param currTime
     *            当前时间。
     */
    public boolean sendSetTimeReq(String deviceType, String sn, Long currTime) {
        // 设备端日期格式为BCD码，使用时需要转换。
        // 年份只取后面两位，忽略前面两位，因此需要根据当前的时间补齐。
        Instant instant = Instant.ofEpochMilli(currTime);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Shanghai"));
        int currYearAditional = localDateTime.getYear() / 100 * 100;
        byte year = (byte)(localDateTime.getYear() - currYearAditional);
        byte month = (byte)localDateTime.getMonthValue();
        byte date = (byte)localDateTime.getDayOfMonth();
        byte hour = (byte)localDateTime.getHour();
        byte minute = (byte)localDateTime.getMinute();
        byte second = (byte)localDateTime.getSecond();

        byte yearBcd = NumberUtil.byte2Bcd(year);
        byte monthBcd = NumberUtil.byte2Bcd(month);
        byte dateBcd = NumberUtil.byte2Bcd(date);
        byte hourBcd = NumberUtil.byte2Bcd(hour);
        byte minuteBcd = NumberUtil.byte2Bcd(minute);
        byte secondBcd = NumberUtil.byte2Bcd(second);

        boolean success = false;
        try {
            // 0x50-设置时间请求。
            byte[] crcAndEncodeBytes = ProtocolUtil.buildFrame308v2WithCrcAndEncode(0, (byte)0x05, (byte)0x50,
                new byte[] {yearBcd, monthBcd, dateBcd, hourBcd, minuteBcd, secondBcd});

            // 由于异步操作和网络的可靠性，结果不具有确定性，只是尽可能获取到响应结果。
            // 1.先设置响应结果为false。
            // 2.再接收响应结果。
            // 3.根据响应结果做处理。
            ProtocolService.setLastInstructResult(deviceType, sn, (byte)0x51, false);
            deviceControlService.sendToDevice(deviceType, sn, crcAndEncodeBytes, 0);
            try {
                // 此处只是预估一个网络的开销，预估这个时间应该有响应。
                Thread.sleep(2000);
            } catch (Exception e) {
            }
            success = ProtocolService.isLastInstructSuccess(deviceType, sn, (byte)0x51);

            if (success) {
                log.info("发送时间设置请求成功，deviceType={}，sn={}", deviceType, sn);
            } else {
                log.info("发送时间设置请求失败，deviceType={}，sn={}", deviceType, sn);
            }
        } catch (Exception e) {
            log.error("发送时间设置请求异常，deviceType={}，sn={}", deviceType, sn, e);
        }

        return success;
    }

    /**
     * <pre>
     *     为设备设置模块开关状态，下发到设备。
     * </pre>
     *
     * @param deviceType
     *            设备型号。
     * @param sn
     *            设备号。
     * @param currState
     *            要设置的开关状态，0-待机，127-开机。
     */
    public boolean sendSetModCtrlStateReq(String deviceType, String sn, Integer currState) {
        boolean success = false;

        try {
            // 0x58-设置设备模块开关状态请求。
            byte[] crcAndEncodeBytes = ProtocolUtil.buildFrame308v2WithCrcAndEncode(0, (byte)0x05, (byte)0x58,
                new byte[] {currState.byteValue()});

            // 由于异步操作和网络的可靠性，结果不具有确定性，只是尽可能获取到响应结果。
            // 1.先设置响应结果为false。
            // 2.再接收响应结果。
            // 3.根据响应结果做处理。
            ProtocolService.setLastInstructResult(deviceType, sn, (byte)0x59, false);
            deviceControlService.sendToDevice(deviceType, sn, crcAndEncodeBytes, 0);
            try {
                // 此处只是预估一个网络的开销，预估这个时间应该有响应。
                Thread.sleep(5000);
            } catch (Exception e) {
            }
            success = ProtocolService.isLastInstructSuccess(deviceType, sn, (byte)0x59);

            if (success) {
                // 模块开关状态更新到设备配置中。
                DeviceConfig deviceConfig = ProtocolService.getConfig(deviceType, sn);
                if (deviceConfig != null) {
                    deviceConfig.setModCtrlState(currState.byteValue());
                }

                log.info("发送设置模块开关状态请求成功，deviceType={}，sn={}", deviceType, sn);
            } else {
                log.info("发送设置模块开关状态请求失败，deviceType={}，sn={}", deviceType, sn);
            }
        } catch (Exception e) {
            log.error("发送设置模块开关状态请求异常，deviceType={}，sn={}", deviceType, sn, e);
        }

        return success;
    }

    /**
     * <pre>
     *     从配置中获取设备状态数据。
     *     设备配置在系统启动时读取到内存中，也可以通过手工触发。
     *     当有修改时，需要将最新数据同步更新到设备配置中，以保持为最新的数据。
     *     busiType：1-正常设备状态数据，2-默认设备状态数据。
     * </pre>
     * 
     * @param deviceType
     * @param sn
     * @param busiType
     * @return
     */
    public Map<String, Object> getDeviceState(String deviceType, String sn, Integer busiType) {
        Map<String, Object> map = new HashMap<>();

        // 不存在配置，则采用模拟数据。
        // 调试完成，正式上线时，需要给出异常的提示。
        DeviceConfig deviceConfig = ProtocolService.getConfig(deviceType, sn);
        if (deviceConfig == null || deviceConfig.getSensorSeqConfigs() == null
            || deviceConfig.getSensorSeqConfigs().size() == 0) {
            return getSimDeviceState(deviceType, sn, busiType);
        }

        map.put("deviceType", deviceType);
        map.put("sn", sn);
        map.put("hwVer", deviceConfig.getHwVer());
        map.put("swVer", deviceConfig.getSwVer());
        map.put("ipAddr", deviceConfig.getIpAddr());
        // todo by junzai：这三个值需要WEB设置。
        // 如果不存在，给一个特殊的值，可以通过WEB知道是否设置成功。
        // 设置之后，将当前的值设置为空，下次收到心跳时，会再次获取刚才设置的值。
        map.put("sceneType", (deviceConfig.getSceneType() == null) ? 0 : deviceConfig.getSceneType());
        map.put("deviceTime",
            (deviceConfig.getDeviceTime() == null) ? System.currentTimeMillis() : deviceConfig.getDeviceTime());
        map.put("modCtrlState", (deviceConfig.getModCtrlState() == null) ? 127 : deviceConfig.getModCtrlState());

        // 1-全部状态，2-默认状态，3-简单状态（设置用的几个原始数值）。
        if (busiType == 3) {
            return map;
        }

        List<SensorData> tvocSensors = new ArrayList<>();
        List<SensorData> cwaSensors = new ArrayList<>();
        Map<String, SensorConfig> sensorSeqConfigs = deviceConfig.getSensorSeqConfigs();
        sensorSeqConfigs.forEach((sensorKey, sensorValue) -> {
            byte sensorType;
            if (sensorKey.startsWith("1_")) {
                sensorType = SensorTypeDictDef.SENSOR_TIC;
            } else {
                sensorType = SensorTypeDictDef.SENSOR_CWA;
            }
            SensorData sensorData = new SensorData();
            sensorData.setSensorSeq(sensorValue.getSensorSeq());
            sensorData.setSensorType(sensorType);
            sensorData.setCode(sensorValue.getCode());
            sensorData.setName(sensorValue.getName());
            sensorData.setVersion(sensorValue.getVersion());
            sensorData.setFullScall(sensorValue.getFullScall());
            sensorData.setSensorSn(sensorValue.getSensorSn());
            sensorData.setOriginUnit(sensorValue.getOriginUnit());
            sensorData.setUnit(sensorValue.getUnit());
            sensorData.setCurrentParam(deviceConfig.getCurrentParams().get(sensorKey));
            sensorData.setDefault1Param(deviceConfig.getDefault1Params().get(sensorKey));
            sensorData.setDefault2Param(deviceConfig.getDefault2Params().get(sensorKey));
            if (sensorType == SensorTypeDictDef.SENSOR_TIC) {
                tvocSensors.add(sensorData);
            } else {
                cwaSensors.add(sensorData);
            }
        });

        map.put("tvocSensors", tvocSensors);
        map.put("cwaSensors", cwaSensors);

        int sensorCount = tvocSensors.size() + cwaSensors.size();
        map.put("sensorCount", sensorCount);

        return map;
    }

    /**
     * <pre>
     *     生成模拟设备状态数据。
     *     busiType：1-正常模拟数据，2-默认设备状态数据。
     * </pre>
     * 
     * @param deviceType
     * @param sn
     * @param busiType
     * @return
     */
    private Map<String, Object> getSimDeviceState(String deviceType, String sn, Integer busiType) {
        Map<String, Object> map = new HashMap<>();
        map.put("sn", sn);
        map.put("name", "模拟设备名称");
        map.put("deviceType", deviceType);
        map.put("hwVer", "V1.00");
        map.put("swVer", "V1.03");
        map.put("ipAddr", "192.168.1.100");
        map.put("sceneType", SensorTypeDictDef.SCENE_WORK);// 0-工作场景，1-公共场景。
        map.put("deviceTime", System.currentTimeMillis());
        map.put("modCtrlState", 127);

        // 1-全部状态，2-默认状态，3-简单状态（设置用的几个原始数值）。
        if (busiType == 3) {
            return map;
        }

        int sensorSeq = 0;
        List<Map<String, Object>> tempSensors = new ArrayList<>();
        for (Iterator<String> it = SensorTypeDictDef.TYPE_TIC_CODES.keySet().iterator(); it.hasNext();) {
            Map<String, Object> tempSensor = new HashMap<>();
            String sensorCode = it.next();

            // 烟气由最后自动生成，此处不做处理。
            if (SensorTypeDictDef.SENSOR_TYPE_SMOKE_CODE.equals(sensorCode)) {
                continue;
            }

            tempSensor.put("sensorSeq", sensorSeq++);
            tempSensor.put("sensorType", SensorTypeDictDef.SENSOR_TIC);
            tempSensor.put("code", sensorCode);
            tempSensor.put("name", SensorTypeDictDef.getName(sensorCode));
            tempSensor.put("version", "V" + RandomUtil.getRandomFloat(1.0f, 2.5f, 2));
            tempSensor.put("fullScall", RandomUtil.getRandomInt(1000, 5000));
            tempSensor.put("sensorSn", "1sn" + sensorSeq);
            tempSensor.put("unit", UnitDictDef.UNIT_CODE_PPM);

            Map<String, Float> currentParam = new HashMap<>();
            currentParam.put(SensorTypeDictDef.PARAM_VALUE_HIGH, RandomUtil.getRandomFloat(5.0f, 50.0f, 2));
            currentParam.put(SensorTypeDictDef.PARAM_VALUE_LOW, RandomUtil.getRandomFloat(5.0f, 50.0f, 2));
            currentParam.put(SensorTypeDictDef.PARAM_VALUE_STEL, RandomUtil.getRandomFloat(5.0f, 50.0f, 2));
            currentParam.put(SensorTypeDictDef.PARAM_VALUE_TWA, RandomUtil.getRandomFloat(5.0f, 50.0f, 2));
            currentParam.put(SensorTypeDictDef.PARAM_VALUE_MAC, RandomUtil.getRandomFloat(5.0f, 50.0f, 2));
            tempSensor.put("currentParam", currentParam);

            Map<String, Float> default1Param = new HashMap<>();
            default1Param.put(SensorTypeDictDef.PARAM_VALUE_HIGH, RandomUtil.getRandomFloat(5.0f, 50.0f, 2));
            default1Param.put(SensorTypeDictDef.PARAM_VALUE_LOW, RandomUtil.getRandomFloat(5.0f, 50.0f, 2));
            default1Param.put(SensorTypeDictDef.PARAM_VALUE_STEL, RandomUtil.getRandomFloat(5.0f, 50.0f, 2));
            default1Param.put(SensorTypeDictDef.PARAM_VALUE_TWA, RandomUtil.getRandomFloat(5.0f, 50.0f, 2));
            default1Param.put(SensorTypeDictDef.PARAM_VALUE_MAC, RandomUtil.getRandomFloat(5.0f, 50.0f, 2));
            tempSensor.put("default1Param", default1Param);

            Map<String, Float> default2Param = new HashMap<>();
            default2Param.put(SensorTypeDictDef.PARAM_VALUE_HIGH, RandomUtil.getRandomFloat(5.0f, 50.0f, 2));
            default2Param.put(SensorTypeDictDef.PARAM_VALUE_LOW, RandomUtil.getRandomFloat(5.0f, 50.0f, 2));
            default2Param.put(SensorTypeDictDef.PARAM_VALUE_STEL, RandomUtil.getRandomFloat(5.0f, 50.0f, 2));
            default2Param.put(SensorTypeDictDef.PARAM_VALUE_TWA, RandomUtil.getRandomFloat(5.0f, 50.0f, 2));
            default2Param.put(SensorTypeDictDef.PARAM_VALUE_MAC, RandomUtil.getRandomFloat(5.0f, 50.0f, 2));
            tempSensor.put("default2Param", default2Param);

            tempSensors.add(tempSensor);
        }
        map.put("tvocSensors", tempSensors);

        sensorSeq = 0;
        tempSensors = new ArrayList<>();
        for (Iterator<String> it = SensorTypeDictDef.TYPE_CWA_CODES.keySet().iterator(); it.hasNext();) {
            Map<String, Object> tempSensor = new HashMap<>();
            String sensorCode = it.next();

            tempSensor.put("sensorSeq", sensorSeq++);
            tempSensor.put("sensorType", SensorTypeDictDef.SENSOR_CWA);
            tempSensor.put("code", sensorCode);
            tempSensor.put("name", SensorTypeDictDef.getName(sensorCode));
            tempSensor.put("version", "V" + RandomUtil.getRandomFloat(1.0f, 2.5f, 2));
            tempSensor.put("fullScall", RandomUtil.getRandomInt(1000, 5000));
            tempSensor.put("sensorSn", "2sn" + sensorSeq);
            tempSensor.put("unit", UnitDictDef.UNIT_CODE_PPB);

            Map<String, Float> currentParam = new HashMap<>();
            currentParam.put(SensorTypeDictDef.PARAM_VALUE_HIGH, RandomUtil.getRandomFloat(5.0f, 50.0f, 2));
            tempSensor.put("currentParam", currentParam);

            Map<String, Float> default1Param = new HashMap<>();
            default1Param.put(SensorTypeDictDef.PARAM_VALUE_HIGH, RandomUtil.getRandomFloat(5.0f, 50.0f, 2));
            tempSensor.put("default1Param", default1Param);

            Map<String, Float> default2Param = new HashMap<>();
            default2Param.put(SensorTypeDictDef.PARAM_VALUE_HIGH, RandomUtil.getRandomFloat(5.0f, 50.0f, 2));
            tempSensor.put("default2Param", default2Param);

            tempSensors.add(tempSensor);
        }
        map.put("cwaSensors", tempSensors);

        map.put("sensorCount", 25);

        return map;
    }

    /**
     * <pre>
     *     FCBR-100M解析的传感器来自tvoc和cwa两个模块的sensor列表。
     *     实际应用需要将部分数据分配到smoke模块的sensor列表中。
     * </pre>
     *
     * @param deviceDataDto
     */
    public void reArrangeFcbr100mData(DeviceDataDto deviceDataDto) {
        ModTvocDto modTvocDto = deviceDataDto.getTvocMod();
        ModCwaDto modCwaDto = deviceDataDto.getCwaMod();
        ModSmokeDto modSmokeDto = deviceDataDto.getSmokeMod();
        ModSysDto modSysDto = deviceDataDto.getSysMod();
        if (modTvocDto != null && modCwaDto != null && modSmokeDto != null && modSysDto != null) {
            List<String> tvocRemovedKeys = SensorTypeDictDef.TYPE_TIC_TO_SMOKE_CODES;
            List<SensorData> tvocSensors = modTvocDto.getSensors();
            List<SensorData> tvocRemoveds = new ArrayList<>();
            tvocSensors.forEach(tempSensor -> {
                if (tvocRemovedKeys.contains(tempSensor.getCode())) {
                    tvocRemoveds.add(tempSensor);
                }
            });

            // 将"O2", "CH2O", "VOC", "VOC-10.0ev", "CH4", "CO2"放到空气质量检测（烟雾模块）中。
            tvocRemoveds.forEach(tempSensor -> {
                if (tvocSensors.contains(tempSensor)) {
                    modSmokeDto.addSensor(tempSensor);
                    tvocSensors.remove(tempSensor);
                }
            });

            // 将原有烟雾传感器放到最前面显示。
            SensorData smokeSensor = new SensorData();
            smokeSensor.setSensorSeq((byte)18);
            smokeSensor.setSensorType(SensorTypeDictDef.SENSOR_TIC);
            smokeSensor.setInstallState((byte)1);
            smokeSensor.setAlarmState((byte)modSmokeDto.getState());
            smokeSensor.setRunstate((byte)modSmokeDto.getRunstate());
            smokeSensor.setOriginReading(modSmokeDto.getSmokeData());
            smokeSensor.setValue(modSmokeDto.getSmokeData());
            smokeSensor.setOriginValue(smokeSensor.getValue());
            smokeSensor.setType(0x0501);
            smokeSensor.setCode(SensorTypeDictDef.SENSOR_TYPE_SMOKE_CODE);
            smokeSensor.setName(SensorTypeDictDef.SENSOR_TYPE_SMOKE_NAME);
            smokeSensor.setOriginUnit(UnitDictDef.UNIT_CODE_PPM);
            smokeSensor.setUnit(UnitDictDef.UNIT_CODE_PPM);
            smokeSensor.setUnitName(UnitDictDef.getUnitName(smokeSensor.getUnit()));
            smokeSensor.setMolecularWeight(0);
            smokeSensor.setFullScall(1000);
            smokeSensor.setSensibility(0.1f);
            smokeSensor.setDisplayOrder(SensorTypeDictDef.getDisplayOrder(smokeSensor.getCode()));
            modSmokeDto.getSensors().add(0, smokeSensor);

            // 经过报警算法之后，处理优先级。
            // 报警处理优先级：CWA报警处理 >毒性气体报警处理 >一般毒性气体报警处理 >烟感> 故障处理 > 实时检测 > 检测页面。
            // 处理模块报警状态。
            String deviceType = deviceDataDto.getDeviceType();
            String sn = deviceDataDto.getSn();
            DeviceParamVo deviceParamVo = null;
            DeviceConfig deviceConfig = ProtocolService.getConfig(deviceType, sn);
            if (deviceParamVo == null) {
                // todo by junzai：Android端软件，不输出此消息。
                // log.error("设备{}未配置数据库参数", sn);
            }
            if (deviceConfig == null) {
                log.error("设备{}未配置固件参数", sn);
            }
            handleModState(modTvocDto, SensorTypeDictDef.SENSOR_TIC, modTvocDto.getSensors(), deviceParamVo,
                deviceConfig);
            handleModState(modCwaDto, SensorTypeDictDef.SENSOR_CWA, modCwaDto.getSensors(), deviceParamVo,
                deviceConfig);
            handleModState(modSmokeDto, SensorTypeDictDef.SENSOR_TIC, modSmokeDto.getSensors(), deviceParamVo,
                deviceConfig);

            // 处理设备的报警列表及排序。
            handleDeviceAlarms(deviceDataDto, modTvocDto.getSensors());
            handleDeviceAlarms(deviceDataDto, modCwaDto.getSensors());
            handleDeviceAlarms(deviceDataDto, modSmokeDto.getSensors());
            if (deviceDataDto.getAlarms() != null) {
                deviceDataDto.getAlarms().sort(Comparator.comparing(AlarmData::getDisplayOrder).reversed());
            }

            // 处理设备的故障列表及排序。
            Map<String, String> faults = modSysDto.getFaults();
            if (faults != null) {
                faults.forEach((faultCode, faultName) -> {
                    FaultData faultData = new FaultData();
                    faultData.setFaultCode(faultCode);
                    faultData.setFaultName(faultName);
                    faultData.setBeginTimeD(new Date());
                    faultData.setBeginTime(faultData.getBeginTimeD().getTime());
                    faultData.setEndTimeD(new Date());
                    faultData.setEndTime(faultData.getEndTimeD().getTime());
                    deviceDataDto.addFault(faultData);
                });
            }

            // 处理设备本身的报警状态和运行状态。
            List<Integer> modStates = new ArrayList<>();
            modStates.add(modSmokeDto.getState());
            modStates.add(modTvocDto.getState());
            modStates.add(modCwaDto.getState());
            deviceDataDto.setState(Collections.max(modStates));

            List<Integer> modRunstates = new ArrayList<>();
            modRunstates.add(modSmokeDto.getRunstate());
            modRunstates.add(modTvocDto.getRunstate());
            modRunstates.add(modCwaDto.getRunstate());
            deviceDataDto.setRunstate(Collections.max(modRunstates));

            // 温度值以sysMode为准，取两者最大值，前端显示统一放到cwa模块中。
            modCwaDto
                    .setTemp(modSysDto.getTemp() > modSysDto.getMainTemp() ? modSysDto.getTemp() : modSysDto.getMainTemp());
            modCwaDto.setHumidity(modSysDto.getHumidity() > modSysDto.getMainHumidity() ? modSysDto.getHumidity()
                    : modSysDto.getMainHumidity());
        }
    }

    private void handleModState(AbstractMod mod, byte sensorType, List<SensorData> sensors, DeviceParamVo deviceParamVo,
        DeviceConfig deviceConfig) {
        Map<String, SensorConfig> sensorConfigMap = null;
        Byte sceneType = SensorTypeDictDef.SCENE_WORK;
        if (deviceConfig != null) {
            sensorConfigMap = deviceConfig.getSensorCodeConfigs();
            sceneType = deviceConfig.getSceneType();
            if (sceneType == null) {
                sceneType = 0;
            }
        }

        int runstate = RunStateDef.NORMAL;
        int state = AlarmDef.STATE_NORMAL;
        for (int i = 0; i < sensors.size(); i++) {
            SensorData sensorData = sensors.get(i);
            String sensorCode = sensorData.getCode();
            if (StringUtils.isNotBlank(sensorCode)) {
                // 数据库中参数为lewisiteHighThreshold和lewisiteUnit，但传感器的名称L。
                // 考虑到以后名称可能会变化，固定数据库中的配置名字为Lewisite。
                // 此处做相应转换，这样前后端都不受影响。
                String dbParamCode = getDbParamCode(sensorCode);
                String dbParamCodeLowerCase = dbParamCode.toLowerCase();

                // 判断是否需要进行单位转换。
                String key = dbParamCodeLowerCase + "Unit";
                if (deviceParamVo != null && sensorConfigMap != null && deviceParamVo.containsItem(key)
                    && sensorConfigMap.containsKey(sensorType + "_" + sensorCode)) {
                    SensorConfig sensorConfig = sensorConfigMap.get(sensorType + "_" + sensorCode);

                    byte originUnit = sensorConfig.getOriginUnit();
                    byte currentUnit;
                    if (sceneType == SensorTypeDictDef.SCENE_WORK) {
                        currentUnit =
                            (byte)Float.parseFloat(deviceParamVo.getStringItemValue(key, DeviceParamVo.LEVEL_LOW, "0"));
                    } else {
                        currentUnit = (byte)Float
                            .parseFloat(deviceParamVo.getStringItemValue(key, DeviceParamVo.LEVEL_HIGH, "0"));
                    }

                    // todo by junzai：此处根据单位转换规则，以及原始单位的规则，进行单位数值的转换。
                    if (currentUnit != originUnit) {

                    }
                }
            }

            if (sensorData.getAlarmState() > state) {
                state = sensorData.getAlarmState();
            }
            if (sensorData.getFaultState() > 0) {
                runstate = RunStateDef.FAULT;
            }
        }
        mod.setState(state);
        mod.setRunstate(runstate);
    }

    private void handleDeviceAlarms(DeviceDataDto deviceDataDto, List<SensorData> sensors) {
        sensors.forEach(tempSensorData -> {
            if (tempSensorData.getAlarmState() > 0) {
                AlarmData alarmData = new AlarmData();
                alarmData.setSensorCode(tempSensorData.getCode());
                alarmData.setSensorName(tempSensorData.getName());
                alarmData.setAlarmContent(tempSensorData.getName());
                alarmData.setConcentration(tempSensorData.getValue() + " " + tempSensorData.getUnitName());
                alarmData.setBeginTimeD(new Date());
                alarmData.setBeginTime(alarmData.getBeginTimeD().getTime());
                alarmData.setEndTimeD(new Date());
                alarmData.setEndTime(alarmData.getEndTimeD().getTime());
                alarmData.setDisplayOrder(SensorTypeDictDef.getDisplayOrder(alarmData.getSensorCode()));
                deviceDataDto.addAlarm(alarmData);
            }
        });

        // 报警优先级高的在前，低的在后。
        // 同等情况下，有故障的在前。
        sensors.sort(Comparator.comparing(SensorData::getAlarmState).reversed()
            .thenComparing(Comparator.comparing(SensorData::getRunstate).reversed()));
    }

    private String getDbParamCode(String sensorCode) {
        // 数据库中参数为lewisiteHighThreshold和lewisiteUnit，但传感器的名称L。
        // 考虑到以后名称可能会变化，固定数据库中的配置名字为Lewisite。
        // 此处做相应转换，这样前后端都不受影响。
        if (SensorTypeDictDef.SENSOR_TYPE_LEWISITE_CODE.equals(sensorCode)) {
            return "Lewisite";
        }

        return sensorCode;
    }
}
