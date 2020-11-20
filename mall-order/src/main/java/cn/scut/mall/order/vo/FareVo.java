package cn.scut.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FareVo {
    private MemberAddressVo memberAddressVo;//地址信息
    private BigDecimal fare;//运费
}
