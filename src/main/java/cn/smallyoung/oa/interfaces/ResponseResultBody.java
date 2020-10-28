package cn.smallyoung.oa.interfaces;

import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

/**
 * @author smallyoung
 */
@RestController
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ResponseResultBody {

}
