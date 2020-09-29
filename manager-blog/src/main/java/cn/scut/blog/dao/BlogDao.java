package cn.scut.blog.dao;

import cn.scut.blog.entity.BlogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-18 10:45:51
 */
@Mapper
public interface BlogDao extends BaseMapper<BlogEntity> {
	
}
