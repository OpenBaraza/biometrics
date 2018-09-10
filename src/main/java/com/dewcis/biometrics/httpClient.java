package com.dewcis.biometrics;


import java.util.logging.Logger;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.JOptionPane;

import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;

import org.json.JSONObject;

public class httpClient {
	Logger log = Logger.getLogger(httpClient.class.getName());

	String domain = "";
	
	public httpClient(String domain) {
		this.domain = domain;
	}

	public String getCookies(String url, String sData) {
		String content = "";
		String message = null;
                
		DefaultHttpClient client = new DefaultHttpClient();

        System.out.println("URL : " + url);

		HttpPost pt = new HttpPost(url);
		pt.setEntity(new StringEntity(sData, "UTF8"));
		pt.setHeader("Content-type", "application/json");
		//pt.getHeaders("set-cookie");
		
		try {
			CloseableHttpResponse httpResponse = client.execute(pt);
			content = EntityUtils.toString(httpResponse.getEntity());
			int statusCode = httpResponse.getStatusLine().getStatusCode();

// System.out.println("Status code : " + statusCode);
// System.out.println("Content : " + content);

			if(statusCode == 200) {
				Header[] cookies = httpResponse.getHeaders("set-cookie");
				if (cookies != null) message = Arrays.toString(cookies);
			} else if(statusCode != 200) {
				JSONObject jObject = new JSONObject(content);
				JOptionPane.showMessageDialog(null, jObject.getString("message"));
				System.out.println("IO Error : " + jObject);
			}
		} catch (IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return message;
	}  

	public String post(String url, String sData, String snID) {
		String content = "";
		String message = null;
                
		DefaultHttpClient client = new DefaultHttpClient();

		HttpPost pt = new HttpPost(url);
		pt.setEntity(new StringEntity(sData, "UTF8"));
		pt.setHeader("Content-type", "application/json");
                 
		CookieStore cookieStore = new BasicCookieStore();
		BasicClientCookie cookie = new BasicClientCookie("bs-cloud-session-id", snID);
		cookie.setDomain(domain);
		cookie.setPath("/");
		cookieStore.addCookie(cookie);
        
		HttpClientContext context = HttpClientContext.create();
		context.setCookieStore(cookieStore);
        
		try {
			CloseableHttpResponse httpResponse = client.execute(pt, context);
			content = EntityUtils.toString(httpResponse.getEntity());
			int statusCode = httpResponse.getStatusLine().getStatusCode();

			if(statusCode == 200) { 
				message = content;
			} else if(statusCode != 200) {
				JSONObject jObject = new JSONObject(content);
				JOptionPane.showMessageDialog(null, jObject.getString("message"));
				message = content;
			}
		} catch (IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return message;
	}  

	public String get(URI uri, String snID) {
		String content = "";
		String message = null;

		DefaultHttpClient client = new DefaultHttpClient();

		HttpGet gt = new HttpGet(uri);
                 
		CookieStore cookieStore = new BasicCookieStore();
		BasicClientCookie cookie = new BasicClientCookie("bs-cloud-session-id", snID);
		cookie.setDomain(domain);
		cookie.setPath("/");
		cookieStore.addCookie(cookie);
        
		HttpClientContext context = HttpClientContext.create();
		context.setCookieStore(cookieStore);
        
		try {
			CloseableHttpResponse httpResponse = client.execute(gt, context);
			content = EntityUtils.toString(httpResponse.getEntity());
			int statusCode = httpResponse.getStatusLine().getStatusCode();

			if(statusCode == 200) { 
				message = content;
			} else if(statusCode != 200) {
				JSONObject jObject = new JSONObject(content);
				JOptionPane.showMessageDialog(null,(String) jObject.get("message"));
				message = content;
			}
		} catch (IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return message;
	} 

	public String put(String url, String sData, String snID, String contentType){
		String content = "";
		String message = null;

		DefaultHttpClient client = new DefaultHttpClient();

		HttpPut pt = new HttpPut(url);
		pt.setEntity(new StringEntity(sData, "UTF8"));
		pt.setHeader("Content-type", contentType);

		CookieStore cookieStore = new BasicCookieStore();
		BasicClientCookie cookie = new BasicClientCookie("bs-cloud-session-id",snID);
		cookie.setDomain(domain);
		cookie.setPath("/");
		cookieStore.addCookie(cookie);

		HttpClientContext context = HttpClientContext.create();
		context.setCookieStore(cookieStore);
        
        try{
			CloseableHttpResponse httpResponse = client.execute(pt, context);
			content = EntityUtils.toString(httpResponse.getEntity());
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			JSONObject jObject = new JSONObject(content);
			if(statusCode== 200){ 
				message= content;
				JOptionPane.showMessageDialog(null,(String) jObject.get("message"));
			} else if(statusCode != 200) {
				JOptionPane.showMessageDialog(null,(String) jObject.get("message"));
				message= content;
			}
		} catch (IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return message;
    }

    public String delete(String url,String snID) {
		String content = "";
		String message = null;

		DefaultHttpClient client = new DefaultHttpClient();

		HttpDelete dl = new HttpDelete(url);
                 
		CookieStore cookieStore = new BasicCookieStore();
		BasicClientCookie cookie = new BasicClientCookie("bs-cloud-session-id", snID);
		cookie.setDomain(domain);
		cookie.setPath("/");
		cookieStore.addCookie(cookie);
        
		HttpClientContext context = HttpClientContext.create();
		context.setCookieStore(cookieStore);
        
		try {
			CloseableHttpResponse httpResponse = client.execute(dl, context);
			content = EntityUtils.toString(httpResponse.getEntity());
			int statusCode = httpResponse.getStatusLine().getStatusCode();

			if(statusCode == 200) { 
				message = content;
			} else if(statusCode != 200) {
				JSONObject jObject = new JSONObject(content);
				JOptionPane.showMessageDialog(null,(String) jObject.get("message"));
				message = content;
			}
		} catch (IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return message;
	}

    public String putPhoto(URI uri, JSONObject jobject, String snID, String contentType) {
		String content = "";
		String message = null;

		DefaultHttpClient client = new DefaultHttpClient();

		HttpPut pt = new HttpPut(uri);
		pt.setEntity(new StringEntity(jobject.getString("encoded_File"), "UTF8"));
		pt.setHeader("Content-type", contentType);

		CookieStore cookieStore = new BasicCookieStore();
		BasicClientCookie cookie = new BasicClientCookie("bs-cloud-session-id",snID);
		cookie.setDomain(domain);
		cookie.setPath("/");
		cookieStore.addCookie(cookie);

		HttpClientContext context = HttpClientContext.create();
		context.setCookieStore(cookieStore);
        
		try{
			CloseableHttpResponse httpResponse = client.execute(pt, context);
			content = EntityUtils.toString(httpResponse.getEntity());
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if(statusCode== 200){ 
				message = content;
			} else if(statusCode != 200) {
				JSONObject jObject = new JSONObject(content);
				JOptionPane.showMessageDialog(null,(String) jObject.get("message"));
				message = content;
			}
		} catch (IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return message;
    }
}
