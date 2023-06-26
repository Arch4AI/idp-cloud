package com.dataease.cloud.interceptor;

import com.dataease.cloud.utils.BeanUtils;
import com.dataease.cloud.utils.EncryptUtils;
import lombok.Data;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MyBatis数据库字段加密
 *          MybatisInterceptor interceptor = new MybatisInterceptor();
 *         List<InterceptorConfig> InterceptorConfigList = new ArrayList<>();
 *         InterceptorConfigList.add(new InterceptorConfig(ServerAccount.class, "password"));
 *         InterceptorConfigList.add(new InterceptorConfig(MailInterceptorConfig.class, "password"));
 *         InterceptorConfigList.add(new InterceptorConfig(ServerAccountDb.class, "dbPassword"));
 *         interceptor.setInterceptorInterceptorConfigList(InterceptorConfigList);
 *         return interceptor;
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/6/26 1:41 PM
 **/
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
public class MybatisInterceptor implements Interceptor {
    private List<InterceptorConfig> interceptorInterceptorConfigList;

    private ConcurrentHashMap<String, Class> classMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Map<String, Map<String, InterceptorConfig>>> interceptorInterceptorConfigMap = new ConcurrentHashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        String methodName = invocation.getMethod().getName();
        Object parameter = invocation.getArgs()[1];
        if (parameter != null && methodName.equals("update")) {
            invocation.getArgs()[1] = process(parameter);
        }
        Object returnValue = invocation.proceed();
        Object result = returnValue;
        if (returnValue instanceof ArrayList<?>) {
            List<Object> list = new ArrayList<>();
            boolean isDecrypted = false;
            for (Object val : (ArrayList<?>) returnValue) {
                Object a = undo(val);
                if (a != val) {
                    isDecrypted = true;
                    list.add(a);
                } else {
                    break;
                }
            }
            if (isDecrypted) {
                result = list;
            }
        } else {
            result = undo(returnValue);
        }
        return result;
    }

    private Map<String, Map<String, InterceptorConfig>> getInterceptorConfig(Object p) {
        Map<String, Map<String, InterceptorConfig>> result = new HashMap<>();
        if (p == null) {
            return null;
        }
        String pClassName = p.getClass().getName();
        if (interceptorInterceptorConfigMap.get(pClassName) != null) {
            return interceptorInterceptorConfigMap.get(pClassName);
        }
        Map<String, List<InterceptorConfig>> m = new HashMap<>();
        for (InterceptorConfig interceptorInterceptorConfig : interceptorInterceptorConfigList) {
            String className = interceptorInterceptorConfig.getModelName();
            String attrName = interceptorInterceptorConfig.getAttrName();
            if (StringUtils.hasText(className)) {
                Class c = classMap.get(className);
                if (c == null) {
                    try {
                        c = Class.forName(className);
                        classMap.put(className, c);
                    } catch (ClassNotFoundException e) {
                        continue;
                    }
                }
                if (c.isInstance(p)) {
                    if (result.get(attrName) == null) {
                        result.put(attrName, new HashMap<>());
                    }
                    if (StringUtils.hasText(interceptorInterceptorConfig.getInterceptorMethod())) {
                        result.get(attrName).put(Methods.encrypt.name(), interceptorInterceptorConfig);
                    }
                    if (StringUtils.hasText(interceptorInterceptorConfig.getInterceptorMethod())) {
                        result.get(attrName).put(Methods.decrypt.name(), interceptorInterceptorConfig);
                    }
                }
            }
        }
        interceptorInterceptorConfigMap.put(pClassName, result);
        return result;
    }

    private Object process(Object obj) throws Throwable {
        if (obj instanceof Map) {
            Map paramMap = (Map) obj;
            for (Object key : paramMap.keySet()) {
                if (paramMap.get(key) != null) {
                    paramMap.put(key, process(paramMap.get(key)));
                }
            }
            return paramMap;
        }
        Map<String, Map<String, InterceptorConfig>> localInterceptorInterceptorConfigMap = getInterceptorConfig(obj);
        if (localInterceptorInterceptorConfigMap == null || localInterceptorInterceptorConfigMap.isEmpty()) {
            return obj;
        }
        Object newObject = obj.getClass().newInstance();
        BeanUtils.copyBean(newObject, obj);
        for (String attrName : localInterceptorInterceptorConfigMap.keySet()) {
            Map attrNameMap = localInterceptorInterceptorConfigMap.get(attrName);
            if (attrNameMap == null || attrNameMap.isEmpty()) {
                continue;
            }
            InterceptorConfig interceptorInterceptorConfig = localInterceptorInterceptorConfigMap.get(attrName).get(Methods.encrypt.name());
            if (interceptorInterceptorConfig == null || !StringUtils.hasText(interceptorInterceptorConfig.getInterceptorClass())
                    || !StringUtils.hasText(interceptorInterceptorConfig.getInterceptorMethod())) {
                continue;
            }
            Object fieldValue = BeanUtils.getFieldValueByName(interceptorInterceptorConfig.getAttrName(), newObject);
            if (fieldValue != null) {
                Class<?> processClazz = Class.forName(interceptorInterceptorConfig.getInterceptorClass());
                Method method = processClazz.getMethod(interceptorInterceptorConfig.getInterceptorMethod(), Object.class);
                Object processedValue = method.invoke(null, fieldValue);
                if (processedValue instanceof byte[]) {
                    BeanUtils.setFieldValueByName(newObject, interceptorInterceptorConfig.getAttrName(), processedValue, byte[].class);
                } else {
                    BeanUtils.setFieldValueByName(newObject, interceptorInterceptorConfig.getAttrName(), processedValue, fieldValue.getClass());
                }
            }
        }

        return newObject;
    }

    private Object undo(Object obj) throws Throwable {
        Map<String, Map<String, InterceptorConfig>> localDecryptInterceptorConfigMap = getInterceptorConfig(obj);
        Object result;
        if (localDecryptInterceptorConfigMap == null || localDecryptInterceptorConfigMap.isEmpty()) {
            return obj;
        }
        result = obj.getClass().newInstance();
        BeanUtils.copyBean(result, obj);
        for (String attrName : localDecryptInterceptorConfigMap.keySet()) {
            Map attrMap = localDecryptInterceptorConfigMap.get(attrName);
            if (attrMap == null || attrMap.isEmpty()) {
                continue;
            }
            InterceptorConfig interceptorInterceptorConfig = localDecryptInterceptorConfigMap.get(attrName).get(Methods.decrypt.name());
            if (interceptorInterceptorConfig == null || !StringUtils.hasText(interceptorInterceptorConfig.getUndoClass())
                    || !StringUtils.hasText(interceptorInterceptorConfig.getUndoMethod())) {
                continue;
            }
            Object fieldValue = BeanUtils.getFieldValueByName(interceptorInterceptorConfig.getAttrName(), result);
            if (fieldValue != null) {
                Class<?> processClazz = Class.forName(interceptorInterceptorConfig.getUndoClass());
                Object undoValue;
                if (fieldValue instanceof List) {
                    Method method = processClazz.getMethod(interceptorInterceptorConfig.getUndoMethod(), List.class, String.class);
                    //fieldValue获取的是list的引用，所以list类型的属性不需要再调用setFieldValueByName了
                    method.invoke(null, fieldValue, interceptorInterceptorConfig.getAttrNameForList());
                } else {
                    Method method = processClazz.getMethod(interceptorInterceptorConfig.getUndoMethod(), Object.class);
                    undoValue = method.invoke(null, fieldValue);
                    if (undoValue instanceof byte[]) {
                        BeanUtils.setFieldValueByName(result, interceptorInterceptorConfig.getAttrName(), undoValue, byte[].class);
                    } else {
                        BeanUtils.setFieldValueByName(result, interceptorInterceptorConfig.getAttrName(), undoValue, fieldValue.getClass());
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // TODO Auto-generated method stub

    }

    public List<InterceptorConfig> getInterceptorInterceptorConfigList() {
        return interceptorInterceptorConfigList;
    }

    public void setInterceptorInterceptorConfigList(List<InterceptorConfig> interceptorInterceptorConfigList) {
        this.interceptorInterceptorConfigList = interceptorInterceptorConfigList;
    }

    private enum Methods {
        encrypt, decrypt
    }

    /**
     * 当前拦截器的配置项
     * @author ShrChang.Liu
     * @version v1.0
     * @date 2023/6/26 2:00 PM
    **/
    @Data
    public class InterceptorConfig{
        private String modelName;
        private String attrName;
        private String attrNameForList;
        private String interceptorClass;
        private String interceptorMethod;
        private String undoClass;
        private String undoMethod;

        public InterceptorConfig(){

        }

        /**
         * 用时需谨慎！！！！！
         * 主要配置多个的时候，参数少一点
         *
         * @param modelClass
         * @param attrName
         */
        public InterceptorConfig(Class<?> modelClass, String attrName) {
            this.modelName = modelClass.getName();
            this.attrName = attrName;
            this.interceptorClass = EncryptUtils.class.getName();
            this.interceptorMethod = "aesEncrypt";
            this.undoClass = EncryptUtils.class.getName();
            this.undoMethod = "aesDecrypt";
        }

        public InterceptorConfig(Class<?> modelClass, String attrName, Class<?> interceptorClass, String interceptorMethod, String undoMethod) {
            this.modelName = modelClass.getName();
            this.attrName = attrName;
            this.interceptorClass = interceptorClass.getName();
            this.interceptorMethod = interceptorMethod;
            this.undoClass = interceptorClass.getName();
            this.undoMethod = undoMethod;
        }
    }
}
