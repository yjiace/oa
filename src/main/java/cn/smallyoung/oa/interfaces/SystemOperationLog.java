package cn.smallyoung.oa.interfaces;

import cn.smallyoung.oa.base.BaseService;
import cn.smallyoung.oa.entity.SysOperationLogWayEnum;

import java.lang.annotation.*;

/**
 * 记录编辑详细信息的标注
 * @author smallyoung
 */

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SystemOperationLog {

    /**
     * 查询模块
     */
    String module() default "";

    /**
     * 查询模块名称
     */
    String methods() default "";

    /**
     * 查询的bean名称
     */
    Class<?> serviceClass() default BaseService.class;

    /**
     * 查询单个详情的bean的方法
     */
    String queryMethod() default "";

    /**
     * 查询详情的参数类型
     */
    String parameterType() default "";

    /**
     * 从页面参数中解析出要查询的id，
     * 如域名修改中要从参数中获取customerDomainId的值进行查询
     */
    String parameterKey() default "";

    /**
     * 操作方式，
     * RecordChanges：记录变化；
     * RecordOnly：仅记录；
     * RecordTheChange：记录变化后的值；
     * RecordTheBefore：记录变换之前的值；
     */
    SysOperationLogWayEnum way() default SysOperationLogWayEnum.UserAfter;
}
