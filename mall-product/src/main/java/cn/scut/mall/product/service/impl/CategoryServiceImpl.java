package cn.scut.mall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.Query;

import cn.scut.mall.product.dao.CategoryDao;
import cn.scut.mall.product.entity.CategoryEntity;
import cn.scut.mall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


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
     * @return
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        return  baseMapper.selectList(null);//没有查询条件;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //以后要检查
        //TODO 1，检查当前删除的菜单，是否被其他地方引用
        //开发期间使用逻辑删除
        baseMapper.deleteBatchIds(asList);//批量删除
    }

}