package com.fjw.notificationhelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class MainActivity extends Activity {

	Button btnStatus;
	Button btnNotification;
	Button btnOnGoing;
	Button btnCheckcurr;
	CheckBox cbxRing;
	CheckBox cbxVibrat;

	int notifactionID = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		cbxRing = (CheckBox) findViewById(R.id.cbxRing);
		cbxVibrat = (CheckBox) findViewById(R.id.cbxVibrat);
		NHelper.getNHelper().init(this);

		// 显示状态信息
		btnStatus = (Button) findViewById(R.id.btnSendStatus);
		btnStatus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean ring = cbxRing.isChecked();
				boolean vibrat = cbxVibrat.isChecked();
				NHelper.getNHelper().showStatus(MainActivity.this, "登录成功",
						ring, vibrat);
			}
		});

		// 显示通知
		btnNotification = (Button) findViewById(R.id.btnSendNotification);
		btnNotification.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean ring = cbxRing.isChecked();
				boolean vibrat = cbxVibrat.isChecked();
				Intent intent = new Intent(MainActivity.this,
						MessageActivity.class);
				// 注意，相同的目标activity要使用相同ID
				notifactionID = NHelper.getNHelper().showNotification(
						MainActivity.this, R.drawable.ic_launcher, "新消息",
						"消息消息消息", intent, notifactionID, ring, vibrat, false);
			}
		});

		// 显示正在运行图标
		btnOnGoing = (Button) findViewById(R.id.btnOnGoing);
		btnOnGoing.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean ring = cbxRing.isChecked();
				boolean vibrat = cbxVibrat.isChecked();
				Intent intent = new Intent(MainActivity.this,
						MainActivity.class);
				// 固定一个ID
				NHelper.getNHelper().showNotification(MainActivity.this,
						R.drawable.ic_launcher, "正在运行", "", intent, 900, ring,
						vibrat, true);
			}
		});

		// 循环判断是否为当前应用
		btnCheckcurr = (Button) findViewById(R.id.btnCheckcurr);
		btnCheckcurr.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread() {

					@Override
					public void run() {
						while (true) {
							boolean curr = NHelper.getNHelper().isOnTop(
									MainActivity.this);
							Log.d("NHelper", "当前应用:" + curr);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

				}.start();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
