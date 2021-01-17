package cn.scut.ware.service;

import cn.scut.common.util.PageUtils;
import cn.scut.ware.entity.WareInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface WareInfoService extends IService<WareInfoEntity> {
    PageUtils queryByCondition(Map<String, Object> param);
}
