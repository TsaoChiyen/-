/*
 * Copyright 2011 Sina.
 *
 * Licensed under the Apache License and Weibo License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.open.weibo.com
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chengxin.net;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.channels.UnresolvedAddressException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.chengxin.R;
import com.chengxin.Entity.MorePicture;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.ResearchCommon;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.map.BMapApiApp;
import com.chengxin.net.ResearchException;
import com.chengxin.net.ResearchParameters;
import com.chengxin.net.Utility;
import com.chengxin.service.SnsService;

/**
 * Utility class for Weibo object.
 * 
 * @author ZhangJie (zhangjie2@staff.sina.com.cn)
 */

public class Utility {

	private static WeiYuanParameters mRequestHeader = new WeiYuanParameters();
	private static HttpHeaderFactory mAuth;

	public static final String BOUNDARY = "7cd4a6d158c";
	public static final String MP_BOUNDARY = "--" + BOUNDARY;
	public static final String END_MP_BOUNDARY = "--" + BOUNDARY + "--";
	public static final String MULTIPART_FORM_DATA = "multipart/form-data";

	public static final String HTTPMETHOD_POST = "POST";
	public static final String HTTPMETHOD_GET = "GET";
	public static final String HTTPMETHOD_DELETE = "DELETE";

	private static final int SET_CONNECTION_TIMEOUT = 50000;
	private static final int SET_SOCKET_TIMEOUT = 30000;
	private static HttpClient mClient;

	public static void setAuthorization(HttpHeaderFactory auth) {
		mAuth = auth;
	}
	   public static void setHeader(String httpMethod, HttpUriRequest request,
	    		ResearchParameters authParam, String url) {
	    	if (!isBundleEmpty(mRequestHeader)) {
	            for (int loc = 0; loc < mRequestHeader.size(); loc++) {
	                String key = mRequestHeader.getKey(loc);
	                request.setHeader(key, mRequestHeader.getValue(key));
	            }
	        }
	        if (!isBundleEmpty(authParam) && mAuth != null) {
	            String authHeader = mAuth.getWeiboAuthHeader(httpMethod, url, authParam);
	            if (authHeader != null) {
	                request.setHeader("Authorization", authHeader);
	            }
	        }
	        request.setHeader("User-Agent", System.getProperties().getProperty("http.agent")
	                + " WeiboAndroidSDK");
	    }
	public static void setHeader(String httpMethod, HttpUriRequest request, WeiYuanParameters authParam, String url) {
		if (!isBundleEmpty(mRequestHeader)) {
			for (int loc = 0; loc < mRequestHeader.size(); loc++) {
				String key = mRequestHeader.getKey(loc);
				request.setHeader(key, mRequestHeader.getValue(key));
			}
		}
		if (!isBundleEmpty(authParam) && mAuth != null) {
			String authHeader = mAuth.getWeiboAuthHeader(httpMethod, url, authParam);
			if (authHeader != null) {
				request.setHeader("Authorization", authHeader);
			}
		}
		request.setHeader("User-Agent", System.getProperties().getProperty("http.agent") + " WeiboAndroidSDK");
	}

	public static boolean isBundleEmpty(ResearchParameters bundle) {
		/*
		 * if (bundle == null || bundle.size() == 0) { return true; }
		 */
		if (bundle == null) {
			return true;
		}
		return false;
	}

	public static boolean isBundleEmpty(WeiYuanParameters bundle) {
		/*
		 * if (bundle == null || bundle.size() == 0) { return true; }
		 */
		if (bundle == null) {
			return true;
		}
		return false;
	}

	public static void setRequestHeader(String key, String value) {
		// mRequestHeader.clear();
		mRequestHeader.add(key, value);
	}

	public static void setRequestHeader(WeiYuanParameters params) {
		mRequestHeader.addAll(params);
	}

	public static void clearRequestHeader() {
		mRequestHeader.clear();

	}

	public static String encodePostBody(Bundle parameters, String boundary) {
		if (parameters == null)
			return "";
		StringBuilder sb = new StringBuilder();

		for (String key : parameters.keySet()) {
			if (parameters.getByteArray(key) != null) {
				continue;
			}

			sb.append("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n" + parameters.getString(key));
			sb.append("\r\n" + "--" + boundary + "\r\n");
		}

		return sb.toString();
	}

	public static String encodeUrl(ResearchParameters parameters) {
		if (parameters == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (int loc = 0; loc < parameters.size(); loc++) {
			if (first)
				first = false;
			else {
				sb.append("&");
			}
			try {
				sb.append(URLEncoder.encode(parameters.getKey(loc), "UTF-8") + "=" + URLEncoder.encode(parameters.getValue(loc), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static String encodeUrl(WeiYuanParameters parameters) {
		if (parameters == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (int loc = 0; loc < parameters.size(); loc++) {
			if (first)
				first = false;
			else {
				sb.append("&");
			}
			try {
				sb.append(URLEncoder.encode(parameters.getKey(loc), "UTF-8") + "=" + URLEncoder.encode(parameters.getValue(loc), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static Bundle decodeUrl(String s) {
		Bundle params = new Bundle();
		if (s != null) {
			String array[] = s.split("&");
			for (String parameter : array) {
				String v[] = parameter.split("=");
				params.putString(URLDecoder.decode(v[0]), URLDecoder.decode(v[1]));
			}
		}
		return params;
	}

	/**
	 * Parse a URL query and fragment parameters into a key-value bundle.
	 * 
	 * @param url
	 *            the URL to parse
	 * @return a dictionary bundle of keys and values
	 */
	public static Bundle parseUrl(String url) {
		// hack to prevent MalformedURLException
		url = url.replace("weiboconnect", "http");
		try {
			URL u = new URL(url);
			Bundle b = decodeUrl(u.getQuery());
			b.putAll(decodeUrl(u.getRef()));
			return b;
		} catch (MalformedURLException e) {
			return new Bundle();
		}
	}

	/**
	 * Construct a url encoded entity by parameters .
	 * 
	 * @param bundle
	 *            :parameters key pairs
	 * @return UrlEncodedFormEntity: encoed entity
	 */
	public static UrlEncodedFormEntity getPostParamters(Bundle bundle) {
		if (bundle == null || bundle.isEmpty()) {
			return null;
		}
		try {
			List<NameValuePair> form = new ArrayList<NameValuePair>();
			for (String key : bundle.keySet()) {
				form.add(new BasicNameValuePair(key, bundle.getString(key)));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, "UTF-8");
			return entity;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Implement a weibo http request and return results .
	 * 
	 * @param context
	 *            : context of activity
	 * @param url
	 *            : request url of open api
	 * @param method
	 *            : HTTP METHOD.GET, POST, DELETE
	 * @param params
	 *            : Http params , query or postparameters
	 * @param Token
	 *            : oauth token or accesstoken
	 * @param loginType
	 *            1-需要检测用户是否登录 0-不需要检测
	 * @return UrlEncodedFormEntity: encoed entity
	 * @throws ConnectTimeoutException
	 * @throws UnresolvedAddressException
	 * @throws SocketTimeoutException
	 * @throws UnknownHostException
	 * 
	 */

	public static String openUrl(String url, String method, WeiYuanParameters params, int loginType) throws WeiYuanException {
		/* params.add("userType", "2"); */

		if (loginType == 1) {
			if (TextUtils.isEmpty(WeiYuanCommon.getUserId(BMapApiApp.getInstance()))) {
				Intent toastIntent = new Intent(GlobalParam.ACTION_SHOW_TOAST);
				toastIntent.putExtra("toast_msg", BMapApiApp.getInstance().getResources().getString(R.string.account_repeat));
				BMapApiApp.getInstance().sendBroadcast(toastIntent);

				WeiYuanCommon.saveLoginResult(BMapApiApp.getInstance(), null);
				WeiYuanCommon.setUid("");

				BMapApiApp.getInstance().sendBroadcast(new Intent(GlobalParam.ACTION_DESTROY_CURRENT_ACTIVITY));
				Intent serviceIntent = new Intent(BMapApiApp.getInstance(), SnsService.class);
				BMapApiApp.getInstance().stopService(serviceIntent);

				BMapApiApp.getInstance().sendBroadcast(new Intent(GlobalParam.ACTION_LOGIN_OUT));
				return "";
			}
		}

		/*
		 * if(loginType == 1){ params.add("uid", Common.getUid(StringsApp.getInstance())); }else if(loginType == 2){ if(!TextUtils.isEmpty(Common.getUid(StringsApp.getInstance()))){ params.add("uid", Common.getUid(StringsApp.getInstance())); } }
		 */

		String rlt = "";
		List<MorePicture> filePath = new ArrayList<MorePicture>();
		if (params != null && !params.equals("")) {
			for (int loc = 0; loc < params.size(); loc++) {
				String key = params.getKey(loc);
				if (key.equals("pic")) {
					// HashMap<String,List<MorePicture>> picMap =params.getPicList("pic");
					filePath = params.getPicList("pic");
					params.remove(key);
				}
			}
		}
		if (filePath == null || filePath.equals("")) {
			rlt = openUrl(url, method, params, null);
		} else {
			rlt = openUrl(url, method, params, filePath);
		}

		return rlt;
	}

	/**
	 * Implement a weibo http request and return results .
	 * 
	 * @param context
	 *            : context of activity
	 * @param url
	 *            : request url of open api
	 * @param method
	 *            : HTTP METHOD.GET, POST, DELETE
	 * @param params
	 *            : Http params , query or postparameters
	 * @param Token
	 *            : oauth token or accesstoken
	 * @param loginType
	 *            1-需要检测用户是否登录 0-不需要检测
	 * @return UrlEncodedFormEntity: encoed entity
	 * @throws ConnectTimeoutException
	 * @throws UnresolvedAddressException
	 * @throws SocketTimeoutException
	 * @throws UnknownHostException
	 * 
	 */

	public static String openUrl(String url, String method, ResearchParameters params, int loginType) throws ResearchException {
		/* params.add("userType", "2"); */

		if (loginType == 1) {
			if (TextUtils.isEmpty(ResearchCommon.getUserId(BMapApiApp.getInstance()))) {
				// Intent toastIntent = new Intent(GlobalParam.ACTION_SHOW_TOAST);
				// toastIntent.putExtra("toast_msg",BMapApiApp.getInstance().getResources().getString(R.string.account_repeat));
				// BMapApiApp.getInstance().sendBroadcast(toastIntent);

				ResearchCommon.saveLoginResult(BMapApiApp.getInstance(), null);
				ResearchCommon.setUid("");

				BMapApiApp.getInstance().sendBroadcast(new Intent(GlobalParam.ACTION_DESTROY_CURRENT_ACTIVITY));
				Intent serviceIntent = new Intent(BMapApiApp.getInstance(), SnsService.class);
				BMapApiApp.getInstance().stopService(serviceIntent);

				BMapApiApp.getInstance().sendBroadcast(new Intent(GlobalParam.ACTION_LOGIN_OUT));
				return "";
			}
		}

		/*
		 * if(loginType == 1){ params.add("uid", Common.getUid(StringsApp.getInstance())); }else if(loginType == 2){ if(!TextUtils.isEmpty(Common.getUid(StringsApp.getInstance()))){ params.add("uid", Common.getUid(StringsApp.getInstance())); } }
		 */

		String rlt = "";
		List<MorePicture> filePath = new ArrayList<MorePicture>();
		if (params != null && !params.equals("")) {
			for (int loc = 0; loc < params.size(); loc++) {
				String key = params.getKey(loc);
				if (key.equals("pic")) {
					// HashMap<String,List<MorePicture>> picMap =params.getPicList("pic");
					filePath = params.getPicList("pic");
					params.remove(key);
				}
			}
		}
		if (filePath == null || filePath.equals("")) {
			rlt = openUrl(url, method, params, null);
		} else {
			rlt = openUrl(url, method, params, filePath);
		}

		return rlt;
	}

	public static HttpClient getClient() {
		return mClient;
	}

	public static String openUrl(String url, String method, ResearchParameters params, List<MorePicture> filePath) throws ResearchException {
		String result = "";

		long timeout = 0;
		/*
		 * File files = null; if(!TextUtils.isEmpty(file)){ files = new File(file); timeout = files.length() * 1000/(PER_SPEED * 1024); }
		 */

		HttpClient client = getNewHttpClient(timeout);
		// mClient = client;
		try {
			HttpUriRequest request = null;
			ByteArrayOutputStream bos = null;

			if (method.equals("GET")) {
				url = url + "?" + encodeUrl(params); // Log.e("url",url);
				Log.e("url", url);
				HttpGet get = new HttpGet(url);
				request = get;

			} else if (method.equals("POST")) {
				HttpPost post = new HttpPost(url);
				byte[] data = null;
				bos = new ByteArrayOutputStream(1024 * 50);
				if (filePath != null && filePath.size() > 0) {
					/*
					 * UrlEncodedFormEntity entity = new UrlEncodedFormEntity((List<? extends NameValuePair>) params); entity.setContentEncoding("UTF-8"); //entity.setContentType("application/json"); post.setEntity(entity);
					 */
					Utility.paramToUpload(bos, params);
					post.setHeader("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);
					// post.setHeader("Charset", "UTF-8");
					for (int i = 0; i < filePath.size(); i++) {
						/* Bitmap bf = BitmapFactory.decodeFile(filePath.get(i).filePath); */
						// Log.e("length", String.valueOf(file.length()));
						Utility.fileContentToUpload(bos, new File(filePath.get(i).filePath), filePath.get(i).key);
					}
					bos.write(("\r\n" + END_MP_BOUNDARY).getBytes());
				} else {
					post.setHeader("Content-Type", "application/x-www-form-urlencoded");
					String postParam = encodeParameters(params);
					data = postParam.getBytes("UTF-8");
					bos.write(data);
				}
				data = bos.toByteArray();
				bos.close();
				// UrlEncodedFormEntity entity = getPostParamters(params);
				ByteArrayEntity formEntity = new ByteArrayEntity(data);
				post.setEntity(formEntity);
				request = post;
			} else if (method.equals("DELETE")) {
				request = new HttpDelete(url);
			}
			setHeader(method, request, params, url);
			HttpResponse response = client.execute(request);
			StatusLine status = response.getStatusLine();
			int statusCode = status.getStatusCode();

			if (statusCode != 200) {
				result = read(response);
				try {
					JSONObject json = new JSONObject(result);
					json.getString("error");
					json.getInt("error_code");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// throw new WeiboException(String.format(err), errCode);e
			}
			// parse content stream from response
			result = read(response);
			return result;
		} catch (IOException e) {
			Log.e("e.getClass()", e.getClass().toString());
			if (e.getClass().toString().equalsIgnoreCase("class java.nio.channels.UnresolvedAddressException")) {
				throw new ResearchException("UnresolvedAddress", e, R.string.unknown_addr);
			} else if (e.getClass().toString().equalsIgnoreCase("class java.net.UnknownHostException")) {
				throw new ResearchException("UnknownHost", e, R.string.error_host);
			} else if (e.getClass().toString().equalsIgnoreCase("class org.apache.http.conn.ConnectTimeoutException")) {
				throw new ResearchException("ConnectionTimeout", e, R.string.timeout);
			} else if (e.getClass().toString().equalsIgnoreCase("class java.net.SocketTimeoutException")) {
				throw new ResearchException("SocketTimeout", e, R.string.timeout);
			}
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null && client.getConnectionManager() != null) {
				client.getConnectionManager().shutdown();
				client = null;
			}
		}
		return null;
	}

	public static String openUrl(String url, String method, WeiYuanParameters params, List<MorePicture> filePath) throws WeiYuanException {
		String result = "";

		long timeout = 0;
		/*
		 * File files = null; if(!TextUtils.isEmpty(file)){ files = new File(file); timeout = files.length() * 1000/(PER_SPEED * 1024); }
		 */

		HttpClient client = getNewHttpClient(timeout);
		// mClient = client;
		try {
			HttpUriRequest request = null;
			ByteArrayOutputStream bos = null;

			if (method.equals("GET")) {
				url = url + "?" + encodeUrl(params); // Log.e("url",url);
				Log.e("url", url);
				HttpGet get = new HttpGet(url);
				request = get;

			} else if (method.equals("POST")) {
				HttpPost post = new HttpPost(url);
				byte[] data = null;
				bos = new ByteArrayOutputStream(1024 * 50);
				if (filePath != null && filePath.size() > 0) {
					/*
					 * UrlEncodedFormEntity entity = new UrlEncodedFormEntity((List<? extends NameValuePair>) params); entity.setContentEncoding("UTF-8"); //entity.setContentType("application/json"); post.setEntity(entity);
					 */
					Utility.paramToUpload(bos, params);
					post.setHeader("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);
					// post.setHeader("Charset", "UTF-8");
					for (int i = 0; i < filePath.size(); i++) {
						Log.e("filePath", filePath.get(i).filePath);
						/* Bitmap bf = BitmapFactory.decodeFile(filePath.get(i).filePath); */
						// Log.e("length", String.valueOf(file.length()));
						Utility.fileContentToUpload(bos, new File(filePath.get(i).filePath), filePath.get(i).key);
					}
					bos.write(("\r\n" + END_MP_BOUNDARY).getBytes());
				} else {
					post.setHeader("Content-Type", "application/x-www-form-urlencoded");
					String postParam = encodeParameters(params);
					data = postParam.getBytes("UTF-8");
					bos.write(data);
				}
				data = bos.toByteArray();
				bos.close();
				// UrlEncodedFormEntity entity = getPostParamters(params);
				ByteArrayEntity formEntity = new ByteArrayEntity(data);
				post.setEntity(formEntity);
				request = post;
			} else if (method.equals("DELETE")) {
				request = new HttpDelete(url);
			}
			setHeader(method, request, params, url);
			HttpResponse response = client.execute(request);
			StatusLine status = response.getStatusLine();
			int statusCode = status.getStatusCode();

			if (statusCode != 200) {
				result = read(response);
				try {
					JSONObject json = new JSONObject(result);
					json.getString("error");
					json.getInt("error_code");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// throw new WeiboException(String.format(err), errCode);e
			}
			// parse content stream from response
			result = read(response);
			return result;
		} catch (IOException e) {
			Log.e("e.getClass()", e.getClass().toString());
			if (e.getClass().toString().equalsIgnoreCase("class java.nio.channels.UnresolvedAddressException")) {
				throw new WeiYuanException("UnresolvedAddress", e, R.string.unknown_addr);
			} else if (e.getClass().toString().equalsIgnoreCase("class java.net.UnknownHostException")) {
				throw new WeiYuanException("UnknownHost", e, R.string.error_host);
			} else if (e.getClass().toString().equalsIgnoreCase("class org.apache.http.conn.ConnectTimeoutException")) {
				throw new WeiYuanException("ConnectionTimeout", e, R.string.timeout);
			} else if (e.getClass().toString().equalsIgnoreCase("class java.net.SocketTimeoutException")) {
				throw new WeiYuanException("SocketTimeout", e, R.string.timeout);
			}
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null && client.getConnectionManager() != null) {
				client.getConnectionManager().shutdown();
				client = null;
			}
		}
		return null;
	}

	public static HttpClient getNewHttpClient(long timeout) {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();

			// HttpConnectionParams.setConnectionTimeout(params, 10000);
			// HttpConnectionParams.setSoTimeout(params, 10000);

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			// HttpProtocolParams.setContentCharset(params, HTTP.);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			// Set the default socket timeout (SO_TIMEOUT) // in
			// milliseconds which is the timeout for waiting for data.
			HttpConnectionParams.setConnectionTimeout(params, Utility.SET_CONNECTION_TIMEOUT);
			long soc_time = Utility.SET_SOCKET_TIMEOUT + timeout;
			HttpConnectionParams.setSoTimeout(params, (int) soc_time);
			HttpClient client = new DefaultHttpClient(ccm, params);
			return client;
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	public static class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	/**
	 * Get a HttpClient object which is setting correctly .
	 * 
	 * @param context
	 *            : context of activity
	 * @return HttpClient: HttpClient object
	 */
	public static DefaultHttpClient getHttpClient(Context context) {
		BasicHttpParams httpParameters = new BasicHttpParams();
		// Set the default socket timeout (SO_TIMEOUT) // in
		// milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setConnectionTimeout(httpParameters, Utility.SET_CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters, Utility.SET_SOCKET_TIMEOUT);
		DefaultHttpClient client = new DefaultHttpClient(httpParameters);
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {
			// 鑾峰彇褰撳墠姝ｅ湪浣跨敤鐨凙PN鎺ュ叆鐐�?
			Uri uri = Uri.parse("content://telephony/carriers/preferapn");
			Cursor mCursor = context.getContentResolver().query(uri, null, null, null, null);
			if (mCursor != null && mCursor.moveToFirst()) {
				// 娓告爣绉昏嚦绗竴鏉¤褰曪紝褰撶劧涔熷彧鏈変竴鏉�?
				String proxyStr = mCursor.getString(mCursor.getColumnIndex("proxy"));
				if (proxyStr != null && proxyStr.trim().length() > 0) {
					HttpHost proxy = new HttpHost(proxyStr, 80);
					client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
				}
				mCursor.close();
			}
		}
		return client;
	}

	/**
	 * Upload file into output stream .
	 * 
	 * @param out
	 *            : output stream for uploading
	 * @param file
	 *            : file for uploading
	 * @param filekey
	 *            :uploaded files' key;
	 * @return void
	 */
	private static void fileContentToUpload(OutputStream out, /* Bitmap imgpath */File file, String key) throws WeiYuanException {
		StringBuilder temp = new StringBuilder();

		temp.append(MP_BOUNDARY).append("\r\n");
		/*
		 * temp.append("Content-Disposition: form-data; name=\"f_upload\"; filename=\"" + file.getName() + "") .append("").append("\"\r\n");
		 */
		temp.append("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + file.getName())
		/* .append(key+".png") */.append("").append("\"\r\n");
		byte[] fileData = getFileByte(file);
		String filetype = "multipart/form-data";

		temp.append("Content-Type: ").append(filetype).append("\r\n\r\n");
		byte[] res = temp.toString().getBytes();
		BufferedInputStream bis = null;
		try {
			out.write(res);
			out.write(fileData);
			// imgpath.compress(CompressFormat.PNG, 75, out);
			out.write("\r\n".getBytes());
			// out.write(("\r\n" + END_MP_BOUNDARY).getBytes());
		} catch (IOException e) {
			throw new WeiYuanException(e);
		} finally {
			if (null != bis) {
				try {
					bis.close();
				} catch (IOException e) {
					throw new WeiYuanException(e);
				}
			}
		}
	}

	private static byte[] getFileByte(File file) {

		byte[] buffer = null;
		FileInputStream fin;
		try {
			fin = new FileInputStream(file.getPath());
			int length;
			try {
				length = fin.available();
				buffer = new byte[length];
				fin.read(buffer);
				fin.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buffer;
	}

    /**
     * Upload weibo contents into output stream .
     * 
     * @param baos
     *            : output stream for uploading weibo
     * @param params
     *            : post parameters for uploading
     * @return void
     */
    private static void paramToUpload(OutputStream baos, ResearchParameters params){
        String key = "";
        for (int loc = 0; loc < params.size(); loc++) {
        	try {
	            //key = URLEncoder.encode(params.getKey(loc), "UTF-8");
        		key = params.getKey(loc);
	            StringBuilder temp = new StringBuilder(10);
	            temp.setLength(0);
	            temp.append(MP_BOUNDARY).append("\r\n");
	            temp.append("content-disposition: form-data; name=\"").append(key).append("\"\r\n\r\n");
            	//temp.append(URLEncoder.encode(params.getValue(key), "UTF-8")).append("\r\n");
	            temp.append(params.getValue(key)).append("\r\n");
	            byte[] res;
	            res = temp.toString().getBytes();
                baos.write(res);
            } catch (IOException e) {
               e.printStackTrace();
            }
        }
    }
	/**
	 * Upload weibo contents into output stream .
	 * 
	 * @param baos
	 *            : output stream for uploading weibo
	 * @param params
	 *            : post parameters for uploading
	 * @return void
	 */
	private static void paramToUpload(OutputStream baos, WeiYuanParameters params) {
		String key = "";
		for (int loc = 0; loc < params.size(); loc++) {
			try {
				// key = URLEncoder.encode(params.getKey(loc), "UTF-8");
				key = params.getKey(loc);
				StringBuilder temp = new StringBuilder(10);
				temp.setLength(0);
				temp.append(MP_BOUNDARY).append("\r\n");
				temp.append("content-disposition: form-data; name=\"").append(key).append("\"\r\n\r\n");
				// temp.append(URLEncoder.encode(params.getValue(key), "UTF-8")).append("\r\n");
				temp.append(params.getValue(key)).append("\r\n");
				byte[] res;
				res = temp.toString().getBytes();
				baos.write(res);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Read http requests result from response .
	 * 
	 * @param response
	 *            : http response by executing httpclient
	 * 
	 * @return String : http response content
	 */
	public static String read(HttpResponse response) {
		String result = "";
		HttpEntity entity = response.getEntity();
		InputStream inputStream;
		try {
			inputStream = entity.getContent();
			ByteArrayOutputStream content = new ByteArrayOutputStream();

			Header header = response.getFirstHeader("Content-Encoding");
			if (header != null && header.getValue().toLowerCase().indexOf("gzip") > -1) {
				inputStream = new GZIPInputStream(inputStream);
			}

			// Read response into a buffered stream
			int readBytes = 0;
			byte[] sBuffer = new byte[512];
			while ((readBytes = inputStream.read(sBuffer)) != -1) {
				content.write(sBuffer, 0, readBytes);
			}
			// Return result from buffered stream
			result = new String(content.toByteArray(), "UTF-8");
			return result;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Read http requests result from inputstream .
	 * 
	 * @param inputstream
	 *            : http inputstream from HttpConnection
	 * 
	 * @return String : http response content
	 */
	@SuppressWarnings("unused")
	private static String read(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}

	/**
	 * Clear current context cookies .
	 * 
	 * @param context
	 *            : current activity context.
	 * 
	 * @return void
	 */
	public static void clearCookies(Context context) {
		@SuppressWarnings("unused")
		CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
	}

	/**
	 * Display a simple alert dialog with the given text and title.
	 * 
	 * @param context
	 *            Android context in which the dialog should be displayed
	 * @param title
	 *            Alert dialog title
	 * @param text
	 *            Alert dialog message
	 */
	public static void showAlert(Context context, String title, String text) {
		Builder alertBuilder = new Builder(context);
		alertBuilder.setTitle(title);
		alertBuilder.setMessage(text);
		alertBuilder.create().show();
	}

	public static String encodeParameters(ResearchParameters httpParams) {
		if (null == httpParams || Utility.isBundleEmpty(httpParams)) {
			return "";
		}
		StringBuilder buf = new StringBuilder();
		int j = 0;
		for (int loc = 0; loc < httpParams.size(); loc++) {
			String key = httpParams.getKey(loc);
			if (j != 0) {
				buf.append("&");
			}
			try {
				buf.append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(httpParams.getValue(key), "UTF-8"));
			} catch (java.io.UnsupportedEncodingException neverHappen) {
			}
			j++;
		}
		return buf.toString();

	}

	public static String encodeParameters(WeiYuanParameters httpParams) {
		if (null == httpParams || Utility.isBundleEmpty(httpParams)) {
			return "";
		}
		StringBuilder buf = new StringBuilder();
		int j = 0;
		for (int loc = 0; loc < httpParams.size(); loc++) {
			String key = httpParams.getKey(loc);
			if (j != 0) {
				buf.append("&");
			}
			try {
				buf.append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(httpParams.getValue(key), "UTF-8"));
			} catch (java.io.UnsupportedEncodingException neverHappen) {
			}
			j++;
		}
		return buf.toString();

	}

	/**
	 * Base64 encode mehtod for weibo request.Refer to weibo development document.
	 * 
	 */
	public static char[] base64Encode(byte[] data) {
		final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
		char[] out = new char[((data.length + 2) / 3) * 4];
		for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
			boolean quad = false;
			boolean trip = false;
			int val = (0xFF & (int) data[i]);
			val <<= 8;
			if ((i + 1) < data.length) {
				val |= (0xFF & (int) data[i + 1]);
				trip = true;
			}
			val <<= 8;
			if ((i + 2) < data.length) {
				val |= (0xFF & (int) data[i + 2]);
				quad = true;
			}
			out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
			val >>= 6;
			out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
			val >>= 6;
			out[index + 1] = alphabet[val & 0x3F];
			val >>= 6;
			out[index + 0] = alphabet[val & 0x3F];
		}
		return out;
	}

}
