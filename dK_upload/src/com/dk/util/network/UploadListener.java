package com.dk.util.network;

public abstract interface UploadListener {
	public abstract void onUploadProgress(int progress);
	public abstract void onUploadEnd(String msg);
	public abstract void onUploadError();
}
