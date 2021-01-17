package cn.scut.product.service.impl;

import cn.scut.common.constant.ProductConstant;
import cn.scut.common.util.PageUtils;
import cn.scut.common.util.Query;
import cn.scut.product.dao.AttrAttrgroupRelationDao;
import cn.scut.product.dao.AttrDao;
import cn.scut.product.dao.AttrGroupDao;
import cn.scut.product.dao.CategoryDao;
import cn.scut.product.entity.AttrAttrgroupRelationEntity;
import cn.scut.product.entity.AttrEntity;
import cn.scut.product.entity.AttrGroupEntity;
import cn.scut.product.entity.CategoryEntity;
import cn.scut.product.service.AttrAttrgroupRelationService;
import cn.scut.product.service.AttrService;
import cn.scut.product.service.CategoryService;
import cn.scut.product.vo.AttrGroupRelationVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_type", ("sale".equalsIgnoreCase(type) ? ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()));
        if (catelogId != 0) {
            wrapper.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and(obj -> {
                obj.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        //给数据 加上 额外对象
        List<AttrEntity> records = page.getRecords();
        List<AttrEntity> attrEntities = records.stream()
                .map(item -> {
            //TODO 基础属性 才有分组信息
                    if("base".equalsIgnoreCase(type)){//基础属性 才有 分组
                        AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", item.getAttrId()));
                        if (Objects.nonNull(relationEntity) && relationEntity.getAttrGroupId()!=null) {
                            AttrGroupEntity groupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                            item.setGroupName(groupEntity.getAttrGroupName());//取到分组名字
                        }
                    }
                    CategoryEntity categoryEntity = categoryDao.selectById(item.getCatelogId());
                    if (Objects.nonNull(categoryEntity)) {
                        item.setCatelogName(categoryEntity.getName());//取到分类的名字
                    }
                    return item;
        }).collect(Collectors.toList());
        pageUtils.setList(attrEntities);
        return pageUtils;
    }

    @Override
    public void saveAttr(AttrEntity attrEntity) {
        this.save(attrEntity);
        // 基本属性才需要 保存 关联信息
        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            Long attrId = attrEntity.getAttrId();
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attrId);
            attrAttrgroupRelationEntity.setAttrGroupId(attrEntity.getAttrGroupId());

            this.attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }
    }

    @Override
    public AttrEntity getAttrInfo(Long attrId) {
        AttrEntity attrEntity = this.getById(attrId);
        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
            if (Objects.nonNull(attrAttrgroupRelationEntity)) {
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectOne(new QueryWrapper<AttrGroupEntity>().eq("attr_group_id", attrAttrgroupRelationEntity.getAttrGroupId()));
                if (Objects.nonNull(attrGroupEntity)) {
                    attrEntity.setGroupName(attrGroupEntity.getAttrGroupName());
                    attrEntity.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
                }
            }
        }

        CategoryEntity categoryEntity = categoryDao.selectOne(new QueryWrapper<CategoryEntity>().eq("cat_id", attrEntity.getCatelogId()));
        attrEntity.setCatelogName(categoryEntity.getName());
        Long[] catelogPath = categoryService.findCatelogPath(attrEntity.getCatelogId());
        attrEntity.setCatelogPath(catelogPath);
        return attrEntity;
    }

    @Override
    public void updateAttr(AttrEntity attrEntity) {
        this.updateById(attrEntity);
        //修改分组关联
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
        attrAttrgroupRelationEntity.setAttrGroupId(attrEntity.getAttrGroupId());//修改的字段
        attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
        //这里有可能是新增 或者 修改
        Integer count = attrAttrgroupRelationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
        if (count > 0) {//修改
            attrAttrgroupRelationDao.update(attrAttrgroupRelationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
        } else {//否则新增
            attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }
    }

    @Override
    public List<AttrEntity> getRelationAttr(Long attrGroupId) {
        List<AttrAttrgroupRelationEntity> attrAttrGroupRelationEntities = attrAttrgroupRelationDao
                .selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrGroupId));
        List<Long> attrIds = attrAttrGroupRelationEntities.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(attrIds)) {
            return null;
        }
        List<AttrEntity> attrEntities = attrService.listByIds(attrIds);
        return attrEntities;
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        List<AttrAttrgroupRelationEntity> collect = Arrays.stream(vos).map(vo -> {
            AttrAttrgroupRelationEntity attrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(vo, attrgroupRelationEntity);
            return attrgroupRelationEntity;
        }).collect(Collectors.toList());
         attrAttrgroupRelationDao.deleteBatchRelation(collect);
//        attrAttrgroupRelationDao.delete(new QueryWrapper<>(AttrAttrgroupRelationEntity).eq("attr_id",).eq("attr_group_id",));
        //TODO 批量删除 DELETE FROM `pms_attr_attrgroup_relation` where (attr_id=1 AND attr_group_id=1) OR (attr_id=1 AND attr_group_id=1) or (attr_id=1 AND attr_group_id=1)

    }

    //获取当前分组没有关联的所有属性
    //TODO 1 当前分组 只能关联 自己所属分类里面的所有属性
    //TODO 2 当前分组 只能关联 别的分组没有引用的基础属性
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrGroupId) {
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        // 当前分类下的其他分组
        List<AttrGroupEntity> groups = attrGroupDao
                .selectList(
                        new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        // 这些分组的关联的属性
        List<Long> groupIds = groups.stream().map(group -> {
            return group.getAttrGroupId();
        }).collect(Collectors.toList());
        List<AttrAttrgroupRelationEntity> attrgroupRelationEntities = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id",groupIds));
        //  得到这些全部属性
        List<Long> attrIds = attrgroupRelationEntities.stream().map(attrAttrgroupRelationEntity -> {
            return attrAttrgroupRelationEntity.getAttrId();
        }).collect(Collectors.toList());
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id", catelogId)
                .eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        // 从当前分类的所有属性中移除这些属性
        if(!CollectionUtils.isEmpty(attrIds)){
            wrapper.notIn("attr_id", attrIds);
        }
        String key = (String) params.get("key");
        if(StringUtils.isNotEmpty(key)){
            wrapper.and((wrap)->{
                wrap.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    @Override
    public void saveRelation(AttrGroupRelationVo[] vos) {
        List<AttrAttrgroupRelationEntity> collect = Arrays.stream(vos).map(item -> {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, attrAttrgroupRelationEntity);
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());
        attrAttrgroupRelationService.saveBatch(collect);
    }
    //选出  检索属性  attr.getSearchType() == 1
    @Override
    public List<Long> selectSearchAttrs(List<Long> attrIds) {
        List<AttrEntity> attrEntities = this.baseMapper.selectBatchIds(attrIds);
        List<Long> longs = attrEntities.stream()
                .filter(attr -> {
                    return attr.getSearchType() == 1;
                })
                .map(attr -> {
                    return attr.getAttrId();
                }).collect(Collectors.toList());
        return longs;
    }
}
