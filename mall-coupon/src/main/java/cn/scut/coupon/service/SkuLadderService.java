package cn.scut.coupon.service;


import cn.scut.common.util.PageUtils;
import cn.scut.coupon.entity.SkuLadderEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 商品阶梯价格
 *
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 12:11:09
 */
public interface SkuLadderService extends IService<SkuLadderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

