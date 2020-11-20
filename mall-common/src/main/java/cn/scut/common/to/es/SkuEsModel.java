package cn.scut.common.to.es;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@ToString
@Data
public class SkuEsModel {
    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    private Boolean hasStock;
    private Long hotScore;
    private Long brandId;
    private Long catalogId;
    private String brandName;
    private String brandImg;
    private String catalogName;
    private List<Attrs> attrs ;

    @Data
    public static class Attrs{
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
