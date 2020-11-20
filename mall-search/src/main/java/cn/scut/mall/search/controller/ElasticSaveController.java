package cn.scut.mall.search.controller;

import cn.scut.common.exception.BizCodeEnume;
import cn.scut.common.to.es.SkuEsModel;
import cn.scut.common.utils.R;
import cn.scut.mall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/search/save")
public class ElasticSaveController {

    @Autowired
    ProductSaveService productSaveService;

    //上架商品
    @PostMapping("/prodcut")
    public R prodcutStatusUp(@RequestBody List<SkuEsModel>skuEsModels){
        boolean b = false;
        try {
            b = productSaveService.productStatusUp(skuEsModels);
        } catch (IOException e) {
            log.error("商品上架错误:{}",e);
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnume.PRODUCT_UP_EXCEPTION.getMessage());
        }
        if(b)  {
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnume.PRODUCT_UP_EXCEPTION.getMessage());

        }else {
            return R.ok();
        }
    }
}
