package cn.scut.common.constant;

/**
 * 错误码列表
 *  10：通用
 *      001：参数格式校验失败
 *  11：商品
 *  12：订单
 *  13：购物车
 *  14：物流
 */
public enum ResponseCode {

    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VAILD_EXCEPTION(10001,"参数格式校验失败"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常");

    private int code;
    private String msg;
    ResponseCode(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
