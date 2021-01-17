package cn.scut.coupon.service;

import cn.scut.common.util.PageUtils;
import cn.scut.coupon.entity.MemberPriceEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 商品会员价格
 *
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 12:11:09
 */
public interface MemberPriceService extends IService<MemberPriceEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

