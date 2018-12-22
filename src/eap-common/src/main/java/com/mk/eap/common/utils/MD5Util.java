/*      						
 * Copyright 2016 Beijing RRTM, Inc. All rights reserved.
 * 
 * History:
 * ------------------------------------------------------------------------------
 * Date    			|  		Who  			|  		What  
 * 2016-05-28		| 	 lihaitao 			| 	create the file                       
 */

package com.mk.eap.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * 数据库密码加密算法类( MD5 加密算法 )
 * 
 * <p>
 * 	数据库密码加密算法
 * </p> 
 * 
 * @author ZL
 * 
 */
public final class MD5Util {
	//定义格式化字符
	private static final char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
	
	private static final String defaultKey = "yiJia9*";
	
	/**
	 * 加密字符串
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	public static String getMD5Str(String strTemp) throws NoSuchAlgorithmException{
		return getMD5Str(strTemp,defaultKey);
	}
	public static String getMD5Str(String strTemp,String privateKey) throws NoSuchAlgorithmException{
		
		String key = privateKey;
		if(key==null){
			key="";
		}
		MessageDigest mdInst = MessageDigest.getInstance("MD5");
		// 使用指定的字节更新摘要
        mdInst.update((strTemp+key).getBytes());
        // 获得密文
        byte[] md = mdInst.digest();
        // 把密文转换成十六进制的字符串形式
        int j = md.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = md[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        
        return String.valueOf(str);
	}
	
	public static void main(String args[]) throws NoSuchAlgorithmException{
		System.out.println(MD5Util.getMD5Str("94f6d7e04a4d452035300f18b984988c",""));
	}

}
