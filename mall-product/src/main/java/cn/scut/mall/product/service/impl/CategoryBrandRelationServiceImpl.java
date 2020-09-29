package cn.scut.mall.product.service.impl;

import cn.scut.mall.product.dao.BrandDao;
import cn.scut.mall.product.dao.CategoryDao;
import cn.scut.mall.product.entity.BrandEntity;
import cn.scut.mall.product.entity.CategoryEntity;
import cn.scut.mall.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.Query;

import cn.scut.mall.product.dao.CategoryBrandRelationDao;
import cn.scut.mall.product.entity.CategoryBrandRelationEntity;
import cn.scut.mall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    BrandDao brandDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        //查询分类名字 和 品牌名字
        BrandEntity brandEntity = brandDao.selectById(categoryBrandRelation.getBrandId());
        CategoryEntity categoryEntity = categoryDao.selectById(categoryBrandRelation.getCatelogId());

        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
//        this.baseMapper.insert(categoryBrandRelation);//调用自己的的dao
        this.save(categoryBrandRelation);//或者直接调用service的save方法，一样的
    }

    @Override
    public void updateBrand(Long brandId, String name) {
        CategoryBrandRelationEntity brandRelationEntity = new CategoryBrandRelationEntity();
        brandRelationEntity.setBrandId(brandId);
        brandRelationEntity.setBrandName(name);
        this.update(brandRelationEntity,new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId));
    }

    @Override
    public void updateCategory(Long catId, String name) {
        this.baseMapper.updateCategory(catId,name);
    }

    /**
     * 这样写 是 为了 重用 这个 方法
     * 将 信息 封装为 brand  ，因为 有些请求 需要 获得 brand 信息
     * @param catId
     * @return
     */
    @Override
    public List<BrandEntity> getBrandsByCatId(Long catId) {
        List<CategoryBrandRelationEntity> relationEntityList = categoryBrandRelationService.list(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
        List<BrandEntity> collect = relationEntityList.stream().map(item -> {
            BrandEntity brandEntity = brandService.getById(item.getBrandId());
            return brandEntity;
        }).collect(Collectors.toList());
        return collect;
    }

}