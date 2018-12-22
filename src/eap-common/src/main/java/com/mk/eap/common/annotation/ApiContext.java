package com.mk.eap.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER,ElementType.METHOD,ElementType.FIELD })
/**
 * 
 * 将token中的Id注入到参数中。注入多个属性用逗号分隔。 注入Long类型参数 示例：orgId 注入对象的多个属性
 * 示例：idInMyObj:userId,orgId:orgId
 * 
 * @author lisga
 *
 */
public @interface ApiContext {

	/**
	 * aa
	 * 
	 * @return
	 */
	String value() default "";

}
