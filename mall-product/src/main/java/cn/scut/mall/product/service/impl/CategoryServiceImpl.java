package cn.scut.mall.product.service.impl;

import cn.scut.mall.product.entity.CategoryBrandRelationEntity;
import cn.scut.mall.product.service.CategoryBrandRelationService;
import cn.scut.mall.product.vo.Catalog2Vo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.Query;

import cn.scut.mall.product.dao.CategoryDao;
import cn.scut.mall.product.entity.CategoryEntity;
import cn.scut.mall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 注意 这里的baseMap相当于自己的dao层
     *
     * @return
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        return baseMapper.selectList(null);//没有查询条件;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //以后要检查
        //TODO 1，检查当前删除的菜单，是否被其他地方引用
        //开发期间使用逻辑删除
        baseMapper.deleteBatchIds(asList);//批量删除
    }

    /**
     * 找到catelogId的完整路径【父/子/孙】
     *
     * @param catelogId
     * @return
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);//【255，25，2】
        Collections.reverse(parentPath);//【2，25，233】
        return paths.toArray(new Long[parentPath.size()]);
    }

    //级联更新所有关联的数据
    @Override
    @Transactional//加上事务
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);//更新自己
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());//更新关联表数据
    }

    //查询所有 一级分类
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
//        long start = System.currentTimeMillis();
        List<CategoryEntity> entityList = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
//        long end = System.currentTimeMillis();
//        System.out.println("消耗时间:"+ (end-start));
        return entityList;
    }

    /**
     * 从数据库中查询并且封装分类信息
     * 对这个方法进行优化---将数据库的多次查询 变为一次
     * ---再次优化 为  使用redis
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDb() {
        List<CategoryEntity> entities = this.baseMapper.selectList(null);//查出全部

        //1 查出所有1级分类
        List<CategoryEntity> level1Categorys = getParent_cid(entities,0L);//
        Map<String, List<Catalog2Vo>> stringListMap = level1Categorys.stream().collect(Collectors.toMap(x -> x.getCatId().toString(), l1 -> {
            //2 查到 每一个 一级 下 的 二级分类
            List<CategoryEntity> entities2 = getParent_cid(entities,l1.getCatId());
            List<Catalog2Vo> catalog2VoList = null;
            if (CollectionUtils.isNotEmpty(entities2)) {
                catalog2VoList = entities2.stream().map(l2 -> {
                    Catalog2Vo catalog2Vo = new Catalog2Vo(l1.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //3 找到 当前 二级分类 下的 三级分类
                    List<CategoryEntity> entities3 = getParent_cid(entities,l2.getCatId());
                    if(CollectionUtils.isNotEmpty(entities3)){
                        //封装为指定格式的数据
                        List<Catalog2Vo.Catalog3Vo> catalog3VoList = entities3.stream().map(l3 -> {
                            Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catalog3Vo;
                        }).collect(Collectors.toList());
                        catalog2Vo.setCatalog3List(catalog3VoList);//---------------------------三级分类封装成功
                    }
                    return catalog2Vo;
                }).collect(Collectors.toList());
            }
            return catalog2VoList;//--------------------------------二级分类 为 value
        }));
        return stringListMap;
    }

    //注意 这里有个 细节，数据存入的是JSon而不是字节流
    //这样可以做到跨语言跨平台兼容，因为如果是字节流 的 话 ，使用 其他语言拿到 不一定能转化回去
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        //1 加入 缓存中
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if(StringUtils.isEmpty(catalogJson)){
            // 2 说明缓存没有数据，从数据库中查询
            Map<String, List<Catalog2Vo>> catalogJsonFromDb = getCatalogJsonFromDb();
            String jsonString = JSON.toJSONString(catalogJsonFromDb);
            //3 将查到的数据再放入缓存,将查出来的对象转化为Json在放入缓存中
            stringRedisTemplate.opsForValue().set("catalogJson",jsonString);
            return catalogJsonFromDb;
        }
        //转化为 对象
        Map<String, List<Catalog2Vo>> stringListMap = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {});
        return stringListMap;
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList,Long parent_cid) {
//        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", l1.getCatId()));
        List<CategoryEntity> collect = selectList.stream().filter(item -> {
            return item.getParentCid() == parent_cid;//过滤
        }).collect(Collectors.toList());
        return collect;

    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //收集当前节点id
        paths.add(catelogId);
        CategoryEntity categoryEntity = this.getById(catelogId);//根据 Id 查出当前分类的信息
        if (categoryEntity.getParentCid() != 0) {
            findParentPath(categoryEntity.getParentCid(), paths);
        }
        return paths;
    }
}