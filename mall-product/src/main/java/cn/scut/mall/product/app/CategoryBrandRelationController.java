package cn.scut.mall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.scut.mall.product.entity.BrandEntity;
import cn.scut.mall.product.vo.BrandVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.scut.mall.product.entity.CategoryBrandRelationEntity;
import cn.scut.mall.product.service.CategoryBrandRelationService;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 11:09:21
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 获取品牌关联的分类
     */
    @GetMapping("/catelog/list")
    public R categoryBrandList(@RequestParam("brandId") Long brandId){
        List<CategoryBrandRelationEntity> list = categoryBrandRelationService.list(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId)
        );
        return R.ok().put("data", list);
    }
    /**
     * 保品牌于 分类的 关联信息
     * {"brandId":1,"catelogId":2}
     *  我们同时保存 brandName,CategoryName
     */
    @PostMapping("/save")
    public R categoryBrandSave(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
        categoryBrandRelationService.saveDetail(categoryBrandRelation);
        return R.ok();
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 获取分类关联的品牌
     * @param catId
     * @return
     */
    @GetMapping("/brands/list")
    public R relationBrandsList(@RequestParam(value = "catId",required = true)Long catId){
        List<BrandEntity>vos = categoryBrandRelationService.getBrandsByCatId(catId);//这里不返回Vo 是因为 可能 会有 其他 字段 信息 需要,在service层 返回通用的
        List<BrandVo> collect = vos.stream().map((item) -> {
            BrandVo brandVo = new BrandVo();
            brandVo.setBrandId(item.getBrandId());
            brandVo.setBrandName(item.getName());//因为 名字 不一样
            return brandVo;
        }).collect(Collectors.toList());
        return R.ok().put("data",collect);
    }
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
