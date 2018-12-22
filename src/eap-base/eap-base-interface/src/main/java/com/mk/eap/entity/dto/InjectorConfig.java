package com.mk.eap.entity.dto;

import java.util.HashMap;
import java.util.List;

import com.mk.eap.common.domain.DTO;
import com.mk.eap.common.domain.VO;
import com.mk.eap.common.utils.BeanExtendUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

/**
 * 实体注入器配置信息
 * 
 * @author lisga
 * 
 */
@Getter
@Setter
@ToString
public class InjectorConfig extends DTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2912132377042024423L;

	// 引用级别
	private int refLevel = 0;
	
	// 组合级别
	private int compLevel = 1;

	// 返回字段
	private String selectFields;

	// 排序字段
	private String orderBy;

	// 搜索关键字
	private String search;

	// 搜索字段
	private String searchFields;

	// 过滤条件
	private HashMap<String, Object> where;

	// JsonSelectField解析
	private JsonSelectorDto jsonSelectorDto;

	public static InjectorConfig RefLevel(int level) {
		return new InjectorConfig(level);
	}

	public InjectorConfig subConfig(String subFieldName, String addFieldName) {
		InjectorConfig config = new InjectorConfig();
		BeanExtendUtils.copyProperties(this, config);
		if (config.getSelectFields() != null) {
			JsonSelectorDto subSelectorDto = this.getJsonSelectorDto().getSubFields().get(subFieldName);
			config.setJsonSelectorDto(subSelectorDto);
			if (subSelectorDto == null) {
				return null;
			} else {
				config.setSelectFields(subSelectorDto.getSelectFieldsJson());
				if (null != addFieldName && subSelectorDto.getFields() != null
						&& !subSelectorDto.getFields().contains(addFieldName)) {
					subSelectorDto.getFields().add(addFieldName);
				}
			}
		} else {
			config.setRefLevel(this.getRefLevel() - 1);
			config.setCompLevel(this.getCompLevel() - 1); 
		}
		return config;
	}

	public InjectorConfig() {
		super();
	}

	/**
	 * @param level，-1
	 *            本表字段，0 包含组合关系，1 包含一级引用关系，
	 */
	public InjectorConfig(int refLevel) {
		super();
		this.refLevel = refLevel;
	}

	public <V extends VO> Example getExample(Class<?> voClazz, V vo) {
		Example example = new Example(voClazz, false, false);
		if (this.selectFields != null) {
			List<String> fields = this.getJsonSelectorDto().getFields();
			if (fields != null) {
				String[] fs = new String[fields.size()];
				example.selectProperties(fields.toArray(fs));
			}
		}
		if (this.where != null && this.where.size() > 0) {
			Criteria criteria = example.createCriteria();
			for (String key : where.keySet()) {
				Object value = where.get(key);
				if (value == null) {
					criteria.andIsNull(key);
				} else if (value instanceof Iterable) {
					criteria.andIn(key, (Iterable) value);
				} else {
					criteria.andEqualTo(key, value);
				}
			}
		}
		return example;
	}

	public JsonSelectorDto getJsonSelectorDto() {
		if (this.jsonSelectorDto == null) {
			this.jsonSelectorDto = new JsonSelectorDto(this.selectFields);
		}
		return this.jsonSelectorDto;
	}

	public boolean hasSubSelectFields() {
		if (selectFields == null) {
			return false;
		} else if (selectFields.indexOf(":") != -1) {
			return true;
		} else {
			return false;
		}
	}
}
