package cn.scut.product.service;

import cn.scut.common.util.PageUtils;
import cn.scut.product.entity.AttrEntity;
import cn.scut.product.vo.AttrGroupRelationVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface AttrService extends IService<AttrEntity> {
    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    void saveAttr(AttrEntity attr);

    AttrEntity getAttrInfo(Long attrId);

    void updateAttr(AttrEntity attr);

    List<AttrEntity> getRelationAttr(Long attrGroupId);

    void deleteRelation(AttrGroupRelationVo[] vos);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrGroupId);

    void saveRelation(AttrGroupRelationVo[] vos);

    List<Long> selectSearchAttrs(List<Long> attrIds);
}
