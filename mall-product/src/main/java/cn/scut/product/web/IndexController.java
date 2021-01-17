package cn.scut.product.web;

import cn.scut.product.entity.CategoryEntity;
import cn.scut.product.service.CategoryService;
import cn.scut.product.vo.Catalog2Vo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){
        List<CategoryEntity> categoryLevels = categoryService.getLevel1Categorys();
        model.addAttribute("categorys",categoryLevels);
        //视图解析器：前缀classpath:/templates + {。。。。} + 后缀.html
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/json/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatalogJson(){
        long start = System.currentTimeMillis();
        Map<String, List<Catalog2Vo>> map = categoryService.getCatalogJson();
        long end = System.currentTimeMillis();
        System.out.println("耗时："+(end-start)+"毫秒");
        return map;
    }
    @ResponseBody
    @GetMapping("/index/json2/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatalogJson2(){
        long start = System.currentTimeMillis();
        Map<String, List<Catalog2Vo>> map = categoryService.getCatalogJsonFromDB();
        long end = System.currentTimeMillis();
        System.out.println("耗时："+(end-start)+"毫秒");
        return map;
    }
}
