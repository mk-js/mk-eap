package com.mk.eap.entity.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mk.eap.common.domain.BusinessException;
import com.mk.eap.common.domain.DTO;
import com.mk.eap.common.domain.Token;
import com.mk.eap.entity.dto.ComposFieldDto;
import com.mk.eap.entity.dto.InjectorConfig;
import com.mk.eap.entity.dto.PageQueryDto;
import com.mk.eap.entity.dto.PageResultDto;
import com.mk.eap.entity.itf.IEntityInjector;
import com.mk.eap.entity.itf.IEntityService;
import com.mk.eap.entity.itf.IFunctionGetEntitiyService;

/**
 * 组合关系的持久化帮助类
 * 
 * @author lisga
 *
 * @param <P>
 *            主DTO类型
 * @param <C>
 *            子DTO类型
 */
public class EntityCompositionInjector<P extends DTO, C extends DTO> implements IEntityInjector<P> {

	// 集合关系DTO
	ComposFieldDto<C> field;
	IFunctionGetEntitiyService<C> funChildrenEntityService;
	List<String> relatedKeys = new ArrayList<>();
	List<Object> relatedValues = new ArrayList<>();

	/** 泛型 D 的具体类型 */
	protected Class<C> childDtoClass;

	/**
	 * 
	 * @param composField
	 *            集合字段定义
	 * @param funChildrenEntityService
	 *            子对象的实体服务
	 * @param relations
	 *            其它关联条件，如："typeId",1
	 */
	public EntityCompositionInjector(ComposFieldDto<C> composField,
			IFunctionGetEntitiyService<C> funChildrenEntityService, Object... relations) {
		init(composField, funChildrenEntityService, relations);
	}

	/**
	 * 
	 * @param name
	 *            字段名
	 * @param keyNameInChild
	 *            子对象中的外键字段
	 * @param childDtoClass
	 *            子对象的类型
	 * @param funChildrenEntityService
	 *            子对象的实体服务
	 * @param relations
	 *            其它关联条件，如："typeId",1
	 */
	public EntityCompositionInjector(String name, String keyNameInChild, Class<C> childDtoClass,
			IFunctionGetEntitiyService<C> funChildrenEntityService, Object... relations) {
		ComposFieldDto<C> composField = new ComposFieldDto<C>(name, keyNameInChild, childDtoClass);
		init(composField, funChildrenEntityService, relations);
	}

	@SuppressWarnings("unchecked")
	private void init(ComposFieldDto<C> composField, IFunctionGetEntitiyService<C> funChildrenEntityService,
			Object... relations) {
		this.field = composField;
		this.funChildrenEntityService = funChildrenEntityService;
		this.childDtoClass = (Class<C>) composField.getChildDtoClass();
		if (relations != null && relations.length > 0) {
			for (int i = 0; i < relations.length; i += 2) {
				relatedKeys.add(relations[i].toString());
				relatedValues.add(relations[i + 1]);
			}
		}
	}

	IEntityService<C> childEntityService;

	protected IEntityService<C> getChildEntityService() {
		if (childEntityService == null) {
			childEntityService = funChildrenEntityService.getService();
		}
		return childEntityService;
	}

	// 返回带关联条件值的子对象
	protected C getRelatedInstance(C dto, Object key) {
		if (dto == null) {
			try {
				dto = childDtoClass.newInstance();
			} catch (InstantiationException | IllegalAccessException ex) {
				throw new BusinessException("", "", null, ex);
			}
		}
		dto.setFieldValue(field.getKeyNameInChild(), key);
		for (int i = 0; i < relatedKeys.size(); i++) {
			dto.setFieldValue(relatedKeys.get(i), relatedValues.get(i));
		}
		return dto;
	}

	private void loadChildDtos(List<P> dtos, InjectorConfig cfg) {

		if (dtos == null) {
			return;
		}
		if (cfg == null) {
			cfg = new InjectorConfig();
		} else if (cfg.getCompLevel() < 0) {
			return;
		}
		InjectorConfig config = cfg.subConfig(field.getName(), field.getKeyNameInChild());
		if (config == null || config.getCompLevel() < 0) {
			return;
		} else {
			config.setRefLevel(cfg.getRefLevel());
			if (config.hasSubSelectFields()) {
				config.setRefLevel(cfg.getRefLevel() + 1);
				config.setCompLevel(cfg.getCompLevel() + 1);
			}
		}
		List<Object> relatedKeys = new ArrayList<>();
		for (P parentDto : dtos) {
			Object key = parentDto.getFieldValue(field.getKeyName());
			relatedKeys.add(key);
		}
		HashMap<String, Object> where = new HashMap<>();
		where.put(field.getKeyNameInChild(), relatedKeys);
		config.setWhere(where);
		List<C> childrenAll = getChildEntityService().queryByCfg(null, config);

		for (P parentDto : dtos) {
			Object key = parentDto.getFieldValue(field.getKeyName());
			List<C> children = new ArrayList<>();
			parentDto.setFieldValue(field.getName(), children);
			for (C dto : childrenAll) {
				Object keyInChild = dto.getFieldValue(field.getKeyNameInChild());
				if (key.equals(keyInChild)) {
					children.add(dto);
				}
			}
		}

	}

	private List<C> loadChildDtos(P parentDto, InjectorConfig cfg) {

		if (parentDto == null) {
			return null;
		}
		Object key = parentDto.getFieldValue(field.getKeyName());
		C dto = getRelatedInstance(null, key);
		if (cfg == null) {
			cfg = new InjectorConfig();
		} else if (cfg.getCompLevel() < 1) {
			return null;
		}
		List<C> children = null;

		InjectorConfig config = cfg.subConfig(field.getName(), field.getKeyNameInChild());
		if (config == null || config.getCompLevel() < 0) {
			children = null;
		} else {
			config.setRefLevel(cfg.getRefLevel());
			if (config.hasSubSelectFields()) {
				config.setRefLevel(cfg.getRefLevel() + 1);
				config.setCompLevel(cfg.getCompLevel() + 1);
			}
			children = getChildEntityService().queryByCfg(dto, config);
		}
		parentDto.setFieldValue(field.getName(), children);
		return children;
	}

	@Override
	public void afterCreate(P createDto) {
		List<P> dtos = new ArrayList<P>();
		dtos.add(createDto);
		this.afterCreateBatch(dtos, createDto.getToken());
	}

	@Override
	public void afterCreateBatch(List<P> createDtos, Token token) {
		createDtos.forEach(parent -> {
			List<C> children = getChildren(parent);
			if (children != null) {
				Object key = parent.getFieldValue(field.getKeyName());
				children.forEach(child -> {
					getRelatedInstance(child, key);
					if (child.getFieldValue(field.getChildKeyName()) == null) {
						getChildEntityService().create(child);
					} else {
						getChildEntityService().update(child);
					}
				});
			}
		});
	}

	@Override
	public void afterDelete(P deleteDto) {
		List<P> dtos = new ArrayList<P>();
		dtos.add(deleteDto);
		this.afterDeleteBatch(dtos, deleteDto.getToken());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void afterDeleteBatch(List<P> deleteDtos, Token token) {
		List<C> toDtos = new ArrayList<C>();
		deleteDtos.forEach(parentDto -> {
			List<C> children = (List<C>) parentDto.getFieldValue(field.getName());
			if (children != null && children.size() > 0) {
				toDtos.addAll(children);
			}
		});
		if (toDtos.size() > 0) {
			getChildEntityService().deleteBatch(toDtos,token);
		}
	}

	@Override
	public void afterQuery(List<P> resultList, InjectorConfig cfg) {
		// resultList.forEach(parentDto -> loadChildDtos(parentDto, cfg));
		loadChildDtos(resultList, cfg);
	}

	@Override
	public void afterQueryByPrimaryKey(P queryDto, InjectorConfig cfg) {
		loadChildDtos(queryDto, cfg);
	}

	@Override
	public void afterUpdate(P updateDto) {
		List<P> dtos = new ArrayList<P>();
		dtos.add(updateDto);
		this.afterUpdateBatch(dtos, updateDto.getToken());
	}

	@SuppressWarnings("unchecked")
	private List<C> getChildren(P fromDto) {
		if (field.getName() == null) {
			return null;
		}
		return (List<C>) fromDto.getFieldValue(field.getName());
	}

	@Override
	public void afterUpdateBatch(List<P> updateDtos,Token token) {
		// List<C> updateChildList = new ArrayList<>();//基类没有批量更新接口
		// List<C> createChildList = new ArrayList<>();
		List<C> deleteChildList = new ArrayList<>();
		updateDtos.forEach(parentDto -> {
			List<C> children = getChildren(parentDto);
			Object parentKey = parentDto.getFieldValue(field.getKeyName());
			if (children != null) {
				C dto = getRelatedInstance(null, parentKey);
				List<C> childrenInDB = getChildEntityService().query(dto);
				// 新增key值为空的子数据
				children.forEach(child -> {
					if (child.getFieldValue(field.getChildKeyName()) == null) {
						getRelatedInstance(child, parentKey);
						// createChildList.add(child);
						System.out.println("getChildEntityService().create");
						getChildEntityService().create(child);
					} else {
						getChildEntityService().update(child); // 基类没有批量更新接口
					}
				});
				// 删除未提交的子数据
				childrenInDB.forEach(child -> {
					Object key = child.getFieldValue(field.getChildKeyName());
					children.forEach(c -> {
						Object childKey = c.getFieldValue(field.getChildKeyName());
						if (childKey != null && childKey.equals(key)) {
							child.setFieldValue(field.getChildKeyName(), null);
						}
					});
					if (child.getFieldValue(field.getChildKeyName()) != null) {
						deleteChildList.add(child);
					}
				});
			}
		});

		// if (updateDtos.size() > 0) {
		// getChildEntityService().updateBatch(updateDtos);
		// }
		// if (createChildList.size() > 0) {
		// getChildEntityService().createBatch(createChildList); //未使用数据库默认值 ！？
		// }
		if (deleteChildList.size() > 0) {
			getChildEntityService().deleteBatch(deleteChildList, token);
		}

	}

	@Override
	public void beforeCreate(P createDto) {

	}

	@Override
	public void beforeCreateBatch(List<P> createDtos, Token token) {

	}

	@Override
	public void beforeDelete(P deleteDto) {

	}

	@Override
	public void beforeDeleteBatch(List<P> deleteDto, Token token) {

	}

	@Override
	public void beforeUpdate(P updateDto) {

	}

	@Override
	public void beforeUpdateBatch(List<P> updateDto, Token token) {

	}

	@Override
	public void beforeQueryByPrimaryKey(P queryDto) {

	}

}
