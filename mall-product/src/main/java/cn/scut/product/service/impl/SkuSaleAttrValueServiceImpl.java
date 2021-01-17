package cn.scut.product.service.impl;

import cn.scut.product.dao.SkuSaleAttrValueDao;
import cn.scut.product.entity.SkuSaleAttrValueEntity;
import cn.scut.product.service.SkuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {
}
