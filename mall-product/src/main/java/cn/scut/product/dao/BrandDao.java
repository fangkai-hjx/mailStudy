package cn.scut.product.dao;


import cn.scut.product.entity.BrandEntity;
import cn.scut.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BrandDao extends BaseMapper<BrandEntity> {
}
