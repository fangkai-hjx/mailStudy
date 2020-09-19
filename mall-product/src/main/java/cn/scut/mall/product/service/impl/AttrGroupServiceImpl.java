package cn.scut.mall.product.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.Query;

import cn.scut.mall.product.dao.AttrGroupDao;
import cn.scut.mall.product.entity.AttrGroupEntity;
import cn.scut.mall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        //select * from pms_attr_group where category_id =? and (attr_group_name =key 或者 attr_group_name like %key%)
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<AttrGroupEntity>();
        String key = (String) params.get("key");//如果带上检索条件
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((obj) -> {
                obj.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }
        if (catelogId == 0) {//如果传过来的分类id为0，查询全部
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),//工具类，将用户传入的参数取出 封装为Page对象
                    queryWrapper//查询信息
            );
            return new PageUtils(page);
        }//按照三级分类查询
        queryWrapper = queryWrapper.eq("catelog_id", catelogId);
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),//工具类，将用户传入的参数取出 封装为Page对象
                queryWrapper//查询信息
        );
        return new PageUtils(page);
    }
}