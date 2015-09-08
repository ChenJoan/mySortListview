package com.chenchong.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class NetworkUtils {

	public static enum netType {
		wifi, CMNET, CMWAP, noneNet
	}

	/**
	 * 网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager mgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = mgr.getAllNetworkInfo();
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断是否有网络连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 判断WIFI网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 判断MOBILE网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 获取当前网络连接的类型信息
	 * 
	 * @param context
	 * @return
	 */
	public static int getConnectedType(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}

	/**
	 * 获取当前的网络状态 -1：没有网络 1：WIFI网络 2：wap网络 3：net网络
	 * 
	 * @param context
	 * 
	 * @return
	 */
	public static netType getAPNType(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType.noneNet;
		}
		int nType = networkInfo.getType();

		if (nType == ConnectivityManager.TYPE_MOBILE) {
			if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
				return netType.CMNET;
			} else {
				return netType.CMWAP;
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			return netType.wifi;
		}
		return netType.noneNet;

	}

	/**
	 * 
	 * 类名NormalPostRequest 实现的主要功能：封装的普通的带参数的post请求 创建日期2015-8-12 创建人 chenchong <br/>
	 * 联系QQ:695933593
	 */
	public static class NormalPostRequest extends Request<JSONObject> {
		private Map<String, String> mMap;
		private Listener<JSONObject> mListener;

		public NormalPostRequest(String url, Listener<JSONObject> listener,
				ErrorListener errorListener, Map<String, String> map) {
			super(Request.Method.POST, url, errorListener);
			mListener = listener;
			mMap = map;
		}

		// mMap是已经按照前面的方式,设置了参数的实例
		@Override
		protected Map<String, String> getParams() throws AuthFailureError {
			return mMap;
		}

		// 此处因为response返回值需要json数据,和JsonObjectRequest类一样即可
		@Override
		protected Response<JSONObject> parseNetworkResponse(
				NetworkResponse response) {
			try {
				String jsonString = new String(response.data,
						HttpHeaderParser.parseCharset(response.headers));
				return Response.success(new JSONObject(jsonString),
						HttpHeaderParser.parseCacheHeaders(response));
			} catch (UnsupportedEncodingException e) {
				return Response.error(new ParseError(e));
			} catch (JSONException je) {
				return Response.error(new ParseError(je));
			}
		}

		@Override
		protected void deliverResponse(JSONObject response) {
			mListener.onResponse(response);
		}
	}

	/**
	 * 类名UnNormalPostRequest 实现的主要功能。 创建日期2015-9-6 创建人 chenchong <br/>
	 * 联系QQ:695933593
	 */
	public static class UnNormalPostRequest extends Request<JSONObject> {
		private Listener<JSONObject> mListener;

		public UnNormalPostRequest(String url, Listener<JSONObject> listener,
				ErrorListener errorListener) {
			super(Request.Method.POST, url, errorListener);
			mListener = listener;
		}

		// 此处因为response返回值需要json数据,和JsonObjectRequest类一样即可
		@Override
		protected Response<JSONObject> parseNetworkResponse(
				NetworkResponse response) {
			try {
				String jsonString = new String(response.data,
						HttpHeaderParser.parseCharset(response.headers));
				return Response.success(new JSONObject(jsonString),
						HttpHeaderParser.parseCacheHeaders(response));
			} catch (UnsupportedEncodingException e) {
				return Response.error(new ParseError(e));
			} catch (JSONException je) {
				return Response.error(new ParseError(je));
			}
		}

		@Override
		protected void deliverResponse(JSONObject response) {
			mListener.onResponse(response);
		}
	}

	/**
	 * 类名NormalGetRequest 实现的主要功能：普通的get请求 创建日期2015-8-12 创建人 chenchong <br/>
	 * 联系QQ:695933593
	 */
	public static class NormalGetRequest extends Request<JSONObject> {
		private Listener<JSONObject> mListener;

		public NormalGetRequest(String url, Listener<JSONObject> listener,
				ErrorListener errorListener) {
			super(Request.Method.GET, url, errorListener);
			mListener = listener;
		}

		// 此处因为response返回值需要json数据,和JsonObjectRequest类一样即可
		@Override
		protected Response<JSONObject> parseNetworkResponse(
				NetworkResponse response) {
			Log.i("CC", "===response===" + response.toString());
			try {
				String jsonString = new String(response.data,
						HttpHeaderParser.parseCharset(response.headers));
				return Response.success(new JSONObject(jsonString),
						HttpHeaderParser.parseCacheHeaders(response));
			} catch (UnsupportedEncodingException e) {
				return Response.error(new ParseError(e));
			} catch (JSONException je) {
				return Response.error(new ParseError(je));
			}
		}

		@Override
		protected void deliverResponse(JSONObject response) {
			mListener.onResponse(response);
		}
	}

	
	
	/**
	 * 方法的一句话概述：傻瓜式封装，只用来获取无参数的post请求的json
	 * <p>方法详述（简单方法可不必详述）</p>
	 * @param s 说明参数含义
	 * @return 说明返回值含义
	 * @throws IOException 说明发生此异常的条件
	 * @throws NullPointerException 说明发生此异常的条件
	 */
	public static JSONObject getJSONObject(String url,final VolleyJsonCallback callback, final Context ctx) {
		RequestQueue requestQueue = Volley.newRequestQueue(ctx);
		Request<JSONObject> request = new NetworkUtils.UnNormalPostRequest(url, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				callback.onSuccess(response);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(ctx, "获取数据失败", Toast.LENGTH_LONG).show();
			}
		});
		requestQueue.add(request);
		return null;
	}

	// 以下是在同一个类中定义的接口
	public interface VolleyJsonCallback {
		void onSuccess(JSONObject result);
	}

}
