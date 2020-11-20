package cn.scut.mall.search.vo;

import lombok.Data;

import java.util.List;

//keyword=小米
// &sort=saleCount_desc/asc---排序排序:saleCount(销量)、hotScore(热度分)、skuPrice(价格)
// &hasStock=0/1
// &skuPrice=400_1900
// &brandId=1
// &catalog3Id=1
// &attrs=1_3G:4G:5G&attrs=2_骁龙845&attrs=4_高清屏
@Data
public class SearchParam {
    private String keyword;//页面传递过来的全文匹配关键字
    private Long catalog3Id;//三级分类id
    //sort=saleCount_desc/asc
    //sort=hotScore_desc/asc
    //sort=skuPrice_desc/asc
    private String sort;//排序条件

    //过滤条件
    // hasStock: hasStock=0/1
    // skuPrice区间:skuPrice=1_500/_500/500_
    // brandId=1
    // attrs=1_安卓:安卓&attrs=2_5寸:6寸
    private Integer hasStock ;//是否有货  0无 1有
    private String skuPrice;//价格区间
    private List<Long> brandId;//品牌选择,可多选
    private List<String> attrs;//按照属性进行筛选

    private Integer pageNum = 1;//页码
}
