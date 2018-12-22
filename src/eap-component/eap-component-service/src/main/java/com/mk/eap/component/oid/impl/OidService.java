package com.mk.eap.component.oid.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.dubbo.config.annotation.Service;
import com.mk.eap.component.oid.itf.IOidService;



@Component
@Service
public class OidService implements IOidService {

	@Autowired
	private ZKSnowflakeIDGenerator idGenerator;

	//@Override
	//public Long generateObjectID() {
	//   return Long.parseLong(IDGenerator.generateObjectID(""));
	//}

	@Override
	public Long generateObjectID() {
		return idGenerator.getId();

	}


}
