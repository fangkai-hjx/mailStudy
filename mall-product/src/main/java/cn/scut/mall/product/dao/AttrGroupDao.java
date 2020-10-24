package cn.scut.mall.product.dao;

import cn.scut.mall.product.entity.AttrGroupEntity;
import cn.scut.mall.product.vo.SkuItemVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 10:45:17
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SkuItemVo.SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(@Param("spuId") Long spuId, @Param("catelogId") Long catelogId);
}
