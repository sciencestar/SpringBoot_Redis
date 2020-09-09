package com.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    /** key前缀 */
    String prefix() default "";

    /** 过期秒数,默认为5秒 */
    int expire() default 5;

    /** 超时时间单位，默认为秒 */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /** Key的分隔符（默认 :）  */
    String delimiter() default ":";

}
