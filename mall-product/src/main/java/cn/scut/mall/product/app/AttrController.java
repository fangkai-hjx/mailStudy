package cn.scut.mall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.scut.mall.product.entity.ProductAttrValueEntity;
import cn.scut.mall.product.service.ProductAttrValueService;
import cn.scut.mall.product.vo.AttrRespVo;
import cn.scut.mall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.scut.mall.product.service.AttrService;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.R;



/**
 * 商品属性
 *
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 11:09:21
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * 获取 基础属性 sale/list/0
     * 获取 销售属性 base/list/{catelogId}
     */
    @GetMapping("{attrType}/list/{catelogId}")
    public R baseList(@RequestParam Map<String, Object> params,
                     @PathVariable("catelogId")Long catelogId,
                     @PathVariable("attrType")String type){
        PageUtils page = attrService.queryBaseAttrPage(params,catelogId,type);

        return R.ok().put("page", page);
    }
    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrListForSpu(@PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity> list =  productAttrValueService.baseAttrListForSpu(spuId);
        return R.ok().put("data", list);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
		AttrRespVo attrRespVo = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", attrRespVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改,这里修改基础属性 的 同时 要 修改 属性分组关联表
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVo attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
