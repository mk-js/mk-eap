package com.mk.eap.entity.dto;

import java.util.List;

import com.mk.eap.common.domain.DTO;
import com.mk.eap.entity.itf.IEntityService;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 集合字段的定义
 * 
 * @author gaoxue
 * 
 * @param <C>
 *            dto 实体类型
 */
@Getter
@Setter
@ToString
public class ComposFieldDto<C extends DTO> extends DTO {

	public ComposFieldDto() {
		super();
	}

	/**
	 * 创建集合关系字段
	 * 
	 * @param name
	 *            字段名
	 * @param keyNameInChild
	 *            子对象中的外键字段
	 * @param childDtoClass
	 *            子对象的类型
	 */
	public ComposFieldDto(String name, String keyNameInChild, Class<C> childDtoClass) {
		super();
		this.name = name;
		this.keyNameInChild = keyNameInChild;
		this.childDtoClass = childDtoClass;
	}

	private static final long serialVersionUID = -8609194488567733201L;

	// 字段名
	private String name;

	// 父对象的主键
	private String keyName = "id";

	// 子对象保存外键的字段名
	private String keyNameInChild;

	// 子对象的主键
	private String childKeyName = "id";

	// 子对象类型
	private Class<?> childDtoClass;
}
