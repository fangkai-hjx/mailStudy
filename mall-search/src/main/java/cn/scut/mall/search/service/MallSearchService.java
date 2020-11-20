package cn.scut.mall.search.service;

import cn.scut.mall.search.vo.SearchParam;
import cn.scut.mall.search.vo.SearchResult;

public interface MallSearchService {

    /**
     * @param searchParam:检索的所有参数
     * @return 返回检索的结果，里面包含页面需要的信息
     */
    public SearchResult search(SearchParam searchParam);
}
