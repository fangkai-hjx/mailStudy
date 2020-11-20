package cn.scut.mall.member.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class MemberRegistVo {

    private String password;

    private String username;

    private String phone;
}
