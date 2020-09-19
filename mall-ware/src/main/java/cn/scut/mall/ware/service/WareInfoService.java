package cn.scut.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.scut.common.utils.PageUtils;
import cn.scut.mall.ware.entity.WareInfoEntity;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 12:48:49
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}
