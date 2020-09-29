package cn.scut.blog.service;

import cn.scut.blog.entity.BlogEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.scut.common.utils.PageUtils;

import java.util.Map;

/**
 * 
 *
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-18 10:45:51
 */
public interface BlogService extends IService<BlogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

