package cn.smallyoung.oa.aspect;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.smallyoung.oa.entity.SysOperationLog;
import cn.smallyoung.oa.entity.SysOperationLogWayEnum;
import cn.smallyoung.oa.interfaces.DataName;
import cn.smallyoung.oa.interfaces.SystemOperationLog;
import cn.smallyoung.oa.service.SysOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author smallyoung
 */
@Slf4j
@Aspect
@Component
public class SysOperationLogAspect {

    private static final Map<String, Function<String, ?>> MAP_TO_FUNCTION = new HashMap<String, Function<String, ?>>() {

        private static final long serialVersionUID = 4864337579178718079L;

        {
        put("int", Integer::parseInt);
        put("Integer", Integer::parseInt);
        put("long", Long::parseLong);
        put("Long", Long::parseLong);
        put("String", Function.identity());
    }};

    @Resource
    private SysOperationLogService sysOperationLogService;
    @Resource
    private ApplicationContext applicationContext;


    @Around("@annotation(systemOperationLog)")
    public Object around(ProceedingJoinPoint pjp, SystemOperationLog systemOperationLog) throws Throwable {
        SysOperationLog sysOperationLog = new SysOperationLog();
        Signature signature = pjp.getSignature();
        if(signature != null){
            sysOperationLog.setPackageAndMethod(signature.getDeclaringTypeName() + "." + signature.getName());
        }
        sysOperationLog.setStartTime(LocalDateTime.now());
        sysOperationLog.setMethod(systemOperationLog.methods());
        sysOperationLog.setModule(systemOperationLog.module());
        JSONObject params = getParam(pjp);
        sysOperationLog.setParams(params.toString());
        String value = getValue(systemOperationLog.parameterKey(), params);
        Map<String, Object> oldMap = null;
        if(SysOperationLogWayEnum.RecordBeforeAndAfterChanges == systemOperationLog.way() || SysOperationLogWayEnum.RecordTheBefore == systemOperationLog.way()){
            Object oldObject = getOperateBeforeDataByParamType(systemOperationLog.serviceClass(),
                    systemOperationLog.queryMethod(), value, MAP_TO_FUNCTION.get(systemOperationLog.parameterType()));
            oldMap = BeanUtil.beanToMap(oldObject);
            if (oldObject != null) {
                sysOperationLog.setBeforeData(new JSONObject(oldObject).toString());
            }
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
            throw e;
        }
        if(SysOperationLogWayEnum.RecordBeforeAndAfterChanges == systemOperationLog.way() || SysOperationLogWayEnum.RecordTheAfter == systemOperationLog.way()){
            Object newObject = getOperateBeforeDataByParamType(systemOperationLog.serviceClass(),
                    systemOperationLog.queryMethod(), value, MAP_TO_FUNCTION.get(systemOperationLog.parameterType()));
            if (newObject != null) {
                sysOperationLog.setAfterData(new JSONObject(newObject).toString());
            }
            sysOperationLog.setContent(updateContent(oldMap, newObject));
        }
        sysOperationLog.setEndTime(LocalDateTime.now());
        sysOperationLog.setResultStatus("SUCCESS");
        sysOperationLogService.save(sysOperationLog);
        return object;
    }

    public JSONObject getParam(ProceedingJoinPoint pjp) {
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        String[] fieldsName = methodSignature.getParameterNames();
        if (fieldsName == null || fieldsName.length <= 0) {
            return null;
        }
        // 拦截的方法参数
        Object[] args = pjp.getArgs();
        JSONObject result = new JSONObject();
        Object obj;
        for (int i = 0; i < args.length; i++) {
            obj = args[i];
            if(ClassUtil.isSimpleValueType(obj.getClass())){
                result.set(fieldsName[i], args[i]);
            }else{
                result.set(fieldsName[i], new JSONObject(obj));
            }
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
        if (function == null || StrUtil.hasBlank(value, queryMethod) || serviceClass == null) {
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
     * 内容变更说明，默认只记录被@DataName标记的字段
     */
    private String updateContent(Map<String, Object> oldMap, Object newObject) {
        JSONObject result = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;

        Map<String, Object> newMap = BeanUtil.beanToMap(newObject);
        Object val;
        Field field;
        DataName dataName;
        StringBuilder stringBuilder = new StringBuilder();
        Id id;
        for (Map.Entry<String, Object> entry : newMap.entrySet()) {
            //查询操作ID
            field = ReflectUtil.getField(newObject.getClass(), entry.getKey());
            dataName = AnnotationUtil.getAnnotation(field, DataName.class);
            id = AnnotationUtil.getAnnotation(field, Id.class);
            if(id != null){
                stringBuilder.append(entry.getValue()).append(";");
            }

            val = oldMap != null ? oldMap.get(entry.getKey()) : null;
            if (Objects.equals(val, entry.getValue())) {
                continue;
            }
            jsonObject = new JSONObject();
            //默认只有注释字段方可增加变更，避免敏感信息泄露。
            if (dataName != null ) {
                jsonObject.set("name", dataName.name());
                jsonObject.set("oldVal", val);
                jsonObject.set("newVal", entry.getValue());
                jsonArray.set(jsonObject);
            }
        }
        result.set("data", jsonArray);
        result.set("id", stringBuilder.toString());
        //新增
        if(oldMap == null || oldMap.size() == 0){
            result.set("operation", "新增");
        }else{
            result.set("operation", "编辑");
        }
        return result.toString();
    }

    private String getValue(String key, JSONObject params){
        if(StrUtil.isBlank(key) || params == null){
            return null;
        }
        CharSequence s = ".";
        String regex = "\\.";
        if(!key.contains(s)){
            return params.getStr(key);
        }
        String [] keys = key.split(regex, 0);
        return getValue(keys[1], params.getJSONObject(keys[0]));
    }
}