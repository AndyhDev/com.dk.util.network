package com.dk.util.network;

import org.json.JSONException;
import org.json.JSONObject;

public class dKSession {
	private Boolean error = false;
	private Integer errorCode = 0;
	private JSONObject data;
	
	public static final int ERROR_NO = 0;
	public static final int ERROR_UNKNOWN = 1;
	public static final int ERROR_RESPONSE = 2;
	public static final int ERROR_NO_SESSION = 10;
	public static final int ERROR_LOGIN_WRONG = 11;
	public static final int ERROR_NO_USER = 12;
	public static final int ERROR_NO_PASSWORD = 13;
	
	public static final int ERROR_FILEUPLOAD = 30;
	public static final int ERROR_UPLOADFILE_EXISTS = 31;
	public static final int ERROR_NO_PATH = 32;
	public static final int ERROR_PATH_NOT_CREATED = 33;
	public static final int ERROR_PATH_NO_DIRECTORY = 34;
	
	public dKSession(String response){
		if(response.startsWith("error:")){
			error = true;
			String code = response.replace("error:", "").trim();
			try{ 
				errorCode = Integer.parseInt(code); 
		    }catch(NumberFormatException e){ 
		        errorCode = ERROR_UNKNOWN;
		    }
		}else{
			try{
				data = new JSONObject(response);
				data.getString("sessionId");
				data.getString("sessionName");
			}catch (JSONException e){
				error = true;
				errorCode = ERROR_RESPONSE;
				e.printStackTrace();
			}
		}
	}
	public String getSessionName(){
		if(error){
			return null;
		}
		try {
			return data.getString("sessionName");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	public String getSessionId(){
		if(error){
			return null;
		}
		try {
			return data.getString("sessionId");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	public String getUrlGET(){
		String name = getSessionName();
		String id = getSessionId();
		if(name != null && id != null){
			return "?" + name + "=" + id;
		}
		return null;
	}
	public boolean OK(){
		if(error == false){
			return true;
		}
		return false;
	}
	
	public int getError(){
		return errorCode;
	}
}
