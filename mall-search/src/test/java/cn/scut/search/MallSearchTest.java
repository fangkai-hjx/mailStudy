package cn.scut.search;

import cn.scut.search.config.MallElasticSearchConfig;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MallSearchTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void test() {
        System.out.println(restHighLevelClient);
    }

    //存储数据到es
    @Test
    public void index() throws IOException {
        IndexRequest request = new IndexRequest("users");
        request.id("2");
//        request.source("username","zhangsang","age",18."gender","男");
        User user = new User("zhangsang", "男", 12);
        String toJSONString = JSON.toJSONString(user);
        request.source(toJSONString, XContentType.JSON);

        IndexResponse response = restHighLevelClient.index(request, MallElasticSearchConfig.COMMON_OPTIONS);
        //提取有用的想要数据
        System.out.printf(response.toString());
//        IndexResponse[index=users,type=_doc,id=1,version=1,result=created,seqNo=0,primaryTerm=1,shards={"total":2,"successful":1,"failed":0}]

    }

    @Data
    @AllArgsConstructor
    class User {
        private String username;
        private String gender;
        private Integer age;
    }

    //检索数据
    @Test
    public void search() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");//指定索引
        //DSL语句，检索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        //按照年龄进行聚合
        TermsAggregationBuilder agg_aggregation = AggregationBuilders.terms("ageAgg").field("age").size(10);
        sourceBuilder.aggregation(agg_aggregation);
        //按照年龄进行聚合 按照薪资进行聚合
        AvgAggregationBuilder balance_aggregation = AggregationBuilders.avg("avg_balance").field("balance");
        sourceBuilder.aggregation(balance_aggregation);
        //2 执行检索
        System.out.println(sourceBuilder.toString());
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, MallElasticSearchConfig.COMMON_OPTIONS);
        //分析结果
        RestStatus status = searchResponse.status();
        TimeValue took = searchResponse.getTook();
        Boolean terminatedEarly = searchResponse.isTerminatedEarly();
        boolean timedOut = searchResponse.isTimedOut();
        System.out.println(status);
        System.out.println(took);
        System.out.println(terminatedEarly);
        System.out.println(timedOut);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
//        Map map = JSON.parseObject(searchResponse.toString(), Map.class);
        for (SearchHit hit : searchHits) {
            // do something with the SearchHit
            Account account = JSON.parseObject(hit.getSourceAsString(), Account.class);
            System.out.println(account.toString());
        }
        System.out.println("聚合信息");
        Aggregations aggregations = searchResponse.getAggregations();
        Terms terms = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : terms.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄："+ keyAsString+" 个数："+bucket.getDocCount());
        }
        Avg balance = aggregations.get("avg_balance");
        System.out.println("平均薪资："+ balance.getValue());
    }
    @ToString
    @Data
    static class Account {
        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;

    }
}
