package com.asiainfo.fsip.datasource;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@MapperScan(value = {"com.asiainfo.mcp.tmc.mapper", "com.asiainfo.mcp.tmc.dingding.mapper", "com.asiainfo.mcp.tmc.sso.mapper","com.asiainfo.fsip.mapper.tmc"}, sqlSessionTemplateRef = "tmcSqlSessionTemplate")
public class TmcDtaSourceConfig {

    @Bean(name = "tmcDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.tmc")
    public DataSource tmcDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "tmcSqlSessionFactory")
    public SqlSessionFactory databaseSqlSessionFactory(@Qualifier("tmcDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<Resource> resourceList = new ArrayList<>();
        resourceList.addAll(Arrays.asList(resolver.getResources("classpath*:mapper/*.xml")));
        resourceList.addAll(Arrays.asList(resolver.getResources("classpath*:mapper/tmc/*.xml")));
        factoryBean.setMapperLocations(resourceList.toArray(new Resource[resourceList.size()]));
        return factoryBean.getObject();
    }

    @Bean(name = "tmcTransactionManager")
    public DataSourceTransactionManager masterDataSourceTransactionManager(@Qualifier("tmcDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "tmcSqlSessionTemplate")
    public SqlSessionTemplate tmcSqlSessionTemplate(@Qualifier("tmcSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
