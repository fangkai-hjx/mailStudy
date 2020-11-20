package cn.scut.mall.product.service.impl;

import cn.scut.mall.product.dao.SysCustomFormJsonDao;
import cn.scut.mall.product.entity.SysCustomFormJson;
import cn.scut.mall.product.service.ISysCustomFormJsonService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

@Service
public class SysCustomFormJsonServiceImpl extends ServiceImpl<SysCustomFormJsonDao, SysCustomFormJson> implements ISysCustomFormJsonService {

    @Autowired
    private SysCustomFormJsonDao sysCustomFormJsonDao;

    @Override
    public void createAutoTask(String tableName, Map<String, String> tableFields) {
        sysCustomFormJsonDao.createAutoTask(tableName,tableFields);
    }

    @Override
    public Map<String, Object> queryTableByProcessId(String processId, String tableName) {
        return null;
    }

    @Override
    public void addTableData(Map<String, Object> tableData) {

    }

    @Override
    public void removeByTableName(String tableName) {

    }
}
