package cn.scut.product.service;


import cn.scut.common.util.PageUtils;
import cn.scut.product.entity.CategoryEntity;
import cn.scut.product.vo.Catalog2Vo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface CategoryService extends IService<CategoryEntity> {

    List<CategoryEntity> listWithTree();

    Long[] findCatelogPath(Long catelogId);

    List<CategoryEntity> getLevel1Categorys();

    Map<String, List<Catalog2Vo>> getCatalogJson();

    Map<String, List<Catalog2Vo>> getCatalogJsonFromDB();
}
