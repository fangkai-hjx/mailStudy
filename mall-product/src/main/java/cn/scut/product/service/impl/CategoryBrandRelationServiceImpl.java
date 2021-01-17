package cn.scut.product.service.impl;

import cn.scut.product.dao.BrandDao;
import cn.scut.product.dao.CategoryBrandRelationDao;
import cn.scut.product.dao.CategoryDao;
import cn.scut.product.entity.BrandEntity;
import cn.scut.product.entity.CategoryBrandRelationEntity;
import cn.scut.product.entity.CategoryEntity;
import cn.scut.product.service.CategoryBrandRelationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private BrandDao brandDao;


    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        BrandEntity brandEntity = brandDao.selectOne(new QueryWrapper<BrandEntity>().eq("brand_id", brandId));
        Long catelogId = categoryBrandRelation.getCatelogId();
        CategoryEntity categoryEntity = categoryDao.selectOne(new QueryWrapper<CategoryEntity>().eq("cat_id", catelogId));
        categoryBrandRelation.setBrandName(brandEntity.getBrandName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        this.save(categoryBrandRelation);
    }

    @Override
    public List<BrandEntity> getBrandsByCatId(Long catId) {
        List<CategoryBrandRelationEntity> relationEntityList = this.list(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
        List<BrandEntity> collect = relationEntityList.stream().map(item -> {
            BrandEntity brandEntity = brandDao.selectById(item.getBrandId());
            return brandEntity;
        }).collect(Collectors.toList());
        return collect;
    }
}
