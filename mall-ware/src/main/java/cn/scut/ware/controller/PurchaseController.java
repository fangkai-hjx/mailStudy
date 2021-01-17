package cn.scut.ware.controller;


import cn.scut.common.constant.WareConstant;
import cn.scut.common.util.PageUtils;
import cn.scut.common.util.R;
import cn.scut.ware.entity.PurchaseEntity;
import cn.scut.ware.service.PurchaseService;
import cn.scut.ware.vo.MergeVo;
import cn.scut.ware.vo.PurchaseDoneVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Api("采购单 controller")
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @ApiOperation("完成采购单 任务")
    @PostMapping("/done")
    public R done(@RequestBody PurchaseDoneVo purchaseDoneVo){
        purchaseService.done(purchaseDoneVo);
        return R.ok();
    }

    @ApiOperation("合并采购需求---》生成采购单或者指定到采购单")
    @PostMapping("/merge")
    public R merge(@RequestBody MergeVo mergeVo){
        purchaseService.mergePurchase(mergeVo);
        return R.ok();
    }
    /**
     * 列表
     */
    @ApiOperation("查询采购单（未被其他人领取）")
    @GetMapping("unreceive/list")
    public R unreceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnReceivePurchase(params);
        return R.ok().put("page", page);
    }


    @ApiOperation("查询全部采购单信息")
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }
    @ApiOperation("查询 某个 采购单信息")
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);
        return R.ok().put("purchase", purchase);
    }
    @ApiOperation("保存 采购单信息")
    @PostMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
        purchase.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());//默认状态为0 新增
		purchaseService.save(purchase);
        return R.ok();
    }
    @ApiOperation("修改 采购单信息")
    @PostMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);
        return R.ok();
    }
    @ApiOperation("领取 采购单信息")
    @PostMapping("/received")
    public R receive(@RequestBody List<Long> ids){
        purchaseService.received(ids);
        return R.ok();
    }
    @ApiOperation("删除 采购单信息")
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
