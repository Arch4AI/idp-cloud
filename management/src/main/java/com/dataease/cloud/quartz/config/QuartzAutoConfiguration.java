package com.dataease.cloud.quartz.config;

import com.dataease.cloud.quartz.properties.CloudQuartzProperties;
import com.dataease.cloud.quartz.utils.QuartzInstanceIdGenerator;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

/**
 * 自定义quartz启动器
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/17 11:25 AM
 **/
@Configuration
@EnableConfigurationProperties({CloudQuartzProperties.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
public class QuartzAutoConfiguration {
    private CloudQuartzProperties cloudQuartzProperties;

    public QuartzAutoConfiguration(CloudQuartzProperties cloudQuartzProperties) {
        this.cloudQuartzProperties = cloudQuartzProperties;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.quartz.hikari")
    @QuartzDataSource
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "cloud.quartz", value = "enabled", havingValue = "true")
    public DataSource quartzDataSource(DataSourceProperties properties) {
        return DataSourceBuilder.create(properties.getClassLoader()).type(HikariDataSource.class)
                .driverClassName(properties.determineDriverClassName())
                .url(properties.determineUrl())
                .username(properties.determineUsername())
                .password(properties.determinePassword())
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "cloud.quartz", value = "enabled", havingValue = "true")
    public TimeZone quartzTimeZone() {
        return TimeZone.getTimeZone(this.cloudQuartzProperties.getTimeZone());
    }

    private DataSource getDataSource(DataSource dataSource,@QuartzDataSource ObjectProvider<DataSource> quartzDataSource) {
        DataSource dataSourceIfAvailable = (DataSource)quartzDataSource.getIfAvailable();
        return dataSourceIfAvailable != null ? dataSourceIfAvailable : dataSource;
    }

    @Bean
    @ConditionalOnClass({DataSource.class})
    @ConditionalOnProperty(prefix = "cloud.quartz", value = "enabled", havingValue = "true")
    public SchedulerFactoryBean clusterSchedulerFactoryBean(DataSource dataSource,@QuartzDataSource ObjectProvider<DataSource> quartzDataSource,ApplicationContext applicationContext) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        // 设置待定
        schedulerFactoryBean.setDataSource(getDataSource(dataSource,quartzDataSource));
        schedulerFactoryBean.setApplicationContext(applicationContext);
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setStartupDelay((int)this.cloudQuartzProperties.getStartupDelay().getSeconds());
        Properties props = new Properties();
        props.put("org.quartz.scheduler.instanceName", this.cloudQuartzProperties.getSchedulerName());
        props.put("org.quartz.scheduler.instanceId", "AUTO");
        props.put("org.quartz.scheduler.instanceIdGenerator.class", QuartzInstanceIdGenerator.class.getName());
        props.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        props.put("org.quartz.jobStore.tablePrefix", "cloud_qrtz_");//自定义的表结构
        props.put("org.quartz.jobStore.isClustered", "true");
        props.put("org.quartz.jobStore.clusterCheckinInterval", "20000");
        props.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        String threadCount = this.cloudQuartzProperties.getThreadCount().toString();
        props.put("org.quartz.threadPool.threadCount", threadCount);
        props.put("org.quartz.threadPool.threadPriority", "5");
        props.put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
        Map<String, String> properties = this.cloudQuartzProperties.getProperties();
        if (!CollectionUtils.isEmpty(properties)) {
            props.putAll(properties);
        }

        schedulerFactoryBean.setQuartzProperties(props);
        if (this.cloudQuartzProperties.getSchedulerName() != null) {
            schedulerFactoryBean.setBeanName(this.cloudQuartzProperties.getSchedulerName());
        }

        return schedulerFactoryBean;
    }
}
