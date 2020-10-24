package cn.scut.mall.product.service.impl;

import cn.scut.mall.product.vo.SkuItemVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.Query;

import cn.scut.mall.product.dao.SkuSaleAttrValueDao;
import cn.scut.mall.product.entity.SkuSaleAttrValueEntity;
import cn.scut.mall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    /**
     *   SELECT attr_name attr_name,attr_id attr_id,GROUP_CONCAT(DISTINCT ssav.attr_value) attr_values FROM `pms_sku_info` si
     *         LEFT JOIN `pms_sku_sale_attr_value`  ssav ON ssav.sku_id = si.sku_id
     *         WHERE si.spu_id = #{spuId}
     *         GROUP BY attr_name,attr_id
     * @param spuId
     * @return
     */
    @Override
    public List<SkuItemVo.SkuItemSaleAttrVo> getSaleAttrsBySpuId(Long spuId) {
        SkuSaleAttrValueDao baseMapper = this.baseMapper;
        List<SkuItemVo.SkuItemSaleAttrVo> list = baseMapper.getSaleAttrsBySpuId(spuId);
        return list;
    }

    @Override
    public List<String> getSkuSaleAttrValuesAsString(Long skuId) {
        SkuSaleAttrValueDao baseMapper = this.baseMapper;
        List<String> strings = baseMapper.getSkuSaleAttrValuesAsString(skuId);
        return strings;
    }

}