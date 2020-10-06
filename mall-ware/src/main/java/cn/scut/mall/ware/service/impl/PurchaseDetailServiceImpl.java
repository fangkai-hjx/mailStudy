package cn.scut.mall.ware.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.Query;

import cn.scut.mall.ware.dao.PurchaseDetailDao;
import cn.scut.mall.ware.entity.PurchaseDetailEntity;
import cn.scut.mall.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                new QueryWrapper<PurchaseDetailEntity>()
        );

        return new PageUtils(page);
    }
    //key=&status=0&wareId=1
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        String key = (String)params.get("key");
        String status = (String)params.get("status");
        String wareId = (String)params.get("wareId");
        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(key)){
           wrapper.and(w->{
               w.eq("sku_id",key).or().eq("purchase_id",key);
           });
        }
        if(StringUtils.isNotEmpty(status)){
            wrapper.eq("status",status);
        }
        if(StringUtils.isNotEmpty(wareId)){
            wrapper.eq("ware_id",wareId);
        }

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public List<PurchaseDetailEntity> listDetailByPurchaseId(Long id) {
        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<PurchaseDetailEntity>();
        wrapper.eq("purchase_id",id);
        List<PurchaseDetailEntity> list = this.list(wrapper);
        return list;
    }

}