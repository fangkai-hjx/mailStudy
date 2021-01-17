package cn.scut.member.controller;


import cn.scut.common.util.PageUtils;
import cn.scut.common.util.R;
import cn.scut.member.entity.MemberLevelEntity;
import cn.scut.member.service.MemberLevelService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(value = "会员等级信息 Controller")
@RestController
@RequestMapping("/member/memberlevel")
public class MemberLevelController {
    @Autowired
    private MemberLevelService memberLevelService;

    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberLevelService.queryPage(params);

        return R.ok().put("page", page);
    }
    @PostMapping("/save")
    public R save(@RequestBody MemberLevelEntity memberLevelEntity){
        memberLevelService.save(memberLevelEntity);
        return R.ok();
    }
}
