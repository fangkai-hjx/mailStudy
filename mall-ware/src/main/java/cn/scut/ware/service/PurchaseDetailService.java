package cn.scut.ware.service;

import cn.scut.common.util.PageUtils;
import cn.scut.ware.entity.PurchaseDetailEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {
    PageUtils queryPageByCondition(Map<String, Object> params);

    List<PurchaseDetailEntity>  listDetailByPurchaseId(Long id);
}
