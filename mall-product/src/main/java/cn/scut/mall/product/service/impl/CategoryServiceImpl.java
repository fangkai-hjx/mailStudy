package cn.scut.mall.product.service.impl;

import cn.scut.mall.product.entity.CategoryBrandRelationEntity;
import cn.scut.mall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.Query;

import cn.scut.mall.product.dao.CategoryDao;
import cn.scut.mall.product.entity.CategoryEntity;
import cn.scut.mall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 注意 这里的baseMap相当于自己的dao层
     *
     * @return
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        return baseMapper.selectList(null);//没有查询条件;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //以后要检查
        //TODO 1，检查当前删除的菜单，是否被其他地方引用
        //开发期间使用逻辑删除
        baseMapper.deleteBatchIds(asList);//批量删除
    }

    /**
     * 找到catelogId的完整路径【父/子/孙】
     *
     * @param catelogId
     * @return
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);//【255，25，2】
        Collections.reverse(parentPath);//【2，25，233】
        return paths.toArray(new Long[parentPath.size()]);
    }

    //级联更新所有关联的数据
    @Override
    @Transactional//加上事务
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);//更新自己
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());//更新关联表数据
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //收集当前节点id
        paths.add(catelogId);
        CategoryEntity categoryEntity = this.getById(catelogId);//根据 Id 查出当前分类的信息
        if (categoryEntity.getParentCid() != 0) {
            findParentPath(categoryEntity.getParentCid(), paths);
        }
        return paths;
    }
}