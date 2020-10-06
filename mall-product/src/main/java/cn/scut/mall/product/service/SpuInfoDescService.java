package cn.scut.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.scut.common.utils.PageUtils;
import cn.scut.mall.product.entity.SpuInfoDescEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * spu信息介绍
 *
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 10:45:17
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {


    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfoDesc(SpuInfoDescEntity descEntity);
}

