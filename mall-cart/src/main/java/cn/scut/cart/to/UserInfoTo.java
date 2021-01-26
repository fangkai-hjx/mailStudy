package cn.scut.cart.to;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserInfoTo {
    private Long userId; //登录了就有userId
    private String userKey;//不管有没有登录 都有 userKey
    private Boolean tempUser = false;//如果cookie里面有临时用户，则为true，否则为false
}
