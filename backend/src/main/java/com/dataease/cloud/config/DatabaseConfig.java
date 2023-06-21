package com.dataease.cloud.config;

import com.dataease.cloud.properties.MybatisProperties;
import com.github.pagehelper.PageInterceptor;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.session.jdbc.config.annotation.SpringSessionDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 数据库配置加载
 * 如果配置多数据源的话 请覆盖{@link org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration}
 * 如果需要自定义配置的话需要 exclude {@link DataSourceAutoConfiguration}
 * {@link org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration}
 * 分别对应的初始化bean datasource transactionManager sqlSessionFactory sqlSessionTemplate
 */
@Configuration(proxyBeanMethods = false)
@EnableTransactionManagement
@EnableConfigurationProperties({DataSourceProperties.class, MybatisProperties.class})
@MapperScan(basePackages = {"com.dataease.cloud.base.mapper"})
public class DatabaseConfig {

    /**
     * 注册一个mybatis分页插件
     * 默认不允许重复注册
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public PageInterceptor pageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("helperDialect", "mysql");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("reasonable", "true");
        properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("pageSizeZero", "true");
        pageInterceptor.setProperties(properties);
        return pageInterceptor;
    }

    /**
     * 自定义dataSource
     * 必须支持SpringSessionDataSource
     * 并且作为系统的主数据源
     *
     * @param properties
     * @return
     */
    @Primary
    @Bean
    @SpringSessionDataSource
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource dataSource(DataSourceProperties properties) {
        HikariDataSource dataSource = createDataSource(properties, HikariDataSource.class);
        if (StringUtils.hasText(properties.getName())) {
            dataSource.setPoolName(properties.getName());
        }
        return dataSource;
    }

    /**
     * 事务管理器
     * 作为系统的主事务管理器
     *
     * @param dataSource
     * @return
     */
    @Primary
    @Bean(name = "baseTransactionManager")
    @ConditionalOnBean(DataSource.class)
    PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 默认的sqlSessionFactory
     * 作为主sql会话工厂
     * {@link org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration#sqlSessionFactory(DataSource)}
     *
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Primary
    @Bean
    @ConditionalOnBean({DataSource.class, PageInterceptor.class})
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource,
                                               MybatisProperties mybatisProperties,
                                               ApplicationContext applicationContext,
                                               PageInterceptor pageInterceptor) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        if (mybatisProperties.getConfiguration() == null || mybatisProperties.getConfiguration().getVfsImpl() == null) {
            factory.setVfs(SpringBootVFS.class);
        }
        if (StringUtils.hasText(mybatisProperties.getConfigLocation())) {
            factory.setConfigLocation(applicationContext.getResource(mybatisProperties.getConfigLocation()));
        }
        applyConfiguration(factory, mybatisProperties);
        if (mybatisProperties.getConfigurationProperties() != null) {
            factory.setConfigurationProperties(mybatisProperties.getConfigurationProperties());
        }
        factory.setPlugins(pageInterceptor);
        if (StringUtils.hasLength(mybatisProperties.getTypeAliasesPackage())) {
            factory.setTypeAliasesPackage(mybatisProperties.getTypeAliasesPackage());
        }
        if (mybatisProperties.getTypeAliasesSuperType() != null) {
            factory.setTypeAliasesSuperType(mybatisProperties.getTypeAliasesSuperType());
        }
        if (StringUtils.hasLength(mybatisProperties.getTypeHandlersPackage())) {
            factory.setTypeHandlersPackage(mybatisProperties.getTypeHandlersPackage());
        }
        Resource[] mapperLocations = mybatisProperties.resolveMapperLocations();
        if (!ObjectUtils.isEmpty(mapperLocations)) {
            factory.setMapperLocations(mapperLocations);
        }
        Set<String> factoryPropertyNames = Stream
                .of(new BeanWrapperImpl(SqlSessionFactoryBean.class).getPropertyDescriptors()).map(PropertyDescriptor::getName)
                .collect(Collectors.toSet());
        Class<? extends LanguageDriver> defaultLanguageDriver = mybatisProperties.getDefaultScriptingLanguageDriver();
        if (factoryPropertyNames.contains("defaultScriptingLanguageDriver")) {
            factory.setDefaultScriptingLanguageDriver(defaultLanguageDriver);
        }
        return factory.getObject();
    }

    /**
     * 属性转换
     *
     * @param factory
     * @param mybatisProperties
     */
    private void applyConfiguration(SqlSessionFactoryBean factory, MybatisProperties mybatisProperties) {
        MybatisProperties.CoreConfiguration coreConfiguration = mybatisProperties.getConfiguration();
        org.apache.ibatis.session.Configuration configuration = null;
        if (coreConfiguration != null || !StringUtils.hasText(mybatisProperties.getConfigLocation())) {
            configuration = new org.apache.ibatis.session.Configuration();
        }
        if (configuration != null && coreConfiguration != null) {
            coreConfiguration.applyTo(configuration);
        }
        factory.setConfiguration(configuration);
    }

    /**
     * 按照类型去创建数据源
     *
     * @param properties
     * @param type
     * @param <T>
     * @return
     */
    protected static <T> T createDataSource(DataSourceProperties properties, Class<? extends DataSource> type) {
        return (T) properties.initializeDataSourceBuilder().type(type).build();
    }

}
