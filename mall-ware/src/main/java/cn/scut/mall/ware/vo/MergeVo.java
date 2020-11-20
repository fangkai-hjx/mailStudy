package cn.scut.mall.ware.vo;

import lombok.Data;
import java.util.List;

@Data
public class
MergeVo {
    private Long purchaseId;//采购单 id
    private List<Long> items;//合并的采购需求
}
