package cn.scut.common.to;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class SpuBoundTo {
    private Long spuId;
    private BigDecimal growBounds;
    private BigDecimal buyBounds;
}
