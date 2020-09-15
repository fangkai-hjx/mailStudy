package cn.scut.mall.coupon.dao;

import cn.scut.mall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 12:11:09
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
