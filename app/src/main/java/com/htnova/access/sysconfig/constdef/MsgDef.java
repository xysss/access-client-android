package com.htnova.access.sysconfig.constdef;

import java.util.HashMap;
import java.util.Map;

/** 协议消息定义。 */
public interface MsgDef {
    /** 协议消息号，MsgId，数据对象对应的消息号，见协议文档《HT-FCBR-100-通信协议.doc》。 */
    Map<String, Number> MID_308_INSTRUCTS = new HashMap<String, Number>() {
        {
            // 偶数为请求，奇数为响应，0x54-0x55不包含在内。
            // 只处理获取数据的响应，因为需要解析获取到的数据，并更新到配置中。
            // 设置响应不做处理，默认为设置成功。
            // 这些为FCBR-100系列共有（少数几个含义不一样）。
            put(Long.toString(0x01), 0x01); // 心跳读取响应。
            put(Long.toString(0x03), 0x03); // 版本读取响应（FCBR-100M：获取设备信息响应）。
            put(Long.toString(0x05), 0x05); // 开始固件更新响应。
            put(Long.toString(0x07), 0x07); // 获取映像长度响应。
            put(Long.toString(0x09), 0x09); // 发送映像文件响应。
            put(Long.toString(0x0B), 0x0B); // 结束映像文件响应。
            // put(Long.toString(0x43), 0x43); // 下发报警状态响应。
            // put(Long.toString(0x45), 0x45); // 重置烟感响应。
            // put(Long.toString(0x47), 0x47); // 重置生物响应。
            // put(Long.toString(0x49), 0x49); // 重置HT响应。
            // put(Long.toString(0x4B), 0x4B); // 设置SN响应（FCBR-100M请求内容不同）。
            put(Long.toString(0x4D), 0x4D); // 设置通知时间间隔响应。
            put(Long.toString(0x4F), 0x4F); // 设置HT灵敏度响应（FCBR-100M：获取通知时间间隔响应）。
            put(Long.toString(0x51), 0x51); // 设置滤毒罐状态响应（FCBR-100M：设置设备时间响应）。
            put(Long.toString(0x53), 0x53); // 设置生物采样器状态响应（FCBR-100M，获取设备时间响应）。
            put(Long.toString(0x57), 0x57); // 获取传感器配置信息响应。
            put(Long.toString(0x59), 0x59); // 设置模块开关状态响应。

            // 以下为FCBR-100M独有。
            // put(Long.toString(0x5B), 0x5B); // 获取传感器序列号响应（传感器序列号包含在0x02获取设备基本信息中，不单独处理）。
            put(Long.toString(0x5D), 0x5D); // 获取传感器报警阈值响应。
            put(Long.toString(0x5F), 0x5F); // 设置传感器报警阈值响应。
            put(Long.toString(0x61), 0x61); // 获取传感器报警阈值场景缺省值响应。
            // put(Long.toString(0x63), 0x63); // 设置传感器报警阈值场景缺省值响应。
            put(Long.toString(0x67), 0x67); // 获取设备IP地址响应。
            // put(Long.toString(0x69), 0x69); // 设置设备IP地址响应。
            put(Long.toString(0x6B), 0x6B); // 获取网络服务参数响应。
            // put(Long.toString(0x6D), 0x6D); // 设置网络服务参数响应。
            put(Long.toString(0x6F), 0x6F); // 获取设备场景响应。
            put(Long.toString(0x71), 0x71); // 设置设备场景响应。
            put(Long.toString(0x79), 0x79);// 获取模块开关状态响应。
        }
    };
}
