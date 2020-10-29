package cn.scut.mall.ware.service.impl;

import cn.scut.common.to.mq.OrderTo;
import cn.scut.common.to.mq.StockDetailTo;
import cn.scut.common.to.mq.StockLockedTo;
import cn.scut.common.utils.R;
import cn.scut.mall.ware.entity.WareOrderTaskDetailEntity;
import cn.scut.mall.ware.entity.WareOrderTaskEntity;
import cn.scut.mall.ware.exception.NoStockException;
import cn.scut.mall.ware.feign.OrderFeignService;
import cn.scut.mall.ware.feign.ProductFeignService;
import cn.scut.mall.ware.service.WareOrderTaskDetailService;
import cn.scut.mall.ware.service.WareOrderTaskService;
import cn.scut.mall.ware.vo.*;
import com.alibaba.fastjson.TypeReference;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.rabbitmq.client.Channel;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    @Autowired
    private OrderFeignService orderFeignService;




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
     * 默认只要是运行时 异常 都会回滚
     * <p>
     * 库存解锁的场景：
     * 1）下订单成功，订单过期没有支付被系统自动取消，被用户主动取消，都要解锁库存
     * 2）下订单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚。
     * 之前  锁定的库存 就要  自动解锁（使用seata分布式事务太慢了）
     *
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
        task.setTaskStatus(1);//库存锁定
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
            //1 如果 每 个 商品 都 锁成功，将当前商品锁定了几件的工作单记录发送给MQ(类似日志)
            //2 如果锁定失败。前面保存的 工作单消息就回滚了。发送出去的消息，即使要解锁记录，由于去数据库查不到id，所以就不用解锁
            //
            for (Long wareId : wareIds) {
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, hasStock.getNum());
                if (count == 1) {
                    skuStocked = true;
                    //TODO 告诉 MQ 库存锁定成功
                    //每锁一个 库存 生成 一个 工作单
                    WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity(null, skuId, "", hasStock.getNum(), task.getId(), wareId, 1);
                    wareOrderTaskDetailService.save(entity);
                    StockLockedTo stockLockedTo = new StockLockedTo();
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(entity, stockDetailTo);
                    stockLockedTo.setId(task.getId());
                    stockLockedTo.setDetailTo(stockDetailTo);
                    rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", stockLockedTo);
                    break;
                }
                //当前仓库锁定失败，重试下一个仓库

            }
            if (skuStocked == false) {//当前商品所有仓库都锁定失败
                throw new NoStockException(skuId);
            }
        }
        //3 肯定全部都是锁定成功的
        return true;
    }


    public void unLockStock(Long skuId, Long wareId, Integer num, Long taskDetailId) {
        //库存解锁
        wareSkuDao.unlockStock(skuId, wareId, num);
        //更新库存工作单的状态
        WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
        entity.setId(taskDetailId);
        entity.setLockStatus(2);//修改库存工作单的 状态
        wareOrderTaskDetailService.updateById(entity);
    }

    @Override
    public void unLockStock(StockLockedTo to) {
        Long id = to.getId();//库存工作单的id
        StockDetailTo detailTo = to.getDetailTo();
        Long skuId = detailTo.getSkuId();
        Long detailId = detailTo.getId();
        //解锁
        //1 查询数据库关于这个订单的锁定库存消息
        //有：证明库存锁定成功了
        // 解锁：订单情况
        //1 没有这个订单，必须解锁
        //2 有这个订单。不是解锁库存。
        //订单状态：已取消：解锁库存
        //订单状态：没取消：不能解锁
        //无：库存锁定失败了，库存回滚了。这种情况无需解锁
        WareOrderTaskDetailEntity byId = wareOrderTaskDetailService.getById(detailId);
        if (byId != null && (byId.getLockStatus() == 1)) {
            Long id1 = to.getId();
            WareOrderTaskEntity byId1 = wareOrderTaskService.getById(id1);//工作单消息
            String orderSn = byId1.getOrderSn();
            R r = orderFeignService.getOrderStatus(orderSn);
            if (r.getCode() == 0) {
                OrderVo data = r.getData(new TypeReference<OrderVo>() {
                });
                if (data == null || data.getStatus() == 4) {//订单不存在 或者 订单被取消了=》解锁
                    // TODO 1 需解锁
                    unLockStock(skuId, detailTo.getWareId(), detailTo.getSkuNum(), detailId);
                }
            }else{
                //消息拒绝以后重新放回队列，让别人继续消费
                throw new RuntimeException("远程服务调用失败");//TODO 重新解锁
            }
        } else {
            //----------------------------TODO 无需解锁
        }
    }

    @Transactional
    @Override
    public void unLockStock(OrderTo to) {
        String orderSn = to.getOrderSn();
        //查一下最新库存的状态，防止重读解锁
        WareOrderTaskEntity task = wareOrderTaskService.getOrderTaskByOrderSn(orderSn);
        Long taskId = task.getId();
        //按照工作单 找到 所有 没有 解锁的 库存
        List<WareOrderTaskDetailEntity> list = wareOrderTaskDetailService.list(
                new QueryWrapper<WareOrderTaskDetailEntity>()
                        .eq("task_id", taskId)
                        .eq("lock_status", 1)
        );
        for (WareOrderTaskDetailEntity entity : list) {
            unLockStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum(),entity.getId());
        }
    }

    //sku在哪些仓库有库存
    @Data
    class SkuWareHasStock {
        private Long skuId;
        private List<Long> wareId;
        private Integer num;
    }
}