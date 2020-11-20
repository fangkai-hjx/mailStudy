package cn.scut.mall.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class MallFeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                //拿到刚进来的这个请求---addTrade的reguest

                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if(attributes !=null){
                    HttpServletRequest request = attributes.getRequest();
                    if(request!=null){
                        //同步请求头的cookie
                        String cookie = request.getHeader("Cookie");
                        //给新请求 带上Cookie
                        template.header("Cookie",cookie);
                        System.out.println("feign远程之前先进行");
                    }
                }
            }
        };
    }
}
