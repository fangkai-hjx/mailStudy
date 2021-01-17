package cn.scut.product.service;

import cn.scut.common.util.PageUtils;
import cn.scut.product.entity.AttrGroupEntity;
import cn.scut.product.vo.AttrGroupWithAttrsVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface AttrGroupService extends IService<AttrGroupEntity> {
    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCategory(Long catelogId);

}
