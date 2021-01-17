package cn.scut.product.app;

import cn.scut.common.util.PageUtils;
import cn.scut.common.util.R;
import cn.scut.product.service.SpuInfoService;
import cn.scut.product.vo.SpuSaveVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(value = "商品SPU信息 controller")
@RestController
@RequestMapping("/product/spuinfo")
public class SpuInfoController {

    @Autowired
    private SpuInfoService spuInfoService;

    @ApiOperation(value = "查询全部spu信息")
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }

    @ApiOperation(value = "发布SPU商品 接收 所有的 数据")
    @PostMapping("/save")
    public R save(@RequestBody SpuSaveVo spuSaveVo){
        spuInfoService.saveSpuInfo(spuSaveVo);
        return R.ok();
    }
    @ApiOperation(value = "上架 SPU")
    @PostMapping("/{spuId}/up")
    public R save(@PathVariable("spuId")Long spuId){
        spuInfoService.up(spuId);
        return R.ok();
    }
}
