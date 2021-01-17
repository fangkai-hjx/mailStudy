package cn.scut.ware.service.impl;

import cn.scut.common.util.PageUtils;
import cn.scut.common.util.Query;
import cn.scut.common.util.R;
import cn.scut.ware.dao.WareInfoDao;
import cn.scut.ware.dao.WareSkuDao;
import cn.scut.ware.entity.WareInfoEntity;
import cn.scut.ware.entity.WareSkuEntity;
import cn.scut.ware.feign.ProductFeignService;
import cn.scut.ware.service.WareInfoService;
import cn.scut.ware.service.WareSkuService;
import cn.scut.ware.vo.SkuHasStock;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private ProductFeignService productFeignService;

    //    skuId=&wareId=
    @Override
    public PageUtils queryByCondition(Map<String, Object> param) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String skuId = (String) param.get("skuId");
        if (StringUtils.isNotEmpty(skuId)) {
            wrapper.eq("sku_id", skuId);
        }
        String wareId = (String) param.get("wareId");
        if (StringUtils.isNotEmpty(wareId)) {
            wrapper.eq("ware_id", wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(param),
                wrapper);
        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //TODO 这里 可能是 新增 或者 修改，一开始没有该sku的库存，此时为新增
        //1 如果没有该sku的库存
        Integer integer = wareSkuDao.selectCount(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (integer == 0) {//新增
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //TODO 远程查询sku的名字  这样处理 就可以使得不用回滚了
            //TODO 还可以使用什么方法可以让异常出现以后不会滚？
            try {
                R info = productFeignService.info(skuId);
                if (info.getCode() == 0) {
                    Map<String, Object> map = (Map<String, Object>) info.get("skuInfo");
                    wareSkuEntity.setSkuName((String) map.get("skuName"));
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            wareSkuDao.insert(wareSkuEntity);
        } else {//修改
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }
    }
    //SELECT SUM(stock-stock_locked) FROM `wms_ware_sku` WHERE sku_id = 1
    @Override
    public List<SkuHasStock> getSkusHasStock(List<Long> skuIds) {
        List<SkuHasStock> hasStocks = skuIds.stream().map(skuId -> {
            Long count = wareSkuDao.getSkuStock(skuId);
            SkuHasStock skuHasStock = new SkuHasStock();
            skuHasStock.setSkuId(skuId);
            skuHasStock.setHasStock(count==null?false:count > 0);
            return skuHasStock;
        }).collect(Collectors.toList());

        return hasStocks;
    }
}
