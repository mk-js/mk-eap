package com.mk.eap.common.domain;

public class BusinessException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4368873598973127606L; 
	private final String code;
	private Exception innerException = null;
	private Object data;
	private Type type;

	public enum Type {
		error,		//对应于前端的红叉提示
		warning		//对应于前端的黄叹号提示
	}

	public BusinessException(String code, String message) {
		super(message);
		this.code = code;
		this.data = null;
	}

	public BusinessException(String code, String message, Type type) {
		super(message);
		this.code = code;
		this.data = null;
		this.type = type;
	}
	
	public BusinessException(String code,String message, Object data, Exception ex) { 

        super(message);
		this.code = code; 
		this.data = data;
		this.innerException = ex;
	} 
	
	public Exception getInnerException() {
		return innerException;
	}
	public Object getData() {
		return data;
	}
	public BusinessException setData(Object data) {
		this.data = data;
		return this;
	}
	public BusinessException setInnerException(Exception ex) {
		this.innerException = ex;
		return this;
	}
	public String getCode() {
		return code;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public static final BusinessException UnLoginedException = new BusinessException("40100","未登录!");
	public static final BusinessException TokenExapiredException = new BusinessException("40101","登录信息过期!");
	public static final BusinessException ServerVersionChanged = new BusinessException("40102","服务器版本已更新，请重新登录!");
	public static final BusinessException TEMPLATE_NOT_EXIST = new BusinessException("40401","模板不存在!");
	public static final BusinessException TEMPLATE_READ_FAIL = new BusinessException("40402","模板读取错误!");
	public static final BusinessException TEMPLATE_BIND_DATA_FAIL = new BusinessException("40403","模板绑定数据错误!");
}
