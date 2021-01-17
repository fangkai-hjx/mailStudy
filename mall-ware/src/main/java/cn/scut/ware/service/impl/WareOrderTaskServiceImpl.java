package cn.scut.ware.service.impl;

import cn.scut.common.util.PageUtils;
import cn.scut.common.util.Query;
import cn.scut.ware.dao.WareInfoDao;
import cn.scut.ware.dao.WareOrderTaskDao;
import cn.scut.ware.entity.WareInfoEntity;
import cn.scut.ware.entity.WareOrderTaskEntity;
import cn.scut.ware.service.WareInfoService;
import cn.scut.ware.service.WareOrderTaskService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("wareOrderTaskService")
public class WareOrderTaskServiceImpl extends ServiceImpl<WareOrderTaskDao, WareOrderTaskEntity> implements WareOrderTaskService {
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareOrderTaskEntity> page = this.page(
                new Query<WareOrderTaskEntity>().getPage(params),
                new QueryWrapper<WareOrderTaskEntity>()
        );

        return new PageUtils(page);
    }
}
