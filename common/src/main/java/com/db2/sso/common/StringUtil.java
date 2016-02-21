package com.db2.sso.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * 字符串相关操作
 * 
 * @author db2
 *
 */
public class StringUtil {

	private StringUtil() {
	}

	/**
	 * 向url后追加参数，拼接时需要判断连接符是? or &，同时需要对参数值进行编码
	 * 
	 * @param origUrl
	 * @param parameterName
	 * @param parameterVal
	 * @return
	 */
	public static String appendUrlParameter(String origUrl,
			String parameterName, String parameterVal) {
		if (origUrl == null) {
			return null;
		}

		String bound = origUrl.contains("?") ? "&" : "?";
		try {
			return origUrl + bound + parameterName + "="
					+ URLEncoder.encode(parameterVal, "utf-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/**
	 * 利用UUID生成全局唯一32位长的hex字符串
	 * 
	 * @return
	 */
	public static String uniqueKey() {
		return UUID.randomUUID().toString().replace("-", "").toLowerCase();
	}
}
