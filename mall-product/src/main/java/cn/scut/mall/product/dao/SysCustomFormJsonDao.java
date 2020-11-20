package cn.scut.mall.product.dao;

import cn.scut.mall.product.entity.SysCustomFormJson;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface SysCustomFormJsonDao extends BaseMapper<SysCustomFormJson> {

    public void createAutoTask(String tableName, Map<String, String> tableFields);
}
