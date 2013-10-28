package com.fjw.notificationhelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	Button btnStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		NHelper.getNHelper().init(this);
		btnStatus = (Button) findViewById(R.id.btnSendStatus);
		btnStatus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				NHelper.getNHelper().showStatus(MainActivity.this, "登录成功",
						false, false);
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
