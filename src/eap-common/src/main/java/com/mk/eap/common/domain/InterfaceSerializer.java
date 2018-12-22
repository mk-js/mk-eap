package com.mk.eap.common.domain;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mk.eap.common.annotation.ApiContext;
import com.mk.eap.common.annotation.ApiMapping;
import com.mk.eap.common.annotation.ApiResult;
import com.mk.eap.common.domain.ClassDto;
import com.mk.eap.common.domain.InterfaceDto;
import com.mk.eap.common.domain.MethodDto;
import com.mk.eap.common.domain.ParameterDto;

public class InterfaceSerializer {

	public InterfaceSerializer() {

	}

	static InterfaceSerializer INSTANCE = new InterfaceSerializer();

	public static ArrayList<ClassDto> serialize(String[] interfaceNames) throws Exception {
		return INSTANCE.serializeInterfaces(interfaceNames);
	}

	public ArrayList<ClassDto> serializeInterfaces(String[] interfaceNames) throws Exception {
		ArrayList<ClassDto> list = new ArrayList<>();
		HashMap<String, ClassDto> clsDtoCache = new HashMap<>();

		for (int i = 0; i < interfaceNames.length; i++) {
			ClassDto itf = new ClassDto();
			itf.name = interfaceNames[i];
			itf.requestMapping = new HashMap<String, String>();
			serializeMethods(itf, clsDtoCache);
			if (itf.methods != null && itf.methods.size() > 0) {
				list.add(itf);
			}
		}

		list.addAll(clsDtoCache.values());

		return list;
	}

	String ROOTTAG = "@";
	String DELIMITER = ",";

	void serializeMethods(InterfaceDto itfDto, HashMap<String, ClassDto> clsDtoCache) throws Exception {
		Class<?> itf = null;
		try {
			itf = Class.forName(itfDto.name);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
			return;
		}

		if (itf == null || !itf.isAnnotationPresent(RequestMapping.class)) {
			return;
		}
		boolean publishAllMethod = false;
		if (itf.isAnnotationPresent(ApiMapping.class)) {
			ApiMapping apiMapping = (ApiMapping) itf.getAnnotation(ApiMapping.class);
			String apiString = apiMapping.value();
			if (apiString.equals("*")) {
				publishAllMethod = true;
			}
			String[] apis = apiString.split(";");
			for (String api : apis) {
				String method = api.trim();
				String url = "/" + api.trim();
				if (api.indexOf(":") != -1) {
					method = api.split(":")[0].trim();
					url = api.split(":")[1].trim();
				}
				itfDto.requestMapping.put(method, url);
			}
		}

		RequestMapping itfMapping = (RequestMapping) itf.getAnnotation(RequestMapping.class);
		itfDto.requestMapping.put(ROOTTAG, String.join(DELIMITER, itfMapping.value()));

		Method[] methods = itf.getMethods();
		Type[] genericInterfaces = itf.getGenericInterfaces();
		itfDto.methods = new ArrayList<>();
		List<String> methodNames = new ArrayList<>();
		for (Method m : methods) {
			boolean isWebApi = Modifier.isAbstract(m.getModifiers()) && (publishAllMethod
					|| m.isAnnotationPresent(RequestMapping.class) || itfDto.requestMapping.containsKey(m.getName()));
			if (!isWebApi) {
				continue;
			}
			Type[] paraTypes = null;
			for (Type mGenItfType : genericInterfaces) {
				if (mGenItfType instanceof  ParameterizedType && 
						((ParameterizedType) mGenItfType).getRawType().toString()
						.equals(m.getDeclaringClass().toString())) {
					paraTypes = ((ParameterizedType) mGenItfType).getActualTypeArguments();
					break;
				}
			}
			MethodDto methodDto = new MethodDto();
			methodDto.name = m.getName();
			if (methodNames.contains(methodDto.name)) {
				throw new Exception("暂时不支持接口的方法名相同！");
			} else {
				methodNames.add(methodDto.name);
			}
			methodDto.parameters = serializeParameterTypes(m, clsDtoCache, genericInterfaces, paraTypes);
			String urls = "";
			if (publishAllMethod) {
				urls = "/" + m.getName();
			}
			if (itfDto.requestMapping.containsKey(m.getName())) {
				urls = itfDto.requestMapping.get(m.getName());
			}
			RequestMapping methodMapping = (RequestMapping) m.getAnnotation(RequestMapping.class);
			if (methodMapping != null) {
				urls = String.join(DELIMITER, methodMapping.value());
			}
			ApiResult apiResult = (ApiResult) m.getAnnotation(ApiResult.class);
			ApiContext apiContext = (ApiContext) m.getAnnotation(ApiContext.class);
			if (apiContext != null && methodDto.parameters.size() > 0) {
				methodDto.parameters.get(0).$ctx = apiContext.value();
			}
			if (apiResult != null) {
				urls += "?" + apiResult.value();
			}
			Class<?> returnType = m.getReturnType();
			Type genRetType = m.getGenericReturnType();
			if (returnType != null) {
				List<String> dependTypes = new ArrayList<>();
				if (genRetType instanceof TypeVariable<?>) {
					genRetType = getRealType(genericInterfaces, (TypeVariable<?>) genRetType);
					try {
						returnType = Class.forName(genRetType.getTypeName());
					} catch (Exception e) {
					}
				}
				serializeType(returnType, genericInterfaces, genRetType, paraTypes, dependTypes, clsDtoCache);
				ParameterDto returnDto = new ParameterDto();
				returnDto.$class = returnType.getName();
				if (genRetType != null) {
					returnDto.$class = getTypeNameByGenericInterfaces(genericInterfaces, genRetType, paraTypes);
				}
				methodDto.returnType = returnDto;
			}
			itfDto.requestMapping.put(m.getName(), urls);
			itfDto.methods.add(methodDto);
		}
	}

	private String getTypeNameByGenericInterfaces(Type[] itfTypes, Type type, Type[] typeParas) {
		if (type == null) {
			return null;
		}
		String typeName = type.getTypeName();
		TypeVariable<?> varType = null;
		ParameterizedType paraType = null;
		if (type instanceof TypeVariable) {
			varType = (TypeVariable<?>) type;
			Type realType = getRealType(itfTypes, varType);
			if (realType != null) {
				typeName = realType.getTypeName();
			}

		} else if (type instanceof ParameterizedType) {
			paraType = (ParameterizedType) type;
			typeName = type.toString();
			Type[] types = paraType.getActualTypeArguments();
			for (int i = 0; i < types.length; i++) {
				Type t = types[i];
				if (typeParas != null) {
					for (Type realType : typeParas) {
						if (realType.getTypeName().equals(t.getTypeName())) {
							t = realType;
							break;
						}
					}
				}
				String atlTypeName = getTypeNameByGenericInterfaces(itfTypes, t, typeParas);
				typeName = completeReplace(typeName, t.getTypeName(), atlTypeName);
			}
		} else if (type != null) {
			typeName = type.getTypeName();
		}

		return typeName;
	}

	public static String completeReplace(String str, String old, String newWord) {
		Pattern pattern = Pattern.compile("\\b" + old + "\\b");
		Matcher matcher = pattern.matcher(str);
		String result = matcher.replaceAll(newWord);
		return result;
	}

	private Type getRealType(Type[] itfTypes, TypeVariable<?> varType) {
		String itfName = varType.getGenericDeclaration().toString();

		ParameterizedType pType = null;
		for (int i = 0; i < itfTypes.length; i++) {
			ParameterizedType paraItf = (ParameterizedType) itfTypes[i];
			if (paraItf != null && itfName.equals(paraItf.getRawType().toString())) {
				pType = paraItf;
			}
		}

		int varTypeIndex = -1;
		TypeVariable<?>[] genTypes = varType.getGenericDeclaration().getTypeParameters();
		for (int i = 0; i < genTypes.length; i++) {
			if (genTypes[i].equals(varType)) {
				varTypeIndex = i;
				break;
			}
		}
		if (pType == null) {
			return null;
		}

		return pType.getActualTypeArguments()[varTypeIndex];
	}

	List<ParameterDto> serializeParameterTypes(Method m, Map<String, ClassDto> clsDtoCache, Type[] genericInterfaces,
			Type[] paraTypes) {

		Parameter[] parameters = m.getParameters();
		List<String> dependTypes = new ArrayList<>();
		List<ParameterDto> parameterDtos = new ArrayList<>();

		for (int i = 0; i < parameters.length; i++) {
			Parameter param = parameters[i];
			Class<?> cls = param.getType();
			Type paraType = param.getParameterizedType();
			ParameterDto para = new ParameterDto();
			parameterDtos.add(para);
			if (param.isAnnotationPresent(ApiContext.class)) {
				ApiContext restContext = (ApiContext) param.getAnnotation(ApiContext.class);
				para.$ctx = restContext.value();
			}

			String typeName = null;
			if (paraType != null) {
				if (paraType instanceof TypeVariable<?>) {
					paraType = getRealType(genericInterfaces, (TypeVariable<?>) paraType);
					try {
						cls = Class.forName(paraType.getTypeName());
					} catch (Exception e) {
					}
				}
				if (paraType instanceof ParameterizedType) {
					paraType = paraType;
					cls = cls;
					param = param;
				}
				typeName = getTypeNameByGenericInterfaces(genericInterfaces, paraType, paraTypes);
			} else {
				typeName = cls.getTypeName();
			}
			para.$class = typeName;
			para.$name = param.getName();

			String ctx = serializeType(cls, genericInterfaces, paraType, paraTypes, dependTypes, clsDtoCache);
			if (ctx != null && !ctx.equals("")) {
				if (para.$ctx == null) {
					para.$ctx = ctx;
				} else {
					para.$ctx = para.$ctx + "," + ctx;
				}
			}
		}
		dependTypes.forEach((item) -> serializeClass(item, genericInterfaces, clsDtoCache));
		return parameterDtos;
	}

	String serializeClass(String className, Type[] genericInterfaces, Map<String, ClassDto> clsDtoCache) {
		try {
			Class<?> cls = Class.forName(className);
			return serializeClass(cls, genericInterfaces, clsDtoCache, null, null);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	String serializeClass(Class<?> cls, Type[] genericInterfaces, Map<String, ClassDto> clsDtoCache, String genClsName,
			Type[] typeParams) {
		String apiCtx = "";
		String clsName = cls.getName();
		boolean isGenicCls = null != genClsName;
		if (isGenicCls) {
			clsName = genClsName;
		}
		ClassDto dto = new ClassDto();
		List<String> dependTypes = new ArrayList<>();
		if (clsDtoCache.containsKey(clsName) && clsDtoCache.get(clsName).name != null || clsName.indexOf("java.") == 0
				|| clsName.indexOf("sun.") == 0) {
			dto = clsDtoCache.get(clsName);
			if (dto != null) {
				return dto.apiContext;
			}
			return null;
		}
		try {
			dto.instance = cls.newInstance();
			if (!(dto.instance instanceof Serializable)) {
				System.out.println(clsName);
				dto.instance = null;
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		dto.name = clsName;
		dto.fields = new HashMap<String, String>();
		List<Field> fieldList = new ArrayList<>();
		Class<?> tempClass = cls;
		while (tempClass != null) {
			fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
			tempClass = tempClass.getSuperclass(); // 递归父类
		}
		clsDtoCache.put(clsName, dto);
		for (Field field : fieldList) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			Type genType = field.getGenericType();
			Class<?> fieldCls = field.getType();
			String typeName = genType.getTypeName();
			if (genType instanceof TypeVariable) {
				if (typeParams == null) {
					typeParams = null;
				}
				for (Type typePara : typeParams) {
					if (typePara.getTypeName().equals(genType.getTypeName())) {
						genType = typePara;
						break;
					}
				}
				Type realType = getRealType(genericInterfaces, (TypeVariable<?>) genType);
				typeName = realType.getTypeName();
				try {
					fieldCls = Class.forName(typeName);
				} catch (ClassNotFoundException e) {
					System.out.println(e.getMessage());
				}
			}
			if (genType instanceof ParameterizedType) {
				typeName = getTypeNameByGenericInterfaces(genericInterfaces, genType, typeParams);
			}
			if (field.isAnnotationPresent(ApiContext.class)) {
				ApiContext apiContext = (ApiContext) field.getAnnotation(ApiContext.class);
				String ctx = apiContext.value();
				if (ctx == null || ctx.equals("") || ctx.equals("token")) {
					ctx = field.getName() + ":token";
				} else {
					ctx = field.getName() + ":" + ctx;
				}
				apiCtx = apiCtx + "," + ctx;
			}
			if (field.isAnnotationPresent(JsonFormat.class)) {
				JsonFormat jsonFormat = (JsonFormat) field.getAnnotation(JsonFormat.class);
				typeName += "`JsonFormat('" + jsonFormat.pattern() + "')";
			}
			if (field.isAnnotationPresent(JsonProperty.class)) {
				JsonProperty jsonProperty = (JsonProperty) field.getAnnotation(JsonProperty.class);
				typeName += "`JsonProperty('" + jsonProperty.value() + "')";
			}
			if (field.isAnnotationPresent(JsonIgnore.class)) {
				JsonIgnore jsonIgnore = (JsonIgnore) field.getAnnotation(JsonIgnore.class);
				typeName += "`JsonIgnore('" + jsonIgnore.value() + "')";
			}
			dto.fields.put(field.getName(), typeName);
			serializeType(fieldCls, genericInterfaces, genType, typeParams, dependTypes, clsDtoCache);
		}

		dependTypes.forEach((item) -> serializeClass(item, genericInterfaces, clsDtoCache));
		if (apiCtx.startsWith(",")) {
			apiCtx = apiCtx.substring(1);
		}
		dto.apiContext = apiCtx;
		return apiCtx;
	}

	String serializeType(Class<?> cls, Type[] genericInterfaces, Type genType, Type[] typeParams,
			List<String> dependTypes, Map<String, ClassDto> clsDtoCache) {
		if (cls.isPrimitive()) {
			return null;
		}
		String apictx = "";
		String typeName = genType.getTypeName();
		Type[] actualTypes = null;
		ClassDto emptyClassDto = new ClassDto();
		emptyClassDto.fields = new HashMap<>();
		emptyClassDto.name = typeName;
		Class<?> realCls = cls;
		String realTypeName = typeName;

		if (typeName.indexOf("<") != -1) {
			actualTypes = ((ParameterizedType) genType).getActualTypeArguments();
			for (int j = 0; j < actualTypes.length; j++) {
				Type realType = actualTypes[j];
				realTypeName = realType.getTypeName();
				if (realType instanceof TypeVariable<?>) {
					TypeVariable<?> varType = (TypeVariable<?>) realType;
					if (typeParams != null) {
						for (Type t : typeParams) {
							if (t.getTypeName().equals(varType.getTypeName())) {
								varType = (TypeVariable<?>) t;
								break;
							}
						}
					}
					realType = getRealType(genericInterfaces, varType);
					try {
						if (realType != null) {
							realCls = Class.forName(realType.getTypeName());
							realTypeName = realType.getTypeName();
						}
					} catch (Exception e) {
					}
					realTypeName = completeReplace(typeName, varType.getTypeName(), realTypeName);
				} else {
					try {
						realCls = Class.forName(realTypeName);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				dependTypes.add(realTypeName);
			}
		}

		if (cls.isArray()) {
			realCls = cls.getComponentType();
			clsDtoCache.put(typeName, emptyClassDto);
		} else if (typeName.indexOf("java.util.List<") != -1) {
			if (realTypeName.indexOf("<") != -1) {
				emptyClassDto.name = realTypeName;
				typeName = realTypeName;
			}
			if (realCls != null && clsDtoCache.get(realCls.getName()) != null) {
				emptyClassDto.apiContext = clsDtoCache.get(realCls.getName()).apiContext;
			}
			clsDtoCache.put(emptyClassDto.name, emptyClassDto);
		} else if (typeName.indexOf("Map<") != -1) {
			emptyClassDto.fields.put("*", actualTypes[1].getTypeName());
			clsDtoCache.put(typeName, emptyClassDto);
		} else if (typeName.indexOf("<") != -1) {
			// 普通泛型类型，未验证
		}
		apictx = serializeClass(realCls, genericInterfaces, clsDtoCache, typeName, typeParams);
		return apictx;
	}
}
