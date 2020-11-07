package cn.smallyoung.oa.interfaces;

import java.lang.annotation.*;

/**
 * @author yangn
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface DataName {

    /**
     * @return 字段名称
     */
    String name() default "";

}
