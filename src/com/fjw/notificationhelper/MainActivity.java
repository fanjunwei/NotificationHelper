package com.fjw.notificationhelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		NHelper.getNHelper().init(this);
		NHelper.getNHelper().sendStatusBarNotification(this,
				NHelper.NotifactionAlign.RIGHT, "sdfasdfadsfsdfasdfsadfasdfafsdsdsadfasdfasdfasdfasdfdsf");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
