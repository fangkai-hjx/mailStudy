package cn.scut.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;


@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");//所有方式，get put post
        corsConfiguration.addAllowedOrigin("*");//任意来源
        corsConfiguration.setAllowCredentials(true);//携带cookie

        source.registerCorsConfiguration("/**",corsConfiguration);//任意路径均跨域
        return new CorsWebFilter(source);
    }
}
