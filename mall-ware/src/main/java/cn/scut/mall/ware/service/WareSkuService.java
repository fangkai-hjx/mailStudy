package cn.scut.mall.ware.service;

import cn.scut.common.to.mq.OrderTo;
import cn.scut.common.to.mq.StockLockedTo;
import cn.scut.mall.ware.vo.LockStockResult;
import cn.scut.mall.ware.vo.SkuHasStockVo;
import cn.scut.mall.ware.vo.WareSkuLockVo;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.scut.common.utils.PageUtils;
import cn.scut.mall.ware.entity.WareSkuEntity;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 12:48:49
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCondition(Map<String, Object> params);

    void addStock(Long wareId, Long skuId, Integer skuNum);

    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    Boolean orderLockStock(WareSkuLockVo vo);

    void unLockStock(StockLockedTo to);

    void unLockStock(OrderTo to);
}

