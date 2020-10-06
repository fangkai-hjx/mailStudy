package cn.scut.mall.product.exception;

import cn.scut.common.exception.BizCodeEnume;
import cn.scut.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一异常处理
 */
@Slf4j
//@ResponseBody
//@ControllerAdvice(basePackages = "cn.scut.mall.product.app")
@RestControllerAdvice(basePackages = "cn.scut.mall.product.app")//等价于上面两个
public class MallExceptionControllerAdvice {


    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public R handleValidException(MethodArgumentNotValidException e) {//这里的void也可以改成放回ModelAndView，放回错误页面
        log.error("数据校验出现问题{},异常类型{}", e.getMessage(), e.getClass());
        BindingResult result = e.getBindingResult();
        Map<String, String> map = new HashMap<>();
        result.getFieldErrors().forEach((item) -> {
            //FiledError
            String message = item.getDefaultMessage();//获取默认错误消息，如果没配就是默认的，否则就是自己的
            String field = item.getField();//哪个字段发生错误
            map.put(field, message);
        });
        return R.error(BizCodeEnume.VALID_EXCEPTION.getCode(), BizCodeEnume.VALID_EXCEPTION.getMessage()).put("data", map);
    }

    @ExceptionHandler(value = {Throwable.class})
    public R handleValidException(Throwable throwable) {
        return R.error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(),BizCodeEnume.UNKNOW_EXCEPTION.getMessage());
    }
}
