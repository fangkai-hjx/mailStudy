package cn.scut.mall.cart.interceptor;

import cn.scut.common.constant.AuthServerConstant;
import cn.scut.common.constant.CartConstant;
import cn.scut.common.vo.MemberRespVo;
import cn.scut.mall.cart.vo.UserInfoTo;
import org.assertj.core.util.Arrays;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Stack;
import java.util.UUID;

/**
 * 在执行目标方法前 判断用户的登录状态 并封装传递给controller目标请求
 */
@Component
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    /**
     * 目标方法执行之前
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();//这里的session都是spring session 包装后的
        MemberRespVo memberRespVo = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);

        UserInfoTo userInfoTo = new UserInfoTo();//封装用户信息

        if (memberRespVo != null) {
            userInfoTo.setUserId(memberRespVo.getId());//用户登录
        }
        //用户没登录
        Cookie[] cookies = request.getCookies();
        if (!Arrays.isNullOrEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (CartConstant.TEMP_USER_COOKIE_NAME.equals(name)) {
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setTempUser(true);
                }
            }
        }
        //如果没有临时用户 一定分配一个临时用户
        if (StringUtils.isEmpty(userInfoTo.getUserKey())) {
            String uuid = UUID.randomUUID().toString();
            userInfoTo.setUserKey(uuid);
        }
        threadLocal.set(userInfoTo);
        return true;
    }

    /**
     * 业务执行之后：让浏览器保存一个cookie，记录临时用户信息
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        //如果没有临时用户，一定要提供临时用户信息
        if (!userInfoTo.getTempUser()) {
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);//单位 秒
            response.addCookie(cookie);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    public static void main(String[] args) {

    }
//请设计一个函数，用来判断在一个矩阵中是否存在一条包含某字符串所有字符的路径。
//路径可以从矩阵中的任意一格开始，每一步可以在矩阵中向左、右、上、下移动一格。
//如果一条路径经过了矩阵的某一格，那么该路径不能再次进入该格子。
//例如，在下面的3×4的矩阵中包含一条字符串“bfce”的路径（路径中的字母用加粗标出）。
//
//[["a","b","c","e"],
//["s","f","c","s"],
//["a","d","e","e"]]
//
//但矩阵中不包含字符串“abfb”的路径，因为字符串的第一个字符b占据了矩阵中的第一行第二个格子之后，路径不能再次进入这个格子。

}
