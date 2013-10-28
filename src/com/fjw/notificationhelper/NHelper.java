package com.fjw.notificationhelper;

import java.lang.reflect.Field;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class NHelper {
	private static NHelper instance;
	private static Handler mHandler;
	private final int STATUS_BAR_LOCATION_NONE = 0;
	private final int STATUS_BAR_LOCATION_TOP = 1;
	private final int STATUS_BAR_LOCATION_BOTTOM = 2;
	private boolean inited = false;
	private int statusBarLocation = STATUS_BAR_LOCATION_NONE;
	private int statusBarHeight;
	private View notiView;
	WindowManager mWindowManager;
	DisplayMetrics mDisplayMetrics;

	public enum NotifactionAlign {
		LEFT, CENTER, RIGHT
	}

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
	 * 初始化,使用此类需要先调用此函数
	 * 
	 * @param activity
	 */
	public void init(Activity activity) {
		statusBarHeight = getStatusBarHeight(activity);
		mDisplayMetrics = new DisplayMetrics();
		mWindowManager = (WindowManager) activity.getSystemService("window");
		mWindowManager.getDefaultDisplay().getMetrics(mDisplayMetrics);
		if (!inited) {
			// 平板模式statusbar在下方，手机模式statusbar在上方
			if (isTablet(activity)) {
				statusBarLocation = STATUS_BAR_LOCATION_BOTTOM;
			} else {
				statusBarLocation = STATUS_BAR_LOCATION_TOP;
			}
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

	public void sendStatusBarNotification(Context context,
			NotifactionAlign align, String message) {
		LayoutInflater inflater = LayoutInflater.from(context);
		if (notiView == null) {
			notiView = inflater.inflate(R.layout.notification, null);
		}
		notiView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.d("tttt", "click");

			}
		});

		// 平板模式，左侧为功能键位置
		if (isTablet(context) && align == NotifactionAlign.LEFT) {
			align = NotifactionAlign.RIGHT;
		}
		TextView txtView = (TextView) notiView.findViewById(R.id.title);
		txtView.setText(message);

		WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

		wmParams.format = PixelFormat.RGBA_8888;
		wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		wmParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
				| WindowManager.LayoutParams.FLAG_FULLSCREEN
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

		wmParams.width = (int) (mDisplayMetrics.density * 300);
		wmParams.height = statusBarHeight + 10;

		if (statusBarLocation == STATUS_BAR_LOCATION_BOTTOM) {
			wmParams.gravity = Gravity.BOTTOM;
			wmParams.y = -statusBarHeight;
		} else {
			wmParams.gravity = Gravity.TOP;
			wmParams.y = 0;
		}

		switch (align) {
		case LEFT:
			wmParams.gravity |= Gravity.LEFT;
			wmParams.x = 0;
			break;
		case CENTER:
			wmParams.gravity |= Gravity.LEFT;
			wmParams.x = (mDisplayMetrics.widthPixels - wmParams.width) / 2;
			break;
		case RIGHT:
			wmParams.gravity |= Gravity.RIGHT;
			wmParams.x = 0;
			break;
		}
		mWindowManager.addView(notiView, wmParams);
		new Thread() {

			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
				if (notiView != null) {
					// mWindowManager.removeView(notiView);
				}
			}

		}.start();
	}

}
