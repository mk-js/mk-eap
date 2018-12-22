package com.mk.eap.entity.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.mk.eap.common.domain.DTO;
import com.mk.eap.common.domain.Token;
import com.mk.eap.common.domain.VO;
import com.mk.eap.common.utils.JarResourceUtil;
import com.mk.eap.entity.dao.EntityMapper;
import com.mk.eap.entity.dto.InjectorConfig;
import com.mk.eap.entity.itf.IEntityInjector;

public class EntityJsInjector<P extends DTO, V extends VO, M extends EntityMapper<V>> implements IEntityInjector<P> {
	
	private M mapper; 
	private ScriptEngine engine;
	private Invocable invocable;
	private String jsPath;
	private boolean existsInjector = false;
	
	public boolean exists(){
		return existsInjector;
	}
	
	public EntityJsInjector(String entityName, M mapper) {
		this.mapper = mapper;
		String fileName = entityName.substring(entityName.lastIndexOf('.') + 1);
		String filePath = JarResourceUtil.JarUtil.getResourseFolder();
		String jsPath = filePath + String.join(File.separator, "..", "js", "entityJsInjector", fileName + ".js");
		
		this.jsPath = jsPath;
		File file = new File(jsPath);   
		if(file.exists()){
			this.existsInjector = true;
		}else{
			this.existsInjector = false;
			return;
		}

		ScriptEngineManager manager = new ScriptEngineManager();
		this.engine = manager.getEngineByName("JavaScript");
		try {
			engine.eval(new FileReader(jsPath));
			invocable = (Invocable) engine;
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		} catch (ScriptException e) { 
			e.printStackTrace();
		}
	}
	
	private void runJsHandler(HashMap<String,Object> map) {

		try {
			engine.eval(new FileReader(jsPath));
			invocable = (Invocable) engine;
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		} catch (ScriptException e) { 
			e.printStackTrace();
		}
		
		if(invocable != null) {
			try { 
				map.put("mapper", mapper);
				invocable.invokeFunction("handler", map);
			} catch (NoSuchMethodException e) { 
				e.printStackTrace();
			} catch (ScriptException e) { 
				e.printStackTrace();
			}
		} 
	}

	@Override
	public void afterCreate(P createDto) { 
		HashMap<String,Object> map = new HashMap<>();
		map.put("event", "afterCreate");
		map.put("createDto", createDto);  
		runJsHandler(map);
	}

	@Override
	public void afterCreateBatch(List<P> createDtos, Token token) {
		HashMap<String,Object> map = new HashMap<>();
		map.put("event", "afterCreateBatch");
		map.put("createDtos", createDtos);
		map.put("token", token);
		runJsHandler(map);  
	}

	@Override
	public void afterDelete(P deleteDto) {
		HashMap<String,Object> map = new HashMap<>();
		map.put("event", "afterDelete");
		map.put("deleteDto", deleteDto); 
		runJsHandler(map);    
	}

	@Override
	public void afterDeleteBatch(List<P> deleteDtos, Token token) {
		HashMap<String,Object> map = new HashMap<>();
		map.put("event", "afterDeleteBatch");
		map.put("deleteDtos", deleteDtos); 
		map.put("token", token);
		runJsHandler(map);    
		
	}

	@Override
	public void afterQuery(List<P> resultList, InjectorConfig cfg) {
		HashMap<String,Object> map = new HashMap<>();
		map.put("event", "afterQuery");
		map.put("resultList", resultList); 
		map.put("cfg", cfg);
		runJsHandler(map);  
	}

	@Override
	public void afterQueryByPrimaryKey(P queryDto, InjectorConfig cfg) {
		HashMap<String,Object> map = new HashMap<>();
		map.put("event", "afterQueryByPrimaryKey");
		map.put("queryDto", queryDto); 
		map.put("cfg", cfg);
		runJsHandler(map);    
	}

	@Override
	public void afterUpdate(P updateDto) {
		HashMap<String,Object> map = new HashMap<>();
		map.put("event", "afterUpdate");
		map.put("updateDto", updateDto); 
		runJsHandler(map);    
	}

	@Override
	public void afterUpdateBatch(List<P> updateDto, Token token) {
		HashMap<String,Object> map = new HashMap<>();
		map.put("event", "afterUpdateBatch");
		map.put("updateDto", updateDto); 
		map.put("token", token);
		runJsHandler(map);     
	}

	@Override
	public void beforeCreate(P createDto) {
		HashMap<String,Object> map = new HashMap<>();
		map.put("event", "beforeCreate");
		map.put("createDto", createDto); 
		runJsHandler(map);     
	}

	@Override
	public void beforeCreateBatch(List<P> createDtos, Token token) {
		HashMap<String,Object> map = new HashMap<>();
		map.put("event", "beforeCreateBatch");
		map.put("createDtos", createDtos); 
		map.put("token", token);
		runJsHandler(map);    
	}

	@Override
	public void beforeDelete(P deleteDto) {
		HashMap<String,Object> map = new HashMap<>();
		map.put("event", "beforeDelete");
		map.put("deleteDto", deleteDto); 
		runJsHandler(map);   
	}

	@Override
	public void beforeDeleteBatch(List<P> deleteDto, Token token) {
		HashMap<String,Object> map = new HashMap<>();
		map.put("event", "beforeDeleteBatch");
		map.put("deleteDto", deleteDto); 
		map.put("token", token);
		runJsHandler(map);    
		
		
	}

	@Override
	public void beforeUpdate(P updateDto) {
		HashMap<String,Object> map = new HashMap<>();
		map.put("event", "beforeUpdate");
		map.put("updateDto", updateDto); 
		runJsHandler(map);    
		
	}

	@Override
	public void beforeUpdateBatch(List<P> updateDto, Token token) {
		HashMap<String,Object> map = new HashMap<>();
		map.put("event", "beforeUpdateBatch");
		map.put("updateDto", updateDto); 
		map.put("token", token);
		runJsHandler(map);    
		
	}

	@Override
	public void beforeQueryByPrimaryKey(P queryDto) {
		HashMap<String,Object> map = new HashMap<>();
		map.put("event", "beforeQueryByPrimaryKey");
		map.put("queryDto", queryDto); 
		runJsHandler(map);    
	} 

}
