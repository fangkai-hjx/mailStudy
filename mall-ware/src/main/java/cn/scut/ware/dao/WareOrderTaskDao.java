package cn.scut.ware.dao;

import cn.scut.ware.entity.WareInfoEntity;
import cn.scut.ware.entity.WareOrderTaskEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WareOrderTaskDao extends BaseMapper<WareOrderTaskEntity> {
}
