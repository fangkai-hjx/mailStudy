package cn.scut.mall.order.feign;

import cn.scut.common.utils.R;
import cn.scut.mall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("mall-ware")
public interface WmsFeignService {

    @PostMapping("ware/waresku/hasstock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds);

    @RequestMapping("ware/wareinfo/fare")
    public R getFare(@RequestParam("addrId")Long addrId);

    @PostMapping("ware/waresku/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo vo);
}
