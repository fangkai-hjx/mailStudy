package cn.scut.ware.controller;

import cn.scut.common.util.PageUtils;
import cn.scut.common.util.R;
import cn.scut.ware.entity.WareInfoEntity;
import cn.scut.ware.entity.WareSkuEntity;
import cn.scut.ware.service.WareInfoService;
import cn.scut.ware.service.WareSkuService;
import cn.scut.ware.vo.SkuHasStock;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(value = "仓库的SKU信息 controller")
@RestController
@RequestMapping("/ware/waresku")
public class WareSkuController {

    @Autowired
    private WareSkuService wareSkuService;

    @ApiOperation("查询全部 库存SKU信息")
    @GetMapping("/list")
    public R list(@RequestParam Map<String,Object> param){
        PageUtils page = wareSkuService.queryByCondition(param);
        return R.ok().put("page",page);
    }
    @ApiOperation("保存SKU")
    @PostMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSkuEntity){
        wareSkuService.save(wareSkuEntity);
        return R.ok();
    }
    @ApiOperation("查询某个仓库下的SKU信息")
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }
    @ApiOperation("更新 仓库下的SKU信息")
    @PostMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
        wareSkuService.updateById(wareSku);

        return R.ok();
    }
    @ApiOperation("查询sku是否有库存")
    @PostMapping("/hasstock")
    public R hasstock(@RequestBody List<Long> skuIds){
        List<SkuHasStock> hasStocks = wareSkuService.getSkusHasStock(skuIds);
        return R.ok().put("data",hasStocks);
    }
}
