package cn.scut.mall.order.interceptor;

import cn.scut.common.constant.AuthServerConstant;
import cn.scut.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberRespVo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //order/order/status/{orderSn}
        String uri = request.getRequestURI();
        //其他服务 调用 订单服务的方法 ，不需要登录拦截
        AntPathMatcher matcher = new AntPathMatcher();
        boolean match = matcher.match("/order/order/status/**", uri);
        boolean match1 = matcher.match("/payed/notify", uri);
        if(match || match1){
            return true;//放行
        }
        HttpSession session = request.getSession();
        MemberRespVo memberRespVo = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (memberRespVo != null) {//已经登录
            threadLocal.set(memberRespVo);
            return true;
        } else {//没登录---》去登陆
            request.getSession().setAttribute("msg","请先进行登录！");
            response.sendRedirect("http://auth.mall.com/login.html");
            return false;
        }

    }

}
