package cn.scut.mall.order.exception;

public class NoStockException extends RuntimeException {

    private Long skuId;

    public NoStockException(String msg) {
        super(msg);
    }

    public NoStockException(Long skuId) {
        super("商品Id为：" + skuId + "没有足够的库存");
    }
}
