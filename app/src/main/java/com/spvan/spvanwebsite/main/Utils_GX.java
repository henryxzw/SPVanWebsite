package com.spvan.spvanwebsite.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import android.app.Activity;
import android.app.Dialog; 
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.spvan.spvanwebsite.R;

public class Utils_GX {
	// /////////////////////////////////////////////////////////////////////////////////////////////////
	private static Activity activity;
	public static Dialog logoutDialog1;
	public static String gx_ver, gx_rizi, gx_version;
	// ////////////
	// 返回的安装包url
	public static String apkUrl = "";
	/* 下载包安装路径 */
	public static String savePath = "/sdcard/updatedemo/";
	public static String vername;
	public static   String saveFileName ;
	/* 进度条与通知ui刷新的handler和msg常量 */
	public static ProgressBar mProgress;
	public static TextView gx_baifengbie;

	private static final int DOWN_UPDATE = 1;

	private static final int DOWN_OVER = 2;

	private static int progress;

	private static Thread downLoadThread;

	public static boolean interceptFlag = false;

	private static Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				gx_baifengbie.setText(progress + "%");
				break;
			case DOWN_OVER:
				logoutDialog1.dismiss();
				installApk();
				break;
			default:
				break;
			}
		};
	};
	// /////////////////////////////////
	private static Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				URL url = new URL(apkUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdir();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);
				int count = 0;
				byte buf[] = new byte[1024];
				do {
					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// 下载完成通知安装
						mHandler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载.
				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

	/**
	 * 下载apk
	 * 
	 * @param url
	 */

	private static void downloadApk() {
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	/**
	 * 安装apk
	 * 
	 * @param url
	 */
	private static void installApk() {
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		activity.startActivity(i);

	}

	public static void dialog_gengxing(String gx_rizi1, String gx_ver1,
			String gx_version1, String apkUrl1, String vername1,
			Activity activity1,String apkName) {
		activity = activity1;
		gx_rizi = gx_rizi1;
		gx_ver = gx_ver1;
		gx_version = gx_version1;
		apkUrl = apkUrl1;
		vername = vername1;
		///给下载的APK命名
		saveFileName = savePath + "naboya" + vername
				+ ".apk";
		System.out.println("---------->"+vername);
	///	Toast.makeText(activity1, "------>"+vername, Toast.LENGTH_SHORT).show();
		logoutDialog1 = new Dialog(activity, R.style.DiDiDialog);
		logoutDialog1.setContentView(R.layout.gengxing);
		logoutDialog1.setCancelable(false);
		Button qure_but = (Button) logoutDialog1.findViewById(R.id.qure_but);
		Button quxiao_but = (Button) logoutDialog1
				.findViewById(R.id.quxiao_but);
		Button quxiao_gengxing = (Button) logoutDialog1
				.findViewById(R.id.quxiao_gengxing);
		TextView gxrizi = (TextView) logoutDialog1.findViewById(R.id.gx_rizi);
		TextView gxversion = (TextView) logoutDialog1
				.findViewById(R.id.gx_version);
		TextView textView = (TextView)logoutDialog1.findViewById(R.id.textView4);
		textView.setText(apkName);
		gx_baifengbie = (TextView) logoutDialog1
				.findViewById(R.id.gx_baifengbie);
		mProgress = (ProgressBar) logoutDialog1.findViewById(R.id.progress);
		final LinearLayout linear_gengxingy = (LinearLayout) logoutDialog1
				.findViewById(R.id.linear_gengxingy);
		final LinearLayout linear_gengxingn = (LinearLayout) logoutDialog1
				.findViewById(R.id.linear_gengxingn);
		gxrizi.setText(gx_rizi);
		gxversion.setText("V " + gx_ver);
		// /////确认下载
		qure_but.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				linear_gengxingn.setVisibility(View.GONE);
				linear_gengxingy.setVisibility(View.VISIBLE);
				interceptFlag = false;
				downloadApk();
			}
		});
		// /////以后再说
		quxiao_but.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				logoutDialog1.dismiss();
			}
		});
		// /////取消（正下载的时候）
		quxiao_gengxing.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				logoutDialog1.dismiss();
				interceptFlag = true;
			}
		});
		if (logoutDialog1.isShowing())
			return;
		logoutDialog1.show();
	}
}
