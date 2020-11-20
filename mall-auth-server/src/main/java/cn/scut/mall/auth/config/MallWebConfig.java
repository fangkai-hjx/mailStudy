package cn.scut.mall.auth.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class MallWebConfig implements WebMvcConfigurer{

    /**
     *  @GetMapping("/login.html")
     *     public String loginPage(){
     *         return "login";
     *     }
     * @GetMapping("/reg.html")
     *     public String regPage(){
     *         return "reg";
     *     }
     * @param registry
     */
    //路径映射 默认 都是 GET 方式访问
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/login.html").setViewName("login");//添加视图控制器
        registry.addViewController("/reg.html").setViewName("reg");//添加视图控制器
    }

}
