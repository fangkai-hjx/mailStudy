package cn.scut.mall.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class MallSessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("MALLSESSION");
//        serializer.setCookiePath("/");
        serializer.setDomainName("mall.com");
//        serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$");
        return serializer;
    }

    /**
     * 序列化机制
     * @return
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {

        return new GenericJackson2JsonRedisSerializer();
    }
}
