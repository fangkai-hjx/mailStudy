package cn.scut.product.app;

import cn.scut.common.util.R;
import cn.scut.product.entity.CategoryEntity;
import cn.scut.product.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/* 类注解 */
@Api(value = "CategoryController API")
@RestController
@RequestMapping("product/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /* 方法注解 */
    @ApiOperation(value = "查出 所有 分类 以 树形结构显示", notes = "")
    @GetMapping("/list/tree")
    public R list(){
//        List<CategoryEntity> list = categoryService.list();//查出所有分类
        List<CategoryEntity> categories = categoryService.listWithTree();
        return R.ok().put("data",categories);
    }
}
