package com.mk.eap.common.domain;

import java.io.Serializable;
import java.util.List;

import com.mk.eap.common.domain.ParameterDto;

public class MethodDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 834381071350626830L;
	public String name;
	public List<ParameterDto> parameters;
	public ParameterDto returnType;
}
