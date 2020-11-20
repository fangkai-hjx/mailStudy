package cn.scut.mall.order.config;

import com.zaxxer.hikari.HikariDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

@Configuration
public class MySeataConfig {

//    @Autowired
//    DataSourceProperties dataSourceProperties;
//
//    /**
//     * 使用seata的代理数据源 包装 我们自己的数据源
//     * @return
//     */
//    @Bean
//    public DataSource dataSource(){
//        HikariDataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
//        if (StringUtils.hasText(dataSourceProperties.getName())) {
//            dataSource.setPoolName(dataSourceProperties.getName());
//        }
//        return new DataSourceProxy(dataSource);
//    }
}
