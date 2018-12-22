package com.mk.eap.common.utils;

import java.util.ArrayList;
import java.util.List;

import com.mk.eap.common.domain.DTO;

public class DtoTreeUtil {
	public static <D extends DTO> List<D> Builder(List<D> dtos, String keyName, String parentKeyName,
			String subDtosFieldName, Object rootKeyValue) {

		List<D> tree = new ArrayList<>();

		dtos.forEach(d -> {
			Object parentKeyValue = d.getFieldValue(parentKeyName);
			if (parentKeyValue == rootKeyValue || parentKeyValue != null && parentKeyValue.equals(rootKeyValue)) {
				tree.add(d);
				List<D> subDtos = Builder(dtos, keyName, parentKeyName, subDtosFieldName, d.getFieldValue(keyName));
				d.setFieldValue(subDtosFieldName, subDtos);
			}
		});

		if (tree.isEmpty()) {
			return null;
		}

		return tree;
	}
}
