package com.mk.eap.common.domain;

import java.io.Serializable;

public class FileDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7793933119841347576L;
 
	private String name;
	private String contentType;
	private Boolean isOpen = false;
	private byte[] content;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public Boolean getIsOpen() {
		return isOpen;
	}
	public void setIsOpen(Boolean isOpen) {
		this.isOpen = isOpen;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
