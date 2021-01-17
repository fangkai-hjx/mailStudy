package cn.scut.coupon.service;


import cn.scut.common.to.SkuReductionTo;
import cn.scut.coupon.entity.SkuFullReductionEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {
    void saveSkuReduction(SkuReductionTo skuReductionTo);
}
