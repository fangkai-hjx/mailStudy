package cn.scut.mall.ware.service.impl;

import cn.scut.common.utils.R;
import cn.scut.mall.ware.feign.MemberFeignService;
import cn.scut.mall.ware.vo.FareVo;
import cn.scut.mall.ware.vo.MemberAddressVo;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.Query;

import cn.scut.mall.ware.dao.WareInfoDao;
import cn.scut.mall.ware.entity.WareInfoEntity;
import cn.scut.mall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                new QueryWrapper<WareInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            wrapper.eq("id", key).or()
                    .like("name", key).or()
                    .like("address", key).or()
                    .like("areacode", key);
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 根据用户的收货地址 计算 运费信息
     *
     * @param addrId
     * @return
     */
    @Override
    public FareVo getFare(Long addrId) {
        FareVo fareVo = new FareVo();
        R info = memberFeignService.info(addrId);
        MemberAddressVo data = info.getData("memberReceiveAddress",new TypeReference<MemberAddressVo>() {//查到收货地址
        });
        if (data != null) {
            String phone = data.getPhone();
            //截取 手机号 最后一个 数字
            String substring = phone.substring(phone.length() - 1);
            fareVo.setFare(new BigDecimal(substring));//运费
            fareVo.setMemberAddressVo(data);
            return fareVo;
        }
        return null;
    }
}