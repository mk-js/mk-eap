package com.mk.eap.common.itf;

import java.util.ArrayList; 

import org.springframework.web.bind.annotation.RequestMapping;

import com.mk.eap.common.annotation.ApiContext;
import com.mk.eap.common.domain.ClassDto;
import com.mk.eap.common.domain.Context;

/**
 * @Title:       IInterfaceService.java
 * @Package:     com.mk.eap.component.itf
 * @Description: 发现服务接口
 * 
 * <p>
 * 	发现服务接口
 * </p> 
 * 
 * @author zl
 * @version 1.0
 * 
 */
public interface IInterfaceService {
	
	/**
	 * 接口类型序列化
	 * @param className
	 * @return
	 */
	@RequestMapping("/interfaceSerializer")
	public ArrayList<ClassDto> interfaceSerializer(String[] interfaceNames); 
	

	@RequestMapping("/notFound")
	public Object notFound(@ApiContext("*")Context context, String payload); 
	
}
