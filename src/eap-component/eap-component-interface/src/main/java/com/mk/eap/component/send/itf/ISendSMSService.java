package com.mk.eap.component.send.itf;

import java.util.Map;

/**
 * <p>发送短信接口</p>
 * @author zl
 * @version 1.0
 * 
 */
public interface ISendSMSService {

	/**
	 * 发送短信
	 * @param mobile 手机号码
	 * @param signName 签名名称(null时默认为'易嘉人')
	 * @param messageCode 短信编号
	 * @param param 参数
	 * @return
	 * @throws ApiException 
	 */
	public void send(String mobile, String signName, String messageCode, Map<String, String> paramMap);
}
