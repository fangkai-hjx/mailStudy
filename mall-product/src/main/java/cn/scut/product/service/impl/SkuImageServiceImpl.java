package cn.scut.product.service.impl;

import cn.scut.product.dao.SkuImageDao;
import cn.scut.product.entity.SkuImagesEntity;
import cn.scut.product.service.SkuImageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service("skuImageService")
public class SkuImageServiceImpl extends ServiceImpl<SkuImageDao, SkuImagesEntity> implements SkuImageService {
}
