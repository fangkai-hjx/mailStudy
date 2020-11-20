package cn.scut.common.to.mq;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeckillOrderTo {
    private String orderSn;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    private Integer num;//买了几件
    private Long memberId;//会员id
}
