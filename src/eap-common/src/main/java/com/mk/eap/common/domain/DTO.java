package com.mk.eap.common.domain;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mk.eap.common.utils.BeanExtendUtils;

public class DTO implements Serializable {

	private static final long serialVersionUID = -7030510712004741358L;

	private final static Logger logger = LoggerFactory.getLogger(DTO.class);

	/** 时间戳字段名称 */
	public static final String TS_FILED_NAME = "ts";

	/** 属性为 null 的字段是否更新到数据库，默认 false */
	//	@JsonIgnore
	private boolean nullUpdate = false;
	
	private Token token = null;

	/**
	 * 是否存在对应的属性
	 * 
	 * @param field
	 *            属性名称
	 * @return
	 */
	public boolean existField(String field) {
		Class<?> clazz = getClass();
		while (clazz != Object.class) {
			try {
				clazz.getDeclaredField(field);
				return true;
			} catch (NoSuchFieldException ex) {
				// log.error(ex.toString());
				clazz = clazz.getSuperclass();
			} catch (SecurityException ex) {
				// log.error(ex.toString());
				break;
			}
		}
		return false;
	}

	/**
	 * 是否存在时间戳字段 {@code ts}
	 * 
	 * @return
	 */
	public boolean existTs() {
		return existField(TS_FILED_NAME);
	}

	public Object getFieldValue(String field) {
		String pre = field.substring(0, 1);
		String methodName = "get" + pre.toUpperCase() + field.substring(1);
		Class<?> clazz = getClass();
		while (clazz != Object.class) {
			try {
				// clazz.getMethod(methodName);
				Method method = clazz.getDeclaredMethod(methodName);
				return method.invoke(this);
			} catch (NoSuchMethodException ex) {
				clazz = clazz.getSuperclass();
				// log.error(ex.toString());
			} catch (Exception ex) {
				// log.error(ex.toString());
				break;
			}
		}
		return null;
	}

	public void setFieldValue(String fieldName, Object value) {
		String pre = fieldName.substring(0, 1);
		String methodName = "set" + pre.toUpperCase() + fieldName.substring(1);
		Class<?> clazz = getClass();
		while (clazz != Object.class) {
			try {
				Field field = clazz.getDeclaredField(fieldName);
				Method method = clazz.getDeclaredMethod(methodName, field.getType());
				Object fieldValue = conertToFieldValue(field, value);
				method.invoke(this, fieldValue);
				break;
			} catch (NoSuchMethodException | NoSuchFieldException ex) {
				clazz = clazz.getSuperclass();
				// log.error(ex.toString());
			} catch (Exception ex) {
				// log.error(ex.toString());
				break;
			}
		}
	}

	/**
	 * 按字段类型做数值转换
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	private Object conertToFieldValue(Field field, Object value) {
		if (value == null) {
			return null;
		}
		// 得到vo中成员变量的类型
		Class<?> type = field.getType();
		// 下面代码都是参数类型是什么，如果有需求可以自行增加
		// 当set方法中的参数为int或者Integer
		Object parseValue = value;
		if (type == Integer.class || type == int.class) {
			if (value instanceof Integer == false) {
				parseValue = Integer.parseInt(value.toString());
			}
			// 当set方法中的参数为Long
		} else if (type == Long.class) {
			if (value instanceof Long == false) {
				parseValue = Long.parseLong(value.toString());
			}
			// 当set方法中的参数为Float
		} else if (type == Date.class) {
			if (value instanceof Date == false) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				try {
					parseValue = sdf.parse(value.toString());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			// 当set方法中的参数为Float
		} else if (type == double.class || type == Double.class) {
			if (value instanceof Double == false) {
				parseValue = Double.parseDouble(value.toString());
			}
			// 当set方法中的参数为其他
		} else if (type == String.class) {

			if (value instanceof String[]) {
				String[] tempArray = (String[]) value;
				parseValue = String.join(",", tempArray);
			} else if (value != null) {
				parseValue = value.toString();
			}
		}
		return parseValue;
	}

	/**
	 * dto 实体转换为 vo 实体
	 * 
	 * @param clazz
	 *            vo 实体类型
	 * @return vo 实体
	 */
	public <TVo extends VO> TVo toVo(Class<TVo> clazz) {
		TVo result;
		try {
			result = clazz.newInstance();
			// TODO gaoxue date 不能转换，效率问题
			BeanExtendUtils.copyProperties(this, result);
			return result;
		} catch (InstantiationException | IllegalAccessException ex) {
			logger.error(ex.getMessage());
			// Vo should have constructor, this should not happen
		}
		return null;
	}

	/**
	 * dto 实体转换为 vo 实体
	 * 
	 * @param clazz
	 *            vo 实体类型
	 * @param propertyList
	 *            需要转换的属性
	 * @return vo 实体
	 */
	public <TVo extends VO> TVo toVo(Class<TVo> clazz, List<String> propertyList) {
		TVo result;
		try {
			result = clazz.newInstance();
			// TODO gaoxue date 不能转换，效率问题
			BeanExtendUtils.copyProperties(this, result, propertyList);
			return result;
		} catch (InstantiationException | IllegalAccessException ex) {
			logger.error(ex.getMessage());
			// Vo should have constructor, this should not happen
		}
		return null;
	}

	/**
	 * vo 实体转换为 dto 实体
	 * 
	 * @param vo
	 *            需要转换的 vo 实体
	 * @return dto 实体
	 */
	@SuppressWarnings("unchecked")
	public <TVo extends VO, TDto extends DTO> TDto fromVo(TVo vo) {
		if (vo == null) {
			return null;
		}
		TDto result;
		try {
			result = (TDto) getClass().newInstance();
			BeanExtendUtils.copyProperties(vo, result);
			return result;
		} catch (InstantiationException | IllegalAccessException ex) {
			logger.error(ex.getMessage());
			// Dto should have constructor, this should not happen
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <TDto extends DTO> TDto fromMap(HashMap<String, Object> map) {
		if (map == null) {
			return null;
		}
		map.forEach((key, value) -> setFieldValue(key, value));
		return (TDto) this;
	}

	public <TVo extends VO, TDto extends DTO> List<TDto> fromVo(List<TVo> vos) {
		List<TDto> result = new ArrayList<>();
		for (TVo vo : vos) {
			TDto dto = fromVo(vo);
			if (dto != null) {
				result.add(dto);
			}
		}
		return result;
	}

	/**
	 * 获取属性为 null 的字段是否更新到数据库，默认 false
	 * 
	 * @return 属性为 null 的字段是否更新到数据库，默认 false
	 */
	public boolean getNullUpdate() {
		return nullUpdate;
	}

	/**
	 * 设置属性为 null 的字段是否更新到数据库，默认 false
	 * 
	 * @param nullUpdate
	 *            属性为 null 的字段是否更新到数据库，默认 false
	 */
	public void setNullUpdate(boolean nullUpdate) {
		this.nullUpdate = nullUpdate;
	}

	public Object getFieldValueByPath(String key) {
		String[] paths = key.split("[.]");
		DTO curObj = this;
		Object curValue = null;
		for (String k : paths) {
			if (k.indexOf("[") != -1) {
				String listName = k.substring(0, k.indexOf("["));
				String index = k.substring(k.indexOf("[") + 1, k.indexOf("]")); 
				Object list = curObj.getFieldValue(listName);
				if (list instanceof List<?>) {
					curValue = ((List<?>) list).get(Integer.parseInt(index));
				} 
			} else {
				curValue = curObj.getFieldValue(k);
			}
			if (curValue instanceof DTO) {
				curObj = (DTO) curValue;
			}
			if (curValue == null) {
				return curValue;
			}
		}
		return curValue;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

}
