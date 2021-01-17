package cn.scut.product.service;

import cn.scut.product.entity.SpuImagesEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SpuImageService extends IService<SpuImagesEntity> {
    void saveImages(Long id, List<String> images);
}
