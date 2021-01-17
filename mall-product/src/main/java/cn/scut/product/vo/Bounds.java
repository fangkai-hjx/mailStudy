/**
  * Copyright 2020 bejson.com 
  */
package cn.scut.product.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Bounds {
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}