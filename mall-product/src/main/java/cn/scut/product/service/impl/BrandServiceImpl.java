package cn.scut.product.service.impl;

import cn.scut.common.util.PageUtils;
import cn.scut.common.util.Query;
import cn.scut.product.dao.BrandDao;
import cn.scut.product.entity.BrandEntity;
import cn.scut.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {
    @Override
    public PageUtils queryPage(Map<String, Object> param) {
        String key = (String)param.get("key");
        //构造查询参数
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(key)){
            queryWrapper.eq("brand_id",key).or().like("name",key);
        }
        IPage<BrandEntity> page = this.page(new Query<BrandEntity>().getPage(param), queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public void updateDetail(BrandEntity brandEntity) {
        this.updateById(brandEntity);
        //TODO 修改 分类----品牌  关连表
    }
}
