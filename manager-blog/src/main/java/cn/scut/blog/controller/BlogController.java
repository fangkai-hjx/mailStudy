package cn.scut.blog.controller;

import java.util.Arrays;
import java.util.Map;

import cn.scut.blog.entity.BlogEntity;
import cn.scut.blog.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.R;



/**
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-18 10:45:51
 */
@RestController
@RequestMapping("blog/blog")
public class BlogController {
    @Autowired
    private BlogService blogService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = blogService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		BlogEntity blog = blogService.getById(id);

        return R.ok().put("blog", blog);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody BlogEntity blog){
		blogService.save(blog);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody BlogEntity blog){
		blogService.updateById(blog);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		blogService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
