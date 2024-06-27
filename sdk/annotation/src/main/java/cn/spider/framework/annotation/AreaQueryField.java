package cn.spider.framework.annotation;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AreaQueryField {
    String areaName() default StringUtils.EMPTY;

    String fieldName() default StringUtils.EMPTY;
}
