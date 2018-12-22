package com.mk.eap.entity.dto;

import java.util.HashMap;

import com.mk.eap.common.domain.DTO;
import com.mk.eap.common.domain.PageObject;

import tk.mybatis.mapper.entity.Example;

/**
 * 分页查询条件基类
 * 
 * @author gaoxue
 * 
 * @param <D>
 *            dto 实体类型
 */
public class PageQueryDto<D extends DTO> extends DTO {

	private static final long serialVersionUID = -8609194488567733278L;

	/** 分页条件 */
	private PageObject pagination;

	/** 简单实体条件 */
	private D entity;

	/** 自定义条件 */
	private HashMap<String, Object> filter;

	/** 简单实体条件 */
	private Example example;
	
	private String[] searchFields;

	/**
	 * 获取简单实体条件
	 * 
	 * @return 简单实体条件
	 */
	public D getEntity() {
		return entity;
	}

	/**
	 * 设置简单实体条件
	 * 
	 * @param entity
	 *            简单实体条件
	 */
	public void setEntity(D entity) {
		this.entity = entity;
	}

	/**
	 * 获取分页条件
	 * 
	 * @return 分页条件
	 */
	public PageObject getPage() {
		return pagination;
	}

	/**
	 * 设置分页条件
	 * 
	 * @param page
	 *            分页条件
	 */
	public void setPage(PageObject page) {
		this.pagination = page;
	}

	public Example getExample() {
		return example;
	}

	public void setExample(Example example) {
		this.example = example;
	}

	/**
	 * @return 自定义条件  保留字：search,orderBy,selectFields
	 */
	public HashMap<String, Object> getFilter() {
		return filter;
	}

	/**
	 * @param 自定义条件
	 */
	public void setFilter(HashMap<String, Object> filter) {
		this.filter = filter;
	}

	public String[] getSearchFields() {
		return searchFields;
	}

	public void setSearchFields(String[] searchFields) {
		this.searchFields = searchFields;
	}

}
