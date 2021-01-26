package cn.scut.cart.interceptor;

import cn.scut.cart.to.UserInfoTo;
import cn.scut.common.constant.AuthServerConstant;
import cn.scut.common.constant.CartConstant;
import cn.scut.common.vo.MemberRespVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * 在执行目标方法前，判断用户的登录状态。并封装传递给controller目标请求
 */
public class CartInterceptor implements HandlerInterceptor {

    public  static
    ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();
    /**
     * 目标方法执行之前拦截
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final HttpSession session = request.getSession();//这个session是spring session 包装后的session,从redis获取的
        final MemberRespVo memberRespVo = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);

        UserInfoTo userInfoTo = new UserInfoTo();
        if (memberRespVo != null) {
            //用户已经登录
            userInfoTo.setUserId(memberRespVo.getId());
        }
        //用户没登录
        final Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies) {
                final String name = cookie.getName();
                if(name.equals(CartConstant.TEMP_USER_COOKIE_NAME)){
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setTempUser(true);//已经有临时用户信息了
                }
            }
        }
        //如果用户第一次登录系统，需要分配一个user-key给用户
        //如果没有临时用户，分配一个临时用户
        if(StringUtils.isEmpty(userInfoTo.getUserKey())){
            String uuid = UUID.randomUUID().toString();//临时用户ID
            userInfoTo.setUserKey(uuid);
        }
        threadLocal.set(userInfoTo);
        return true;
    }

    /**
     * 目标方法执行之后拦截,如果是临时用户让浏览器保存一个cookie
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        final UserInfoTo userInfoTo = threadLocal.get();
        //如果 cookie 没有 临时用户信息--->生成cookie信息 保存到浏览器
        if(userInfoTo.getTempUser() == false){
            final Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            cookie.setDomain("fkmall.shop");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);//一个月
            response.addCookie(cookie);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
