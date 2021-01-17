package cn.scut.product.app;

import cn.scut.common.util.PageUtils;
import cn.scut.common.util.R;
import cn.scut.product.entity.BrandEntity;
import cn.scut.product.service.BrandService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@Api(value = "BrandController API")
@RestController
@RequestMapping("product/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * @param param{page ：1 ，limit： 10 ，key： ‘’}
     * @return
     */
    @ApiOperation(value = "查出品牌分类信息", notes = "打哒哒")
    @GetMapping("/list")
    public R list(@RequestParam Map<String,Object> param){
        PageUtils page = brandService.queryPage(param);
        return R.ok().put("page",page);
    }
    @PostMapping("/save")
    @ApiOperation(value = "保存品牌信息",notes = "保存品牌信息")
    public R save(@RequestBody BrandEntity brandEntity){
        brandService.save(brandEntity);
        return R.ok();
    }
    @PostMapping("/delete")
    @ApiOperation(value = "删除品牌信息",notes = "删除品牌信息")
    public R delete(@RequestBody Long[] brandIds){
        brandService.removeByIds(Arrays.asList(brandIds));
        return R.ok();
    }
    @GetMapping("/info/{brandId}")
    @ApiOperation(value = "根据 id 查询品牌信息",notes = "删除品牌信息")
    public R info(@PathVariable(name = "brandId") Long brandId){
        BrandEntity brand = brandService.getById(brandId);
        return R.ok().put("brand",brand);
    }
    @PostMapping("/update")
    @ApiOperation(value = "修改品牌信息",notes = "修改品牌信息")
    public R info(@RequestBody BrandEntity brandEntity){
        brandService.updateDetail(brandEntity);
        return R.ok();
    }

    @PostMapping("/update/status")
    @ApiOperation(value = "修改品牌状态信息",notes = "修改品牌状态信息")
    public R updateStatus(@RequestBody BrandEntity brandEntity){
        brandService.updateById(brandEntity);
        return R.ok();
    }
}
