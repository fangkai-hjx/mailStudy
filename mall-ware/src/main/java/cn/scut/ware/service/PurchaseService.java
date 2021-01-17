package cn.scut.ware.service;


import cn.scut.common.util.PageUtils;
import cn.scut.ware.entity.PurchaseEntity;
import cn.scut.ware.vo.MergeVo;
import cn.scut.ware.vo.PurchaseDoneVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface PurchaseService extends IService<PurchaseEntity> {
    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnReceivePurchase(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    void received(List<Long> ids);

    void done(PurchaseDoneVo purchaseDoneVo);
}
