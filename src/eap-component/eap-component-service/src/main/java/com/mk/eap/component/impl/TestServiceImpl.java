package com.mk.eap.component.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.mk.eap.component.cache.itf.ICacheService;
import com.mk.eap.component.file.itf.IFileService;
import com.mk.eap.component.itf.ITestService;
import com.mk.eap.component.oid.itf.IOidService;
import com.mk.eap.component.send.itf.ISendMailService;
import com.mk.eap.component.send.itf.ISendSMSService;

/**
 * @Title: InterfceServiceImpl.java
 * @Package: com.mk.eap.component.impl
 * @Description: 测试服务实现类
 * @version 1.0
 * 
 *               <p>
 *               发现服务实现类
 *               </p>
 * 
 * @author zl
 * 
 */
@Component
@Service
public class TestServiceImpl implements ITestService {

	@Autowired
	private IOidService oidService;
	
	@Autowired
	private ICacheService cacheService;
	
	@Autowired
	private ISendMailService mailService;
	
	@Autowired
	private ISendSMSService smsService;
	
	@Autowired
	private IFileService fileService;
	
	@Override
	public Map<String, Object> test() throws UnsupportedEncodingException, MessagingException {
		Map<String, Object> map = new HashMap<>();
		map.put("id", oidService.generateObjectID());
		//mailService.send("zl@rrtimes.com", "测试", "组件测试", true);
		//smsService.send("13501128603", null, messageCode, paramMap);
		cacheService.addString("test", "123", 60L);
		map.put("cache", cacheService.getString("test"));
		return map;
	}
}
