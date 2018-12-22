package com.mk.eap.entity.itf;

import java.util.List;

import com.mk.eap.common.domain.DTO;
import com.mk.eap.common.domain.PageObject;
import com.mk.eap.common.domain.Token;
import com.mk.eap.entity.dto.InjectorConfig;
import com.mk.eap.entity.dto.PageQueryDto;
import com.mk.eap.entity.dto.PageResultDto;

public interface IEntityInjector<D extends DTO> {

	// 进行数据库新增后执行
	void afterCreate(D createDto);

	// 进行数据库新增后执行
	void afterCreateBatch(List<D> createDtos, Token token);

	// 进行数据库删除后执行
	void afterDelete(D deleteDto);

	// 进行数据库删除后执行
	void afterDeleteBatch(List<D> deleteDtos, Token token);

	// 按照主键查询实体后执行
	void afterQuery(List<D> resultList, InjectorConfig cfg);

	// 按照主键查询实体后执行
	void afterQueryByPrimaryKey(D queryDto, InjectorConfig cfg); 

	// 进行数据库更新后操作
	void afterUpdate(D updateDto); 

	// 进行数据库更新后操作
	void afterUpdateBatch(List<D> updateDto, Token token);

	// 进行数据库新增前执行
	void beforeCreate(D createDto);

	// 进行数据库新增前执行
	void beforeCreateBatch(List<D> createDtos, Token token);

	// 进行数据库删除前执行
	void beforeDelete(D deleteDto);

	// 进行数据库删除前执行
	void beforeDeleteBatch(List<D> deleteDto, Token token);

	// 进行数据库更新前操作
	void beforeUpdate(D updateDto);

	// 进行数据库更新前操作
	void beforeUpdateBatch(List<D> updateDto, Token token);

	// 按照主键查询实体后执行
	void beforeQueryByPrimaryKey(D queryDto); 
}
