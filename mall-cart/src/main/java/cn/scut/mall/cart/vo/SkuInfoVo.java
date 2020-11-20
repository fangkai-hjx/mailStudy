package cn.scut.mall.cart.vo;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SkuInfoVo implements Serializable {
	private static final long serialVersionUID = 1L;


	private Long skuId;

	private Long spuId;

	private String skuName;

	private String skuDesc;

	private Long catalogId;

	private Long brandId;

	private String skuDefaultImg;

	private String skuTitle;

	private String skuSubtitle;

	private BigDecimal price;

	private Long saleCount;

}
