package com.dk.util.network;

public abstract interface RequestListener {
	public abstract void onRequestEnd(String msg);
	public abstract void onRequestError();
}
