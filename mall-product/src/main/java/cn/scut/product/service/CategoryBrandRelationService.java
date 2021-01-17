package cn.scut.product.service;

import cn.scut.product.entity.BrandEntity;
import cn.scut.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {
    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    List<BrandEntity> getBrandsByCatId(Long catId);
}
