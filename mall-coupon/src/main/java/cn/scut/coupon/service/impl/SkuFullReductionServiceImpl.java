package cn.scut.coupon.service.impl;

import cn.scut.common.to.MemberPrice;
import cn.scut.common.to.SkuReductionTo;
import cn.scut.coupon.dao.SkuFullReductionDao;
import cn.scut.coupon.entity.MemberPriceEntity;
import cn.scut.coupon.entity.SkuFullReductionEntity;
import cn.scut.coupon.entity.SkuLadderEntity;
import cn.scut.coupon.service.MemberPriceService;
import cn.scut.coupon.service.SkuFullReductionService;
import cn.scut.coupon.service.SkuLadderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao,SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Transactional
    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        //1 满多少件 减少 多少钱
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuReductionTo,skuLadderEntity);
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());//是否参与叠加优惠
//        skuLadderEntity.setPrice();这个价格是根据spu的价格定的，也可也在下订单计算
        skuLadderService.save(skuLadderEntity);
        //2 满多少钱 打几折
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo,skuFullReductionEntity);
        this.save(skuFullReductionEntity);

        //3 会员价格
        List<MemberPrice> memberPrices = skuReductionTo.getMemberPrice();
        List<MemberPriceEntity> collect = memberPrices.stream().map(memberPrice -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
            memberPriceEntity.setMemberLevelId(memberPrice.getId());
            memberPriceEntity.setMemberLevelName(memberPrice.getName());
            memberPriceEntity.setMemberPrice(memberPrice.getPrice());
            memberPriceEntity.setAddOther(1);//默认叠加优惠
            return memberPriceEntity;
        }).filter(item->{
            return item.getMemberPrice().compareTo(new BigDecimal("0")) == 1; //会员价格大于0 才保存
        }).collect(Collectors.toList());
        memberPriceService.saveBatch(collect);
    }
}
