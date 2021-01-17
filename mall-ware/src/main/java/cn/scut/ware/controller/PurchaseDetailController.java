package cn.scut.ware.controller;


import cn.scut.common.util.PageUtils;
import cn.scut.common.util.R;
import cn.scut.ware.entity.PurchaseDetailEntity;
import cn.scut.ware.service.PurchaseDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@Api("采购需求 controller")
@RestController
@RequestMapping("/ware/purchasedetail")
public class PurchaseDetailController {
    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @ApiOperation("查询 全部 采购需求")
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseDetailService.queryPageByCondition(params);
        return R.ok().put("page", page);
    }
    @ApiOperation("查询 指定 采购需求")
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		PurchaseDetailEntity purchaseDetail = purchaseDetailService.getById(id);
        return R.ok().put("purchaseDetail", purchaseDetail);
    }
    @ApiOperation("保存 采购需求")
    @PostMapping("/save")
    public R save(@RequestBody PurchaseDetailEntity purchaseDetail){
		purchaseDetailService.save(purchaseDetail);
        return R.ok();
    }
    @ApiOperation("查询 全部 采购需求")
    @PostMapping("/update")
    public R update(@RequestBody PurchaseDetailEntity purchaseDetail){
		purchaseDetailService.updateById(purchaseDetail);
        return R.ok();
    }
    @ApiOperation("删除 采购需求")
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		purchaseDetailService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }
}
