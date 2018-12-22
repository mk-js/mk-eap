package com.mk.eap.common.domain;

import java.io.Serializable;
import java.util.Map;

public class Context extends Token implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1019895023777043336L;
	Token token;
	Map<String,String> query;
	Map<String,String> headers;
	String accessIP;
	String path;
	
	public String getAccessIP() {
		return accessIP;
	}
	public void setAccessIP(String accessIP) {
		this.accessIP = accessIP;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	public Token getToken() {
		return token;
	}
	public void setToken(Token token) {
		this.token = token;
	}
	public Map<String, String> getQuery() {
		return query;
	}
	public void setQuery(Map<String, String> query) {
		this.query = query;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
}
