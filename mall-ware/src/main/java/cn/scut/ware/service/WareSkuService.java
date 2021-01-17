package cn.scut.ware.service;

import cn.scut.common.util.PageUtils;
import cn.scut.ware.entity.WareInfoEntity;
import cn.scut.ware.entity.WareSkuEntity;
import cn.scut.ware.vo.SkuHasStock;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface WareSkuService extends IService<WareSkuEntity> {
    PageUtils queryByCondition(Map<String, Object> param);

    public void addStock(Long skuId, Long wareId,Integer skuNum);

    List<SkuHasStock> getSkusHasStock(List<Long> skuIds);
}
