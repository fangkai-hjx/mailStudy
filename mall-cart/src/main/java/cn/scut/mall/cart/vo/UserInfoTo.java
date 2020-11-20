package cn.scut.mall.cart.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserInfoTo {
    private Long userId;
    private String userKey;
    private Boolean tempUser = false;
}
