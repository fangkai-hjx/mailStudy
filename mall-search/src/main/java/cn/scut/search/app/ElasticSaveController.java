package cn.scut.search.app;

import cn.scut.common.constant.ResponseCode;
import cn.scut.common.to.SkuEsModel;
import cn.scut.common.util.R;
import cn.scut.search.service.ProductSaveService;
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
    private ProductSaveService productSaveService;

    //上架商品
    @PostMapping("/product")
    public R productUp(@RequestBody List<SkuEsModel> skuEsModelList){
        boolean b = false;
        try {
            b = productSaveService.productStatusUp(skuEsModelList);
        } catch (IOException e) {
            log.error("ElasticSearch商品上架错误：{}",e);
            return R.error(ResponseCode.PRODUCT_UP_EXCEPTION.getCode(),ResponseCode.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if(b == false){
            return R.ok();
        }else {
            return R.error(ResponseCode.PRODUCT_UP_EXCEPTION.getCode(),ResponseCode.PRODUCT_UP_EXCEPTION.getMsg());
        }

    }
}
