package cn.scut.mall.coupon.service.impl;

import cn.scut.common.to.MemberPrice;
import cn.scut.common.to.SkuReductionTo;
import cn.scut.mall.coupon.entity.MemberPriceEntity;
import cn.scut.mall.coupon.entity.SkuLadderEntity;
import cn.scut.mall.coupon.service.MemberPriceService;
import cn.scut.mall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.Query;

import cn.scut.mall.coupon.dao.SkuFullReductionDao;
import cn.scut.mall.coupon.entity.SkuFullReductionEntity;
import cn.scut.mall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private MemberPriceService memberPriceService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * //TODO 其他更多 的 内容 在 高级篇 完成--比如其中一部分失败 了 怎么办
     * @param skuReductionTo
     */
    //  //6.4 保存 sku 的 优惠信息，满减信息，会员价格---跨库了mall_sms-->`pms_s`sms_sku_ladder` `sms_sku_full_reduction`  `sms_member_price`
    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        //1 保存 sku 优惠
        if(skuReductionTo.getFullCount()>0){
            SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
            BeanUtils.copyProperties(skuReductionTo,skuLadderEntity);
//        skuLadderEntity.setPrice();折后价格 可以 留在 下订单的 时候算
            this.skuLadderService.save(skuLadderEntity);
        }
        // 2保存 sku 满减信息
        if(skuReductionTo.getFullPrice().compareTo(new BigDecimal("0"))==1){
            SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
            BeanUtils.copyProperties(skuReductionTo,skuFullReductionEntity);
            this.save(skuFullReductionEntity);
        }
        //3 会员价格
        List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
        List<MemberPriceEntity> collect = memberPrice.stream().map(item -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
            memberPriceEntity.setMemberLevelName(item.getName());
            memberPriceEntity.setMemberLevelId(item.getId());
            memberPriceEntity.setMemberPrice(item.getPrice());
            memberPriceEntity.setAddOther(1);//默认叠加其他优惠
            return memberPriceEntity;
        }).filter(item->{
            return item.getMemberPrice().compareTo(new BigDecimal("0"))==1;
        }).collect(Collectors.toList());
        memberPriceService.saveBatch(collect);
    }

}