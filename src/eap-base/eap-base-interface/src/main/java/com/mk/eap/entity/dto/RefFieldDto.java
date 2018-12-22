package com.mk.eap.entity.dto;

import com.mk.eap.common.domain.DTO;
import com.mk.eap.entity.itf.IEntityService;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 引用字段的DTO定义
 * @author gaoxue
 * 
 * @param <R> dto 实体类型
 */
@Getter @Setter @ToString
public class RefFieldDto<R extends DTO> extends DTO {

    private static final long serialVersionUID = -8609194488567733200L;

    public RefFieldDto(String name, String refKeyNameInParent, Class<?> refDtoClass) {
		super();
		this.name = name;
		this.refKeyNameInParent = refKeyNameInParent;
		this.refDtoClass = refDtoClass;
	}

	//字段名
    private String name;

	//父对象的主键
	private String keyName = "id";

    //引用对象的主键名
    private String refKeyName = "id";
	
    //父对象中保存外键的字段名
    private String refKeyNameInParent;

    //引用对象
    private  Class<?> refDtoClass; 
}
