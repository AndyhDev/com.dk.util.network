package com.dk.util.network;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

public class dKApiUpload implements UploadListener {
	private Activity activity;
	private dKSession session;
	private String path;
	private Upload up;
	private ApiUploadListener listener;
	private Boolean override = false;
	private String fileName;
	private String filePath;
	
	public dKApiUpload(Activity activity, dKSession session, String path){
		this.activity = activity;
		this.session = session;
		this.path  = path;
		
		up = new Upload(activity, "https://dk-force.de/api/upload.php" + session.getUrlGET());
		up.addFile("file", path);
		up.setListener(this);
	}
	
	public void upload(){
		if(override){
			up.addParam("override", "1");
		}
		if(fileName == null){
			fileName = new File(path).getName();
		}
		if(filePath == null){
			filePath = "/";
		}
		up.addParam("file_name", fileName);
		up.addParam("path", filePath);
		up.start();
	}
	public Activity getActivity() {
		return activity;
	}

	public dKSession getSession() {
		return session;
	}

	public String getPath() {
		return path;
	}
	
	public Boolean getOverride() {
		return override;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setOverride(Boolean override) {
		this.override = override;
	}

	public void setListener(ApiUploadListener listener){
		this.listener = listener;
	}
	@Override
	public void onUploadProgress(int progress) {
		if(listener != null){
			if(progress > 100){
				progress = 100;
			}
			listener.onApiUploadProgress(progress);
		}
	}

	@Override
	public void onUploadEnd(String msg) {
		if(msg.startsWith("error:")){
			String code = msg.replace("error:", "").trim();
			int errorCode = dKSession.ERROR_UNKNOWN;
			
			try{ 
				errorCode = Integer.parseInt(code); 
		    }catch(NumberFormatException e){ 
		        errorCode = dKSession.ERROR_UNKNOWN;
		    }
			if(listener != null){
				listener.onApiUploadError(errorCode);
			}
		}else{
			try {
				JSONObject data = new JSONObject(msg);
				listener.onApiUploadSuccess(data);
			} catch (JSONException e) {
				e.printStackTrace();
				if(listener != null){
					listener.onApiUploadError(dKSession.ERROR_RESPONSE);
				}
			}
		}
	}

	@Override
	public void onUploadError() {
		if(listener != null){
			listener.onApiUploadError(dKSession.ERROR_UNKNOWN);
		}
	}
	
}
