package cn.scut.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("cn.scut.order.dao")
@EnableTransactionManagement
public class MallOrderApplication {
    public static void main(String[] args) {
        System.setProperty("nacos.standalone", "true");
        SpringApplication.run(MallOrderApplication.class,args);
    }
}
