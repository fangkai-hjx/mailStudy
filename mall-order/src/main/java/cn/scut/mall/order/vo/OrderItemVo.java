package cn.scut.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItemVo {
    private Long skuId;
    private String title;
    private String image;//商品图片
    private List<String> skuAttr;//sku属性
    private BigDecimal price;//商品价格
    private Integer count;//商品数量
    private BigDecimal weight;
    private BigDecimal totalPrice;
}
