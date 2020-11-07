package cn.smallyoung.oa.aspect;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONObject;
import cn.smallyoung.oa.entity.SysOperationLog;
import cn.smallyoung.oa.interfaces.DataName;
import cn.smallyoung.oa.interfaces.SystemOperationLog;
import cn.smallyoung.oa.service.SysOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author yangn
 */
@Slf4j
@Aspect
@Component
public class SysOperationLogAspect {

    private static final Map<String, Function<String, ?>> MAP_TO_FUNCTION = new HashMap<String, Function<String, ?>>() {{
        put("int", Integer::parseInt);
        put("Integer", Integer::parseInt);
        put("long", Long::parseLong);
        put("Long", Long::parseLong);
        put("String", Function.identity());
    }};

    private static final HashMap<String, Class<?>> CLASS_TYPE_MAP = new HashMap<String, Class<?>>() {
        {
            put("java.lang.Integer", int.class);
            put("java.lang.Double", double.class);
            put("java.lang.Float", float.class);
            put("java.lang.Long", long.class);
            put("java.lang.Short", short.class);
            put("java.lang.Boolean", boolean.class);
            put("java.lang.Char", char.class);
        }
    };

    @Resource
    private SysOperationLogService sysOperationLogService;
    @Resource
    private ApplicationContext applicationContext;


    @Around("@annotation(systemOperationLog)")
    public Object around(ProceedingJoinPoint pjp, SystemOperationLog systemOperationLog) throws Throwable {
        SysOperationLog sysOperationLog = new SysOperationLog();
        sysOperationLog.setStartTime(LocalDateTime.now());
        sysOperationLog.setMethod(systemOperationLog.methods());
        sysOperationLog.setModule(systemOperationLog.module());
        JSONObject params = getParam(pjp);
        sysOperationLog.setParams(params.toString());
        String value = params.getStr(systemOperationLog.parameterKey());
        Object oldObject = getOperateBeforeDataByParamType(systemOperationLog.serviceClass(),
                systemOperationLog.queryMethod(), value, MAP_TO_FUNCTION.get(systemOperationLog.parameterType()));
        if (oldObject != null) {
            sysOperationLog.setBeforeData(new JSONObject(oldObject).toString());
        }
        Object object;
        //执行service
        try {
            object = pjp.proceed();
        } catch (Exception e) {
            sysOperationLog.setResultMsg(e.getMessage());
            sysOperationLog.setResultStatus("ERROR");
            sysOperationLog.setEndTime(LocalDateTime.now());
            sysOperationLogService.save(sysOperationLog);
            return e;
        }
        Object newObject = getOperateBeforeDataByParamType(systemOperationLog.serviceClass(),
                systemOperationLog.queryMethod(), value, MAP_TO_FUNCTION.get(systemOperationLog.parameterType()));
        if (newObject != null) {
            sysOperationLog.setBeforeData(new JSONObject(newObject).toString());
        }
        sysOperationLog.setContent(updateContent(oldObject, newObject));
        sysOperationLogService.save(sysOperationLog);
        return object;
    }

    public JSONObject getParam(ProceedingJoinPoint pjp) throws NoSuchMethodException, ClassNotFoundException {
        String[] fieldsName = getFieldsName(pjp);
        if (fieldsName == null || fieldsName.length <= 0) {
            return null;
        }
        // 拦截的方法参数
        Object[] args = pjp.getArgs();
        JSONObject result = new JSONObject();
        for (int i = 0; i < args.length; i++) {
            result.set(fieldsName[i], args[i]);
        }
        return result;
    }

    /**
     * 功能描述: <br>
     * 〈功能详细描述〉
     *
     * @param serviceClass：bean名称
     * @param queryMethod：查询method
     * @param value：查询id的value
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    private Object getOperateBeforeDataByParamType(Class<?> serviceClass, String queryMethod, String value, Function<String, ?> function) {
        if (function == null) {
            return null;
        }
        Object obj = function.apply(value);
        Object object = applicationContext.getBean(serviceClass);
        Method mh = ReflectionUtils.findMethod(object.getClass(), queryMethod, obj.getClass());
        //用spring bean获取操作前的参数,此处需要注意：传入的id类型与bean里面的参数类型需要保持一致
        assert mh != null;
        return ReflectionUtils.invokeMethod(mh, object, obj);
    }

    /**
     * 返回方法的参数名
     */
    private static String[] getFieldsName(JoinPoint joinPoint) throws ClassNotFoundException, NoSuchMethodException {
        String classType = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        Class<?>[] classes = new Class[args.length];
        for (int k = 0; k < args.length; k++) {
            if (!args[k].getClass().isPrimitive()) {
                //获取的是封装类型而不是基础类型
                String result = args[k].getClass().getName();
                Class<?> s = CLASS_TYPE_MAP.get(result);
                classes[k] = s == null ? args[k].getClass() : s;
            }
        }
        ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();
        //获取指定的方法，第二个参数可以不传，但是为了防止有重载的现象，还是需要传入参数的类型
        Method method = Class.forName(classType).getMethod(methodName, classes);
        return pnd.getParameterNames(method);
    }

    /**
     * 内容变更说明，默认只记录被@DataName标记的字段
     */
    private String updateContent(Object oldObject, Object newObject) {
        Map<String, Object> oldMap = BeanUtil.beanToMap(oldObject);
        Map<String, Object> newMap = BeanUtil.beanToMap(newObject);
        StringBuilder stringBuilder = new StringBuilder();
        Object val;
        DataName dataName;
        for (Map.Entry<String, Object> entry : oldMap.entrySet()) {
            val = newMap.get(entry.getKey());
            if (!Objects.equals(val, entry.getValue())) {
                Field field = ReflectUtil.getField(newObject.getClass(), entry.getKey());
                dataName = AnnotationUtil.getAnnotation(field, DataName.class);
                //默认只有注释字段方可增加变更，避免敏感信息泄露
                if (dataName != null) {
                    stringBuilder.append("【").append(dataName.name()).append("】从【")
                            .append(entry.getValue()).append("】改为了【").append(val).append("】;\n");
                }
            }
        }
        return stringBuilder.toString();
    }
}