package cn.scut.mall.product.service;

import cn.scut.mall.product.vo.Catalog2Vo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.scut.common.utils.PageUtils;
import cn.scut.mall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 10:45:17
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);

    Long[] findCatelogPath(Long catelogId);

    void updateCascade(CategoryEntity category);

    List<CategoryEntity> getLevel1Categorys();

    Map<String, List<Catalog2Vo>> getCatalogJson();
}

