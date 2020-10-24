package cn.scut.mall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.scut.common.exception.BizCodeEnume;
import cn.scut.mall.ware.exception.NoStockException;
import cn.scut.mall.ware.vo.LockStockResult;
import cn.scut.mall.ware.vo.SkuHasStockVo;
import cn.scut.mall.ware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.scut.mall.ware.entity.WareSkuEntity;
import cn.scut.mall.ware.service.WareSkuService;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.R;



/**
 * 商品库存
 *
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 12:48:49
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 订单锁定库存
     * @return
     */
    @PostMapping("/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo vo){
        try{
            Boolean flag = wareSkuService.orderLockStock(vo);
            return R.ok();
        }catch (NoStockException e){
            return R.error(BizCodeEnume.NO_STOCK_EXCEPTION.getCode(),BizCodeEnume.NO_STOCK_EXCEPTION.getMessage());
        }
    }
    /**
     * 查询 sku是否 有 库存
     * @return
     */
    @PostMapping("/hasstock")
    public R getSkuHasStock(@RequestBody List<Long>skuIds){
        List<SkuHasStockVo> list = wareSkuService.getSkuHasStock(skuIds);//sku_id stock
        return R.ok().put("data",list);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
