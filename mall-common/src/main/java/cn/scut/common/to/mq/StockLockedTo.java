package cn.scut.common.to.mq;

import lombok.Data;

@Data
public class StockLockedTo {
    private Long id;//库存工作单Id
    private StockDetailTo detailTo;//工作详情单的id
}
