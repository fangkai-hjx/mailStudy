package cn.scut.mall.product.service.impl;

import cn.scut.common.constant.ProductConstant;
import cn.scut.common.to.SkuReductionTo;
import cn.scut.common.to.SpuBoundTo;
import cn.scut.common.to.es.SkuEsModel;
import cn.scut.common.utils.R;
import cn.scut.mall.product.entity.*;
import cn.scut.mall.product.feign.CouponFeignService;
import cn.scut.mall.product.feign.SearchFeignService;
import cn.scut.mall.product.feign.WareFeignService;
import cn.scut.mall.product.service.*;
import cn.scut.mall.product.vo.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.Query;

import cn.scut.mall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
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
        this.saveBaseSpuInfo(spuInfoEntity);
        //2. 保存 spu 的 描述图片 `pms_spu_info_desc`
        List<String> descript = spuSaveVo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(spuInfoEntity.getId());
        descEntity.setDecript(String.join(",", descript));
        this.spuInfoDescService.saveSpuInfoDesc(descEntity);

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

            AttrRespVo attrInfo = attrService.getAttrInfo(item.getAttrId());
            productAttrValueEntity.setAttrName(attrInfo.getAttrName());//查询属性名

            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);
        //5. 保存 sou 的 积分信息 mall_sms-->`sms_spu_bounds`
        Bounds bounds = spuSaveVo.getBounds();//积分信息
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r1 = couponFeignService.saveSpuBounds(spuBoundTo);
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
                this.skuImagesService.saveBatch(imageList);
                //6.3 保存 sku 的 销售属性信息 `pms_sku_sale_attr_value`
                List<Attr> attr = sku.getAttr();
                List<SkuSaleAttrValueEntity> saleAttrValueEntityList = attr.stream().map(item -> {
                    SkuSaleAttrValueEntity saleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(item, saleAttrValueEntity);
                    saleAttrValueEntity.setSkuId(skuId);
                    return saleAttrValueEntity;
                }).collect(Collectors.toList());
                this.skuSaleAttrValueService.saveBatch(saleAttrValueEntityList);
                //6.4 保存 sku 的 优惠信息，满减信息---跨库了mall_sms-->`pms_s`sms_sku_ladder` `sms_sku_full_reduction`  `sms_member_price`
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
                    R r2 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r2.getCode() != 0) {
                        log.error("远程保存sku优惠信息失败！");
                    }
                }
            });
        }
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    //status=1&key=&brandId=17&catelogId=225
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        String status = (String) params.get("status");
        String brandId = (String) params.get("brandId");
        String catelogId = (String) params.get("catelogId");
        if (StringUtils.isNotEmpty(key)) {
            //eg status= 1 and id =1 or spu_name like ***  如果 后面成立 当时 status不成立，则 失败
            //改成status= 1 and （id =1 or spu_name like ***）---下面 写法 就会这样 在 最外面 加上 一个 括号
            wrapper.and((w) -> {//这里这样写的 原因 是因为 不这样 写 会有问题
                w.eq("id", key).or().like("spu_name", key);
            });
        }
        if (StringUtils.isNotEmpty(status)) {
            wrapper.eq("publish_status", status);
        }
        if (StringUtils.isNotEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }
        if (StringUtils.isNotEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 商品上架
     *
     * @param spuId
     */
    @Override
    public void up(Long spuId) {
        List<SkuEsModel> upProducts = new ArrayList<>();
        // 查出当前spuId 的 对应的 所有sku信息 品牌的名字
        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);
        //TODO 4 查出 当前sku的所有规格属性---可以用来被检索的---以为 每个 sku的 规格属性 相同 ，因此拿出来
        List<ProductAttrValueEntity> attrList = this.productAttrValueService.baseAttrListForSpu(spuId);
        List<Long> attrIds = attrList.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        List<Long> searchIds = attrService.selectSearchAttrs(attrIds);
        List<SkuEsModel.Attrs> attrs = attrList.stream().filter(attr -> {
            return searchIds.contains(attr.getAttrId());//过滤
        }).map(item->{
            SkuEsModel.Attrs a = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item,a);
            return a;
        }).collect(Collectors.toList());
        //TODO 1 发送 远程调用，库存系统是否 有库存 ，注意 不是 精确查询有多少
        //如果每次 在这里 调用 ，则会 每个 sku 都会 调用一次---优化为 发送一次 批量skuId
        List<Long> longs = skus.stream().map(sku -> {
            return sku.getSkuId();
        }).collect(Collectors.toList());
        Map<Long, Boolean> booleanMap = null;
        try{
            R r = wareFeignService.getSkuHasStock(longs);
            List<SkuHasStockVo> list2 = (List<SkuHasStockVo>) r.get("data");
            TypeReference<List<SkuHasStockVo>> listTypeReference = new TypeReference<List<SkuHasStockVo>>(){};
            List<SkuHasStockVo> list = r.getData(listTypeReference);
//            booleanMap = list.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));
            booleanMap = list.stream().collect(Collectors.toMap(x -> x.getSkuId(), y -> y.getHasStock()));
        }catch (Exception e){
            log.error("库存服务查异常:原因{}",e);
        }

        Map<Long, Boolean> finalBooleanMap = booleanMap;
        List<SkuEsModel> collect = skus.stream().map(sku -> {
            //组装 需要的数据
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(sku,skuEsModel);
            //名字不一样 或者 没有的 //skuPrice skuImg // hasStock //hotScore // BrandName BrandImg
            skuEsModel.setSkuPrice(sku.getPrice());
            skuEsModel.setSkuImg(sku.getSkuDefaultImg());
            if(finalBooleanMap == null){//远程服务有问题
                skuEsModel.setHasStock(true);
            }else {
                skuEsModel.setHasStock(finalBooleanMap.get(sku.getSkuId()));
            }
            //TODO 2 热度评分， 默认0
            skuEsModel.setHotScore(0L);
            //TODO 3 查出 品牌的 分类 和 名字 信息
            BrandEntity brandEntity = this.brandService.getById(sku.getBrandId());
            skuEsModel.setBrandImg(brandEntity.getName());
            skuEsModel.setBrandName(brandEntity.getLogo());
            CategoryEntity categoryEntity = this.categoryService.getById(sku.getCatalogId());
            skuEsModel.setCatalogName(categoryEntity.getName());

            skuEsModel.setAttrs(attrs);
            return skuEsModel;
        }).collect(Collectors.toList());
        //TODO 5 将数据发送给ES进行保存 mall-search
        R r = searchFeignService.prodcutStatusUp(collect);
        if(r.getCode()==0){
            //远程调用成功
            //TODO 6 修改 当前 spu的 转台
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        }else{
            //远程调用失败
            //TODO 7 重复调用？？？？ 接口幂等性
        }
    }


}