package cn.scut.product.service;

import cn.scut.common.util.PageUtils;
import cn.scut.product.entity.SkuInfoEntity;
import cn.scut.product.entity.SpuInfoEntity;
import cn.scut.product.vo.SpuSaveVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface SpuInfoService extends IService<SpuInfoEntity> {
    PageUtils queryPageByCondition(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo spuSaveVo);

    void up(Long spuId);
}
