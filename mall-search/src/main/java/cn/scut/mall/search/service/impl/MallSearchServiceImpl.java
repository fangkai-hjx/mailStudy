package cn.scut.mall.search.service.impl;

import cn.scut.common.to.es.SkuEsModel;
import cn.scut.mall.search.config.ElasticSearchConfig;
import cn.scut.mall.search.constant.EsConstant;
import cn.scut.mall.search.service.MallSearchService;
import cn.scut.mall.search.vo.SearchParam;
import cn.scut.mall.search.vo.SearchResult;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.mysql.cj.QueryBindings;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.join.ScoreMode;
import org.assertj.core.util.Arrays;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public SearchResult search(SearchParam searchParam) {
        //动态构建出查询需要的DSL语句
        SearchResult result = new SearchResult();
        //1 准备 检索请求
        SearchRequest searchRequest = buildSearchQuery(searchParam);
        try {
            //2 执行检索请求
            SearchResponse response = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
            //3 分析响应数据 封装成需要的格式
            result = buildSearchResult(response,searchParam);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //构建结果数据
    private SearchResult buildSearchResult(SearchResponse response,SearchParam searchParam) {
        SearchHits hits = response.getHits();
        SearchResult result = new SearchResult();
        //=========================查询部分取数据
        if(!Arrays.isNullOrEmpty(hits.getHits())){
            List<SkuEsModel> list = new ArrayList<>();
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                if(StringUtils.isNotEmpty(searchParam.getKeyword())){//携带检索条件 则携带 高亮字段
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String string = skuTitle.getFragments()[0].string();
                    esModel.setSkuTitle(string);
                }
                list.add(esModel);
            }
            result.setProducts(list);
        }

        //=========================聚合部分取数据
        ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
        List<? extends Terms.Bucket> bucketsCatalog = catalog_agg.getBuckets();
        List<SearchResult.CatalogVo> listCatalogs = new ArrayList<>();
        for (Terms.Bucket bucket : bucketsCatalog) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            String keyAsString = bucket.getKeyAsString();//得到分类id
            catalogVo.setCatalogId(Long.parseLong(keyAsString));
            //得到分类名
            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            String catalog_name = catalog_name_agg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalog_name);
            listCatalogs.add(catalogVo);
        }
        ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
        List<? extends Terms.Bucket> bucketsBrands = brand_agg.getBuckets();
        List<SearchResult.BrandVo> listBrands = new ArrayList<>();
        for (Terms.Bucket bucket : bucketsBrands) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            Long brandId = bucket.getKeyAsNumber().longValue();//得到品牌id
            //得到品牌名字
            String brand_name = ((ParsedStringTerms)bucket.getAggregations().get("brand_name_agg")).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandId(brandId);
            brandVo.setBrandName(brand_name);
            //得到品牌图片
            String brand_img = ((ParsedStringTerms)bucket.getAggregations().get("brand_img_agg")).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brand_img);
            listBrands.add(brandVo);
        }

        List<SearchResult.AttrVo> listAttrs = new ArrayList<>();
        ParsedNested attr_agg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            //属性的 id
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);
            //属性的 name
            String attr_name = ((ParsedStringTerms)bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attr_name);
            //属性的 所有值
            List<String> attr_value_agg = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets().stream().map(x -> {
                return ((Terms.Bucket) x).getKeyAsString();
            }).collect(Collectors.toList());
            attrVo.setAttrName(attr_name);
            attrVo.setAttrValue(attr_value_agg);
            listAttrs.add(attrVo);
        }
        result.setAttrs(listAttrs);
        result.setBrands(listBrands);
        result.setCatalogs(listCatalogs);
        //=========================分页信息
        long total = hits.getTotalHits().value;
        result.setTotal(total);//总记录数
        Integer pagesize = EsConstant.PRODCUT_PAGESIZE;
        int pageNum = ((int) total % pagesize == 0 )? ((int)total / pagesize) : ((int)total / pagesize + 1);
        result.setTotalPages(pageNum);
        result.setPageNum(searchParam.getPageNum());//当前页码
        return result;
    }

    //准备检索请求--------------根据dsl语句
    //过滤（按照属性 分类 品牌 价格区间 库存）
    //模糊匹配
    //
    //聚合分析
    private SearchRequest buildSearchQuery(SearchParam searchParam) {
        /**
         * 模糊匹配
         */
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //1 构建bool query
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //1.1 must 模糊查询
        if (StringUtils.isNotEmpty(searchParam.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", searchParam.getKeyword()));
        }
        //1.2 构建一个filter
        if (searchParam.getCatalog3Id() != null) {
            boolQuery.filter(QueryBuilders.termQuery("catalogId", searchParam.getCatalog3Id()));
        }
        if (CollectionUtils.isNotEmpty(searchParam.getBrandId())) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId", searchParam.getBrandId()));
        }
        if(searchParam.getHasStock() != null){
            boolQuery.filter(QueryBuilders.termQuery("hasStock", searchParam.getHasStock() == 1));
        }
        if (StringUtils.isNotEmpty(searchParam.getSkuPrice())) {//1_500/_500/500_
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] s = searchParam.getSkuPrice().split("_");
            if (s.length == 2) {
                rangeQuery.gte(s[0]).lte(s[1]);
            } else if (s.length == 1) {
                if (searchParam.getSkuPrice().startsWith("_")) {
                    rangeQuery.lte(s[1]);
                }
                if (searchParam.getSkuPrice().endsWith("_")) {
                    rangeQuery.gte(s[0]);
                }
            }
            boolQuery.filter(rangeQuery);

        }
        if (CollectionUtils.isNotEmpty(searchParam.getAttrs())) {//注意 每一个 attr 都会生成一个 nested 查询
            // attrs=1_安卓:安卓&attrs=2_5寸:6寸
            for (String attr : searchParam.getAttrs()) {
                BoolQueryBuilder nestBoolQuery = QueryBuilders.boolQuery();
                //attrs=1_安卓:苹果
                String[] s = attr.split("_");
                String attrId = s[0];//检索的属性id
                String[] attrValue = s[1].split(":");//这个属性检索用的值
                nestBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValue));
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestBoolQuery, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }
        }
        sourceBuilder.query(boolQuery);

        /**
         * 排序 分页 高亮
         */
        if (StringUtils.isNotEmpty(searchParam.getSort())) {//sort=skuPrice_desc/asc
            String[] s = searchParam.getSort().split("_");
            sourceBuilder.sort(s[0], s[1].equalsIgnoreCase("desc") ? SortOrder.DESC : SortOrder.ASC);
        }
        sourceBuilder.from((searchParam.getPageNum()-1)*EsConstant.PRODCUT_PAGESIZE);//from = (pageNum -1)*pageSize
        sourceBuilder.size(EsConstant.PRODCUT_PAGESIZE);

        if(StringUtils.isNotEmpty(searchParam.getKeyword())){//用户使用了关键字检索才使用高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }
        /**
         * 聚合分析
         */
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));

        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg");
        catalog_agg.field("catalogId").size(50);
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));

        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        attr_agg.subAggregation(attr_id_agg);

        sourceBuilder.aggregation(brand_agg).aggregation(catalog_agg).aggregation(attr_agg);

        System.out.println("构建的dsl语句："+sourceBuilder.toString());
        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
        return searchRequest;
    }
}
