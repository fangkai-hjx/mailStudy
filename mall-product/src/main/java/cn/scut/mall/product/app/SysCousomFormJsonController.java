package cn.scut.mall.product.app;

import cn.scut.common.utils.R;
import cn.scut.mall.product.entity.SysCustomFormJson;
import cn.scut.mall.product.service.ISysCustomFormJsonService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/form")
public class SysCousomFormJsonController {

    @Autowired
    private ISysCustomFormJsonService sysCustomFormJsonService;
    /**
     * 添加
     *
     * @param sysCustomFormJsonObject
     * @return
     */
    @PostMapping(value = "/add")
    public R add(@RequestBody JSONObject sysCustomFormJsonObject) {
        //获取json数据中的tableName
        /**
         * json结构：
         * {
         *     "config": {
         *     	   "tableName": "XXXX",       <--------需要获取
         *         "layout": "horizontal",
         *     },
         * 		"list": [{},{}]
         * }
         */
        Map config = (Map)sysCustomFormJsonObject.get("config");

        //给tableName添加前缀，标识为自定义
        String tableName = "dynamic_" + config.get("tableName");
        SysCustomFormJson sysCustomFormJson = new SysCustomFormJson();
        sysCustomFormJson.setFormName(tableName);
        //将table的数据存入数据库
        sysCustomFormJson.setFormJson(sysCustomFormJsonObject.toString());
        sysCustomFormJsonService.save(sysCustomFormJson);

        //解析json中的控件信息，并存入tableFilelds，这里使用IdentityHashMap可以存储重复type的控件名
        Map<String, String> tableFields = new IdentityHashMap<>();
        List joArray = (List)sysCustomFormJsonObject.get("list");
        for (int i = 0; i < joArray.size(); i++) {
            Map<String, String> map = (Map)joArray.get(i);
            tableFields.put(map.get("type"), map.get("key"));
        }
        sysCustomFormJsonService.createAutoTask(tableName,tableFields);

        return R.ok("添加成功！");
    }

    public static void main(String[] args) {
        HashMap hashMap = new HashMap();
        hashMap.put(2,"bbb");
        hashMap.put(3,"ccc");
        hashMap.put(1,"aaa");
        System.out.println("HashMap的遍历顺序："+hashMap);
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put(2,"bbb");
        linkedHashMap.put(3,"ccc");
        linkedHashMap.put(1,"aaa");
        System.out.println("LinkedHashMap的遍历顺序："+linkedHashMap);
        HashMap<Integer,Integer> map = new HashMap<>();
        Map<Integer, Integer> integerIntegerMap = Collections.synchronizedMap(map);
        ConcurrentHashMap<Integer,Integer> hashmap = new ConcurrentHashMap<>();
    }
}
