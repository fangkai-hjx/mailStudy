package cn.scut.ware.vo;

import lombok.Data;

/**
 *        {"itemId":2,"status":4,"reason":"无货"}
 */
@Data
public class PurchaseItemDoneVo {
    private Long itemId;
    private Integer status;
    private String reason;
}
