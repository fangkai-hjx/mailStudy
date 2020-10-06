package cn.scut.mall.product.app;

import java.util.Arrays;
import java.util.Map;

import cn.scut.mall.product.vo.SpuSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.scut.mall.product.entity.SpuInfoEntity;
import cn.scut.mall.product.service.SpuInfoService;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.R;



/**
 * spu信息
 *
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 11:09:20
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    //http://localhost:88/api/product/spuinfo/12/up
    /**
     * spu 上架
     */
    @PostMapping("/{spuId}/up")
    public R spuUp(@PathVariable(value = "spuId")Long spuId){
       spuInfoService.up(spuId);
       return R.ok();
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 发布商品 接收 所有的 数据
     */
    @RequestMapping("/save")
    public R save(@RequestBody SpuSaveVo spuSaveVo){
        spuInfoService.saveSpuInfo(spuSaveVo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
