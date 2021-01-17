package cn.scut.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuReductionTo {
    private Long skuId;
    //满几件减打折
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    //满多少钱 打折
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    //会员价
    private List<MemberPrice> memberPrice;
}
