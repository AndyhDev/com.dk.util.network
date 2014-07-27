package com.dk.upload;

import java.io.File;
import java.io.FilenameFilter;

import com.dk.util.network.Download;
import com.dk.util.network.DownloadListener;
import com.dk.util.network.DownloadResult;
import com.dk.util.network.Upload;
import com.dk.util.network.UploadListener;

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
	private static int notifyId2 = 1;
	private static NotificationManager nm;
	private static NotificationCompat.Builder builder;
	private static NotificationCompat.Builder builder2;
	
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
	public static class PlaceholderFragment extends Fragment implements OnClickListener, UploadListener, DownloadListener{
		private String[] mFileList;
		private File mPath = Environment.getExternalStorageDirectory();
		private String mChosenFile;
		private static final String FTYPE = ".";    
		private static final int DIALOG_LOAD_FILE = 1000;

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

		protected void upload(String mChosenFile2) {
			Upload up = new Upload(this.getActivity(), "https://dk-force.de/test.php");
			up.addFile("file", mChosenFile2);
			up.setListener(this);
			up.start();
			nm.notify(notifyId, builder.build());
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
	}

}
