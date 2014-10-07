package com.dk.util.network;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;

public class Request {
	private Activity activity;
	private String url;
	private DefaultHttpClient httpClient;
	private MultipartEntityBuilder params;
	private HttpPost post;
	private RequestListener listener;
	
	public Request(Activity activity, String url){
		this.activity = activity;
		this.url = url;
		this.httpClient = new DefaultHttpClient();
		
		params = MultipartEntityBuilder.create();
		post = new HttpPost(url);
		params.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	}
	
	public void addParam(String paramName, String paramValue){
		params.addTextBody(paramName, paramValue);
	}
	
	public void start(){
		post.setEntity(params.build());
		
		new Thread(new Runnable(){
			public void run() {
				doRequest();
			}
		}).start();
	}
	
	private void doRequest(){
		try {
			HttpResponse response = httpClient.execute(post);
			ResponseHandler<String> handler = new BasicResponseHandler();
			String body = handler.handleResponse(response);
			int code = response.getStatusLine().getStatusCode();
			if(code == 200){
				postEnd(body);
			}else{
				postError();
			}
		} catch (ClientProtocolException e) {
			postError();
			e.printStackTrace();
		} catch (IOException e) {
			postError();
			e.printStackTrace();
		}
	}
	
	private void postError(){
		if(listener != null){
			activity.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					listener.onRequestError();
				}
			});
		}
	}
	private void postEnd(final String result){
		if(listener != null){
			activity.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					listener.onRequestEnd(result);
				}
			});
		}
	}
	public void setListener(RequestListener listener){
		this.listener = listener;
	}
	
	public String getUrl(){
		return url;
	}
}
