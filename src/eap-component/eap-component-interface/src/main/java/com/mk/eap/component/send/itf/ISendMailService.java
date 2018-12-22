package com.mk.eap.component.send.itf;

import java.io.UnsupportedEncodingException;
import javax.mail.MessagingException;

/**
 * <p>发送邮件接口</p>
 * @author zl
 * @version 1.0
 * 
 */
public interface ISendMailService {

	
	/**
	 * 发送邮件
	 * @param acount 邮箱账号
	 * @param subject 邮件标题
	 * @param content  邮件内容
	 * @param useSign  是否使用签名
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException 
	 */
	public void send(String acount, String subject, String content, boolean useSign) 
			throws MessagingException, UnsupportedEncodingException;
}
