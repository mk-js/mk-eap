package com.mk.eap.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * @Title:       PropertyUtil.java
 * @Package:     com.mk.rap.utils
 * @Description: Property公用类
 * @author yxq
 */
public class PropertyUtil {

	public static final Logger logger = LoggerFactory.getLogger(PropertyUtil.class);
    private static Map<String,PropertyUtil> propMap = new HashMap<String,PropertyUtil>();
    
    private static String DefaultPropFileName = "rap-comp.properties";
    private static PropertyUtil Default = new PropertyUtil(DefaultPropFileName);

    public static String getPropertyByKey(String key) {
        return Default.getValue(key);
    } 
    public static String getPropertyByKey(String key,String file) {
    	PropertyUtil propUtil = propMap.get(file);
    	if(propUtil==null){
    		propUtil = new PropertyUtil(file);  
    	}
        return propUtil.getValue(key);
    } 
    
	
    private Properties prop = null;
    private PropertyUtil(String fileName){
    	if(fileName == null) { 
			logger.error("配置文件名为null!");
    		return;
    	}
    	fileName = fileName.replace("..", "_");
		try {
			org.springframework.core.io.Resource fileResource = new ClassPathResource("conf/"+fileName);
			if(!fileResource.exists()){
				fileResource = new ClassPathResource("config/"+fileName); 
				if(!fileResource.exists()){
					logger.error("配置文件不存在：config/"+fileName);
					return;
				}
			}
			InputStream in = fileResource.getInputStream();
	    	this.prop = new Properties();
	    	this.prop.load(in);
			propMap.put(fileName, this);
		} catch (IOException e) {
			logger.error("IO异常:", e);
		}
    }
    public String getValue(String key){
    	if(prop==null)return null;
        String value = prop.getProperty(key);
        if(null != value && value.indexOf("${")==0){ 
        	int index = value.indexOf("${");
        	String name = value.substring(index + 2, value.lastIndexOf("}")); 
            value = java.lang.System.getenv(name);
            //System.out.println(name+":"+value);
        }
        return StringUtil.isEmtryStr(value) ? "" : value; 
    }

}
