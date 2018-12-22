package com.mk.eap.entity.impl;

import java.util.ArrayList;
import java.util.List;

import com.mk.eap.common.domain.BusinessException;
import com.mk.eap.common.domain.DTO;
import com.mk.eap.common.domain.Token;
import com.mk.eap.entity.dto.InjectorConfig;
import com.mk.eap.entity.dto.PageQueryDto;
import com.mk.eap.entity.dto.PageResultDto;
import com.mk.eap.entity.dto.RefFieldDto;
import com.mk.eap.entity.itf.IEntityInjector;
import com.mk.eap.entity.itf.IEntityService;
import com.mk.eap.entity.itf.IFunctionGetEntitiyService;

/**
 * 引用关系的持久化帮助类
 * 
 * @author lisga
 *
 * @param <P>
 *            主DTO类型
 * @param <R>
 *            子DTO类型
 */
public class EntityReflectionInjector<P extends DTO, R extends DTO> implements IEntityInjector<P> {

	// 引用关系字段
	RefFieldDto<R> field;
	IFunctionGetEntitiyService<R> funRefEntityService;
	List<String> relatedKeys = new ArrayList<>();
	List<Object> relatedValues = new ArrayList<>();

	/** 泛型 R 的具体类型 */
	protected Class<R> refDtoClass;

	@SuppressWarnings("unchecked")
	private void init(RefFieldDto<R> refField, IFunctionGetEntitiyService<R> funRefEntityService, Object... relations) {
		this.field = refField;
		this.funRefEntityService = funRefEntityService;
		this.refDtoClass = (Class<R>) refField.getRefDtoClass();
		if (relations != null && relations.length > 0) {
			for (int i = 0; i < relations.length; i += 2) {
				relatedKeys.add(relations[i].toString());
				relatedValues.add(relations[i + 1]);
			}
		}
	}

	/**
	 * 
	 * @param refField
	 *            引用字段定义
	 * @param funChildrenEntityService
	 *            子对象的实体服务
	 * @param funIdService
	 *            Id服务
	 * @param relations
	 *            其它关联条件，如："typeId",1
	 */
	public EntityReflectionInjector(RefFieldDto<R> refField, IFunctionGetEntitiyService<R> funRefEntityService,
			Object... relations) {
		init(refField, funRefEntityService, relations);
	}

	/**
	 * 
	 * @param name
	 *            引用字段定义
	 * @param refKeyNameInParent
	 *            父对象中保存外键的字段名
	 * @param refDtoClass
	 *            引用类
	 * @param funChildrenEntityService
	 *            子对象的实体服务
	 * @param funIdService
	 *            Id服务
	 * @param relations
	 *            其它关联条件，如："typeId",1
	 */
	public EntityReflectionInjector(String name, String refKeyNameInParent, Class<R> refDtoClass,
			IFunctionGetEntitiyService<R> funRefEntityService, Object... relations) {
		RefFieldDto<R> refField = new RefFieldDto<>(name, refKeyNameInParent, refDtoClass);

		init(refField, funRefEntityService, relations);
	}

	IEntityService<R> refEntityService;

	protected IEntityService<R> getRefEntityService() {
		if (refEntityService == null) {
			refEntityService = funRefEntityService.getService();
		}
		return refEntityService;
	}

	// 返回带关联条件值的子对象
	protected R getRelatedInstance(R dto, Object key) {
		if (dto == null) {
			try {
				dto = refDtoClass.newInstance();
			} catch (InstantiationException | IllegalAccessException ex) {
				throw new BusinessException("", "", null, ex);
			}
		}
		dto.setFieldValue(field.getRefKeyName(), key);
		for (int i = 0; i < relatedKeys.size(); i++) {
			dto.setFieldValue(relatedKeys.get(i), relatedValues.get(i));
		}
		return dto;
	}

	private void loadRelatedDtos(List<P> dtos, InjectorConfig cfg) {
		if (cfg != null && cfg.getRefLevel() < 1 && cfg.getSelectFields() == null) {
			return;
		}
		InjectorConfig config = cfg.subConfig(field.getName(), field.getRefKeyName());
		if (config == null || config.getRefLevel() < 0) {
			return;
		}
		List<R> relatedDtos = new ArrayList<R>();
		for (P parentDto : dtos) {
			if (parentDto == null) {
				continue;
			}
			Object key = parentDto.getFieldValue(field.getRefKeyNameInParent());
			if (key == null) {
				continue;
			}
			R dto = getRelatedInstance(null, key);
			relatedDtos.add(dto);
		}

		List<R> loadedDtos = getRefEntityService().queryByPrimaryKeysCfg(relatedDtos, config);

		for (P parentDto : dtos) {
			if (parentDto == null) {
				continue;
			}
			Object key = parentDto.getFieldValue(field.getRefKeyNameInParent());
			if (key == null) {
				continue;
			}
			for (R dto : loadedDtos) {
				if (key.equals(dto.getFieldValue(field.getRefKeyName()))) {
					parentDto.setFieldValue(field.getName(), dto);
				}
			}
		}
	}

	private R loadRelatedDto(P parentDto, InjectorConfig cfg) {
		if (cfg != null && cfg.getRefLevel() < 1) {
			return null;
		}
		if (parentDto == null) {
			return null;
		}
		Object key = parentDto.getFieldValue(field.getRefKeyNameInParent());
		if (key == null) {
			return null;
		}
		R dto = getRelatedInstance(null, key);
		InjectorConfig config = cfg.subConfig(field.getName(), field.getRefKeyName());
		if (config == null || config.getRefLevel() < 0) {
			dto = null;
		} else {
			dto = getRefEntityService().queryByPrimaryKeyCfg(dto, config);
		}
		parentDto.setFieldValue(field.getName(), dto);
		return dto;
	}

	@Override
	public void afterCreate(P createDto) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCreateBatch(List<P> createDtos, Token token) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterDelete(P deleteDto) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterDeleteBatch(List<P> deleteDtos, Token token) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterQuery(List<P> resultList, InjectorConfig cfg) {
		loadRelatedDtos(resultList, cfg);

	}

	@Override
	public void afterQueryByPrimaryKey(P queryDto, InjectorConfig cfg) {
		loadRelatedDto(queryDto, cfg);
	}

	@Override
	public void afterUpdate(P updateDto) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterUpdateBatch(List<P> updateDto, Token token) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeCreate(P createDto) {
		if (createDto.getFieldValue(field.getName()) != null) {
			DTO refDto = (DTO) createDto.getFieldValue(field.getName());
			createDto.setFieldValue(field.getRefKeyNameInParent(), refDto.getFieldValue(field.getRefKeyName()));
		}
	}

	@Override
	public void beforeCreateBatch(List<P> createDtos, Token token) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeDelete(P deleteDto) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeDeleteBatch(List<P> deleteDto, Token token) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeUpdate(P updateDto) {
		if(updateDto == null){
			return;
		}
		DTO refDto = (DTO) updateDto.getFieldValue(field.getName());
		if (refDto != null) {
			updateDto.setFieldValue(field.getRefKeyNameInParent(), refDto.getFieldValue(field.getRefKeyName()));
		}
	}

	@Override
	public void beforeUpdateBatch(List<P> updateDto, Token token) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeQueryByPrimaryKey(P queryDto) {
		// TODO Auto-generated method stub

	}

}
