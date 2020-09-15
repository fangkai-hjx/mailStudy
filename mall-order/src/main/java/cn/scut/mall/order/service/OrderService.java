package cn.scut.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.scut.common.utils.PageUtils;
import cn.scut.mall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 12:44:34
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

