package cn.scut.mall.ware.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

@Configuration
public class MySeataConfig {

//    @Autowired
//    DataSourceProperties dataSourceProperties;
//
//    @Bean
//    public DataSource dataSource(){
//        HikariDataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
//        if (StringUtils.hasText(dataSourceProperties.getName())) {
//            dataSource.setPoolName(dataSourceProperties.getName());
//        }
//        return new DataSourceProxy(dataSource);
//    }
}
