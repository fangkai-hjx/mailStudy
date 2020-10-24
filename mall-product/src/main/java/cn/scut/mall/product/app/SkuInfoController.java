package cn.scut.mall.product.app;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.scut.mall.product.service.SkuSaleAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.scut.mall.product.entity.SkuInfoEntity;
import cn.scut.mall.product.service.SkuInfoService;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.R;



/**
 * sku信息
 *
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 11:09:21
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    @GetMapping("/{skuId}/price")
    public BigDecimal getPrice(@PathVariable("skuId")Long skuId){
        SkuInfoEntity infoEntity = skuInfoService.getById(skuId);
        return infoEntity.getPrice();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 发布商品 接收 所有的 数据
     */
    @RequestMapping("/save")
    public R save(@RequestBody SkuInfoEntity spuSaveVo){
		skuInfoService.save(spuSaveVo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

}
