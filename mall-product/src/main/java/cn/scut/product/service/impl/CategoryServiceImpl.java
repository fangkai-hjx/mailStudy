package cn.scut.product.service.impl;

import cn.scut.common.util.PageUtils;
import cn.scut.product.dao.CategoryDao;
import cn.scut.product.entity.CategoryEntity;
import cn.scut.product.service.CategoryService;
import cn.scut.product.vo.Catalog2Vo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<CategoryEntity> listWithTree() {
        //1 查出所有分类
        List<CategoryEntity> categoryEntities = this.baseMapper.selectList(null);
        //2 组装为父子结构的树形结构
        //2.1 先收集一级分类
        List<CategoryEntity> fathers = categoryEntities
                .stream().filter(category -> category.getParentCid() == 0)
                .map(father -> {
                    father.setChildren(getChildren(father, categoryEntities));
                    return father;
                })
                .sorted((x1, x2) -> {
                    return (x1.getSort() == null ? 0 : x1.getSort()) - (x2.getSort() == null ? 0 : x2.getSort());
                })
                .collect(Collectors.toList());
        return fathers;
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);//【225，25，2】
        Collections.reverse(parentPath);//【2，25，225】
        return paths.toArray(new Long[parentPath.size()]);
    }

    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        List<CategoryEntity> list = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return list;
    }

    public  Map<String, List<Catalog2Vo>> getCatalogJsonFromDB() {
        synchronized (this){
            ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
            String catalogJSON = opsForValue.get("catalogJSON");
            if(StringUtils.isNotEmpty(catalogJSON)){
                Map<String, List<Catalog2Vo>>  catalogs = JSON.parseObject(catalogJSON,new TypeReference<Map<String, List<Catalog2Vo>>>(){});
                return catalogs;
            }
            System.out.println("查询数据库==============================");
            List<CategoryEntity> categoryEntities = this.list();//先查询全部的分类信息
            List<CategoryEntity> level1Categorys = getParentFromCid(categoryEntities, 0L);
            Map<String, List<Catalog2Vo>> collect = level1Categorys.stream().collect(Collectors.toMap(x -> x.getCatId().toString(), l1Item -> {

                List<CategoryEntity> categoryEntities2 = getParentFromCid(categoryEntities,l1Item.getCatId());
                List<Catalog2Vo> level2Categorys = new ArrayList<>();
                if (!CollectionUtils.isEmpty(categoryEntities2)) {
                    level2Categorys = categoryEntities2.stream().map(l2Item -> {
                        Catalog2Vo catalog2Vo = new Catalog2Vo();
                        catalog2Vo.setCatalog1Id(l2Item.getParentCid().toString());
                        catalog2Vo.setId(l2Item.getCatId().toString());
                        catalog2Vo.setName(l2Item.getName());

                        List<CategoryEntity> categoryEntities3 =  getParentFromCid(categoryEntities,l2Item.getCatId());
                        List<Catalog2Vo.Catalog3Vo> catalog3VoList = new ArrayList<>();
                        if (!CollectionUtils.isEmpty(categoryEntities3)) {
                            catalog3VoList = categoryEntities3.stream().map(l3Item -> {
                                Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo();
                                catalog3Vo.setCatalog2Id(l3Item.getParentCid().toString());
                                catalog3Vo.setName(l3Item.getName());
                                catalog3Vo.setId(l3Item.getCatId().toString());
                                return catalog3Vo;
                            }).collect(Collectors.toList());
                        }

                        catalog2Vo.setCatalog3List(catalog3VoList);
                        return catalog2Vo;
                    }).collect(Collectors.toList());
                }
                return level2Categorys;
            }));
            String catalogs = JSON.toJSONString(collect);
            opsForValue.set("catalogJSON",catalogs);
            return collect;
        }

    }
    private List<CategoryEntity> getParentFromCid(List<CategoryEntity> categoryEntities,Long cid){
        List<CategoryEntity> collect = categoryEntities.stream().filter(item -> {
            return item.getParentCid() == cid;
        }).collect(Collectors.toList());
        return collect;
    }
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson(){
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        String catalogJSON = opsForValue.get("catalogJSON");
        if(StringUtils.isNotEmpty(catalogJSON)){
            System.out.println("查询缓存");
            Map<String, List<Catalog2Vo>>  catalogs = JSON.parseObject(catalogJSON,new TypeReference<Map<String, List<Catalog2Vo>>>(){});
            return catalogs;
        }else {
            Map<String, List<Catalog2Vo>> catalogJsonFromDB = getCatalogJsonFromDB();
            return catalogJsonFromDB;
        }
    }
    //递归查询父节点
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        CategoryEntity categoryEntity = this.getById(catelogId);
        paths.add(catelogId);
        if (categoryEntity.getParentCid() != 0) {
            findParentPath(categoryEntity.getParentCid(), paths);
        }
        return paths;
    }


    //获取某一个菜单的子菜单
    //从 all 中 找出 root 的 所有 子菜单
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> list = all.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == root.getCatId())
                .map(categoryEntity -> {//TODO 注意这里map函数以及要求categoryEntity不为空 ，为空则不回这些内部递归
                    categoryEntity.setChildren(getChildren(categoryEntity, all));
                    return categoryEntity;
                })
                .sorted((x1, x2) -> {
                    return (x1.getSort() == null ? 0 : x1.getSort()) - (x2.getSort() == null ? 0 : x2.getSort());
                })
                .collect(Collectors.toList());

        return list;
    }
}
