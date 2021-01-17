package cn.scut.member.service;

import cn.scut.common.util.PageUtils;
import cn.scut.member.entity.MemberLevelEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface MemberLevelService extends IService<MemberLevelEntity> {
    PageUtils queryPage(Map<String, Object> params);
}
