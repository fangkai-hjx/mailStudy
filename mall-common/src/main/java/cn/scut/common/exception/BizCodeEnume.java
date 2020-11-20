package cn.scut.common.exception;

import lombok.Getter;

/**
 * 错误码和错误信息定义类
 *  1.错误码定义规则为5位数字
 *  2.前两位表示业务场景，最后三位表示错误码。例如100001中10表示通用 001表示系统未知异常
 *  3.维护错误码后需要维护错误描述。将他们定义位美剧形式
 *  错误码列表：
 *  10：通用
 *      001：参数格式校验
 *      002：验证码获取频率太高
 *  11：商品
 *  12：订单
 *  13：购物车
 *  14：物流
 *  15：用户
 *  21: 库存
 *  31:支付
 */
@Getter
public enum BizCodeEnume {

    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VALID_EXCEPTION(10001,"参数格式校验失败"),
    TOO_MANY_REQUEST(10002,"请求流量过大"),
    SMS_CODE_EXCEPTION(10003,"验证码获取频率太高，稍后再试"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常"),
    USER_EXIST_EXCEPTION(15001,"用户已存在"),
    PHONE_EXIST_EXCEPTION(15002,"手机号已存在"),
    LOGINACCT_PASSWORD_EXCEPTION(15003,"账户密码错误"),
    NO_STOCK_EXCEPTION(21000,"商品库存不足"),
    PAY_CANCEL_EXCEPTION(31000,"支付取消失败");
    private int code;
    private String message;

    BizCodeEnume(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
