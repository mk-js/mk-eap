package com.mk.eap.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.StringUtils;



public class StringUtil {
	
	public static final Logger logger = LoggerFactory.getLogger(StringUtil.class);

	/**
	 * 校验当前传递时间和系统当前时间是否为同一天
	 * 
	 * @Return boolean: true 同一天 false 不同的天
	 * 
	 */
	public static boolean checkTimeIsSameDay(Date date) {
		boolean flag = true;
		String ckDate = getFormatDate(date);
		String currentDate = getFormatDate(new Date());
		if (ckDate.length() > 10 && currentDate.length() > 10) {
			// 如果年月日不相等则表明不为同一天返回 false
			if (!ckDate.substring(0, 10).equals(currentDate.substring(0, 10))) {
				flag = false;
			}
		} else {
			flag = false;
		}
		return flag;
	}
	/**
	 * 校验两个日期是否是同年同月
	 * @param date1
	 * @param date2
	 * @return true :是同年同月
	 */
	public static boolean checkIsSameMonth(Date date1,Date date2){
		Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
		return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) && 
				calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH);
		
	}

	/**
	 * 日期信息转换 yyyy-MM-dd hh:mm:ss
	 * 
	 */
	public static String getFormatDate(Date standardDate) {
		String tmp = "";
		try {
			if (standardDate != null) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				tmp = dateFormat.format(standardDate);
			}
		} catch (Exception ex) {
			logger.error("getFormatDate报错：", ex);
		}
		return tmp;
	}

	/**
	 * 日期信息转换 yyyy-MM-dd
	 * 
	 */
	public static String getFormatDateS(Date standardDate) {
		String tmp = "";
		try {
			if (standardDate != null) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				tmp = dateFormat.format(standardDate);
			}
		} catch (Exception ex) {
			logger.error("getFormatDateS报错：", ex);
		}
		return tmp;
	}

	/**
	 * 日期信息转换 yyyyMM
	 * 
	 */
	public static String getFormatDate_yyyymm(Date standardDate) {
		String tmp = "";
		try {
			if (standardDate != null) {
				DateFormat dateFormat = new SimpleDateFormat("yyyyMM");
				tmp = dateFormat.format(standardDate);
			}
		} catch (Exception ex) {
			logger.error("getFormatDate_yyyymm报错：", ex);
		}
		return tmp;
	}

	/**
	 * 字符串信息转日期 yyyy-MM-dd
	 */
	public static Date getDate(String str, DateType dateType) {
		Date tmp = new Date();
		try{
			if(!str.isEmpty()){
				SimpleDateFormat dateFormat;
				if(dateType == DateType.yyyyMMddHHmmss){
					dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				} else if(dateType == DateType.yyyyMMdd){
					dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				} else if(dateType == DateType.yyyyMM){
					dateFormat = new SimpleDateFormat("yyyy-MM");
				} else {
					dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				}
				tmp=dateFormat.parse(str);
			}
		}catch(Exception ex){
			logger.error("getDate报错：", ex);
		}
		return tmp;
	}
	
	public static String getDateFormatStr(Date date, DateType dateType){
		String str = "";
		try{
			if(date != null){
				SimpleDateFormat dateFormat;
				if(dateType == DateType.yyyyMMddHHmmss){
					dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				} else if(dateType == DateType.yyyyMMdd){
					dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				} else if(dateType == DateType.yyyyMM){
					dateFormat = new SimpleDateFormat("yyyy-MM");
				} else if(dateType == DateType.YYYYMMDDHHMMSS){
					dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				} else {
					dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				}
				str = dateFormat.format(date);
			}
		}catch(Exception ex){
			logger.error("getDateFormatStr报错：", ex);
		}
		return str;
	}
	
	/**
	 * 获取某月的最后一天
	 * @param year
	 * @param month（1-12）
	 * @return
	 */
	public static Date getLastDayOfMonth(int year,int month){
		Date tmp = new Date();
		try{
	        Calendar calendar = Calendar.getInstance();
	        calendar.set(year, month-1, 1);
	        calendar.add(Calendar.MONTH, 1);    
	        calendar.set(Calendar.DATE, 1);       
	        calendar.add(Calendar.DATE, -1);   
	        tmp = calendar.getTime();
		}catch(Exception ex){
			logger.error("getLastDayOfMonth报错：", ex);
		}
		return tmp;
	  }

	/**
	 * 获取某月的第一天
	 * @param year
	 * @param month（1-12）
	 * @return
	 */
	public static Date getFirstDayOfMonth(int year,int month){
		Date tmp = new Date();
		try{
	        Calendar calendar = Calendar.getInstance();
	        calendar.set(year, month-1, 1);     
	        tmp = calendar.getTime();
		}catch(Exception ex){
			logger.error("getLastDayOfMonth报错：", ex);
		}
		return tmp;
	  }
	
	/**
	 * @title 字符串格式类型 
	 * 	yyyyMMddHHmmss="yyyy-MM-dd HH:mm:ss" 
	 * 	yyyyMMdd="yyyy-MM-ss"
	 * @author wangyi
	 *
	 */
	public enum DateType{
		yyyyMMddHHmmss,
		yyyyMMdd,
		yyyyMM,
		YYYYMMDDHHMMSS
	}

	/**
	 * 日期 转字符串 yyyy-MM-dd
	 * @throws ParseException 
	 * 
	 */
	public static String getStringFromDate(Date date) throws ParseException
	{  
		String tmp = null;
		if( date != null )
		{
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				tmp = dateFormat.format(date);
		}
		return tmp;
	}
	
	/**
	 * 日期 转字符串 yyyy年MM月dd日
	 * @throws ParseException 
	 * 
	 */
	public static String getChinaDateString(Date date) throws ParseException
	{  
		String tmp = null;
		if( date != null )
		{
				DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
				tmp = dateFormat.format(date);
		}
		return tmp;
	}
	
	/**
	 * 字符串转日期  yyyy-MM-dd
	 * @throws ParseException 
	 * 
	 */
	public static Date getDateFromString1(String date) throws ParseException
	{  
		Date tmp = null;
		if( date != null )
		{
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				tmp = dateFormat.parse(date);
		}
		return tmp;
	}
	
	/**
	 * 日期信息转换  yyyy/MM/dd
	 * @throws ParseException 
	 * 
	 */
	public static Date getDateFromString2(String date) throws ParseException
	{  
		Date tmp = null;
		if( date != null )
		{
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
				tmp = dateFormat.parse(date);
		}
		return tmp;
	}
	public static Date getDateFromStringAndYear(String date,int year) throws ParseException{
		Calendar cal = Calendar.getInstance();
		Date tmp = null;
		if( date != null )
		{
			    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		        try {
					tmp = dateFormat.parse(date);
				} catch (ParseException e4) {
					dateFormat = new SimpleDateFormat("MM-dd");
					try {
						tmp = dateFormat.parse(date);
					} catch (ParseException e) {
						dateFormat = new SimpleDateFormat("MMdd");
						try {
							tmp = dateFormat.parse(date);
						} catch (ParseException e1) {
							dateFormat = new SimpleDateFormat("yyyy/MM/dd");
							try {
								tmp = dateFormat.parse(date);
							} catch (ParseException e2) {
								dateFormat = new SimpleDateFormat("MM/dd");
								try {
									tmp = dateFormat.parse(date);
								} catch (ParseException e3) {
									dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
									dateFormat = new SimpleDateFormat("MM.dd");
									try {
										tmp = dateFormat.parse(date);
									} catch (ParseException e5) {
										dateFormat = new SimpleDateFormat("MM月dd日");
										try {
											tmp = dateFormat.parse(date);
										} catch (ParseException e6) {
											dateFormat = new SimpleDateFormat("yyyy.MM.dd");
											try {
												tmp = dateFormat.parse(date);
											} catch (ParseException e7) {
												dateFormat = new SimpleDateFormat("MM.dd");
												tmp = dateFormat.parse(date);
											}
										}
									}
								}
							}
							
						}
					}
				}
		}
		if(tmp != null){
			 Calendar cal1 = Calendar.getInstance();
			 cal1.setTime(tmp);
			cal.set(year,cal1.get(Calendar.MONTH), cal1.get(Calendar.DATE));
			return cal.getTime();
		}
		return tmp;
	}
	/**
	 * 日期信息转换  yyyy/MM/dd
	 * @throws ParseException 
	 * 
	 */
	public static Date getDateFromString(String date) throws ParseException
	{  
		Date tmp = null;
		if( date != null )
		{
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				try {
					tmp = dateFormat.parse(date);
				} catch (ParseException e) {
					dateFormat = new SimpleDateFormat("yyyyMMdd");
					try {
						tmp = dateFormat.parse(date);
					} catch (ParseException e1) {
						dateFormat = new SimpleDateFormat("yyyy/MM/dd");
						try {
							tmp = dateFormat.parse(date);
						} catch (ParseException e2) {
							dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
							try {
								tmp = dateFormat.parse(date);
							} catch (ParseException e3) {
								dateFormat = new SimpleDateFormat("yyyy.MM.dd");
								tmp = dateFormat.parse(date);
							}
						}
						
					}
				}
		}
		return tmp;
	}
	 
	/**
	 * 日期信息转换  yyyyMM
	 * 
	 */
	public static String getFormatDateAsyyyymm(Date standardDate)
	{  
		String tmp = "";
		if( standardDate != null )
		{
				DateFormat dateFormat = new SimpleDateFormat("yyyyMM");
				tmp = dateFormat.format(standardDate);
		}
		return tmp;
	}
	
	/**
	 * 日期信息转换  yyyyMM
	 * 
	 */
	public static String getFormatDateAsyyyymmdd(Date standardDate)
	{  
		String tmp = "";
		if( standardDate != null )
		{
				DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
				tmp = dateFormat.format(standardDate);
		}
		return tmp;
	}
	
	/**
	 * 判断字符串是否为空或者空串
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmtryStr(String str) {
		if (str == null || "null".equals(str) || "".equals(str)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 判断字符串是否为空或者空串
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null || "null".equals(str) || "".equals(str.trim())) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 判断两个字符串是否一致 null按""处理
	 * 
	 * @param str
	 * @return
	 */
	public static boolean compare(String str1, String str2) {
		str1 = null == str1?"":str1;
		str2 = null == str2?"":str2;
		return str1.equals(str2);
	}

	/**
	 * 判断List是否为空
	 * 
	 * @param list
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isListNotNull(List list) {
		if (list.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 将ID数组转换成字符串
	 * 
	 * @param ids
	 * @return
	 */
	public static String arrayToString(String[] ids) {
		StringBuffer strBuff = new StringBuffer();
		try {
			for (int i = 0; i < ids.length; i++) {
				if (i == ids.length - 1) {
					strBuff.append(ids[i]);
				} else {
					strBuff.append(ids[i]).append(",");
				}
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
		return strBuff.toString();
	}

	/**
	 * 将数组转换为list
	 * 
	 * @param ids
	 * @return
	 */
	public static List<Integer> arrayToList(String[] ids) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < ids.length; i++) {
			list.add(Integer.valueOf(ids[i]));
		}
		return list;
	}

	/**
	 * 获取随机字母数字组合
	 * 
	 * @param length
	 *            字符串长度
	 * @return
	 */
	public static String getRandomCharAndNumr(Integer length) {
		String str = "";
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			boolean b = random.nextBoolean();
			if (b) { // 字符串
				str += randomCharAndNum(random);
			} else { // 数字
				str += String.valueOf(1 + random.nextInt(9));
			}
		}
		return str;
	}

	private static String randomCharAndNum(Random random) {
		// int choice = random.nextBoolean() ? 65 : 97; 取得65大写字母还是97小写字母
		String s = ((char) (65 + random.nextInt(26))) + "";// 取得大写字母
		if (s.equals("O") || s.equals("I") || s.equals("Z")) {
			return randomCharAndNum(random);
		} else {
			return s;
		}
	}

	/**
	 * 获取随机大写字母组合
	 * 
	 * @param length
	 *            字符串长度
	 * @return
	 */
	public static String getRandomChar(Integer length) {
		String str = "";
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			str += ((char) (65 + random.nextInt(26))) + "";// 取得大写字母
		}
		return str;
	}

	/**
	 * 获取字符串分隔成数组后的某一个值
	 * 
	 * @param length
	 *            字符串长度
	 * @return
	 */
	public static String getSplitItem(String str,String split,String key) { 
		String item = null;
		String[] items = str.split(split);
		for(int i=0,size = items.length;i<size;i++){
			if(items[i].indexOf(key)==0){
				item = items[i]; 
				break;
			}
		}
		return item;
	}

	/** 
	 * 获取随机数字组合 
	 *  
	 * @param length 
	 *            字符串长度 
	 * @return 
	 */  
	public static String getRandomNumr(Integer length) {  
	    String str = "";  
	    Random random = new Random();  
	    for (int i = 0; i < length; i++) {  
	    	str += String.valueOf(random.nextInt(10));  
	    }  
	    return str;  
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
