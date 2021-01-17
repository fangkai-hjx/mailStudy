package cn.scut.ware.service;

import cn.scut.common.util.PageUtils;
import cn.scut.ware.entity.WareInfoEntity;
import cn.scut.ware.entity.WareOrderTaskEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {
    PageUtils queryPage(Map<String, Object> params);
}
