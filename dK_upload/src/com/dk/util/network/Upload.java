package com.dk.util.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import android.app.Activity;
import android.util.Log;

public class Upload {
	private HttpClient httpClient;
	private MultipartEntityBuilder builder;
	private HttpPost post;
	private String url; 
	private UploadListener listener;
	private Long totalSize = (long) 0;

	private HttpEntity httpEntity;
	private Activity activity;

	public Upload(Activity activity, String url){
		this.activity = activity;
		this.url = url;
		httpClient = AllowSSLSocketFactory.createHttpClient();//DefaultHttpClient();
		builder = MultipartEntityBuilder.create();
		post = new HttpPost(url);
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	}
	public void addFile(String name, String filePath){
		File file = new File(filePath);
		totalSize += file.length();
		builder.addPart(name, new FileBody(file));
	}
	public void addParam(String paramName, String paramValue){
		builder.addTextBody(paramName, paramValue);
	}
	public void setListener(UploadListener listener){
		this.listener = listener;
	}
	public void start(){
		httpEntity = builder.build();
		
		new Thread(new Runnable(){
			public void run() {
				doUp();
			}
		}).start();
	}
	private void doUp(){
		class ProgressiveEntity implements HttpEntity {
			@Override
			public void consumeContent() throws IOException {
				httpEntity.consumeContent();                
			}
			@Override
			public InputStream getContent() throws IOException, IllegalStateException {
				return httpEntity.getContent();
			}
			@Override
			public Header getContentEncoding() {             
				return httpEntity.getContentEncoding();
			}
			@Override
			public long getContentLength() {
				return httpEntity.getContentLength();
			}
			@Override
			public Header getContentType() {
				return httpEntity.getContentType();
			}
			@Override
			public boolean isChunked() {             
				return httpEntity.isChunked();
			}
			@Override
			public boolean isRepeatable() {
				return httpEntity.isRepeatable();
			}
			@Override
			public boolean isStreaming() {             
				return httpEntity.isStreaming();
			}
			@Override
			public void writeTo(OutputStream outstream) throws IOException {
				class ProxyOutputStream extends FilterOutputStream {
					public ProxyOutputStream(OutputStream proxy) {
						super(proxy);    
					}
					public void write(int idx) throws IOException {
						out.write(idx);
					}
					public void write(byte[] bts) throws IOException {
						out.write(bts);
					}
					public void write(byte[] bts, int st, int end) throws IOException {
						out.write(bts, st, end);
					}
					public void flush() throws IOException {
						out.flush();
					}
					public void close() throws IOException {
						out.close();
					}
				}
				class ProgressiveOutputStream extends ProxyOutputStream {
					private Long sended;

					public ProgressiveOutputStream(OutputStream proxy) {
						super(proxy);
						sended = (long) 0;
					}
					public void write(byte[] bts, int st, int end) throws IOException {
						sended += end;
						if(listener != null){
							activity.runOnUiThread(new Runnable(){
								@Override
								public void run() {
									listener.onUploadProgress((int) ((sended / (float) totalSize) * 100));
								}
							});
						}

						out.write(bts, st, end);
					}
				}

				httpEntity.writeTo(new ProgressiveOutputStream(outstream));
			}

		};
		ProgressiveEntity myEntity = new ProgressiveEntity();

		post.setEntity(myEntity);
		try {
			final HttpResponse response = httpClient.execute(post);
			Log.d("test", "end");
			if(listener != null){
				activity.runOnUiThread(new Runnable(){
					@Override
					public void run() {
						try {
							listener.onUploadEnd(getContent(response));
						} catch (IOException e) {
							e.printStackTrace();
							listener.onUploadError();
						}
					}
				});

			}
		} catch (IOException e) {
			e.printStackTrace();
			if(listener != null){
				activity.runOnUiThread(new Runnable(){
					@Override
					public void run() {
						listener.onUploadError();
					}
				});
			}
		}        
	} 
	
	public static String getContent(HttpResponse response) throws IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String body = "";
		String content = "";

		while ((body = rd.readLine()) != null){
			content += body + "\n";
		}
		return content.trim();
	}
	public String getUrl() {
		return url;
	}
}
