package com.dk.util.network;

import org.json.JSONObject;

public abstract interface ApiUploadListener {
	public abstract void onApiUploadSuccess(JSONObject data);
	public abstract void onApiUploadProgress(int progress);
	public abstract void onApiUploadError(int code);
}
