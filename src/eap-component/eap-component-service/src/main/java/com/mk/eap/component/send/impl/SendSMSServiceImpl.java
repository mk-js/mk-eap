package com.mk.eap.component.send.impl;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.mk.eap.common.utils.PropertyUtil;
import com.mk.eap.component.send.itf.ISendSMSService;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

@Component
@Service
public class SendSMSServiceImpl implements ISendSMSService {
	public static void main(String[] args) {
	}

	private final static Logger logger = LoggerFactory.getLogger(SendSMSServiceImpl.class);
	
	private final static String TAO_URL = PropertyUtil.getPropertyByKey("SMS_URL");
	private final static String APP_ID = PropertyUtil.getPropertyByKey("SMS_APP_ID");
	private final static String KEY = PropertyUtil.getPropertyByKey("SMS_KEY");
	private final static String NORMAL = "normal";
	public final static String SIGN_YJ = PropertyUtil.getPropertyByKey("SMS_DEFAULT_SIGN_NAME");
	private final static String MOBILE_REG_EXP = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
	
	/**
	 * 发送短信
	 * @param mobile 手机号码
	 * @param signName 签名名称
	 * @param messageCode 短信编号
	 * @param param 参数
	 * @return
	 * @throws ApiException 
	 */
	@Override
	public void send(String mobile, String signName, String messageCode, Map<String, String> paramMap) {
		TaobaoClient client = new DefaultTaobaoClient(TAO_URL, APP_ID, KEY);
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setSmsType(NORMAL);
		req.setSmsFreeSignName(signName == null ? SIGN_YJ : signName);
		//判断并拼接参数
		if(null != paramMap && paramMap.size() > 0){
			req.setSmsParamString(JSONObject.toJSON(paramMap).toString());
		}
		req.setRecNum(mobile);
		req.setSmsTemplateCode(messageCode);
		AlibabaAliqinFcSmsNumSendResponse rsp = null;
		try {
			rsp = client.execute(req);
		} catch (ApiException e) {
			//发生异常
			logger.error("短信发送发生异常：短信编码：" + messageCode + ",参数信息："+JSONObject.toJSONString(paramMap) 
				+ ",异常信息：" + e.getErrCode() + ":" + e.getErrMsg());
		}
		logger.info("短信发送返回值："+rsp.getBody());
	}
	
	/** 
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数 
     * 此方法中前三位格式有： 
     * 13+任意数 
     * 15+除4的任意数 
     * 18+除1和4的任意数 
     * 17+除9的任意数 
     * 147 
     */  
    public boolean isChinaPhoneLegal(String str) {
        Pattern p = Pattern.compile(MOBILE_REG_EXP);  
        Matcher m = p.matcher(str);  
        return m.matches();  
    }  
}
