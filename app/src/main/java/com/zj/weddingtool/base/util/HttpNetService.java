/**
 * Summary: 网络请求层封装
 * Version 1.0
 * Author: zhaomi@jugame.com.cn
 * Company: muji.com
 * Date: 13-11-5
 * Time: 下午12:38
 * Copyright: Copyright (c) 2013
 */

package com.zj.weddingtool.base.util;

import android.util.Log;

import com.zj.weddingtool.base.config.BmobConfig;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

public class HttpNetService {
	
	private static final String TAG = "HttpNetService";

	// HttpGet
	public static String fetchHtml(String url) throws Exception {
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = HttpClientWrapper.getHttpClient().execute(
				httpGet);
		if (response != null) {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				InputStream is = response.getEntity().getContent();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] bytes = new byte[4096];
				int lenRead;
				while ((lenRead = is.read(bytes)) != -1) {
					if (lenRead > 0) {
						baos.write(bytes, 0, lenRead);
					}
				}
				if (baos.size() > 0) {
					return new String(baos.toByteArray(), HTTP.UTF_8);
				}
			} else {
				android.util.Log.w("HttpNetService",
						"response code not correct-------------->"
								+ response.getStatusLine().getStatusCode());
			}
		} else {
			android.util.Log.w("HttpNetService", "response null");
		}
		return null;
	}

	// HttpPost
	public static String request(String url, List<NameValuePair> params)
			throws Exception {
		HttpPost post = new HttpPost(url);
		post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		HttpResponse response = HttpClientWrapper.getHttpClient().execute(post);
		if(response != null) {
			if (response.getStatusLine().getStatusCode() == 200) {
				String res = EntityUtils.toString(response.getEntity());
				if(BmobConfig.DEBUG) {
					Log.i(TAG, "Post Response-1:" + res);
				}
				// 获取返回结果
				return res;
			}
			else {
				android.util.Log.w("HttpNetService",
						"response code not correct-------------->"
								+ response.getStatusLine().getStatusCode());
			}
		} else {
			android.util.Log.w("HttpNetService", "response null");
		}
		return null;
	}

}