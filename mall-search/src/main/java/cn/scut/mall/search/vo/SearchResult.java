package cn.scut.mall.search.vo;

import cn.scut.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

@Data
public class SearchResult {
    private List<SkuEsModel> products;//查询到的商品信息

    //分页信息
    private Integer pageNum;//当前页码
    private Long total;//总记录数
    private Integer totalPages;//总页数

    private List<BrandVo> brands;//当前查询到的结果，所有涉及到的所有品牌
    private List<CatalogVo> catalogs;//当前查询到的结果，所有涉及到的所有分类
    private List<AttrVo> attrs;//当前查询到的结果，所有涉及到的所有属性
    //=======================================以上是返回给页面的信息
    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }
    @Data
    public static class CatalogVo{
        private Long CatalogId;
        private String CatalogName;
    }
}
