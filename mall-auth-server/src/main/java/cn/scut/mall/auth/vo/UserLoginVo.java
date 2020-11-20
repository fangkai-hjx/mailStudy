package cn.scut.mall.auth.vo;

import lombok.Data;

/**
 * 接收用户登录信息
 */
@Data
public class UserLoginVo {
    private String loginacct;
    private String password;
}
