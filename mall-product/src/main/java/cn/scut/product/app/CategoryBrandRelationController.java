package cn.scut.product.app;

import cn.scut.common.util.R;
import cn.scut.product.entity.BrandEntity;
import cn.scut.product.entity.CategoryBrandRelationEntity;
import cn.scut.product.service.CategoryBrandRelationService;
import cn.scut.product.vo.BrandVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(value = "分类-品牌关联信息 Controller")
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @ApiOperation(value = "查询品牌和分类的关联信息")
    @GetMapping("/catelog/list")
    public R categoryBrandList(@RequestParam("brandId") Long brandId){
        List<CategoryBrandRelationEntity> list = categoryBrandRelationService.list(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId)
        );
        return R.ok().put("data", list);
    }
    //TODO 重要思想 1，处理请求，接收和校验数据 2，Service接收controller传来的数据，进行业务处理 3，Controller接收Service处理完的数据，封装页面指定的vo
    @ApiOperation(value = "获取 分类下 关联的品牌")
    @GetMapping("/brands/list")
    public R relationBrandsList(@RequestParam(value = "catId",required = true)Long catId){//TODO step 1接收处理数据
        //TODO step 2，交给service处理
        List<BrandEntity> brandEntities = categoryBrandRelationService.getBrandsByCatId(catId);//这里不返回Vo 是因为 可能 会有 其他 字段 信息 需要,在service层 返回通用的
        //TODO step 3 接收处理数据，交给service处理
        List<BrandVo> brandVos = brandEntities.stream().map(item -> {
            BrandVo brandVo = new BrandVo();
            BeanUtils.copyProperties(item, brandVo);
            return brandVo;
        }).collect(Collectors.toList());
        return R.ok().put("data",brandVos);
    }
    @ApiOperation(value = "保存品牌和分类的关联信息")
    @PostMapping("/save")
    public R categoryBrandSave(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
        categoryBrandRelationService.saveDetail(categoryBrandRelation);
        return R.ok();
    }
}
