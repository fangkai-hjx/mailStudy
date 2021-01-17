package cn.scut.product.app;

import cn.scut.common.util.PageUtils;
import cn.scut.common.util.R;
import cn.scut.product.entity.AttrEntity;
import cn.scut.product.entity.AttrGroupEntity;
import cn.scut.product.service.AttrGroupService;
import cn.scut.product.service.AttrService;
import cn.scut.product.service.CategoryService;
import cn.scut.product.vo.AttrGroupRelationVo;
import cn.scut.product.vo.AttrGroupWithAttrsVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Api(value = "属性分组 controller")
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @ApiOperation(value = "根据三级分类 分页查询 分组属性 信息")
    @GetMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params, @PathVariable("catelogId")Long catelogId){
        PageUtils page = attrGroupService.queryPage(params,catelogId);
        return R.ok().put("page", page);
    }
//    @ApiOperation(value="获取catalog类别下所有的基础属性")
//    @PostMapping("/attr/relation")
//    public R getAttrGroupWithAttrs(){
//        //查出当前 分类 下 的 所有属性 分组
//        List<AttrGroupWithAttrsVo> list = attrGroupService.getAttrGroupWithAttrsByCategory(catelogId);
//        //查出每个属性分组的 所有属性
//        return R.ok().put("data",list);
//    }
    @ApiOperation(value = "保存 分组属性 信息")
    @PostMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
        attrGroupService.save(attrGroup);
        return R.ok();
    }
    @ApiOperation(value = "根据 分组 Id 查询 分组信息")
    @GetMapping("/info/{groudId}")
    public R info(@PathVariable(name = "groudId")Long groupId){
        AttrGroupEntity attrGroup = attrGroupService.getById(groupId);
        //TODO 这里查询 还需要查出 分类信息（一级分类/二级分类/三级分类）
        Long catelogId = attrGroup.getCatelogId();
        Long[] path = categoryService.findCatelogPath(catelogId);
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }
    @ApiOperation(value = "根据 Ids 删除 分组信息")
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));
        return R.ok();
    }
    @ApiOperation(value="获取 分组下的 基础属性 的关联")
    @GetMapping("{attrGroupId}/attr/relation")
    public R attrRelation(@PathVariable(name = "attrGroupId")Long attrGroupId){
        List<AttrEntity> attrEntityList = attrService.getRelationAttr(attrGroupId);
        return R.ok().put("data",attrEntityList);
    }
    @ApiOperation(value="获取分组下 可被关联的 基础属性（没有被其他分组关联）")
    @GetMapping("/{attrGroupId}/noattr/relation")
    public R attrNoRelation(@RequestParam Map<String, Object> params, @PathVariable("attrGroupId")Long attrGroupId){
        //查出当前 分类 下 的 所有属性 分组
        PageUtils page = attrService.getNoRelationAttr(params,attrGroupId);
        //查出每个属性分组的 所有属性
        return R.ok().put("page",page);
    }
    @ApiOperation(value = "删除分组下的属性消息")
    @PostMapping("/attr/relation/delete")
    public R deleteAttrGroupWithAttrs(@RequestBody AttrGroupRelationVo [] vos){
        attrService.deleteRelation(vos);
        return R.ok();
    }
    @ApiOperation(value = "删除分组下的属性消息")
    @PostMapping("/attr/relation")
    public R saveAttrGroupWithAttrs(@RequestBody AttrGroupRelationVo [] vos){
        attrService.saveRelation(vos);
        return R.ok();
    }
    @ApiOperation(value="获取catalog类别下所有的  分组信息 + 组下的基础属性")
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId")Long catelogId){
        //查出当前 分类 下 的 所有属性 分组
        List<AttrGroupWithAttrsVo> list = attrGroupService.getAttrGroupWithAttrsByCategory(catelogId);
        //查出每个属性分组的 所有属性
        return R.ok().put("data",list);
    }

}
