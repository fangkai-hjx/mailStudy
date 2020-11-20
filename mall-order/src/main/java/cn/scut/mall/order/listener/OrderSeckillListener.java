package cn.scut.mall.order.listener;

import cn.scut.common.to.mq.SeckillOrderTo;
import cn.scut.mall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RabbitListener(queues = {"order.seckill.order.queue"})
@Component
public class OrderSeckillListener {

    @Autowired
    OrderService orderService;

    @RabbitListener
    public void listener(SeckillOrderTo orderTo, Channel channel, Message message) throws IOException {
       try{
           log.info("准备创建秒杀单的详细信息");
           orderService.createSeckillOrder(orderService);
           channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
       }catch (Exception e){
           channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
       }
    }
}
