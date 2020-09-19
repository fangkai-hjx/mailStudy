package cn.scut.mall.product.dao;

import cn.scut.mall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 10:45:17
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}