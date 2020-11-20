package cn.scut.mall.product.web;

import cn.scut.mall.product.entity.CategoryEntity;
import cn.scut.mall.product.service.CategoryService;
import cn.scut.mall.product.vo.Catalog2Vo;
import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 处理页面调整
 */
@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping(value = {"/", "/index.html"})
    public String indexPage(Model model) {
        //TODO 查出一节分类
        List<CategoryEntity> list = categoryService.getLevel1Categorys();
        //默认前缀 "classpath:/templates/";
        //默认后缀 ".html";
        //视图解析器进行拼装：
        // "classpath:/templates/"; + 返回 + ".html";
        model.addAttribute("categorys",list);
        return "index";
    }
    //index/catalog.json
    @ResponseBody
    @GetMapping("index/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        Map<String, List<Catalog2Vo>> map = categoryService.getCatalogJson();
        return map;
    }
    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }
}
