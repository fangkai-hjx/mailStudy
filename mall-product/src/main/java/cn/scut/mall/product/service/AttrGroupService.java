package cn.scut.mall.product.service;

import cn.scut.mall.product.vo.AttrGroupWithAttrsVo;
import cn.scut.mall.product.vo.SkuItemVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.scut.common.utils.PageUtils;
import cn.scut.mall.product.entity.AttrGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 10:45:17
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCategory(Long catelogId);

    List<SkuItemVo.SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long id, Long catelogId);
}

