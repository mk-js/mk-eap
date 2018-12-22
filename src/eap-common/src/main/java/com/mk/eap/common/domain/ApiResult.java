package com.mk.eap.common.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class ApiResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3081479644284585061L;

	public interface setValue {
		public Object getValue(ApiResult apiResult);
	}

	public static JSONObject json(setValue result) {
		ApiResult apiResult = new ApiResult();
		try {
			apiResult.setResult(true);
			Object value = result.getValue(apiResult);
			apiResult.setValue(value);
		} catch (Exception ex) {
			apiResult.setError(ex);
		}
		return new JSONObject(apiResult.getResult());
	}

	public static HashMap<String, Object> map(setValue result) {
		ApiResult apiResult = new ApiResult();
		try {
			apiResult.setResult(true);
			Object value = result.getValue(apiResult);
			apiResult.setValue(value);
		} catch (Exception ex) {
			apiResult.setError(ex);
		}
		// 返回结果对象
		return apiResult.getResult();
	}

	// 定义返回MAP
	private HashMap<String, Object> resultMap = new HashMap<String, Object>();
	// 定义token
	private String token = "";

	private final static Logger logger = LoggerFactory.getLogger(ApiResult.class);

	/*
	 * 设置返回结果
	 */
	public void setResult(boolean result) {
		resultMap.put("result", result);
	}

	/*
	 * 设置返回结果
	 */
	public void setToken(String token) {
		this.token = token;
		resultMap.put("token", token);
	}

	/*
	 * 设置返回结果
	 */
	public String getToken() {
		if (this.token == null || this.token.equals("")) {
			Object token = resultMap.get("token");
			if (token != null) {
				this.token = token.toString();
			} else {
				this.token = null;
			}
		}
		return this.token;
	}

	/**
	 * 设置错误返回对象
	 * 
	 * @param ex
	 *            服务抛出的异常
	 */
	public void setError(BusinessException businessException) {

		logger.error("调用出异常:", businessException);

		this.setResult(false);

		Map<String, Object> errMap = new HashMap<String, Object>();

		errMap.put("code", businessException.getCode());

		errMap.put("message", businessException.getMessage());

		if (businessException.getType() != null) {
			errMap.put("type", businessException.getType().name());
		}

		Exception ex = businessException.getInnerException();

		if (ex != null) {
			errMap.put("exception", ex);
		}

		Object data = businessException.getData();

		if (data != null) {
			errMap.put("data", businessException.getData());
		}

		resultMap.put("error", errMap);
	}

	/**
	 * 设置错误返回对象
	 * 
	 * @param code
	 *            错误码
	 * @param message
	 *            错误消息
	 */
	public void setError(String code, String message) {
		this.setResult(false);
		Map<String, Object> errMap = new HashMap<String, Object>();
		errMap.put("code", code);
		errMap.put("message", message);
		resultMap.put("error", errMap);
	}

	/**
	 * 设置错误返回对象
	 * 
	 * @param code
	 *            错误码
	 * @param message
	 *            错误消息
	 * @param ex
	 *            具体异常信息
	 */
	public void setError(String code, String message, Exception ex) {
		logger.error("调用出异常:", ex);
		this.setResult(false);
		Map<String, Object> errMap = new HashMap<String, Object>();
		errMap.put("code", code);
		errMap.put("message", message);
		errMap.put("exception", ex);
		resultMap.put("error", errMap);
	}

	/**
	 * 设置错误返回对象
	 * 
	 * @param code
	 *            错误码
	 * @param message
	 *            错误消息
	 * @param ex
	 *            具体异常信息
	 */
	public void setError(Exception ex) {
		if (ex instanceof BusinessException) {// 兼容BusinessException
			BusinessException bex = (BusinessException) ex;
			setError(bex);
			return;
		}
		logger.error("调用出异常:", ex);
		this.setResult(false);
		Map<String, Object> errMap = new HashMap<String, Object>();
		errMap.put("code", "50000");
//		errMap.put("message", "服务器异常"); // 去掉未知异常的提示信息。在exception的localizedMessage中查看详细信息。
		// 优化系统友好提示
		errMap.put("message", "~哎呀，操作发生错误了，您可以稍后重试哦!");
		errMap.put("ex", ex);
//		errMap.put("exception", ex);
		resultMap.put("error", errMap);
	}

	/**
	 * 设置错误返回对象
	 * 
	 * @param code
	 *            错误码
	 * @param message
	 *            错误消息
	 * @param value
	 *            具体原因
	 */
	public void setError(String code, String message, Exception ex, Object value) {
		logger.error("调用出异常:", ex);
		this.setResult(false);
		Map<String, Object> errMap = new HashMap<String, Object>();
		errMap.put("code", code);
		errMap.put("message", message);
		errMap.put("exception", ex);
		errMap.put("value", value);
		resultMap.put("error", errMap);
	}

	/**
	 * 设置正确返回对象值
	 * 
	 * @param value
	 */
	public void setValue(Object value) {
		resultMap.put("value", value);
	}

	/**
	 * 返回前台对象
	 * 
	 * @return
	 */
	public HashMap<String, Object> getResult() {
		return resultMap;
	}
}
