package cn.scut.mall.search;

import cn.scut.mall.search.config.ElasticSearchConfig;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class SearchTest {


    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void test01(){
        System.out.println(restHighLevelClient);
    }
    /**
     * 测试存储数据到 es
     */
    @Test
    public void indexSave() throws IOException {
        IndexRequest index = new IndexRequest("users");
        index.id("1");
//        index.source("username","zhangsang","age","18","gender","男");
        User user = new User();
        user.setUsername("fangkai");
        user.setAge("18");
        user.setGender("男");
        String jsonString = JSON.toJSONString(user);
        index.source(jsonString, XContentType.JSON);
        //执行操作
        IndexResponse indexResponse = restHighLevelClient.index(index, ElasticSearchConfig.COMMON_OPTIONS);
        //提取有用的 响应数据
        System.out.println(indexResponse);
    }
    @Data
    class User{
        private String username;
        private String gender;
        private String age;
    }
}
