package cn.scut.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 封装订单提交数据
 */
@Data
public class OrderSubmitVo {
    private Long addrId;//收货地址 id
    private Integer payType;//支付方式
    //无需提交需要购买的商品，去购物车在获取一遍----》页面展示的数据不靠谱
    //优惠信息，发票等等。。。
    private String orderToken;
    private BigDecimal payPrice;//应付价格，验价--》将该价格和服务器查询的价格对比，提示用户哪些商品价格发生了变化---》可以不做
    //用户相关信息都在session中
    private String note;//订单备注

}
