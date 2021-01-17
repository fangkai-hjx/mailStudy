package cn.scut.product.app;

import cn.scut.common.util.PageUtils;
import cn.scut.common.util.R;
import cn.scut.product.entity.AttrEntity;
import cn.scut.product.entity.ProductAttrValueEntity;
import cn.scut.product.service.AttrService;
import cn.scut.product.service.ProductAttrValueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(value = "基本属性 controller")
@RestController
@RequestMapping("/product/attr")
public class AttrController {

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @ApiOperation(value = "根据 三级分类和type类型 查询 基础属性 或者销售属性")
    @GetMapping("{type}/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,@PathVariable(name = "type")String type,@PathVariable(name = "catelogId")Long catelogId){
        PageUtils page = attrService.queryBaseAttrPage(params,catelogId,type);
        return R.ok().put("page", page);
    }

    @ApiOperation(value = "保存 基础属性 同时保存 基本属性-分类 的 中间表")
    @PostMapping("/save")
    public R save(@RequestBody AttrEntity attr){
        attrService.saveAttr(attr);

        return R.ok();
    }
    @ApiOperation(value = "查询SPU下的基础属性信息")
    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrListForSpu(@PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity> list =  productAttrValueService.baseAttrListForSpu(spuId);
        return R.ok().put("data", list);
    }
    @ApiOperation(value = "根据属性ID 查询 属性信息")
    @GetMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
        AttrEntity attrRespVo = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", attrRespVo);
    }

    @ApiOperation(value = "修改 属性信息")
    @PostMapping("/update")
    public R update(@RequestBody AttrEntity attr){
        attrService.updateAttr(attr);

        return R.ok();
    }
    @ApiOperation(value = "修改SPU的属性信息")
    @PostMapping("/update/{spuId}")
    public R updateSpuAttr(@RequestBody List<ProductAttrValueEntity> entities,@PathVariable("spuId")Long spuId){
        productAttrValueService.updateAttr(spuId,entities);
        return R.ok();
    }
    @ApiOperation(value = "查询销售属性")
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }
}
