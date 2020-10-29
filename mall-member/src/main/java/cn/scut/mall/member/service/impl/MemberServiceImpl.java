package cn.scut.mall.member.service.impl;

import cn.scut.common.utils.HttpUtils;
import cn.scut.mall.member.dao.MemberLevelDao;
import cn.scut.mall.member.entity.MemberLevelEntity;
import cn.scut.mall.member.exception.PhoneExistException;
import cn.scut.mall.member.exception.UsernameExistException;
import cn.scut.mall.member.service.MemberLevelService;
import cn.scut.mall.member.vo.MemberLoginVo;
import cn.scut.mall.member.vo.MemberRegistVo;
import cn.scut.mall.member.vo.SocialUser;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.Query;

import cn.scut.mall.member.dao.MemberDao;
import cn.scut.mall.member.entity.MemberEntity;
import cn.scut.mall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {


    @Autowired
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegistVo vo) {
        MemberDao baseMapper = this.baseMapper;
        MemberEntity entity = new MemberEntity();
        //设置默认会员等级
        MemberLevelEntity level = memberLevelDao.getDefaultLevel();
        entity.setLevelId(level.getId());
        //检查用户名和手机号的唯一性----为了让controller能感知异常，异常机制
        checkPhoneUnique(vo.getPhone());
        checkUsernameUnique(vo.getUsername());

        entity.setMobile(vo.getPhone());
        entity.setUsername(vo.getUsername());
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        entity.setPassword(bCryptPasswordEncoder.encode(vo.getPassword()));//密码加密进行存储
        baseMapper.insert(entity);
    }

    @Override
    public void checkUsernameUnique(String username) throws UsernameExistException {
        MemberDao memberDao = this.baseMapper;
        Integer integer = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if (integer > 0) {
            throw new UsernameExistException();
        }
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException {
        MemberDao memberDao = this.baseMapper;
        Integer integer = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (integer > 0) {
            throw new PhoneExistException();
        }
    }

    @Override
    public MemberEntity
    login(MemberLoginVo vo) {
        MemberDao baseMapper = this.baseMapper;
        //去数据库查询 加密 后 的 密码
        //用户可以 用 用户名 或者 手机号
        MemberEntity entity = baseMapper.selectOne(new QueryWrapper<MemberEntity>()
                .eq("username", vo.getLoginacct())
                .or()
                .eq("mobile", vo.getLoginacct()));
        if (entity == null) { //登录失败
            return null;
        }
        String passwordDb = entity.getPassword();//数据库的 password 字段
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean matches = passwordEncoder.matches(vo.getPassword(), passwordDb);//密码 对比
        if (matches) {//登录成功
            return entity;
        }
        return null;
    }

    @Override
    public MemberEntity login(SocialUser vo) throws Exception {
        //登录和注册 合并逻辑
        //判断当前社交用户 是否 之前 登陆过
        MemberDao baseMapper = this.baseMapper;
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", vo.getUid()));
        if (memberEntity != null) {//该用户 之前 注册过
            MemberEntity update = new MemberEntity();
            update.setId(memberEntity.getId());
            update.setAccessToken(memberEntity.getAccessToken());
            update.setExpiresIn(memberEntity.getExpiresIn());
            baseMapper.updateById(update);

            memberEntity.setAccessToken(vo.getAccess_token());
            memberEntity.setExpiresIn(vo.getRemind_in());
            return memberEntity;
        } else {
            //没有查到 当前 社交用户 对应的 记录 我们 需要 注册一个
            MemberEntity register = new MemberEntity();
            try {
                //3 查询 当前社交用户的 社交账户信息
                //https://api.weibo.com/2/users/show.json?access_token=2.00xjPcIHXRefSB2bbd244ba3fBE17C&uid=6540257485
                Map<String, String> map = new HashMap<>();
                map.put("access_token", vo.getAccess_token());
                map.put("uid", vo.getUid());
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "GET", new HashMap<>(), map);
                if (response.getStatusLine().getStatusCode() == 200) {
                    //查询成功
                    HttpEntity entity = response.getEntity();
                    String s = EntityUtils.toString(entity);
                    JSONObject jsonObject = JSON.parseObject(s);
                    String name = jsonObject.getString("name");
                    String gender = jsonObject.getString("gender");
                    String location = jsonObject.getString("location");
                    //TODO ....等等数据
                    register.setNickname(name);
                    register.setGender(gender.equals("m") ? 1 : 0);
                    register.setCity(location);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            register.setSocialUid(vo.getUid());
            register.setAccessToken(vo.getAccess_token());
            register.setExpiresIn(vo.getExpires_in());
            baseMapper.insert(register);
            return register;
        }
    }
}
