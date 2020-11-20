package cn.scut.mall.search.service.impl;

import cn.scut.common.to.es.SkuEsModel;
import cn.scut.mall.search.config.ElasticSearchConfig;
import cn.scut.mall.search.constant.EsConstant;
import cn.scut.mall.search.service.ProductSaveService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        //保存到 es
        //  1 给es中建立索引，product,建立好映射关系

        //  2 给es中保存这些数据
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : skuEsModels) {
            //构造 保存 请求
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            String jsonString = JSON.toJSONString(skuEsModel);
            System.out.println(jsonString);
            indexRequest.source(jsonString, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);
        //TODO 如果 批量错误
        boolean b = bulk.hasFailures();//是否有上架错误
        List<String> collect = Arrays.stream(bulk.getItems()).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        log.info("商品上架成功：{}",collect);
        return b;
    }
}
