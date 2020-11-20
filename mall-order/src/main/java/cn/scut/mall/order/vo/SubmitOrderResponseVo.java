package cn.scut.mall.order.vo;

import cn.scut.mall.order.entity.OrderEntity;
import lombok.Data;

@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;//---》下单成功信息
    private Integer
            code;//错误状态码   0成功   其他错误---》下单失败信息
}
