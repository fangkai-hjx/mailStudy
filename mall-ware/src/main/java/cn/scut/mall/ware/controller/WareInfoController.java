package cn.scut.mall.ware.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import cn.scut.mall.ware.vo.FareVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.scut.mall.ware.entity.WareInfoEntity;
import cn.scut.mall.ware.service.WareInfoService;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.R;



/**
 * 仓库信息
 *
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 12:48:49
 */
@RestController
@RequestMapping("ware/wareinfo")
public class WareInfoController {
    @Autowired
    private WareInfoService wareInfoService;


    /**
     * 列表
     */
    @RequestMapping("/fare")
    public R getFare(@RequestParam("addrId")Long addrId){
        FareVo fareVo = wareInfoService.getFare(addrId);
        return R.ok().setData(fareVo);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareInfoEntity wareInfo = wareInfoService.getById(id);

        return R.ok().put("wareInfo", wareInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareInfoEntity wareInfo){
		wareInfoService.save(wareInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareInfoEntity wareInfo){
		wareInfoService.updateById(wareInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
