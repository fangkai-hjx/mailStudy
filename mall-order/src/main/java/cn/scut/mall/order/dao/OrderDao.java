package cn.scut.mall.order.dao;

import cn.scut.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 12:44:34
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
