package com.dk.upload;

import java.io.File;
import java.io.FilenameFilter;

import org.json.JSONObject;

import com.dk.util.network.ApiCallListener;
import com.dk.util.network.ApiUploadListener;
import com.dk.util.network.Download;
import com.dk.util.network.DownloadListener;
import com.dk.util.network.DownloadResult;
import com.dk.util.network.Login;
import com.dk.util.network.LoginListener;
import com.dk.util.network.Request;
import com.dk.util.network.RequestListener;
import com.dk.util.network.Upload;
import com.dk.util.network.UploadListener;
import com.dk.util.network.dKApiCall;
import com.dk.util.network.dKApiUpload;
import com.dk.util.network.dKSession;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class MainActivity extends Activity {
	private static int notifyId = 1;
	private static int notifyId2 = 2;
	private static int notifyId3 = 3;
	private static int notifyId4 = 4;
	private static int notifyId6 = 6;
	
	private static NotificationManager nm;
	private static NotificationCompat.Builder builder;
	private static NotificationCompat.Builder builder2;
	private static NotificationCompat.Builder builder3;
	private static NotificationCompat.Builder builder4;
	private static NotificationCompat.Builder builder6;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}

		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		builder = new NotificationCompat.Builder(this);
		builder.setContentTitle("Upload")
		.setContentText("Upload in progress")
		.setSmallIcon(R.drawable.ic_launcher);

		builder.setProgress(100, 0, false);
		
		builder2 = new NotificationCompat.Builder(this);
		builder2.setContentTitle("Download")
		.setContentText("Download in progress")
		.setSmallIcon(R.drawable.ic_launcher);

		builder2.setProgress(100, 0, false);
		
		builder3 = new NotificationCompat.Builder(this);
		builder3.setContentTitle("Request")
		.setContentText("Request in progress")
		.setSmallIcon(R.drawable.ic_launcher);
		
		builder4 = new NotificationCompat.Builder(this);
		builder4.setContentTitle("Login")
		.setContentText("Login in progress")
		.setSmallIcon(R.drawable.ic_launcher);
		
		builder6 = new NotificationCompat.Builder(this);
		builder6.setContentTitle("Upload")
		.setContentText("Upload in progress")
		.setSmallIcon(R.drawable.ic_launcher);

		builder6.setProgress(100, 0, false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements OnClickListener, UploadListener, DownloadListener, RequestListener, LoginListener, ApiCallListener, ApiUploadListener{
		private String[] mFileList;
		private File mPath = Environment.getExternalStorageDirectory();
		private String mChosenFile;
		private static final String FTYPE = ".";    
		private static final int DIALOG_LOAD_FILE = 1000;
		private dKSession session;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);

			Button bnt = (Button)rootView.findViewById(R.id.button1);
			bnt.setOnClickListener(this);
			
			Button bnt2 = (Button)rootView.findViewById(R.id.button2);
			bnt2.setOnClickListener(this);
			
			Button bnt3 = (Button)rootView.findViewById(R.id.button3);
			bnt3.setOnClickListener(this);
			
			Button bnt4 = (Button)rootView.findViewById(R.id.button4);
			bnt4.setOnClickListener(this);
			
			Button bnt5 = (Button)rootView.findViewById(R.id.button5);
			bnt5.setOnClickListener(this);
			
			Button bnt6 = (Button)rootView.findViewById(R.id.button6);
			bnt6.setOnClickListener(this);
			
			Button bnt7 = (Button)rootView.findViewById(R.id.button7);
			bnt7.setOnClickListener(this);
			
			return rootView;
		}
		private void loadFileList() {
			if(mPath.exists()) {
				FilenameFilter filter = new FilenameFilter() {
					public boolean accept(File dir, String filename) {
						File sel = new File(dir, filename);
						return filename.contains(FTYPE) || sel.isDirectory();
					}
				};
				mFileList = mPath.list(filter);
			}
			else {
				mFileList= new String[0];
			}
		}
		protected Dialog onCreateDialog(int id) {
			Dialog dialog = null;
			AlertDialog.Builder builder = new Builder(this.getActivity());

			switch(id) {
			case DIALOG_LOAD_FILE:
				builder.setTitle("Choose your file");
				if(mFileList == null) {
					Log.e("TAG", "Showing file picker before loading the file list");
					dialog = builder.create();
					return dialog;
				}
				builder.setItems(mFileList, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mChosenFile = mFileList[which];
						upload(new File(Environment.getExternalStorageDirectory(), mChosenFile).getAbsolutePath());
						dialog.dismiss();
					}
				});
				break;
			}
			dialog = builder.show();
			return dialog;
		}
		
		protected Dialog onCreateDialog2(int id) {
			Dialog dialog = null;
			AlertDialog.Builder builder = new Builder(this.getActivity());

			switch(id) {
			case DIALOG_LOAD_FILE:
				builder.setTitle("Choose your file");
				if(mFileList == null) {
					Log.e("TAG", "Showing file picker before loading the file list");
					dialog = builder.create();
					return dialog;
				}
				builder.setItems(mFileList, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mChosenFile = mFileList[which];
						upload2(new File(Environment.getExternalStorageDirectory(), mChosenFile).getAbsolutePath());
						dialog.dismiss();
					}
				});
				break;
			}
			dialog = builder.show();
			return dialog;
		}
		
		protected void upload(String mChosenFile2) {
			Upload up = new Upload(this.getActivity(), "https://dk-force.de/test.php");
			up.addFile("file", mChosenFile2);
			up.setListener(this);
			up.start();
			nm.notify(notifyId, builder.build());
		}
		
		protected void upload2(String path) {
			if(session != null){
				dKApiUpload up = new dKApiUpload(getActivity(), session, path);
				up.setFilePath("dkplay/files");
				up.setListener(this);
				up.upload();
			}
		}
		
		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.button1){
				loadFileList();
				onCreateDialog(DIALOG_LOAD_FILE);
			}else if (v.getId() == R.id.button2) {
				Log.d("TEST", "download start");
				Download down = new Download(this.getActivity(), "https://dk-force.de/text.txt");
				down.setListener(this);
				down.addParam("echo", "cool");
				down.start();
				nm.notify(notifyId2, builder2.build());
			}else if (v.getId() == R.id.button3) {
				Log.d("TEST", "request start");
				Request r = new Request(this.getActivity(), "https://dk-force.de/api/login.php");
				r.addParam("user", "Andyh");
				r.addParam("passw", "hallodu");
				r.setListener(this);
				r.addParam("echo", "cool");
				r.start();
				nm.notify(notifyId3, builder3.build());
			}else if (v.getId() == R.id.button4) {
				Log.d("TEST", "login start");
				Login l = new Login(this.getActivity(), "Andyh", "hallodu");
				l.setListener(this);
				l.login();
				nm.notify(notifyId4, builder4.build());
			}else if (v.getId() == R.id.button5) {
				if(session != null){
					Log.d("TEST", "check session");
					dKApiCall call = new dKApiCall(getActivity(), session, "test");
					call.setListener(this);
					call.call();
					nm.notify(notifyId4, builder4.build());
				}else{
					Log.d("TEST", "erst einloggen");
				}
			}else if(v.getId() == R.id.button6){
				loadFileList();
				onCreateDialog2(DIALOG_LOAD_FILE);
			}else if (v.getId() == R.id.button7) {
				if(session != null){
					Log.d("TEST", "check session");
					dKApiCall call = new dKApiCall(getActivity(), session, "delete_file");
					call.addParam("path", "dkplay/files/todo.txt");
					call.setListener(this);
					call.call();
					nm.notify(notifyId4, builder4.build());
				}else{
					Log.d("TEST", "erst einloggen");
				}
			}
		}

		@Override
		public void onUploadProgress(int progress) {
			Log.d("onProgress", "" + progress);
			builder.setProgress(100, progress, false);
			nm.notify(notifyId, builder.build());

		}

		@Override
		public void onUploadEnd(String msg) {
			Log.d("onEnd", msg);
			nm.cancel(notifyId);

		}

		@Override
		public void onUploadError() {
			Log.d("onError", "error");
			nm.cancel(notifyId);

		}

		@Override
		public void onDownloadProgress(int progress) {
			Log.d("onProgress", "" + progress);
			builder2.setProgress(100, progress, false);
			nm.notify(notifyId2, builder2.build());
		}

		@Override
		public void onDownloadEnd(DownloadResult result) {
			Log.d("onEnd", ""+ result.getSize());
			nm.cancel(notifyId2);
		}

		@Override
		public void onDownloadError() {
			Log.d("onError", "error");
			nm.cancel(notifyId2);
		}

		@Override
		public void onRequestEnd(String msg) {
			Log.d("onEnd", "" + msg);
			nm.cancel(notifyId3);
		}

		@Override
		public void onRequestError() {
			Log.d("onError", "error");
			nm.cancel(notifyId3);
		}

		@Override
		public void onLoginSuccess(dKSession session) {
			Log.d("onLoginSuccess", "session:" + session.getUrlGET());
			this.session = session;
			dKApiCall call = new dKApiCall(getActivity(), session, "test");
			Log.d("TEST", "URL:" + call.getUrl());
			call.setListener(this);
			call.call();
			nm.cancel(notifyId4);
		}

		@Override
		public void onLoginError(int code) {
			Log.d("onLoginError", "code:" + code);
			nm.cancel(notifyId4);
		}

		@Override
		public void onApiCallSuccess(JSONObject data) {
			Log.d("onApiCallSuccess", "data:" + data);
			nm.cancel(notifyId4);
		}

		@Override
		public void onApiCallError(int code) {
			Log.d("onApiCallError", "code:" + code);
			nm.cancel(notifyId4);
		}

		@Override
		public void onApiUploadSuccess(JSONObject data) {
			Log.d("onApiUploadSuccess", "data:" + data);
			nm.cancel(notifyId6);
		}

		@Override
		public void onApiUploadProgress(int progress) {
			Log.d("onApiUploadProgress", "" + progress);
			builder6.setProgress(100, progress, false);
			nm.notify(notifyId6, builder6.build());
			
		}

		@Override
		public void onApiUploadError(int code) {
			Log.d("onApiUploadError", "code:" + code);
			nm.cancel(notifyId6);
		}
	}

}
