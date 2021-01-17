/**
  * Copyright 2020 bejson.com 
  */
package cn.scut.product.vo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SpuSaveVo {
//   sku的基本信息  `pms_sku_info`
    private String spuName;
    private String spuDescription;
    private Long catalogId;
    private Long brandId;
    private BigDecimal weight;
    private int publishStatus;
//    spu 的 描述图片 `pms_spu_info_desc`
    private List<String> decript;
//    spu 的 对应所有的sku信息。`pms_sku_info`
    private List<String> images;
//    sku 的 积分信息 mall_sms-->`sms_spu_bounds`
    private Bounds bounds;
//    spu 的 规格参数 `pms_product_attr_value`
    private List<BaseAttrs> baseAttrs;
//    spu 的 对应所有的sku信息。`pms_sku_info`
    private List<Skus> skus;

}