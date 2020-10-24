package cn.scut.mall.product.service;

import cn.scut.mall.product.entity.SpuInfoEntity;
import cn.scut.mall.product.vo.SkuItemVo;
import cn.scut.mall.product.vo.SpuSaveVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.scut.common.utils.PageUtils;
import cn.scut.mall.product.entity.SkuInfoEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 10:45:17
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils 
    queryPage(Map<String, Object> params);


    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    List<SkuInfoEntity> getSkusBySpuId(Long spuId);

    SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException;

}

