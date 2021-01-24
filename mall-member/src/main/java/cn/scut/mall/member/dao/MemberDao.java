package cn.scut.mall.member.dao;

import cn.scut.mall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 12:27:55
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}