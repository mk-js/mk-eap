package com.mk.eap.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.mk.eap.common.domain.ApiResult;
import com.mk.eap.common.domain.BusinessException;
import com.mk.eap.common.utils.StringUtil;
import com.mk.eap.common.utils.VersionUtil;

public class AuthorizedController {

	public Long getUserId() {
		return (Long) request.getSession().getAttribute("userId");
	}

	public Long getOrgId() {
		return (Long) request.getSession().getAttribute("orgId");
	}

	public Long getAppId() {
		return (Long) request.getSession().getAttribute("appId");
	}

	public Long getExtId() {
		return (Long) request.getSession().getAttribute("extId");
	}

	/***
	 * 获取上传的文件名。
	 * 以当前的企业ID做为目录名，随机生成文件名。
	 * @param extName 扩展名
	 * @return
	 */
	public String getRandomFileName(String extName) {
		String fileName = extName;
		if (this.getOrgId() != null) {
			fileName = this.getOrgId().toString() + File.separator + "${objectId}" + extName;
		}
		return fileName;
	}

	/*
	 * 以当前时间刷新token
	 */
	public String getNewTokenString() {
		String token = StringTokenizer.Default.getToken(this.getUserId(), this.getOrgId(), VersionUtil.getVersionLong(),
				this.getAppId());
		return token;
	}

	/*
	 * 5分钟过期的临时token
	 */
	public String getTempTokenString() {
		String token = StringTokenizer.Temp.getToken(this.getUserId(), this.getOrgId(), VersionUtil.getVersionLong(),
				this.getAppId());
		return token;
	}

	public void setOrg(Long orgId, Long appId, ApiResult result) {
		if (result != null) {
			Long userId = this.getUserId();
			String token = StringTokenizer.Default.getToken(userId, orgId, VersionUtil.getVersionLong(), appId);
			result.setToken(token);
		}
	}

	public String getAccessIP() {
		return getIpAddr(request);
	}

	/**
	 * 获取当前服务的域名
	 * 
	 * @return
	 */
	public String getHost() {
		String host = request.getHeader("Host");
		if (!StringUtil.isEmtryStr(host) && !"unknown".equalsIgnoreCase(host)) {
			return host;
		}

		return request.getServerName();
	}

	/**
	 * 转发请求时,需要转发的header字段
	 * 
	 * @return
	 */
	public Map<String, String> getRequestHeaders() {
		Map<String, String> headers = new HashMap<>();
		if (request.getHeader("token") != null) {
			headers.put("token", request.getHeader("token"));
		}
		if (request.getHeader("content-type") != null) {
			headers.put("content-type", request.getHeader("content-type"));
		}
		return headers;
	}

	@Autowired
	private HttpServletRequest request;

	public HttpServletRequest getHttpServletRequest() {
		return this.request;
	}

	/**
	 * 获取访问者IP
	 * 
	 * 在一般情况下使用Request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效。
	 * 
	 * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)，
	 * 如果还不存在则调用Request .getRemoteAddr()。
	 * 
	 * @param request
	 * @returnA
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		if (!StringUtil.isEmtryStr(ip) && !"unknown".equalsIgnoreCase(ip)) {
			return ip;
		}
		ip = request.getHeader("X-Forwarded-For");
		if (!StringUtil.isEmtryStr(ip) && !"unknown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个IP值，第一个为真实IP。
			int index = ip.indexOf(',');
			if (index != -1) {
				return ip.substring(0, index);
			} else {
				return ip;
			}
		} else {
			return request.getRemoteAddr();
		}
	}

	public static ApiResult handleStreamInfo(FileInputStream fileInputStream, String filename, boolean isOpen,
			HttpServletResponse response) {

		ApiResult result = new ApiResult();
		BufferedInputStream bInputStream = null;
		BufferedOutputStream bOutputStream = null;
		try {
			response.reset();
			response.setContentType("application/pdf");
			filename = URLEncoder.encode(filename, "UTF-8");

			ServletOutputStream servletOutputStream = response.getOutputStream();
			InputStream inputStream = new BufferedInputStream(fileInputStream);
			bInputStream = new BufferedInputStream(inputStream);
			bOutputStream = new BufferedOutputStream(servletOutputStream);
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bInputStream.read(buff, 0, buff.length))) {
				bOutputStream.write(buff, 0, bytesRead);
			}
			bOutputStream.flush();
			bOutputStream.close();
			bInputStream.close();
			result.setResult(true);
			result.setValue(true);
		} catch (BusinessException ex) {
			result.setError(ex);
		} catch (Exception ex) {
			result.setError(ex);
		} finally {
			if (bOutputStream != null) {
				try {
					bOutputStream.close();
				} catch (IOException ex) {
					result.setError(ex);
				}
			}
		}
		return result;
	}

	/**
	 * 打开或者下载对应文件
	 * 
	 * @param fileContent
	 *            pdf或者其他类型的返回的字节数组
	 * @param filename
	 *            打开或者下载的全名（注意：包含扩展名 如.pdf）
	 * @param isOpen
	 *            是否嵌入浏览器打开
	 * @param response
	 *            webapi的参数
	 * @return
	 */
	public ApiResult handleStreamInfo(byte[] fileContent, String filename, boolean isOpen,
			HttpServletResponse response) {
		ApiResult result = new ApiResult();
		BufferedInputStream bInputStream = null;
		BufferedOutputStream bOutputStream = null;
		try {
			response.reset();
			response.setContentType("application/pdf");
			filename = URLEncoder.encode(filename, "UTF-8");
			if (!isOpen) {
				String contentDisposition = String.format("attachment;filename=\"%s\";filename*=utf-8''%s", filename,
						filename);
				response.setHeader("Content-Disposition", contentDisposition);
			} else {
				String contentDisposition = String.format("inline;filename=\"%s\";filename*=utf-8''%s", filename,
						filename);
				response.setHeader("Content-Disposition", contentDisposition);
			}
			ServletOutputStream servletOutputStream = response.getOutputStream();
			InputStream inputStream = new ByteArrayInputStream(fileContent);
			bInputStream = new BufferedInputStream(inputStream);
			bOutputStream = new BufferedOutputStream(servletOutputStream);
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bInputStream.read(buff, 0, buff.length))) {
				bOutputStream.write(buff, 0, bytesRead);
			}
			bOutputStream.flush();
			bOutputStream.close();
			bInputStream.close();
			result.setResult(true);
			result.setValue(true);
		} catch (BusinessException ex) {
			result.setError(ex);
		} catch (Exception ex) {
			result.setError(ex);
		} finally {
			if (bOutputStream != null) {
				try {
					bOutputStream.close();
				} catch (IOException ex) {
					result.setError(ex);
				}
			}
		}
		return result;
	}

	/**
	 * 下载对应文件
	 * 
	 * @param fileContent
	 *            字节数组
	 * @param filename
	 *            下载的全名
	 * @param response
	 *            webapi的参数
	 * @return
	 */
	public ApiResult handleByteInfo(byte[] fileContent, String filename, HttpServletResponse response) {
		ApiResult result = new ApiResult();
		BufferedInputStream bInputStream = null;
		BufferedOutputStream bOutputStream = null;
		try {
			response.reset();
			response.setContentType("multipart/form-data");

			String ua = request.getHeader("User-Agent");
			if (null != ua && ua.toLowerCase().indexOf("macintosh") > -1 && ua.toLowerCase().indexOf("chrome") < 0) {
				filename = new String(filename.getBytes("UTF-8"), "iso8859-1");
			} else {
				filename = URLEncoder.encode(filename, "UTF-8");
			}

			String contentDisposition = String.format("attachment;filename=\"%s\";filename*=utf-8''%s", filename,
					filename);
			response.setHeader("Content-Disposition", contentDisposition);

			ServletOutputStream servletOutputStream = response.getOutputStream();
			InputStream inputStream = new ByteArrayInputStream(fileContent);
			bInputStream = new BufferedInputStream(inputStream);
			bOutputStream = new BufferedOutputStream(servletOutputStream);
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bInputStream.read(buff, 0, buff.length))) {
				bOutputStream.write(buff, 0, bytesRead);
			}
			bOutputStream.flush();
			bOutputStream.close();
			bInputStream.close();
			result.setResult(true);
			result.setValue(true);
		} catch (BusinessException ex) {
			result.setError(ex);
		} catch (Exception ex) {
			result.setError(ex);
		} finally {
			if (bOutputStream != null) {
				try {
					bOutputStream.close();
				} catch (IOException ex) {
					result.setError(ex);
				}
			}
		}
		return result;
	}

}
