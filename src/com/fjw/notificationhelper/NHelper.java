package com.fjw.notificationhelper;

import java.lang.reflect.Field;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class NHelper {
	private static NHelper instance;
	private static Handler mHandler = new Handler();

	private boolean inited = false;

	private int statusBarHeight;
	private View notiView;
	private boolean showStatused = false;
	WindowManager mWindowManager;
	DisplayMetrics mDisplayMetrics;
	Object popWinLock = new Object();
	private int mNotifacationIDIndex = 1000;

	boolean getStatusing = false;

	/**
	 * 单例类，
	 */
	private NHelper() {

	}

	public static NHelper getNHelper() {
		if (instance == null) {
			instance = new NHelper();
		}
		return instance;
	}

	/**
	 * 初始化,请在主activity的onCreate中调用此方法
	 * 
	 * @param activity
	 */
	public void init(Activity activity) {

		if (!inited) {
			statusBarHeight = getStatusBarHeight(activity);
			mDisplayMetrics = new DisplayMetrics();
			mWindowManager = (WindowManager) activity
					.getSystemService("window");
			mWindowManager.getDefaultDisplay().getMetrics(mDisplayMetrics);
			inited = true;
		}
	}

	/**
	 * 判断是否为平板模式
	 * 
	 * @param context
	 * @return
	 */
	public boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	/**
	 * 获取状态栏高度
	 * 
	 * @param context
	 * @return
	 */
	public int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			if (isTablet(context)) {
				field = c.getField("system_bar_height");
			} else {
				field = c.getField("status_bar_height");
			}
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

	/**
	 * 获取是否为当前活动应用
	 * 
	 * @param context
	 * @return
	 */
	public boolean isOnTop(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		if (context.getPackageName().equals(cn.getPackageName())) {
			return true;
		} else {
			return false;
		}
	}

	Runnable removePopWindow = new Runnable() {

		@Override
		public void run() {
			synchronized (popWinLock) {
				if (showStatused) {
					mWindowManager.removeView(notiView);
					showStatused = false;
				}
			}
		}
	};

	/**
	 * 显示状态信息，不能点击
	 * 
	 * @param context
	 * @param message
	 */
	public void showStatus(Context context, String message, boolean ring,
			boolean vibrat) {
		LayoutInflater inflater = LayoutInflater.from(context);
		if (notiView == null) {
			notiView = inflater.inflate(R.layout.notification, null);
		}

		TextView txtView = (TextView) notiView.findViewById(R.id.title);
		txtView.setText(message);

		WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

		wmParams.format = PixelFormat.RGBA_8888;
		wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		wmParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
				| WindowManager.LayoutParams.FLAG_FULLSCREEN
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

		wmParams.width = (int) (mDisplayMetrics.widthPixels / 4 * mDisplayMetrics.density);
		wmParams.height = statusBarHeight;

		if (isTablet(context)) {
			wmParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
			wmParams.y = -statusBarHeight;
			wmParams.x = (mDisplayMetrics.widthPixels - wmParams.width) / 2;
		} else {
			wmParams.gravity = Gravity.TOP | Gravity.RIGHT;
			wmParams.x = 0;
			wmParams.y = 0;
		}
		synchronized (popWinLock) {
			if (!showStatused) {
				mWindowManager.addView(notiView, wmParams);
				showStatused = true;
			}
		}

		if (ring)
			playRing(context);
		if (vibrat)
			vibrat(context);
		mHandler.removeCallbacks(removePopWindow);
		mHandler.postDelayed(removePopWindow, 3000);

	}

	private void playRing(Context context) {
		try {
			Uri notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(context, notification);
			r.play();
		} catch (Throwable ex) {
		}
	}

	private void vibrat(Context context) {
		Vibrator vib = (Vibrator) context
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(200);
	}

	public int showNotification(Context context, int iconID, String title,
			String message, Intent intent, int notificationID, boolean ring,
			boolean vibrat, boolean onGoing) {
		if (notificationID == 0) {
			notificationID = mNotifacationIDIndex++;
		}

		if (intent != null) {

			// 传递ID参数
			intent.putExtra("notificationID", notificationID);
		}
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(iconID, title,
				System.currentTimeMillis());
		if (onGoing) {
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
		} else {
			notification.flags &= ~Notification.FLAG_ONGOING_EVENT;
		}
		if (ring) {
			notification.defaults |= Notification.DEFAULT_SOUND;
		} else {
			notification.defaults &= ~Notification.DEFAULT_SOUND;
		}
		if (vibrat) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		} else {
			notification.defaults &= ~Notification.DEFAULT_VIBRATE;
		}
		PendingIntent contentIntent = PendingIntent.getActivity(context,
				R.string.app_name, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, title, message, contentIntent);
		nm.notify(notificationID, notification);

		return notificationID;
	}
}
