package com.mk.eap.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * "*" 发布全部方法<br>
 * "method1"		发布一个api<br>
 * "mthod1;method2" 发布多个api<br>
 * "mthod1:/m1;method2"		指定api路径<br>
 *
 * @author lisga  
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD }) 
public @interface ApiMapping {

	String value() default "";
}