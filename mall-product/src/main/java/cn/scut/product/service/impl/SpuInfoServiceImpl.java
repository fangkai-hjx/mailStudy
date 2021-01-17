package cn.scut.product.service.impl;

import cn.scut.common.constant.ProductConstant;
import cn.scut.common.to.SkuEsModel;
import cn.scut.common.to.SkuReductionTo;
import cn.scut.common.to.SpuBoundTo;
import cn.scut.common.util.PageUtils;
import cn.scut.common.util.Query;
import cn.scut.common.util.R;
import cn.scut.product.dao.BrandDao;
import cn.scut.product.dao.CategoryDao;
import cn.scut.product.dao.SpuInfoDao;
import cn.scut.product.entity.*;
import cn.scut.product.feign.CouponFeignService;
import cn.scut.product.feign.SearchFeignService;
import cn.scut.product.feign.WareFeignService;
import cn.scut.product.service.*;
import cn.scut.product.vo.*;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    private AttrService attrService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImageService spuImagesService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImageService skuImageService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CategoryDao categoryDao;
    @Autowired
    BrandDao brandDao;
    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    SearchFeignService searchFeignService;

    //status=&key=&brandId=0&catelogId=250&page=1&limit=10
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        //按照/分类/品牌/状态  来查询
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String status = (String)params.get("status");
        if(StringUtils.isNotEmpty(status)){
            wrapper.eq("publish_status",status);
        }
        String brandId = (String)params.get("brandId");
        if(StringUtils.isNotEmpty(brandId)&& !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }
        String catelogId = (String)params.get("catalog_id");
        if(StringUtils.isNotEmpty(catelogId)&& !"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }
        String key = (String)params.get("key");
        if(StringUtils.isNotEmpty(status)){
            wrapper.and(obj->{
                obj.eq("id",key).or().like("spu_name",key);
            });
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        //1. 保存 spu 的 基本信息 `pms_sku_info`
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.save(spuInfoEntity);
        //2. 保存 spu 的 描述图片 `pms_spu_info_desc`
        List<String> descript = spuSaveVo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(spuInfoEntity.getId());
        descEntity.setDecript(String.join(",", descript));
        this.spuInfoDescService.save(descEntity);

        //3. 保存 spu 的 图片集 `pms_spu_images`
        List<String> images = spuSaveVo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);

        //4. 保存 spu 的 规格参数 `pms_product_attr_value`
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(item -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(item.getAttrId());
            productAttrValueEntity.setAttrValue(item.getAttrValues());
            productAttrValueEntity.setQuickShow(item.getShowDesc());

            AttrEntity attrInfo = attrService.getAttrInfo(item.getAttrId());
            productAttrValueEntity.setAttrName(attrInfo.getAttrName());//查询属性名

            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);
        //5. 保存 sku 的 积分信息 mall_sms-->`sms_spu_bounds`
        Bounds bounds = spuSaveVo.getBounds();//积分信息
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r1 = couponFeignService.saveSpuBounds(spuBoundTo);
        //TODO 远程调用，积分信息
        if (r1.getCode() != 0) {
            log.error("远程保存spu积分信息失败！");
        }
        //6. 保存 spu 的 对应所有的sku信息。`pms_sku_info`
        List<Skus> skus = spuSaveVo.getSkus();
        //每个 sku 保存 后 都有 一个 sku Id
        if (CollectionUtils.isNotEmpty(skus)) {
            skus.forEach(sku -> {
                //6.1 保存 sku 的 基本信息 `pms_sku_info`
                String defaultImg = "";
                for (Images image : sku.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);//销量默认是0
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);//默认图片
                skuInfoService.saveSkuInfo(skuInfoEntity);
                //6.2 保存 sku 的 图片信息`pms_spu_images`
                //TODO 没有图片路径的无需保存
                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> imageList = sku.getImages().stream().map(item -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(item.getImgUrl());
                    skuImagesEntity.setDefaultImg(item.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity -> {
                    return StringUtils.isNotEmpty(entity.getImgUrl());//过滤 返回false 会被过滤
                }).collect(Collectors.toList());
                skuImageService.saveBatch(imageList);
                //6.3 保存 sku 的 销售属性信息 `pms_sku_sale_attr_value`
                List<Attr> attr = sku.getAttr();
                List<SkuSaleAttrValueEntity> saleAttrValueEntityList = attr.stream().map(item -> {
                    SkuSaleAttrValueEntity saleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(item, saleAttrValueEntity);
                    saleAttrValueEntity.setSkuId(skuId);
                    return saleAttrValueEntity;
                }).collect(Collectors.toList());
                this.skuSaleAttrValueService.saveBatch(saleAttrValueEntityList);
                ////TODO 6.4 保存 sku 的 优惠信息，满减信息---跨库了mall_sms-->`pms_s`sms_sku_ladder` `sms_sku_full_reduction`  `sms_member_price`
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                //TODO 如果 满N件减x元 和 满N元打x折，当 N=0 则不需要调用 优惠服务
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
                    R r2 = couponFeignService.saveInfo(skuReductionTo);
                    if (r2.getCode() != 0) {
                        log.error("远程保存sku优惠信息失败！");
                    }
                }
            });
        }
    }

    @Transactional
    @Override
    public void up(Long spuId) {
        List<SkuEsModel> spu = new ArrayList<>();
        //TODO 组装需要的数据
        //查出当前spuId下全部sku信息，品牌的名字
        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);
        //TODO 规格属性【可被检索的】----由于规格属性按照spu的，因此查一次就行了
        List<ProductAttrValueEntity> attrValues = productAttrValueService.baseAttrListForSpu(spuId);
        List<Long> attrIds = attrValues.stream().map(attrValue -> {
            return attrValue.getAttrId();
        }).collect(Collectors.toList());
        //去属性表中查询 可被检索的属性
        List<Long> searchAttrIds = attrService.selectSearchAttrs(attrIds);
        Set<Long> set = new HashSet<>(searchAttrIds);
        List<SkuEsModel.Attrs> entities = attrValues.stream().filter(item -> {
            return set.contains(item.getAttrId());
        }).map(item->{
            SkuEsModel.Attrs attr = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item,attr);
            return attr;
        }).collect(Collectors.toList());
        List<Long> skuIds = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        Map<Long, Boolean> booleanMap = null;
        try{
            //TODO 发送远程调用，库存系统是否有库存
            R r = wareFeignService.hasstock(skuIds);
            TypeReference<List<SkuHasStock>> listTypeReference = new TypeReference<List<SkuHasStock>>(){};
            List<SkuHasStock> list = r.getData(listTypeReference);
            booleanMap = list.stream().collect(Collectors.toMap(x->x.getSkuId(),y->y.getHasStock()));
        }catch (Exception e){
            log.error("库存服务查异常:原因{}",e);
        }

        Map<Long, Boolean> finalBooleanMap = booleanMap;
        List<SkuEsModel> skuEsModels = skus.stream().map(sku -> {
            SkuEsModel esModel = new SkuEsModel();
            esModel.setSpuId(sku.getSpuId());
            esModel.setSkuId(sku.getSkuId());
            esModel.setSkuTitle(sku.getSkuTitle());
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());
            esModel.setSaleCount(0L);
            esModel.setDescription(sku.getSkuDesc());
            //设置库存信息
            if(CollectionUtils.isNotEmpty(finalBooleanMap)){
                esModel.setHasStock(finalBooleanMap.get(sku.getSkuId()));
            }else {
                esModel.setHasStock(true);
            }
//            esModel.setHasStock();//这里可以优化----如果每个sku查询需要多次查询----》优化为一次
            //TODO 热度评分
            esModel.setHotScore(0D);
            esModel.setCatalogId(sku.getCatalogId());
            CategoryEntity categoryEntity = categoryDao.selectById(sku.getCatalogId());
            esModel.setCatalogName(categoryEntity.getName());

            esModel.setBrandId(sku.getBrandId());
            BrandEntity brandEntity = brandDao.selectById(sku.getBrandId());
            esModel.setBrandImg(brandEntity.getLogo());
            esModel.setBrandName(brandEntity.getBrandName());
            //设置检索属性
            esModel.setAttrs(entities);
            return esModel;
        }).collect(Collectors.toList());
        //TODO 调用mall-search
        R r = searchFeignService.productUp(skuEsModels);
        if(r.getCode() == 0){
            //TODO 修改当前spu的状态
            SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
            spuInfoEntity.setId(spuId);
            spuInfoEntity.setUpdateTime(new Date());
            spuInfoEntity.setPublishStatus(ProductConstant.StatusEnum.SPU_UP.getCode());
            this.baseMapper.updateById(spuInfoEntity);
        }else {
            //远程调用失败
            //TODO 重复调用？接口幂等性？
        }
    }
}
