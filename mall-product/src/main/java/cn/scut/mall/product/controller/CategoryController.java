package cn.scut.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.scut.mall.product.entity.CategoryEntity;
import cn.scut.mall.product.service.CategoryService;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.R;


/**
 * 商品三级分类
 *
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 11:09:21
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查询所有分类 以及子分类，以树形结构组装起来
     */
    @RequestMapping("/list/tree")
    public R list(@RequestParam Map<String, Object> params) {
        //1,查询所有分类
        List<CategoryEntity> entities = categoryService.listWithTree();
        //2组装成父子的树形结构
        //2.1 查找到一级分类
        List<CategoryEntity> level1Menus = entities
                .stream()//
                .filter(s -> s.getParentCid() == 0)//过滤 cid==0 的节点,把一级分类剔除掉
                .map(s -> {s.setChildren(getChildrens(s, entities));return s; })//递归设置子节点
                .sorted((s, v) -> {return s.getSort() - v.getSort();})
                .collect(Collectors.toList());
        //2.2 查找到二级分类 保存到指定的一级分类
//        level1Menus.stream()
        return R.ok().put("data", level1Menus);
    }

    /**
     * 递归查询 子节点
     * @param root
     * @param all
     * @return
     */
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream()
                .filter(s -> s.getParentCid() == root.getCatId())
                .map(s -> {
                    s.setChildren(getChildrens(s, all));
                    return s;
                })
                .sorted((s, v) -> {
                    return (s.getSort() == null ? 0 : s.getSort()) - (v.getSort() == null ? 0 : v.getSort());
                })
                .collect(Collectors.toList());
        return children;
    }


    /**
     * 根据 catId 发送 请求
     */
    @RequestMapping("/info/{catId}")
    public R info(@PathVariable("catId") Long catId) {
        CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryEntity category) {
        categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryEntity category) {
        categoryService.updateCascade(category);
        return R.ok();
    }

    /**
     * 删除
     * RequestBody:获取请求体，Pots请求
     * SpringMvc会自动将请求体的数据json转化为
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] catIds) {
        //1.检查当前删除的菜单，是否被别的地方引用
//        categoryService.removeByIds(Arrays.asList(catIds));
        categoryService.removeMenuByIds(Arrays.asList(catIds));
        return R.ok();
    }

    /**
     * 批量修改
     */
    @RequestMapping("/update/sort")
    public R updateSort(@RequestBody CategoryEntity[] category) {
        categoryService.updateBatchById(Arrays.asList(category));//批量修改
        return R.ok();
    }
}
