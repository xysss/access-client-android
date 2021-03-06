package com.htnova.access.commons.message;

/** 提示消息枚举常量，可以考虑放到配置文件中。 */
public enum MessageEnum {
    // info级别日志code现用200表示，看情况修改
    INFO_20001(200, "成功"),

    ERROR_1001(1001, "登录账号错误"), ERROR_1002(1002, "用户名或密码错误"), ERROR_1003(1003, "密码错误"), ERROR_1004(1004, "Token错误"),
    ERROR_1005(1005, "未获取到数据"), ERROR_1006(1006, "密码格式错误，应包含大小写字母，数字，至少8位以上"), ERROR_1007(1007, "输入数据不能为空"),
    ERROR_1008(1008, "修改数据失败"), ERROR_1009(1009, "暂无报警记录"), ERROR_1010(1010, "未登录"), ERROR_1011(1011, "签名参数不全"),
    ERROR_1012(1012, "签名时间戳过期"), ERROR_1013(1013, "签名错误"), ERROR_1014(1014, "暂无导出数据"), ERROR_1015(1015, "未获取到详情数据"),
    ERROR_1016(1016, "检测报告导出错误"), ERROR_1017(1017, "邮箱地址错误"), ERROR_1018(1018, "该帐号已禁止登录"),
    ERROR_1019(1019, "每日允许最大邮件数量为10条"), ERROR_1020(1020, "根据设备名称清空对应本底缓存失败"), ERROR_1021(1021, "修改设备名称失败"),

    ERROR_2001(2001, "数据解析异常"), ERROR_2002(2002, "参数不能为空"), ERROR_2003(2003, "添加信息失败"), INFO_2004(200, "success"),
    INFO_2005(200, "上传成功"), INFO_2006(200, "数据处理完成"), INFO_2007(200, "消息下发成功"), ERROR_2008(2008, "处理上传的308字节数据异常"),
    ERROR_2010(2010, "处理上传的226字节数据异常"), ERROR_2011(2011, "输入参数有误"), ERROR_2012(2012, "未获取到文件"),
    ERROR_2013(2013, "导出文件异常"), ERROR_2014(2014, "未获取到检测id"), ERROR_2015(2015, "未查询到sn"), ERROR_2101(2101, "设备场景设置异常"),
    ERROR_2102(2102, "设备时间设置异常"), ERROR_2103(2103, "设备模块开关设置异常"), ERROR_2104(2104, "设备参数阈值设置异常"),

    ERROR_3001(3001, "设备正在使用远程参数，请联系管理员配置使用本地参数"), ERROR_3002(3002, "Level参数异常"), ERROR_3003(3003, "设备不存在"),
    ERROR_3004(3004, "升级中断"), WARN_3005(3005, "升级中"), ERROR_3006(3006, "调用参数异常"), WARN_3007(3007, "开始升级"),
    ERROR_3008(3008, "设备不存在"), ERROR_3009(3009, "状态参数有误"), ERROR_3010(3010, "设备连接异常"), ERROR_3011(3011, "上传失败"),
    ERROR_3012(3012, "无返回结果，请检查项目配置"), ERROR_3013(3013, "sn参数不能为空"), ERROR_3014(3014, "status不能为空"),
    ERROR_3015(3015, "status错误"), ERROR_3016(3016, "sn错误，数据库中未查到开始升级记录"), ERROR_3017(3017, "修改升级记录表失败"),
    INFO_3018(200, "阈值"),

    ERROR_4001(4001, "参数不能为空"), ERROR_4002(4002, "传输逻辑判断字段错误"), ERROR_4003(4003, "添加报警记录失败，监测点不存在"),
    ERROR_4004(4004, "添加报警记录失败，数据库返回为空或零"),

    ERROR_6001(6001, "防sql注入检验失败"), ERROR_6003(6003, "暂无原始数据"), ERROR_6002(6002, "查询odps数仓报错"),

    ERROR_10012(10012, "该设备未开启远程控制"),

    INFO_99001(99001, "未获取到物质数据"), ERROR_99002(99002, "获取基础初始化数据非法"), ERROR_99003(99003, "未获取到基础初始化数据"),
    ERROR_99004(99004, "更新基础初始化数据非法"), ERROR_99005(99005, "更新基础初始化数据异常"), ERROR_99006(99006, "获取表结构非法"),
    ERROR_99007(99007, "未获取到表结构数据"), ERROR_99008(99008, "获取表数据非法"), ERROR_99009(99009, "未获取到表数据"),
    ERROR_99010(99010, "动态获取表数据非法"), ERROR_99011(99011, "未获取到动态表数据"), ERROR_99012(99012, "执行动态更新非法"),
    ERROR_99013(99013, "执行动态更新未返回结果");

    private int code;
    private String message;

    MessageEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(int code) {
        MessageEnum[] values = MessageEnum.values();
        int count = values.length;
        for (int i = 0; i < count; i++) {
            if (values[i].code == code) {
                return values[i].message;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
