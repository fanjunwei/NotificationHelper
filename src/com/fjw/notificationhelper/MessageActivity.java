package com.fjw.notificationhelper;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MessageActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		int id = getIntent().getIntExtra("notificationID", 0);

		NotificationManager nm = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// 清除通知
		nm.cancel(id);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		int id = intent.getIntExtra("notificationID", 0);
		NotificationManager nm = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// 清除通知
		nm.cancel(id);
	}

}
