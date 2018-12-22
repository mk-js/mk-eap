package com.mk.eap.common.utils;

import java.util.Date;
import java.util.List;

import javax.json.Json;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.mk.eap.common.domain.BusinessException;
import com.mk.eap.common.domain.Token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class StringTokenizer {

	public class TokenIds {
		Long userId;
		Long orgId;
		Long versonId;
		Long appId;
		Long extId;

		public Long getUserId() {
			return userId;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}

		public Long getOrgId() {
			return orgId;
		}

		public void setOrgId(Long orgId) {
			this.orgId = orgId;
		}

		public Long getVersonId() {
			return versonId;
		}

		public void setVersonId(Long versonId) {
			this.versonId = versonId;
		}

		public Long getAppId() {
			return appId;
		}

		public void setAppId(Long appId) {
			this.appId = appId;
		}

		public Long getExtId() {
			return extId;
		}

		public void setExtId(Long extId) {
			this.extId = extId;
		}
	}

	private final Logger logger = LoggerFactory.getLogger(StringTokenizer.class);
	/*
	 * 默认过期时间 5天。
	 */
	private long ttlMillis = 432000000;// 5天 5*24*60*60*1000
	/*
	 * 默认token过期时间5分钟。
	 */
	public static final long defaultExpireIn = 5 * 24 * 60 * 60 * 1000;// 5天
	/*
	 * OAuth2产品的token过期时间15天。
	 */
	public static final long oAuthTokenExpiresIn = 15 * 24 * 60 * 60 * 1000;// 15天有效
	/*
	 * 临时token过期时间2分钟。
	 */
	public static final long tempExpireIn = 2 * 60 * 1000;// 2分钟

	private String apiKey = "rrtimes.com/login/token/Key";
	byte[] secretKey = null;

	byte[] getSecretKey() {
		if (secretKey == null) {
			secretKey = DatatypeConverter.parseBase64Binary(apiKey);
		}
		return secretKey;
	}

	public StringTokenizer(long expMillis) {
		this.ttlMillis = expMillis;
	}

	public StringTokenizer() {
	}

	public static final StringTokenizer Default = new StringTokenizer(defaultExpireIn);
	public static final StringTokenizer OAuth2 = new StringTokenizer(oAuthTokenExpiresIn);
	public static final StringTokenizer Temp = new StringTokenizer(tempExpireIn);

	public static StringTokenizer getDefault() {
		return Default;
	}

	public String getToken(Long userId, Long orgId, Long version, Long appId) {
		String json = "[" + userId + "," + orgId + "," + version + "," + appId + "]";
		return getToken(userId,json, ttlMillis);
	}

	public String getToken(Long userId, Long orgId, Long version, Long appId, Long extId) {
		String json = "[" + userId + "," + orgId + "," + version + "," + appId + "," + extId + "]";
		return getToken(userId,json, ttlMillis);
	}

	public String getRefreshToken(Long userId, Long orgId, Long version, Long extId) {
		String json = "[" + userId + "," + orgId + "," + version + "," + extId + "]";
		return getToken(userId,json, ttlMillis * 2);
	}

	public String getRefreshToken(Long userId, Long orgId, Long version, Long appId, Long extId) {
		String json = "[" + userId + "," + orgId + "," + version + "," + appId + "," + extId + "]";
		return getToken(userId,json, ttlMillis * 2);
	}

	private String getToken(Long userId, String json, long expireMillis) {
		String token = "";
		try {
			long nowMillis = System.currentTimeMillis();
			Date now = new Date(nowMillis);
			long expMillis = nowMillis + expireMillis;
			Date exp = new Date(expMillis);
			JwtBuilder builder = Jwts.builder()
					.setId(userId.toString())
					.setSubject(json)
					.setExpiration(exp)
					.setIssuedAt(now)
					.claim("uid",userId.toString())
					.signWith(SignatureAlgorithm.HS512, getSecretKey());

			token = builder.compact();
		} catch (Exception ex) {
			logger.error("调用出异常:", ex);
		}
		return token;
	}

	/**
	 * 
	 * @param token
	 * @return
	 */
	public List<Long> getIdsArray(String token) {
		List<Long> list;
		String userJsonInToken = getJsonInToken(token);
		try {
			list = JSON.parseArray(userJsonInToken, Long.class);
		} catch (Exception ex) {
			throw new BusinessException("10000", "反序列化出错", userJsonInToken, ex);
		}
		return list;
	}

	/**
	 * 
	 * @param token
	 * @return
	 */
	public TokenIds getIds(String token) {
		TokenIds idsObj = new TokenIds();
		List<Long> ids = getIdsArray(token);
		idsObj.setAppId(ids.get(0));
		idsObj.setOrgId(ids.get(1));
		idsObj.setVersonId(ids.get(2));
		idsObj.setAppId(ids.get(3));
		idsObj.setExtId(ids.get(4));
		return idsObj;
	}

	private String getJsonInToken(String token) {
		String userJsonInToken = null;
		try {
			Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(apiKey))
					.parseClaimsJws(token).getBody();
			userJsonInToken = claims.getSubject();
		} catch (ExpiredJwtException ex) {
			throw BusinessException.TokenExapiredException;
		} catch (Exception ex) {
			logger.error("解析token出错," + token + "," + ex.getMessage());
			throw new BusinessException("10000", "解析token出错", token, ex);
		}
		return userJsonInToken;
	}

	/**
	 * token 解析测试用例
	 * 
	 */
	public static void main(String[] args) {
		// Claims claims =
		// Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary("rrtimes.com/login/token/Key"))
		// .parseClaimsJws("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJbMzY4Nzg3NTUyMzY2Mjg0OCxudWxsLG51b"+
		// "GwsMTAwMV0iLCJleHAiOjE1MTU1Njc5NzMsImlhdCI6MTUxNTEzNTk3M30.TE6Yjms1FjtdW_TLAF8xHnGDOPGLVZBFwEP107i009gYOjGyJj4NC7gyQOr2JAyhWzDbGiPYet67fGMGQRhFPw").getBody();
		// System.out.println(claims.getSubject());
	}

	public String getToken(Token token) {
		return getToken(token.getUserId(), token.getOrgId(), token.getVersionId(), token.getAppId(), token.getExtId());
	}

}
