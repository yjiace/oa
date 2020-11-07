package cn.smallyoung.oa.aspect;

import cn.hutool.json.JSONObject;
import cn.smallyoung.oa.entity.SysOperationLog;
import cn.smallyoung.oa.interfaces.SystemOperationLog;
import cn.smallyoung.oa.service.SysOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author yangn
 */
@Slf4j
@Aspect
@Component
public class SysOperationLogAspect {

    private static final Map<String, Function<String, ?>> MAP_TO_FUNCTION = new HashMap<String, Function<String, ?>>(){{
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

    /**
     * 配置接入点,指定controller的类进行切面
     */
    @Pointcut("execution(* cn.smallyoung.oa.controller.*.*(..))")
    private void controllerAspect() {

    }

    @Around("controllerAspect()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        //常见日志实体对象
        SysOperationLog sysOperationLog = new SysOperationLog();
        sysOperationLog.setStartTime(LocalDateTime.now());
        try {
            log.error("进入切面日志方法");
            // 拦截的放参数类型
            Signature sig = pjp.getSignature();
            MethodSignature methodSignature;
            if (!(sig instanceof MethodSignature)) {
                throw new IllegalArgumentException("该注解只能用于方法");
            }
            // 拦截的实体类，就是当前正在执行的controller
            Object target = pjp.getTarget();
            // 拦截的方法名称。当前正在执行的方法
            String methodName = pjp.getSignature().getName();
            JSONObject params = getParam(pjp);
            //设置请求参数
            sysOperationLog.setOperateParams(params.toString());
            methodSignature = (MethodSignature) sig;
            Class<?>[] parameterTypes = methodSignature.getMethod().getParameterTypes();
            // 获得被拦截的方法
            Method method = null;
            try {
                method = target.getClass().getMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException | SecurityException e1) {
                log.error("ControllerLogAopAspect around error", e1);
            }
            if(null == method){
                return pjp.proceed();
            }
            if (!method.isAnnotationPresent(SystemOperationLog.class)) {
                return pjp.proceed();
            }
            SystemOperationLog systemOperationLog = method.getAnnotation(SystemOperationLog.class);
            sysOperationLog.setModule(systemOperationLog.module());
            sysOperationLog.setMethod(systemOperationLog.methods());
            before(systemOperationLog, sysOperationLog, params, systemOperationLog.serviceClass(), systemOperationLog.queryMethod());
            sysOperationLog.setResultStatus("SUCCESS");
        }catch (Throwable throwable){
            sysOperationLog.setResultMsg(throwable.getMessage());
            sysOperationLog.setResultStatus("ERROR");
        }
        //执行页面请求模块方法，并返回
        sysOperationLog.setEndTime(LocalDateTime.now());
        sysOperationLogService.save(sysOperationLog);
        return pjp.proceed();
    }

    public JSONObject getParam(ProceedingJoinPoint pjp) throws NoSuchMethodException, ClassNotFoundException {
        String[] fieldsName = getFieldsName(pjp);
        if(fieldsName == null || fieldsName.length <= 0){
            return null;
        }
        // 拦截的方法参数
        Object[] args = pjp.getArgs();
        JSONObject result = new JSONObject();
        for (int i = 0; i < args.length; i++) {
            result.set(fieldsName[i] , args[i]);
        }
        return result;
    }

    public void before(SystemOperationLog systemOperationLog, SysOperationLog sysOperationLog,
                       JSONObject operateParamArray, String serviceClass, String queryMethod){
        //判断是否需要进行操作前的对象参数查询
        if(StringUtils.isBlank(systemOperationLog.parameterKey())
                || StringUtils.isBlank(systemOperationLog.parameterType())
                || StringUtils.isBlank(systemOperationLog.queryMethod())
                || StringUtils.isBlank(systemOperationLog.serviceClass())){
            return;
        }
        //参数类型
        String paramType = systemOperationLog.parameterType();
        String key = systemOperationLog.parameterKey();
        String value = operateParamArray.getStr(key);
        //在此处获取操作前的spring bean的查询方法
        Object data = getOperateBeforeDataByParamType(serviceClass, queryMethod, value, MAP_TO_FUNCTION.get(paramType));
        sysOperationLog.setBeforeParams(new JSONObject(data).toString());
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
    private Object getOperateBeforeDataByParamType(String serviceClass, String queryMethod, String value, Function<String,?> function){
        if(function == null){
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
     * @param joinPoint
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
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
}