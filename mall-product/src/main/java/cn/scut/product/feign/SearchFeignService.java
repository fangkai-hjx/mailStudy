package cn.scut.product.feign;

import cn.scut.common.to.SkuEsModel;
import cn.scut.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("mall-search")
public interface SearchFeignService {
    @PostMapping("/search/save/product")
    public R productUp(@RequestBody List<SkuEsModel> skuEsModelList);
}
