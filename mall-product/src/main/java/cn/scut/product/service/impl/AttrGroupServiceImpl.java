package cn.scut.product.service.impl;

import cn.scut.common.util.PageUtils;
import cn.scut.common.util.Query;
import cn.scut.product.dao.AttrAttrgroupRelationDao;
import cn.scut.product.dao.AttrGroupDao;
import cn.scut.product.entity.AttrAttrgroupRelationEntity;
import cn.scut.product.entity.AttrEntity;
import cn.scut.product.entity.AttrGroupEntity;
import cn.scut.product.service.AttrGroupService;
import cn.scut.product.service.AttrService;
import cn.scut.product.vo.AttrGroupWithAttrsVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        //TODO  select * from pms_attr_group where category_id =? and (attr_group_id =key or attr_group_name like %key%)
        //TODO  select * from pms_attr_group where category_id =? and attr_group_id =key or attr_group_name like %key%
        //TODO 这两条语句是不一样的
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<AttrGroupEntity>();
        if(catelogId == 0){//查询全部
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    queryWrapper
            );

            return new PageUtils(page);
        }
        queryWrapper.eq("catelog_id",catelogId);//select * from pms_attr_group where category_id =?
        String key = (String) params.get("key");
        if(StringUtils.isNotEmpty(key)){
            //TODO 这是错误的做法
//           queryWrapper.eq("attr_group_id",key).or().like("attr_group_name",key);
            queryWrapper.and((obj)->{
                obj.eq("attr_group_id",key).or().like("attr_group_name",key);
            });
        }
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCategory(Long catelogId) {
        //查询分组信息
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        //查询所有属性
        List<AttrGroupWithAttrsVo> collect = attrGroupEntities.stream().map(group -> {
            AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(group, attrGroupWithAttrsVo);

            List<AttrEntity> attrList = attrService.getRelationAttr(group.getAttrGroupId());
            attrGroupWithAttrsVo.setAttrs(attrList);
            return attrGroupWithAttrsVo;
        }).collect(Collectors.toList());
        return collect;
    }

}
