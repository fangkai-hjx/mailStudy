package cn.scut.mall.product.service.impl;

import cn.scut.mall.product.service.CategoryBrandRelationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.Query;

import cn.scut.mall.product.dao.BrandDao;
import cn.scut.mall.product.entity.BrandEntity;
import cn.scut.mall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        //这里加上品牌的
        String key =(String) params.get("key");
        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<BrandEntity>();
        if(!StringUtils.isEmpty(key)){
            wrapper.eq("brand_id",key).or().like("name",key);
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }
    //保证冗余字段数据
    @Override
    @Transactional//加上事务
    public void updateDetail(BrandEntity brand) {
        //先修改自己表的数据
        this.updateById(brand);
        if(!StringUtils.isEmpty(brand.getName())){
            //同步跟新其他关联表的数据
            categoryBrandRelationService.updateBrand(brand.getBrandId(),brand.getName());//
            //TODO 跟新其他关联信息
        }
    }

}