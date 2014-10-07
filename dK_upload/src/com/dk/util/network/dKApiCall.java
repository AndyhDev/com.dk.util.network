package com.dk.util.network;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

public class dKApiCall implements RequestListener {
	private String command;
	private dKSession session;
	private String url;
	private ApiCallListener listener;
	private Request request;
	
	public dKApiCall(Activity activity, dKSession session, String command){
		this.command = command;
		this.session = session;
		
		url = "https://dk-force.de/api/" + command + ".php" + session.getUrlGET();
		request = new Request(activity, url);
		request.setListener(this);
	}
	public void call(){
		request.start();
	}
	public void addParam(String paramName, String paramValue){
		request.addParam(paramName, paramValue);
	}
	public dKSession getSession(){
		return session;
	}
	public String getCommand() {
		return command;
	}
	public String getUrl() {
		return url;
	}
	public void setListener(ApiCallListener listener){
		this.listener = listener;
	}

	@Override
	public void onRequestEnd(String msg) {
		if(msg.startsWith("error:")){
			String code = msg.replace("error:", "").trim();
			int errorCode = dKSession.ERROR_UNKNOWN;
			
			try{ 
				errorCode = Integer.parseInt(code); 
		    }catch(NumberFormatException e){ 
		        errorCode = dKSession.ERROR_UNKNOWN;
		    }
			if(listener != null){
				listener.onApiCallError(errorCode);
			}
		}else{
			try {
				JSONObject data = new JSONObject(msg);
				listener.onApiCallSuccess(data);
			} catch (JSONException e) {
				e.printStackTrace();
				if(listener != null){
					listener.onApiCallError(dKSession.ERROR_RESPONSE);
				}
			}
		}
	}

	@Override
	public void onRequestError() {
		if(listener != null){
			listener.onApiCallError(dKSession.ERROR_UNKNOWN);
		}
	}
}
