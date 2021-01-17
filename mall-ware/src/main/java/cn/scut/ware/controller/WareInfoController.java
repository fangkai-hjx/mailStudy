package cn.scut.ware.controller;

import cn.scut.common.util.PageUtils;
import cn.scut.common.util.R;
import cn.scut.ware.entity.WareInfoEntity;
import cn.scut.ware.service.WareInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(value = "仓库信息 controller")
@RestController
@RequestMapping("/ware/wareinfo")
public class WareInfoController {

    @Autowired
    private WareInfoService wareInfoService;

    @ApiOperation("查询全部仓库信息")
    @GetMapping("/list")
    public R list(@RequestParam Map<String,Object> param){
        PageUtils page = wareInfoService.queryByCondition(param);
        return R.ok().put("page",page);
    }
    @ApiOperation("保存仓库信息")
    @PostMapping("/save")
    public R save(@RequestBody WareInfoEntity wareInfoEntity){
        wareInfoService.save(wareInfoEntity);
        return R.ok();
    }
    @ApiOperation("查询 仓库SKU信息")
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        WareInfoEntity wareInfo = wareInfoService.getById(id);

        return R.ok().put("wareInfo", wareInfo);
    }
}
