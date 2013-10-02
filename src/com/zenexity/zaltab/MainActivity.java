package com.zenexity.zaltab;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	Intent globalService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		globalService = new Intent(this, TouchService.class);

	}

	public void buttonClicked(View v) {

		if (v.getTag() == null) {
			startService(globalService);
			v.setTag("on");
			Toast.makeText(this, "Start Service", Toast.LENGTH_SHORT).show();
		} else {
			stopService(globalService);
			v.setTag(null);
			Toast.makeText(this, "Stop Service", Toast.LENGTH_SHORT).show();
		}

	}

}
