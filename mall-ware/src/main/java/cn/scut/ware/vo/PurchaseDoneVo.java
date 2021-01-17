package cn.scut.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * {
 *     "id":1,
 *     "items":[
 *         {"itemId":1,"status":3,"reason":"采购成功"},
 *         {"itemId":2,"status":4,"reason":"无货"}
 *     ]
 * }
 */
@Data
public class PurchaseDoneVo {
    private Long id;
    private List<PurchaseItemDoneVo> items;
}
