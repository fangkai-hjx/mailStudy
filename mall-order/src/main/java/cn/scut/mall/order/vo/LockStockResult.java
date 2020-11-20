package cn.scut.mall.order.vo;

import lombok.Data;

@Data
public class LockStockResult {
    private Long skuId;
    private Integer num;//锁了几件
    private Boolean locked;//是否锁定成功
}
