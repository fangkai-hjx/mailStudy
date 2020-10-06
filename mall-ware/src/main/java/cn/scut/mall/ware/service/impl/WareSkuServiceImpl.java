package cn.scut.mall.ware.service.impl;

import cn.scut.common.utils.R;
import cn.scut.mall.ware.feign.ProductFeignService;
import cn.scut.mall.ware.vo.SkuHasStockVo;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.Query;

import cn.scut.mall.ware.dao.WareSkuDao;
import cn.scut.mall.ware.entity.WareSkuEntity;
import cn.scut.mall.ware.service.WareSkuService;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

    //skuId=&wareId=
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String skuId = (String)params.get("skuId");
        if(StringUtils.isNotEmpty(skuId)){
            wrapper.eq("sku_id",skuId);
        }
        String wareId = (String)params.get("wareId");
        if(StringUtils.isNotEmpty(wareId)){
            wrapper.eq("ware_id",wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public void addStock(Long wareId, Long skuId, Integer skuNum) {
        //判断如果 没有 这个 库存 记录 -----------新增
        //判断如果   有 这个 库存 记录 -----------更新
        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if(CollectionUtils.isNotEmpty(wareSkuEntities)){
            wareSkuDao.addStock(wareId,skuId,skuNum);
        }else {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //TODO 远程查询 sku 的 名字,如果失败 不需要回滚
            //TODO 还可以用什么 方法 让 出现 异常 不回滚
           try{//如果 失败了 继续 往下走，不回滚，自己catch掉 异常，不抛出去就可以
               R info = productFeignService.info(skuId);
               if(info.getCode()==0){
                   Map<String,Object> data = (Map<String, Object>) info.get("skuInfo");
                   wareSkuEntity.setSkuName((String) data.get("skuName"));
               }
           }catch (Exception e){
               log.error("查询 sku 名字 失败！");
           }
            wareSkuDao.insert(wareSkuEntity);
        }
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> list = skuIds.stream().map(skuId->{
            SkuHasStockVo stockVo = new SkuHasStockVo();
            //去数据库查询
            //SELECT SUM(stock-stock_locked) FROM `wms_ware_sku` WHERE sku_id = 49
            Long count = this.baseMapper.getSkuStock(skuId);
            stockVo.setSkuId(skuId);
            stockVo.setHasStock(count==null?false:count>0);
            return stockVo;
        }).collect(Collectors.toList());
        return list;
    }

}