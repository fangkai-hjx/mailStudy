package cn.scut.mall.ware.service.impl;

import cn.scut.common.to.mq.StockLockedTo;
import cn.scut.common.utils.R;
import cn.scut.mall.ware.entity.WareOrderTaskDetailEntity;
import cn.scut.mall.ware.entity.WareOrderTaskEntity;
import cn.scut.mall.ware.exception.NoStockException;
import cn.scut.mall.ware.feign.ProductFeignService;
import cn.scut.mall.ware.service.WareOrderTaskDetailService;
import cn.scut.mall.ware.service.WareOrderTaskService;
import cn.scut.mall.ware.vo.LockStockResult;
import cn.scut.mall.ware.vo.OrderItemVo;
import cn.scut.mall.ware.vo.SkuHasStockVo;
import cn.scut.mall.ware.vo.WareSkuLockVo;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.x509.NoSuchStoreException;
import org.checkerframework.checker.units.qual.A;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private WareOrderTaskService wareOrderTaskService;

    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;

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
        String skuId = (String) params.get("skuId");
        if (StringUtils.isNotEmpty(skuId)) {
            wrapper.eq("sku_id", skuId);
        }
        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotEmpty(wareId)) {
            wrapper.eq("ware_id", wareId);
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
        if (CollectionUtils.isNotEmpty(wareSkuEntities)) {
            wareSkuDao.addStock(wareId, skuId, skuNum);
        } else {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //TODO 远程查询 sku 的 名字,如果失败 不需要回滚
            //TODO 还可以用什么 方法 让 出现 异常 不回滚
            try {//如果 失败了 继续 往下走，不回滚，自己catch掉 异常，不抛出去就可以
                R info = productFeignService.info(skuId);
                if (info.getCode() == 0) {
                    Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            } catch (Exception e) {
                log.error("查询 sku 名字 失败！");
            }
            wareSkuDao.insert(wareSkuEntity);
        }
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> list = skuIds.stream().map(skuId -> {
            SkuHasStockVo stockVo = new SkuHasStockVo();
            //去数据库查询
            //SELECT SUM(stock-stock_locked) FROM `wms_ware_sku` WHERE sku_id = 49
            Long count = this.baseMapper.getSkuStock(skuId);
            stockVo.setSkuId(skuId);
            stockVo.setHasStock(count == null ? false : count > 0);
            return stockVo;
        }).collect(Collectors.toList());
        return list;
    }

    /**
     *默认只要是运行时 异常 都会回滚
     *
     * 库存解锁的场景：
     *      1）下订单成功，订单过期没有支付被系统自动取消，被用户主动取消，都要解锁库存
     *      2）下订单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚。
     *              之前  锁定的库存 就要  自动解锁（使用seata分布式事务太慢了）
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = NoStockException.class)
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {
        /**
         * 保存 库存工作单的详情
         * 追朔
         */
        WareOrderTaskEntity task = new WareOrderTaskEntity();
        task.setOrderSn(vo.getOrderSn());
        wareOrderTaskService.save(task);
        //1 按照 下单的收货地址，找到一个就近仓库，锁定库存-------------这里不做了
        //TODO 找到每个商品在那个仓库都有库存
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            //查询这个商品在哪些仓库有库存
            List<Long> list = wareSkuDao.listWareIdHasStock(skuId);
            stock.setWareId(list);
            return stock;
        }).collect(Collectors.toList());
        //TODO 锁定库存
        for (SkuWareHasStock hasStock : collect) {
            Boolean skuStocked = false;//当前sku锁定标志位
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();
            if (CollectionUtils.isEmpty(wareIds)) {
                //没有如何仓库有库存
                throw new NoStockException(skuId);
            }
            //1 如果 每 个 商品 都 锁成功，将当前商品锁定了几件的工作单记录发送给MQ
            //2 如果锁定失败。前面保存的 工作单消息就回滚了。发送出去的消息，即使要解锁记录，由于去数据库查不到ud，所以就不用解锁
            for (Long wareId : wareIds) {
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, hasStock.getNum());
                if (count == 1) {
                    skuStocked = true;
                    //TODO 告诉 MQ 库存锁定成功
                    //每锁一个 库存 生成 一个 工作单
                    WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity(null,skuId,"",hasStock.getNum(),task.getId(),wareId,1);
                    wareOrderTaskDetailService.save(entity);
                    StockLockedTo lockedTo = new StockLockedTo();
                    lockedTo.setId(task.getId());
                    lockedTo.setDetailId(entity.getId());
                    rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",lockedTo);
                    break;
                }
                //当前仓库锁定失败，重试下一个仓库

            }
            if(skuStocked == false){//当前商品所有仓库都锁定失败
                throw new NoStockException(skuId);
            }
        }
        //3 肯定全部都是锁定成功的
        return true;
    }

    //sku在哪些仓库有库存
    @Data
    class SkuWareHasStock {
        private Long skuId;
        private List<Long> wareId;
        private Integer num;
    }
}