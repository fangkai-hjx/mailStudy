package cn.scut.mall.member.service;

import cn.scut.mall.member.vo.MemberLoginVo;
import cn.scut.mall.member.vo.MemberRegistVo;
import cn.scut.mall.member.vo.SocialUser;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.scut.common.utils.PageUtils;
import cn.scut.mall.member.entity.MemberEntity;

import java.io.IOException;
import java.util.Map;

/**
 * 会员
 *
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 12:27:55
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(MemberRegistVo vo);

    void checkUsernameUnique(String username);

    void checkPhoneUnique(String phone);

    MemberEntity login(MemberLoginVo vo);

    MemberEntity login(SocialUser vo) throws Exception;
}

