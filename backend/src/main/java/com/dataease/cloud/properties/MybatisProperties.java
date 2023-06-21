package com.dataease.cloud.properties;

import org.apache.ibatis.io.VFS;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.*;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

/**
 * mybatis的配置信息
 * 用于覆盖原来的properties来实现自定义的sqlSessionFactory
 * {@link org.mybatis.spring.boot.autoconfigure.MybatisProperties}
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/20 3:57 PM
 **/
@ConfigurationProperties(prefix = MybatisProperties.MYBATIS_PREFIX, ignoreUnknownFields = true)
public class MybatisProperties {

    public static final String MYBATIS_PREFIX = "mybatis";

    private static final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    private String configLocation;

    private String[] mapperLocations;

    private String typeAliasesPackage;

    private Class<?> typeAliasesSuperType;

    private String typeHandlersPackage;

    private boolean checkConfigLocation = false;

    private ExecutorType executorType;

    private Class<? extends LanguageDriver> defaultScriptingLanguageDriver;

    private Properties configurationProperties;

    private CoreConfiguration configuration;

    public String getConfigLocation() {
        return this.configLocation;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    public String[] getMapperLocations() {
        return this.mapperLocations;
    }

    public void setMapperLocations(String[] mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public String getTypeHandlersPackage() {
        return this.typeHandlersPackage;
    }

    public void setTypeHandlersPackage(String typeHandlersPackage) {
        this.typeHandlersPackage = typeHandlersPackage;
    }

    public String getTypeAliasesPackage() {
        return this.typeAliasesPackage;
    }

    public void setTypeAliasesPackage(String typeAliasesPackage) {
        this.typeAliasesPackage = typeAliasesPackage;
    }

    public Class<?> getTypeAliasesSuperType() {
        return typeAliasesSuperType;
    }

    public void setTypeAliasesSuperType(Class<?> typeAliasesSuperType) {
        this.typeAliasesSuperType = typeAliasesSuperType;
    }

    public boolean isCheckConfigLocation() {
        return this.checkConfigLocation;
    }

    public void setCheckConfigLocation(boolean checkConfigLocation) {
        this.checkConfigLocation = checkConfigLocation;
    }

    public ExecutorType getExecutorType() {
        return this.executorType;
    }

    public void setExecutorType(ExecutorType executorType) {
        this.executorType = executorType;
    }

    public Class<? extends LanguageDriver> getDefaultScriptingLanguageDriver() {
        return defaultScriptingLanguageDriver;
    }

    public void setDefaultScriptingLanguageDriver(Class<? extends LanguageDriver> defaultScriptingLanguageDriver) {
        this.defaultScriptingLanguageDriver = defaultScriptingLanguageDriver;
    }

    public Properties getConfigurationProperties() {
        return configurationProperties;
    }

    public void setConfigurationProperties(Properties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    public CoreConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(CoreConfiguration configuration) {
        this.configuration = configuration;
    }

    public Resource[] resolveMapperLocations() {
        return Stream.of(Optional.ofNullable(this.mapperLocations).orElse(new String[0]))
                .flatMap(location -> Stream.of(getResources(location))).toArray(Resource[]::new);
    }

    private Resource[] getResources(String location) {
        try {
            return resourceResolver.getResources(location);
        } catch (IOException e) {
            return new Resource[0];
        }
    }

    public static class CoreConfiguration {

        private Boolean safeRowBoundsEnabled;

        private Boolean safeResultHandlerEnabled;

        private Boolean mapUnderscoreToCamelCase;

        private Boolean aggressiveLazyLoading;

        private Boolean multipleResultSetsEnabled;

        private Boolean useGeneratedKeys;

        private Boolean useColumnLabel;

        private Boolean cacheEnabled;

        private Boolean callSettersOnNulls;

        private Boolean useActualParamName;

        private Boolean returnInstanceForEmptyRow;

        private Boolean shrinkWhitespacesInSql;

        private Boolean nullableOnForEach;

        private Boolean argNameBasedConstructorAutoMapping;

        private Boolean lazyLoadingEnabled;

        private Integer defaultStatementTimeout;

        private Integer defaultFetchSize;

        private LocalCacheScope localCacheScope;

        private JdbcType jdbcTypeForNull;

        private ResultSetType defaultResultSetType;

        private ExecutorType defaultExecutorType;

        private AutoMappingBehavior autoMappingBehavior;

        private AutoMappingUnknownColumnBehavior autoMappingUnknownColumnBehavior;

        private String logPrefix;

        private Set<String> lazyLoadTriggerMethods;

        private Class<? extends Log> logImpl;

        private Class<? extends VFS> vfsImpl;

        private Class<?> defaultSqlProviderType;

        Class<? extends TypeHandler> defaultEnumTypeHandler;

        private Class<?> configurationFactory;

        private Properties variables;

        public Boolean getSafeRowBoundsEnabled() {
            return safeRowBoundsEnabled;
        }

        public void setSafeRowBoundsEnabled(Boolean safeRowBoundsEnabled) {
            this.safeRowBoundsEnabled = safeRowBoundsEnabled;
        }

        public Boolean getSafeResultHandlerEnabled() {
            return safeResultHandlerEnabled;
        }

        public void setSafeResultHandlerEnabled(Boolean safeResultHandlerEnabled) {
            this.safeResultHandlerEnabled = safeResultHandlerEnabled;
        }

        public Boolean getMapUnderscoreToCamelCase() {
            return mapUnderscoreToCamelCase;
        }

        public void setMapUnderscoreToCamelCase(Boolean mapUnderscoreToCamelCase) {
            this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
        }

        public Boolean getAggressiveLazyLoading() {
            return aggressiveLazyLoading;
        }

        public void setAggressiveLazyLoading(Boolean aggressiveLazyLoading) {
            this.aggressiveLazyLoading = aggressiveLazyLoading;
        }

        public Boolean getMultipleResultSetsEnabled() {
            return multipleResultSetsEnabled;
        }

        public void setMultipleResultSetsEnabled(Boolean multipleResultSetsEnabled) {
            this.multipleResultSetsEnabled = multipleResultSetsEnabled;
        }

        public Boolean getUseGeneratedKeys() {
            return useGeneratedKeys;
        }

        public void setUseGeneratedKeys(Boolean useGeneratedKeys) {
            this.useGeneratedKeys = useGeneratedKeys;
        }

        public Boolean getUseColumnLabel() {
            return useColumnLabel;
        }

        public void setUseColumnLabel(Boolean useColumnLabel) {
            this.useColumnLabel = useColumnLabel;
        }

        public Boolean getCacheEnabled() {
            return cacheEnabled;
        }

        public void setCacheEnabled(Boolean cacheEnabled) {
            this.cacheEnabled = cacheEnabled;
        }

        public Boolean getCallSettersOnNulls() {
            return callSettersOnNulls;
        }

        public void setCallSettersOnNulls(Boolean callSettersOnNulls) {
            this.callSettersOnNulls = callSettersOnNulls;
        }

        public Boolean getUseActualParamName() {
            return useActualParamName;
        }

        public void setUseActualParamName(Boolean useActualParamName) {
            this.useActualParamName = useActualParamName;
        }

        public Boolean getReturnInstanceForEmptyRow() {
            return returnInstanceForEmptyRow;
        }

        public void setReturnInstanceForEmptyRow(Boolean returnInstanceForEmptyRow) {
            this.returnInstanceForEmptyRow = returnInstanceForEmptyRow;
        }

        public Boolean getShrinkWhitespacesInSql() {
            return shrinkWhitespacesInSql;
        }

        public void setShrinkWhitespacesInSql(Boolean shrinkWhitespacesInSql) {
            this.shrinkWhitespacesInSql = shrinkWhitespacesInSql;
        }

        public Boolean getNullableOnForEach() {
            return nullableOnForEach;
        }

        public void setNullableOnForEach(Boolean nullableOnForEach) {
            this.nullableOnForEach = nullableOnForEach;
        }

        public Boolean getArgNameBasedConstructorAutoMapping() {
            return argNameBasedConstructorAutoMapping;
        }

        public void setArgNameBasedConstructorAutoMapping(Boolean argNameBasedConstructorAutoMapping) {
            this.argNameBasedConstructorAutoMapping = argNameBasedConstructorAutoMapping;
        }

        public String getLogPrefix() {
            return logPrefix;
        }

        public void setLogPrefix(String logPrefix) {
            this.logPrefix = logPrefix;
        }

        public Class<? extends Log> getLogImpl() {
            return logImpl;
        }

        public void setLogImpl(Class<? extends Log> logImpl) {
            this.logImpl = logImpl;
        }

        public Class<? extends VFS> getVfsImpl() {
            return vfsImpl;
        }

        public void setVfsImpl(Class<? extends VFS> vfsImpl) {
            this.vfsImpl = vfsImpl;
        }

        public Class<?> getDefaultSqlProviderType() {
            return defaultSqlProviderType;
        }

        public void setDefaultSqlProviderType(Class<?> defaultSqlProviderType) {
            this.defaultSqlProviderType = defaultSqlProviderType;
        }

        public LocalCacheScope getLocalCacheScope() {
            return localCacheScope;
        }

        public void setLocalCacheScope(LocalCacheScope localCacheScope) {
            this.localCacheScope = localCacheScope;
        }

        public JdbcType getJdbcTypeForNull() {
            return jdbcTypeForNull;
        }

        public void setJdbcTypeForNull(JdbcType jdbcTypeForNull) {
            this.jdbcTypeForNull = jdbcTypeForNull;
        }

        public Set<String> getLazyLoadTriggerMethods() {
            return lazyLoadTriggerMethods;
        }

        public void setLazyLoadTriggerMethods(Set<String> lazyLoadTriggerMethods) {
            this.lazyLoadTriggerMethods = lazyLoadTriggerMethods;
        }

        public Integer getDefaultStatementTimeout() {
            return defaultStatementTimeout;
        }

        public void setDefaultStatementTimeout(Integer defaultStatementTimeout) {
            this.defaultStatementTimeout = defaultStatementTimeout;
        }

        public Integer getDefaultFetchSize() {
            return defaultFetchSize;
        }

        public void setDefaultFetchSize(Integer defaultFetchSize) {
            this.defaultFetchSize = defaultFetchSize;
        }

        public ResultSetType getDefaultResultSetType() {
            return defaultResultSetType;
        }

        public void setDefaultResultSetType(ResultSetType defaultResultSetType) {
            this.defaultResultSetType = defaultResultSetType;
        }

        public ExecutorType getDefaultExecutorType() {
            return defaultExecutorType;
        }

        public void setDefaultExecutorType(ExecutorType defaultExecutorType) {
            this.defaultExecutorType = defaultExecutorType;
        }

        public AutoMappingBehavior getAutoMappingBehavior() {
            return autoMappingBehavior;
        }

        public void setAutoMappingBehavior(AutoMappingBehavior autoMappingBehavior) {
            this.autoMappingBehavior = autoMappingBehavior;
        }

        public AutoMappingUnknownColumnBehavior getAutoMappingUnknownColumnBehavior() {
            return autoMappingUnknownColumnBehavior;
        }

        public void setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior autoMappingUnknownColumnBehavior) {
            this.autoMappingUnknownColumnBehavior = autoMappingUnknownColumnBehavior;
        }

        public Properties getVariables() {
            return variables;
        }

        public void setVariables(Properties variables) {
            this.variables = variables;
        }

        public Boolean getLazyLoadingEnabled() {
            return lazyLoadingEnabled;
        }

        public void setLazyLoadingEnabled(Boolean lazyLoadingEnabled) {
            this.lazyLoadingEnabled = lazyLoadingEnabled;
        }

        public Class<?> getConfigurationFactory() {
            return configurationFactory;
        }

        public void setConfigurationFactory(Class<?> configurationFactory) {
            this.configurationFactory = configurationFactory;
        }

        public Class<? extends TypeHandler> getDefaultEnumTypeHandler() {
            return defaultEnumTypeHandler;
        }

        public void setDefaultEnumTypeHandler(Class<? extends TypeHandler> defaultEnumTypeHandler) {
            this.defaultEnumTypeHandler = defaultEnumTypeHandler;
        }

        public void applyTo(Configuration target) {
            PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
            mapper.from(getSafeRowBoundsEnabled()).to(target::setSafeRowBoundsEnabled);
            mapper.from(getSafeResultHandlerEnabled()).to(target::setSafeResultHandlerEnabled);
            mapper.from(getMapUnderscoreToCamelCase()).to(target::setMapUnderscoreToCamelCase);
            mapper.from(getAggressiveLazyLoading()).to(target::setAggressiveLazyLoading);
            mapper.from(getMultipleResultSetsEnabled()).to(target::setMultipleResultSetsEnabled);
            mapper.from(getUseGeneratedKeys()).to(target::setUseGeneratedKeys);
            mapper.from(getUseColumnLabel()).to(target::setUseColumnLabel);
            mapper.from(getCacheEnabled()).to(target::setCacheEnabled);
            mapper.from(getCallSettersOnNulls()).to(target::setCallSettersOnNulls);
            mapper.from(getUseActualParamName()).to(target::setUseActualParamName);
            mapper.from(getReturnInstanceForEmptyRow()).to(target::setReturnInstanceForEmptyRow);
            mapper.from(getShrinkWhitespacesInSql()).to(target::setShrinkWhitespacesInSql);
            mapper.from(getNullableOnForEach()).to(target::setNullableOnForEach);
            mapper.from(getArgNameBasedConstructorAutoMapping()).to(target::setArgNameBasedConstructorAutoMapping);
            mapper.from(getLazyLoadingEnabled()).to(target::setLazyLoadingEnabled);
            mapper.from(getLogPrefix()).to(target::setLogPrefix);
            mapper.from(getLazyLoadTriggerMethods()).to(target::setLazyLoadTriggerMethods);
            mapper.from(getDefaultStatementTimeout()).to(target::setDefaultStatementTimeout);
            mapper.from(getDefaultFetchSize()).to(target::setDefaultFetchSize);
            mapper.from(getLocalCacheScope()).to(target::setLocalCacheScope);
            mapper.from(getJdbcTypeForNull()).to(target::setJdbcTypeForNull);
            mapper.from(getDefaultResultSetType()).to(target::setDefaultResultSetType);
            mapper.from(getDefaultExecutorType()).to(target::setDefaultExecutorType);
            mapper.from(getAutoMappingBehavior()).to(target::setAutoMappingBehavior);
            mapper.from(getAutoMappingUnknownColumnBehavior()).to(target::setAutoMappingUnknownColumnBehavior);
            mapper.from(getVariables()).to(target::setVariables);
            mapper.from(getLogImpl()).to(target::setLogImpl);
            mapper.from(getVfsImpl()).to(target::setVfsImpl);
            mapper.from(getDefaultSqlProviderType()).to(target::setDefaultSqlProviderType);
            mapper.from(getConfigurationFactory()).to(target::setConfigurationFactory);
            mapper.from(getDefaultEnumTypeHandler()).to(target::setDefaultEnumTypeHandler);
        }
    }
}
