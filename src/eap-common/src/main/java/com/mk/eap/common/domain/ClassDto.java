package com.mk.eap.common.domain;

import java.io.Serializable;
import java.util.Map;

import com.mk.eap.common.domain.InterfaceDto;

public class ClassDto extends InterfaceDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2447815578110650625L;
	
	public String apiContext;

	public Map<String, String> fields;

	public Object instance;
}