package cn.spider.framework.annotation;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注在字段上面
 *
 * 用来指定查询域对象
 *
 * @author DDS
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AreaQueryField {
    String areaName() default StringUtils.EMPTY;

    String fieldName() default StringUtils.EMPTY;
}
