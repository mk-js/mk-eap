package com.mk.eap.entity.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import com.mk.eap.common.domain.PageObject;
import com.mk.eap.entity.dto.PageResultDto;
import com.mk.eap.entity.itf.IPageService;

public class EntityCodeUtil {

	public static String genCode(String pre, IPageService<?> service) {
		String code = pre.toUpperCase();

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime ldt = LocalDateTime.now();
		code = code + ldt.format(dtf);

		HashMap<String, Object> filter = new HashMap<>();
		DateTimeFormatter dtfToday = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		HashMap<String, Object> createTimeBetween = new HashMap<>();
		createTimeBetween.put("$gte", ldt.format(dtfToday) + " 00:00:00");
		createTimeBetween.put("$lte", ldt.format(dtfToday) + " 23:59:59");
		filter.put("createTime", createTimeBetween);
		filter.put("selectFields", "id");
		filter.put("searchFields", new String[0]);
		PageObject pagination = new PageObject();
		pagination.setPageSize(1);
		PageResultDto<?> result = service.queryPageList(filter, pagination);
		int count = result.getPage().getSumCloum() + 1;

		code = code + String.format("%05d", count);
		return code;
	}
}
