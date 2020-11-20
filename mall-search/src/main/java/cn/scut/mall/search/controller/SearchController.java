package cn.scut.mall.search.controller;

import cn.scut.mall.search.service.MallSearchService;
import cn.scut.mall.search.vo.SearchParam;
import cn.scut.mall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

    @Autowired
    private MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model){
        //根据页面传递来的查询参数，去es中检索商品
        SearchResult search = mallSearchService.search(searchParam);
        model.addAttribute("result",search);
        return "list";
    }
}
