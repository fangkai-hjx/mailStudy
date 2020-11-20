package cn.scut.mall.ware.listener;

import cn.scut.common.to.mq.OrderTo;
import cn.scut.common.to.mq.StockDetailTo;
import cn.scut.common.to.mq.StockLockedTo;
import cn.scut.common.utils.R;
import cn.scut.mall.ware.dao.WareSkuDao;
import cn.scut.mall.ware.entity.WareOrderTaskDetailEntity;
import cn.scut.mall.ware.entity.WareOrderTaskEntity;
import cn.scut.mall.ware.feign.OrderFeignService;
import cn.scut.mall.ware.feign.ProductFeignService;
import cn.scut.mall.ware.service.WareOrderTaskDetailService;
import cn.scut.mall.ware.service.WareOrderTaskService;
import cn.scut.mall.ware.service.WareSkuService;
import cn.scut.mall.ware.vo.OrderVo;
import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.impl.ChannelN;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RabbitListener(queues = {"stock.release.stock.queue"})
public class StockReleaseListener {

    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 库存自己过期了，处理
     *
     * @param to
     * @param message
     */
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {
        System.out.println("接收到库存消息");
        try{
           //只要方法 正常运行，都是正常消费消息
           wareSkuService.unLockStock(to);
           channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
       }catch (Exception e){
           //只要有任何异常，都是消息执行失败
           channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
       }
    }

    /**
     * 订单 关单 之后处理
     * @param to
     * @param message
     * @param channel
     */
    @RabbitHandler
    public void handlerOrderCloseRelease(OrderTo to, Message message, Channel channel ) throws IOException {
        System.out.println("订单关闭，准备解锁库存");
        try{
            wareSkuService.unLockStock(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            //只要有任何异常，都是消息执行失败
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }


}
