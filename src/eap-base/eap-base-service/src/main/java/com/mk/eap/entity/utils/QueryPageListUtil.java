package com.mk.eap.entity.utils;

import java.util.HashMap;

import javax.validation.constraints.Null;

import com.mk.eap.common.domain.DTO;
import com.mk.eap.common.domain.PageObject;
import com.mk.eap.entity.dto.PageQueryDto;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

public class QueryPageListUtil {

	public static <D extends DTO> PageQueryDto<D> queryOne(Class<?> voClazz) {
		PageQueryDto<D> queryDto = new PageQueryDto<D>();
		Example example = new Example(voClazz, true, false);
		queryDto.setExample(example);

		PageObject page = new PageObject();
		page.setCurrentPage(0);
		page.setOffset(0);
		page.setPageSize(1);
		queryDto.setPage(page);

		return queryDto;
	}

	@SuppressWarnings("rawtypes")
	public static void advanceSearch(PageQueryDto<?> queryDto, String[] searcFields, Class<?> voClazz) {
		if (queryDto == null || queryDto.getFilter() == null || queryDto.getExample() != null) {
			return;
		}
		boolean isAdvanceSearch = false;
		HashMap<String, Object> filter = queryDto.getFilter();
		Object search = filter.get("search");
		Object orderBy = filter.get("orderBy");
		Object selectFields = filter.get("selectFields");
		if (search == null && orderBy == null && selectFields == null) {
			return;
		}
		Example example = new Example(voClazz, true, false);
		isAdvanceSearch = (search != null || orderBy != null || selectFields != null);
		if (isAdvanceSearch == false) {
			return;
		}
		queryDto.setExample(example);
		if (orderBy != null) {
			example.setOrderByClause(orderBy.toString());
		}
		if (selectFields != null) {
			example.selectProperties(selectFields.toString().split(","));
		}
		for (String fieldName : searcFields) {
			Criteria criteria = example.createCriteria();
			filter.keySet().forEach(key -> {
				if (key.equals("search") || key.equals("orderBy") || key.equals("selectFields")
						|| key.equals("searchFields")) {
					return;
				}
				Object value = filter.get(key);
				if (value == null) {
					criteria.andIsNull(key);
				} else if (value instanceof Iterable) {
					criteria.andIn(key, (Iterable) value);
				} else {
					criteria.andEqualTo(key, value);
				}
			});
			if (search == null) {
				break;
			} else {
				criteria.andLike(fieldName, "%" + search.toString() + "%");
				example.or(criteria);
			}
		}
	}
}
