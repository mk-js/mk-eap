package com.mk.eap.component.send.impl;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.mk.eap.common.utils.PropertyUtil;
import com.mk.eap.component.send.itf.ISendMailService;

/**
 * 发送邮件的测试程序
 * 
 * @author lwq
 * 
 */
@Component
@Service
public class SendMailServiceImpl implements ISendMailService {
	//SSL常量
	private final static String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	//发件人帐号
	private final static String SMTP_USER = PropertyUtil.getPropertyByKey("EMAIL_SMTP_USER");
	//发件人密码
	private final static String SMTP_PS = PropertyUtil.getPropertyByKey("EMAIL_SMTP_PASSWORD");
	//发件服务器
	private final static String SMTP_HOST = PropertyUtil.getPropertyByKey("EMAIL_SMTP_HOST");
	//发件服务器端口
	private final static String SMTP_PORT = PropertyUtil.getPropertyByKey("EMAIL_SMTP_PORT");
	//图片ip
	private final static String SMTP_IP = PropertyUtil.getPropertyByKey("EMAIL_IMG_IP");
	
	private final static String SIGN_HTML = "<div style='color: rgb(0, 0, 0); font-family: verdana; font-size: 13px; font-style: normal; font-variant: normal; font-weight: normal; letter-spacing: normal; line-height: 22px; orphans: auto; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: auto; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255);'><p class='MsoNormal' style='margin: 0pt 0pt 0.0001pt; font-family: 微软雅黑; line-height: 21px; text-align: justify; font-size: 10.5pt;'><b style='background-color: window; color: rgb(51, 51, 51); font-family: Arial;'><br></b></p><p class='MsoNormal' style='margin: 0pt 0pt 0.0001pt; font-family: Calibri; line-height: 21px; text-align: justify; font-size: 10.5pt;'><span style='font-family: 宋体; font-size: 10.5pt;'>咨询电话：</span><span>173-1916-2004</span></p><p class='MsoNormal' style='margin: 0pt 0pt 0.0001pt; font-family: Calibri; line-height: 21px; text-align: justify; font-size: 10.5pt;'><span style='font-family: 宋体; font-size: 10.5pt;'>北京人人时代科技有限公司</span><span style='font-size: 10.5pt;'></span></p><p class='MsoNormal' style='margin: 0pt 0pt 0.0001pt; font-family: Calibri; line-height: 21px; text-align: justify; font-size: 10.5pt;'><span style='font-family: 宋体; font-size: 10.5pt;'>网址：</span><span style='background-color: window;'>www.</span><span style='font-size: 10.5pt; background-color: window;'>rrtimes.com</span></p><p class='MsoNormal' style='margin: 0pt 0pt 0.0001pt; font-family: Calibri; line-height: 21px; text-align: justify; font-size: 10.5pt;'><span style='font-family: 宋体; font-size: 10.5pt;'>地址：北京市海淀区中关村软件园11号中科大洋大厦308</span><span style='font-size: 10.5pt;'></span></p><p class='MsoNormal' style='margin: 0pt 0pt 0.0001pt; font-family: Calibri; line-height: 21px; text-align: justify; font-size: 10.5pt;'><img><img src='https://exmail.qq.com/cgi-bin/viewfile?type=signature&amp;picid=ZX0807-UpKFuJadJYoCAipz09CEC7k&amp;uin=682569408'></p></div><div style='font-style: normal; font-variant: normal; font-weight: normal; letter-spacing: normal; line-height: 22px; orphans: auto; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: auto; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255); font-size: 14px; font-family: Arial; color: rgb(51, 51, 51);'><span><p class='MsoNormal' style='margin: 0cm 0cm 0.0001pt 7.5pt; color: rgb(0, 0, 0); font-family: 'Times New Roman', serif; text-align: justify; line-height: normal;'><span style='font-size: 7.5pt; font-family: 微软雅黑, sans-serif; color: gray;'>&nbsp; &nbsp;本邮件及其附件可能含有相关商业保密信息，仅限于发送给上面地址中列出的个人或群组。</span><span style='color: gray; font-family: 微软雅黑, sans-serif; font-size: 7.5pt; background-color: window;'>禁止任何其他人以任何形式使用本邮件中的信息。如果您错收了本邮件，请您立即电</span></p><p class='MsoNormal' style='margin: 0cm 0cm 0.0001pt 7.5pt; color: rgb(0, 0, 0); font-family: 'Times New Roman', serif; text-align: justify; line-height: normal;'><span style='font-size: 7.5pt; font-family: 微软雅黑, sans-serif; color: gray;'>话或邮件通知发件人并删除本邮件！</span></p></span></div>";
	
	
	
	
	/**
	 * 发送邮件
	 * @param acount 邮箱账号
	 * @param subject 邮件标题
	 * @param content  邮件内容
	 * @param useSign  是否使用签名
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException 
	 */
	@Override
	public void send(String acount, String subject, String content, boolean useSign) throws MessagingException, UnsupportedEncodingException{
		// 配置发送邮件的环境属性
		final Properties props = new Properties();
		// 表示SMTP发送邮件，需要进行身份验证
		props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", SMTP_HOST);
		props.put("mail.smtp.port", SMTP_PORT);
		props.put("mail.smtp.socketFactory.port", SMTP_PORT);
		// 发件人的账号
		props.put("mail.user", SMTP_USER);
		// 访问SMTP服务时需要提供的密码
		props.put("mail.password", SMTP_PS);

		// 构建授权信息，用于进行SMTP进行身份验证
		Authenticator authenticator = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// 用户名、密码
				String userName = SMTP_USER;
				String password = SMTP_PS;
				return new PasswordAuthentication(userName, password);
			}
		};
		// 使用环境属性和授权信息，创建邮件会话
		Session mailSession = Session.getInstance(props, authenticator);
		// 创建邮件消息
		MimeMessage message = new MimeMessage(mailSession);
		// 设置发件人
		message.setFrom(new InternetAddress(SMTP_USER, "易嘉财税", "UTF-8"));

		// 设置收件人
		message.setRecipient(RecipientType.TO, new InternetAddress(acount));

		// 设置邮件标题
		message.setSubject(subject);
		
		//判断是否使用签名 
		if(useSign){
			content += SIGN_HTML;
		}
		
		// 设置邮件的内容体
		message.setContent(content, "text/html;charset=UTF-8");

		// 发送邮件
		Transport.send(message);
	}
	
	public static void main(String[] args){
		try {
			new SendMailServiceImpl().send("zl@rrtimes.com", "测试", "这是一个测试", true);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
