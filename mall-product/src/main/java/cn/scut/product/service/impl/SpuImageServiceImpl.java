package cn.scut.product.service.impl;

import cn.scut.product.dao.SpuImageDao;
import cn.scut.product.entity.SpuImagesEntity;
import cn.scut.product.service.SpuImageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service("spuImageService")
public class SpuImageServiceImpl extends ServiceImpl<SpuImageDao, SpuImagesEntity> implements SpuImageService {

    @Override
    public void saveImages(Long spuId, List<String> images) {
        if(CollectionUtils.isEmpty(images)){
            return;
        }
        List<SpuImagesEntity> collect = images.stream().map(item -> {
            SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
            spuImagesEntity.setImgName(item);
            spuImagesEntity.setSpuId(spuId);
            return spuImagesEntity;
        }).collect(Collectors.toList());
        this.saveBatch(collect);//批量保存
    }
}
