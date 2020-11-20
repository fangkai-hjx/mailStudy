package cn.scut.mall.product.feign;

import cn.scut.common.to.es.SkuEsModel;
import cn.scut.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("mall-search")
public interface SearchFeignService {
    //上架商品
    @PostMapping("/search/save/prodcut")
    R prodcutStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
