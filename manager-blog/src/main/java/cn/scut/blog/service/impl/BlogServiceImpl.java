package cn.scut.blog.service.impl;

import cn.scut.blog.dao.BlogDao;
import cn.scut.blog.entity.BlogEntity;
import cn.scut.blog.service.BlogService;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.Query;


@Service("blogService")
public class BlogServiceImpl extends ServiceImpl<BlogDao, BlogEntity> implements BlogService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BlogEntity> page = this.page(
                new Query<BlogEntity>().getPage(params),
                new QueryWrapper<BlogEntity>()
        );

        return new PageUtils(page);
    }

}