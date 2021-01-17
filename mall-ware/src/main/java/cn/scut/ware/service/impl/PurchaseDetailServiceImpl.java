package cn.scut.ware.service.impl;

import cn.scut.common.constant.WareConstant;
import cn.scut.common.util.PageUtils;
import cn.scut.common.util.Query;
import cn.scut.ware.dao.PurchaseDetailDao;
import cn.scut.ware.entity.PurchaseDetailEntity;
import cn.scut.ware.service.PurchaseDetailService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {
    //key=&status=&wareId=
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
        List<PurchaseDetailEntity> purchaseDetailEntityList = this.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", id));
        return purchaseDetailEntityList;
    }
}
