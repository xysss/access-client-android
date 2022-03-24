package com.htnova.access.pojo.po;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUser implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String id;

    /** 归属公司 */
    private String companyId;

    /** 归属部门 */
    private String officeId;

    /** 登录名 */
    private String loginName;

    /** 密码 */
    private String password;

    /** 可逆密码 */
    private String password2;

    /** 姓名 */
    private String name;

    /** 邮箱 */
    private String email;

    /** 电话 */
    private String phone;

    /** 手机 */
    private String mobile;

    /** 用户类型 */
    private String userType;

    /** 是否可登录 */
    private String loginFlag;

    /** 删除标记 */
    private String delFlag;

    /** 安全密匙 */
    private String securityKey;
}
