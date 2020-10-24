package cn.scut.mall.order.service.impl;

import cn.scut.mall.order.entity.OrderReturnApplyEntity;
import cn.scut.mall.order.entity.OrderReturnReasonEntity;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.impl.AMQImpl;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.Query;

import cn.scut.mall.order.dao.OrderItemDao;
import cn.scut.mall.order.entity.OrderItemEntity;
import cn.scut.mall.order.service.OrderItemService;


@Service("orderItemService")
@RabbitListener(queues = {"hello-java"})
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * Message message：原生消息详细信息；头+体
     * T<发送消息的类型>spring给我们自动转换
     * <p>
     * Queue:可以很多人来监听。只要收到消息，队列删除消息，而且只能由一个收到消息
     * 场景：
     * 1） 订单服务启动多个;同一个消息，只能有一个客户端收到
     * 2） 只有一个消息完全处理完，方法运行结束，我们就可以接收到下一个消息；
     *
     * @param message
     */
    @RabbitHandler
    public void receiveMessage(Message message, OrderReturnReasonEntity orderReturnApplyEntity, Channel channel) {
        //消息体
        byte[] body = message.getBody();
        //消息头属性信息
//        int i = 1/0;
        MessageProperties messageProperties = message.getMessageProperties();
        System.out.println(orderReturnApplyEntity);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        System.out.println();
        try {
            //签收货物，非批量模式
//            channel.basicAck(deliveryTag,false);
            //拒收货物，非批量模式
            channel.basicNack(deliveryTag, false, true);
        } catch (IOException e) {
            //网络中断
        }

    }

}
