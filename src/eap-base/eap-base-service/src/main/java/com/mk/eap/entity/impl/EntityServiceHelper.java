package com.mk.eap.entity.impl;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;

import com.mk.eap.common.domain.DTO;
import com.mk.eap.common.domain.VO;
import com.mk.eap.entity.dao.EntityMapper;
import com.mk.eap.entity.itf.IEntityService;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.annotation.Annotation;

public class EntityServiceHelper {
	@SuppressWarnings("rawtypes")
	public static IEntityService getService(String tableName,Type vo){
		
		try {
			CtClass dtoClz = getDtoClass(tableName, vo);
			
			CtClass voClz = getVoClass(tableName, vo);
			
			CtClass mapperClz = getMapperClass(tableName, voClz);
			
			IEntityService serviceImpl = getEntityServiceClass(tableName, dtoClz, voClz, mapperClz);
	 
			return serviceImpl;
			
		} catch (CannotCompileException e) { 
			e.printStackTrace();
		} catch (NotFoundException e) { 
			e.printStackTrace(); 
		} catch (ClassNotFoundException e) { 
			e.printStackTrace();
		} catch (InstantiationException e) { 
			e.printStackTrace();
		} catch (IllegalAccessException e) { 
			e.printStackTrace();
		} 
		
		return null;
	}
	private static HashMap<String,Class<?>> clzMap = new HashMap<>();
	
	private static IEntityService getEntityServiceClass(String tableName, CtClass dtoClz, CtClass voClz,
			CtClass mapperClz) throws CannotCompileException, NotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException { 
		
		String className = "com.mk.eap.entity.dym." + tableName + "ServiceImpl";
		Class<?> clz = clzMap.get(className);
		if(null != clz){
			return (IEntityService)clz.newInstance();
		}
        ClassPool pool = ClassPool.getDefault();
        CtClass subClass = null;
        try{
        	subClass = pool.get(className); 
        }catch(NotFoundException ex){
        	
        }
        if(subClass == null){
            subClass = pool.makeClass( className);  
            subClass.setSuperclass(pool.get(EntityServiceImpl.class.getName()));  
            
    		String genTypeName = "com.mk.eap.entity.impl.EntityServiceImpl<" + dtoClz.getName() + "," + voClz.getName() + "," + mapperClz.getName() + ">";
            subClass.setGenericSignature(new SignatureAttribute.TypeVariable(genTypeName).encode());

//            CtClass[] params = new CtClass[]{ };
//            CtConstructor ctor = CtNewConstructor.make( params, null, CtNewConstructor.PASS_PARAMS, null, null, subClass );
//            subClass.addConstructor(ctor); 
            clz = subClass.toClass();
            clzMap.put(className, clz);
        } 
        
		return (IEntityService) clz.newInstance();
	}

	private static CtClass getDtoClass(String tableName, Type vo) throws CannotCompileException, NotFoundException{
		String className = "com.mk.eap.entity.dym." + tableName + "Dto";
        ClassPool pool = ClassPool.getDefault();
        CtClass subClass = null; 
        try{
        	subClass = pool.get(className); 
        }catch(NotFoundException ex){
        	
        }
        if(subClass == null){
        	subClass = pool.makeClass(className);   
	        subClass.setSuperclass(pool.get(DTO.class.getName()));  
	        subClass.addInterface(pool.get(Serializable.class.getName())); 
	        subClass.addField(CtField.make("private static final long serialVersionUID = 1L;", subClass)); 
	        subClass.addField(CtField.make("public Long id;", subClass)); 
	        subClass.addField(CtField.make("public String code;", subClass)); 
	        subClass.addField(CtField.make("public String name;", subClass)); 
        }
		
		return subClass;
	}

	private static CtClass getVoClass(String tableName, Type vo) throws CannotCompileException, NotFoundException{
		String className = "com.mk.eap.entity.dym." + tableName ;
        ClassPool pool = ClassPool.getDefault();
        CtClass subClass = null; 
        try{
        	subClass = pool.get(className); 
        }catch(NotFoundException ex){
        	
        }
        if(subClass == null){
        	subClass = pool.makeClass(className); 
	        subClass.setSuperclass(pool.get(VO.class.getName())); 
	        subClass.addInterface(pool.get(Serializable.class.getName())); 
	        subClass.addField(CtField.make("private static final long serialVersionUID = 1L;", subClass));
	        CtField idField = CtField.make("public Long id;", subClass);
	        
	        ConstPool constpool = subClass.getClassFile().getConstPool();
			AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constpool , AnnotationsAttribute.visibleTag);
	        Annotation autowired = new Annotation("javax.persistence.Id",constpool);
	        fieldAttr.addAnnotation(autowired); 
	        idField.getFieldInfo().addAttribute(fieldAttr);
	        
	        subClass.addField(idField); 
	        subClass.addField(CtField.make("public String code;", subClass)); 
	        subClass.addField(CtField.make("public String name;", subClass)); 
        }
		return subClass;
	}
	
	private static CtClass getMapperClass(String tableName,CtClass voClz) throws CannotCompileException, NotFoundException{
		String className = "com.mk.eap.entity.dym." + tableName + "Mapper";
        ClassPool pool = ClassPool.getDefault();
        CtClass subClass = null;
        try{
        	subClass = pool.get(className); 
        }catch(NotFoundException ex){
        	
        }
        if(subClass == null){
        	subClass = pool.makeClass(className);  
	        subClass.setSuperclass(pool.get(EntityMapper.class.getName()));  
	        
			String genTypeName = "EntityMapper<" + voClz.getName() + ">";
	        subClass.setGenericSignature(new SignatureAttribute.TypeVariable(genTypeName).encode());
	
	        CtClass[] params = new CtClass[]{ };
	        CtConstructor ctor = CtNewConstructor.make( params, null, CtNewConstructor.PASS_PARAMS, null, null, subClass );
	        subClass.addConstructor(ctor); 
        }
		return subClass;
	}
}
