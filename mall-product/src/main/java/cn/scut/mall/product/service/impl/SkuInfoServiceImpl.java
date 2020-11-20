package cn.scut.mall.product.service.impl;

import cn.scut.common.utils.R;
import cn.scut.mall.product.entity.SkuImagesEntity;
import cn.scut.mall.product.entity.SpuInfoDescEntity;
import cn.scut.mall.product.entity.SpuInfoEntity;
import cn.scut.mall.product.feign.SeckillFeignService;
import cn.scut.mall.product.service.*;
import cn.scut.mall.product.vo.SeckillInfoVo;
import cn.scut.mall.product.vo.SkuItemVo;
import cn.scut.mall.product.vo.SpuSaveVo;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.Query;

import cn.scut.mall.product.dao.SkuInfoDao;
import cn.scut.mall.product.entity.SkuInfoEntity;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService imagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private SeckillFeignService seckillFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    //key=&catelogId=0&brandId=0&min=0&max=0
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and((w) -> {
                w.eq("sku_id", key).or().like("sku_name", key);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if (StringUtils.isNotEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (StringUtils.isNotEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }
        String min = (String) params.get("min");
        if (StringUtils.isNotEmpty(min)) {
            wrapper.ge("price", min); //大于等于 &gt;=
        }
        String max = (String) params.get("max");
        if (StringUtils.isNotEmpty(max)) {
            try {
                BigDecimal bigDecimal = new BigDecimal(max);
                if (bigDecimal.compareTo(new BigDecimal("0")) == 1) {
                    wrapper.le("price", max);
                }
            } catch (Exception e) {
                log.error("搜索价格 max 格式错误！");
            }
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return list;
    }

    /**
     * 1 SKU 基本信息获取 pms_sku_info
     * 2 SKU 图片信息 pms_sku_image
     * 3 获取 spu 的 销售属性组合。
     * 4 获取 spu 的 介绍
     * 5 获取 spu 的 规格参数信息
     * @param skuId
     * @return
     */
    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        SkuItemVo skuItemVo = new SkuItemVo();
        //1 SKU 基本信息获取 pms_sku_info
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity skuInfo = getById(skuId);
            skuItemVo.setInfoEntity(skuInfo);
            return skuInfo;
        }, executor);
        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            //2 SKU 图片信息 pms_sku_image
            List<SkuImagesEntity> images = imagesService.getImagesBySkuId(skuId);
            skuItemVo.setImages(images);
        }, executor);
        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            //3 获取 spu 的 销售属性组合。
            List<SkuItemVo.SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(res.getSpuId());
            skuItemVo.setSaleAttr(saleAttrVos);
        }, executor);
        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
            //4 获取 spu 的 介绍
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setDesp(spuInfoDescEntity);
        }, executor);
        CompletableFuture<Void> groupFuture = infoFuture.thenAcceptAsync((res) -> {
            //5 获取 spu 的 规格参数信息
            List<SkuItemVo.SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());
            skuItemVo.setGroupAttrs(attrGroupVos);
        }, executor);
        CompletableFuture<Void> secKillFuture = infoFuture.thenAcceptAsync((res) -> {
            //6 查询当前sku是否参与秒杀优惠
            R r = seckillFeignService.skuSeckillInfo(skuId);
            if(r.getCode()==0){
                SeckillInfoVo seckillInfoVo = r.getData(new TypeReference<SeckillInfoVo>() {
                });
                skuItemVo.setSeckillInfoVo(seckillInfoVo);
            }
        }, executor);
        CompletableFuture.allOf(imageFuture,saleAttrFuture,descFuture,groupFuture,secKillFuture).get();
        long end = System.currentTimeMillis();
        System.out.println("执行时间："+(end-start)+" ms");
        return skuItemVo;
    }


}