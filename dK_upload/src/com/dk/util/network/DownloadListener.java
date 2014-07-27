package com.dk.util.network;

public abstract interface DownloadListener {
	public abstract void onDownloadProgress(int progress);
	public abstract void onDownloadEnd(DownloadResult result);
	public abstract void onDownloadError();
}
