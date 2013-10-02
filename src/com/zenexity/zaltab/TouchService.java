package com.zenexity.zaltab;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class TouchService extends Service implements OnTouchListener {

	private String TAG = this.getClass().getSimpleName();
	// window manager
	private WindowManager mWindowManager;
	// linear layout will use to detect touch event
	private LinearLayout touchLayout;

	private List<View> appBoxes = new ArrayList<View>();

	ActivityManager manager;

	private float startX;
	private float startY;

	private boolean appSelected;

	private PackageManager pm;

	private ApplicationInfo lastAppInfo;
	private RecentTaskInfo lastTaskInfo;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		manager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		pm = getPackageManager();

		touchLayout = new LinearLayout(this);
		LayoutParams lp = new LayoutParams(30, LayoutParams.MATCH_PARENT);
		touchLayout.setLayoutParams(lp);
		touchLayout.setBackgroundColor(Color.CYAN);

		touchLayout.setOnTouchListener(this);

		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(30,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		mParams.gravity = Gravity.RIGHT | Gravity.TOP;

		mWindowManager.addView(touchLayout, mParams);
	}

	private void launchLastApp() {
		if (lastTaskInfo != null)
			startActivity(lastTaskInfo.baseIntent);
	}

	private List<Object> getLastAppInfo() {
		List<RunningTaskInfo> list = manager.getRunningTasks(10);
		List<RunningTaskInfo> listFiltered = new ArrayList<ActivityManager.RunningTaskInfo>();
		// Pour avoir les ident r.id = RecentTask.id
		for (RunningTaskInfo r : list) {
			if (!"com.android.launcher".equals(r.baseActivity.getPackageName())
					&& !"com.android.systemui".equals(r.baseActivity
							.getPackageName())) {
				listFiltered.add(r);
			}
		}

		List<RecentTaskInfo> rtis = manager.getRecentTasks(5,
				ActivityManager.RECENT_WITH_EXCLUDED);

		List<Object> res = new ArrayList<Object>();

		if (listFiltered.size() > 1) {
			RecentTaskInfo rti = null;
			for (int i = 0; i < rtis.size(); i++) {
				if (rtis.get(i).id == listFiltered.get(1).id) {
					rti = rtis.get(i);

					try {
						this.lastAppInfo = pm.getApplicationInfo(listFiltered
								.get(1).baseActivity.getPackageName(), 0);
						this.lastTaskInfo = rti;
						// String packageName = appInfo.packageName;
						// String appLabel = (String)
						// pm.getApplicationLabel(appInfo);
						// Drawable icon = pm.getApplicationIcon(appInfo);
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return res;
	}

	private void createAppBox() {
		getLastAppInfo();
		ImageView imgV = new ImageView(this);

		imgV.setImageDrawable(pm.getApplicationIcon(lastAppInfo));

		WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
				100, 100, WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mParams.gravity = Gravity.RIGHT | Gravity.CENTER;

		mWindowManager.addView(imgV, mParams);
		appBoxes.add(imgV);
	}

	public void cleanBoxes() {
		if (mWindowManager != null) {
			for (int i = 0; i < appBoxes.size(); i++) {
				mWindowManager.removeView(appBoxes.get(i));
			}
			appBoxes.clear();
		}
	}

	@Override
	public void onDestroy() {
		if (mWindowManager != null) {
			if (touchLayout != null)
				mWindowManager.removeView(touchLayout);
		}
		cleanBoxes();
		super.onDestroy();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			float x = event.getRawX();
			float y = event.getRawY();
			double distance = Math.sqrt(Math.pow(Math.abs(x - startX), 2)
					+ Math.pow(Math.abs(y - startY), 2));

			if (distance > 200) {
				touchLayout.setBackgroundColor(Color.GREEN);
				createAppBox();
				appSelected = true;
			} else {
				cleanInterface();
			}
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			startX = event.getRawX();
			startY = event.getRawY();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (appSelected) {
				launchLastApp();
			}
			cleanInterface();
		}
		return true;
	}

	private void cleanInterface() {
		touchLayout.setBackgroundColor(Color.CYAN);
		cleanBoxes();
		appSelected = false;
	}

}