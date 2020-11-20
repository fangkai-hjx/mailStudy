package cn.scut.mall.order.to;

import cn.scut.mall.order.entity.OrderEntity;
import cn.scut.mall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单传接好
 */
@Data
public class OrderCreateTo {
    private OrderEntity order;
    private List<OrderItemEntity> orderItems;
    private BigDecimal payPrice;//订单应负价格
    private BigDecimal fare;//运费
}
