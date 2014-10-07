package com.dk.util.network;


public abstract interface LoginListener {
	public abstract void onLoginSuccess(dKSession session);
	public abstract void onLoginError(int code);
}
