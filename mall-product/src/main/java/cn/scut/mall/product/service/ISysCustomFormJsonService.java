package cn.scut.mall.product.service;

import cn.scut.mall.product.entity.SysCustomFormJson;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @Description: 自定义form表
 * @Author: kmlc
 * @Date: 2020-04-14
 * @Version: V1.0
 */
public interface ISysCustomFormJsonService extends IService<SysCustomFormJson> {

    /**
     * 根据tableName,控件field自动创建数据库表
     *
     * @param tableName
     * @param tableFields
     */
    void createAutoTask(String tableName, Map<String, String> tableFields);

    /**
     * 根据tableName，processId查询数据
     *
     * @param processId
     * @param tableName
     * @return
     */
    Map<String, Object> queryTableByProcessId(String processId, String tableName);

    /**
     * 动态添加自定义表单填入的数据。
     *
     * @param tableData Map<String, Object>
     */
    void addTableData(Map<String, Object> tableData);

    /**
     * 根据tableName删除动态生成的表
     *
     * @param tableName
     */
    void removeByTableName(String tableName);
}
