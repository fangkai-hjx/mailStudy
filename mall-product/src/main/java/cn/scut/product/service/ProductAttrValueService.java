package cn.scut.product.service;

import cn.scut.product.entity.ProductAttrValueEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {
    void saveProductAttr(List<ProductAttrValueEntity> collect);

    List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId);

    void updateAttr(Long spuId, List<ProductAttrValueEntity> entities);
}
