package com.dk.util.network;

import android.app.Activity;


public class dKLogin implements RequestListener{
	private Request request;
	private String url;
	private LoginListener listener;
	
	public dKLogin(Activity activity, String userName, String passw) {
		url = "https://dk-force.de/api/login.php";
		request = new Request(activity, url);
		request.addParam("user", userName);
		request.addParam("passw", passw);
		request.setListener(this);
	}

	public void login(){
		request.start();
	}
	@Override
	public void onRequestEnd(String msg) {
		dKSession session = new dKSession(msg);
		if(listener != null){
			if(session.OK()){
				listener.onLoginSuccess(session);
			}else{
				listener.onLoginError(session.getError());
			}
		}
	}

	@Override
	public void onRequestError() {
		if(listener != null){
			listener.onLoginError(dKSession.ERROR_UNKNOWN);
		}
		
	}
	
	public void setListener(LoginListener listener){
		this.listener = listener;
	}
}
