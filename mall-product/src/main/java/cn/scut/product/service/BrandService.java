package cn.scut.product.service;

import cn.scut.common.util.PageUtils;
import cn.scut.product.entity.BrandEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface BrandService extends IService<BrandEntity> {
    PageUtils queryPage(Map<String, Object> param);

    void updateDetail(BrandEntity brandEntity);
}
