package cn.scut.coupon.service.impl;

import cn.scut.coupon.dao.SpuBoundsDao;
import cn.scut.coupon.entity.SpuBoundsEntity;
import cn.scut.coupon.service.SpuBoundsService;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service("spuBoundsService")
public class SpuBoundsServiceImpl extends ServiceImpl<SpuBoundsDao, SpuBoundsEntity> implements SpuBoundsService {
}
