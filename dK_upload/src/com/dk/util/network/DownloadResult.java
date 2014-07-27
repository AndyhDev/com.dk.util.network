package com.dk.util.network;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DownloadResult {
	private ByteArrayOutputStream st;
	
	public DownloadResult(ByteArrayOutputStream st){
		this.st = st;
	}
	
	public int getSize(){
		return st.size();
	}
	
	public boolean toFile(String fileName){
		OutputStream outputStream;
		try {
			outputStream = new FileOutputStream (fileName);
			st.writeTo(outputStream);
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String toString(){
		return st.toString().trim();
	}
	
	public ByteArrayOutputStream getSt() {
		return st;
	}
}
