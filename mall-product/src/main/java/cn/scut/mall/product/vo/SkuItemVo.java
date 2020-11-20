package cn.scut.mall.product.vo;

import cn.scut.mall.product.entity.SkuImagesEntity;
import cn.scut.mall.product.entity.SkuInfoEntity;
import cn.scut.mall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {
    //1 SKU 基本信息获取 pms_sku_info
    SkuInfoEntity infoEntity;
    //2 SKU 图片信息 pms_sku_image
    List<SkuImagesEntity> images;
    //3 获取 spu 的 销售属性组合。
    List<SkuItemSaleAttrVo> saleAttr;
    //4 获取 spu 的 介绍.
    SpuInfoDescEntity desp;
    //5 获取 spu 的 规格参数信息
    List<SpuItemAttrGroupVo> groupAttrs;
    Boolean hasStock = true;

    SeckillInfoVo seckillInfoVo;//当前商品的秒杀优惠信息
    @Data
    public static class SkuItemSaleAttrVo{//销售属性
        private Long attrId;
        private String attrName;
        private String attrValues;
    }
    @Data
    public static class SpuItemAttrGroupVo{
        private String groupName;
        private List<SpuBaseAttrVo> attrs;
    }
    @Data
    public static class SpuBaseAttrVo{//基础属性
        private String attrName;
        private String attrValue;
    }
}
