package com.mk.eap.common.utils;

public class VersionUtil {

	private static String Version = null; 
	private static String VersionType = null; 
	private static Long VersionNumber = null; 
	
	public static String getVersion() {
		if(Version==null){
			Version = PropertyUtil.getPropertyByKey("version", "version.properties");
		}
		return Version;
	} 	
	public static String getVersionType() {
		if(VersionType==null){
			VersionType = PropertyUtil.getPropertyByKey("versionType", "version.properties");
		}
		return VersionType;
	} 
	/*
	 * 0.1.9 => 100109
	 */
	public static Long getVersionLong() { 
		if(VersionNumber != null)return VersionNumber;
		String version = getVersion(); 
		if(!StringUtil.isEmtryStr(version)){
			version = "1" + version.replace('.', '0').replace(":","").replace("-", "").replace(" ", ""); 
			try{
				VersionNumber = Long.parseLong(version);
			}catch(Exception ex){
				VersionNumber = -1L;
			}
		} 
		return VersionNumber;
	}
}
