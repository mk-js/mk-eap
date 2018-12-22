package com.mk.eap.component.itf;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Title:       IInterfaceService.java
 * @Package:     com.mk.eap.component.itf
 * @Description: 测试服务接口
 * 
 * <p>
 * 	发现服务接口
 * </p> 
 * 
 * @author zl
 * @version 1.0
 * 
 */
@RequestMapping("/")
public interface ITestService {
	
	/**
	 * 测试
	 * @return 
	 * @api {POST} test test
	 * @apiName test
	 * @apiGroup app
	 * @apiVersion 1.0.0
	 * @apiDescription 更新应用信息
	 * @apiPermission anyone
	 *
	 * @apiParam {Object} json json
	 * @apiParamExample {json} 请求示例
	 *                  { 
	 *                  	
	 *                  }
	 * @apiSuccessExample {json} 返回结果
	 *                    {
	 *                    		"result":true,
	 *                    		"value":true
	 *                    }
	 *
	 */
	@RequestMapping("/test")
	public Map<String, Object> test() throws UnsupportedEncodingException, MessagingException; 
}
