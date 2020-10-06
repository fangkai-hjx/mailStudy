package cn.scut.mall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.scut.mall.product.entity.AttrEntity;
import cn.scut.mall.product.service.AttrAttrgroupRelationService;
import cn.scut.mall.product.service.AttrService;
import cn.scut.mall.product.service.CategoryService;
import cn.scut.mall.product.vo.AttrGroupRelationVo;
import cn.scut.mall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.scut.mall.product.entity.AttrGroupEntity;
import cn.scut.mall.product.service.AttrGroupService;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.R;



/**
 * 属性分组
 *
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 11:09:21
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    //    product/attrgroup/3/attr/relation 获取所有 分组 和 基本属性的 关联
    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId){
        List<AttrEntity> attrEntityList = attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data",attrEntityList);
    }

    //获取分类下所有分组关联属性
    //product/attrgroup/{catelogId}/withattr
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId")Long catelogId){
        //查出当前 分类 下 的 所有属性 分组
        List<AttrGroupWithAttrsVo> list = attrGroupService.getAttrGroupWithAttrsByCategory(catelogId);
        //查出每个属性分组的 所有属性
        return R.ok().put("data",list);
    }
    //    product/attrgroup/1/noattr/relation?t=1600583406073&page=1&limit=10&key=
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@RequestParam Map<String, Object> params,
                            @PathVariable("attrgroupId") Long attrgroupId){
        PageUtils pageUtils = attrService.getNoRelationAttr(params,attrgroupId);
        return R.ok().put("page",pageUtils);
    }
    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,@PathVariable("catelogId")Long catelogId){
//        PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params,catelogId);
        return R.ok().put("page", page);
    }
//    product/attrgroup/attr/relation
    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVo> relationList){
        attrAttrgroupRelationService.saveBatch(relationList);
        return R.ok();
    }
    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] path = categoryService.findCatelogPath(catelogId);
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存----同时保存 关联表   分组信息表
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }
//    product/attrgroup/attr/relation/delete
    @PostMapping("/attr/relation/delete")
    public R removeAttrGroup(@RequestBody AttrGroupRelationVo[] vos){
        attrService.deleteRelation(vos);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
