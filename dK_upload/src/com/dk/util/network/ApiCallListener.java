package com.dk.util.network;

import org.json.JSONObject;

public abstract interface ApiCallListener {
	public abstract void onApiCallSuccess(JSONObject data);
	public abstract void onApiCallError(int code);
}
