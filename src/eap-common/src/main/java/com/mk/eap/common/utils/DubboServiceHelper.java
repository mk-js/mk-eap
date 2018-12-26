package com.mk.eap.common.utils;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;

public class DubboServiceHelper {
	
	private static String zkServer = null;
	
	private static String getZkServer(){
		if(null == zkServer){
			zkServer = PropertyUtil.getPropertyByKey("dubbo.registry.address", "dubbo.properties");
			if(zkServer.indexOf("//") != -1){
				zkServer = zkServer.substring(zkServer.indexOf("//") + 2 );
			}
		}
		return zkServer;
	}
	 
	
	 @SuppressWarnings("rawtypes")
	public static Object getService(String itfName) {
	        ApplicationConfig application = new ApplicationConfig();
	        application.setName("dubboConsumer");
	        
	        RegistryConfig registry = new RegistryConfig();
	        registry.setAddress(getZkServer());
	        registry.setProtocol("zookeeper");
	 
	        ReferenceConfig referenceConfig = new ReferenceConfig();
	        referenceConfig.setApplication(application);
	        referenceConfig.setRegistry(registry);
	        referenceConfig.setGroup("");
	        referenceConfig.setInterface(itfName); 
	        ReferenceConfigCache cache = ReferenceConfigCache.getCache();
	        Object obj = cache.get(referenceConfig);
	        
	        return obj;
	    }


}
