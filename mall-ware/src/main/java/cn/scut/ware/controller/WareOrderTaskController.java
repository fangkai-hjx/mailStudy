package cn.scut.ware.controller;

import cn.scut.common.util.PageUtils;
import cn.scut.common.util.R;
import cn.scut.ware.entity.WareOrderTaskEntity;
import cn.scut.ware.service.WareOrderTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@Api(value = "采购需求信息 controller")
@RestController
@RequestMapping("/ware/wareordertask")
public class WareOrderTaskController {
    @Autowired
    private WareOrderTaskService wareOrderTaskService;

    @ApiOperation("查询全部 采购需求")
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareOrderTaskService.queryPage(params);

        return R.ok().put("page", page);
    }
    @ApiOperation("查询 指定 采购需求")
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        WareOrderTaskEntity wareOrderTask = wareOrderTaskService.getById(id);
        return R.ok().put("wareOrderTask", wareOrderTask);
    }
    @ApiOperation("新增 指定 采购需求")
    @PostMapping("/save")
    public R save(@RequestBody WareOrderTaskEntity wareOrderTask){
        wareOrderTaskService.save(wareOrderTask);
        return R.ok();
    }
    @ApiOperation("修改 指定 采购需求")
    @PostMapping("/update")
    public R update(@RequestBody WareOrderTaskEntity wareOrderTask){
        wareOrderTaskService.updateById(wareOrderTask);
        return R.ok();
    }
    @ApiOperation("删除 批量 采购需求")
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        wareOrderTaskService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }
}
