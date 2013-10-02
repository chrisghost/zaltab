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

	private List<ApplicationInfo> lastAppInfo = new ArrayList<ApplicationInfo>();
	private List<RecentTaskInfo> lastTaskInfo = new ArrayList<RecentTaskInfo>();

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
		LayoutParams lp = new LayoutParams(10, LayoutParams.MATCH_PARENT);
		touchLayout.setLayoutParams(lp);

		touchLayout.setOnTouchListener(this);

		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(10,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		mParams.gravity = Gravity.RIGHT | Gravity.TOP;

		mWindowManager.addView(touchLayout, mParams);
	}

	private void launchLastApp() {
		if (lastTaskInfo.size() > 0 && lastTaskInfo.get(0) != null)
			startActivity(lastTaskInfo.get(0).baseIntent);
	}

	private void getLastAppInfo() {
		List<RunningTaskInfo> list = manager.getRunningTasks(10);
		List<RunningTaskInfo> listFiltered = new ArrayList<ActivityManager.RunningTaskInfo>();
		// Pour avoir les ident r.id = RecentTask.id
		for (RunningTaskInfo r : list) {
			if (!"com.android.launcher".equals(r.baseActivity.getPackageName())
					&& !"com.android.systemui".equals(r.baseActivity.getPackageName())
					&& !"com.zenexity.zaltab".equals(r.baseActivity.getPackageName())
					) {
				listFiltered.add(r);
			}
		}

		List<RecentTaskInfo> rtis = manager.getRecentTasks(10,
				ActivityManager.RECENT_WITH_EXCLUDED);
		
		this.lastAppInfo.clear();
		this.lastTaskInfo.clear();


		for (int i = 0; i < listFiltered.size(); i++) {
			//We must permute the first 2 entries ( the first is the current app)
			RunningTaskInfo _task = (i == 0) ? listFiltered.get(1) : (i ==1) ? listFiltered.get(0) : listFiltered.get(i);
			boolean found = false;
			for (int j = 0; j < rtis.size() && !found; j++) {
				if (rtis.get(j).id == _task.id) {
					found = true;
					RecentTaskInfo rti = rtis.get(j);

					try {
						this.lastAppInfo.add(pm.getApplicationInfo(_task.baseActivity.getPackageName(), 0));
						this.lastTaskInfo.add(rti);
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
	}

	private void createAppBox() {
		getLastAppInfo();
		
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);

		for(int i = lastAppInfo.size()-1; i >= 0; i--) {
			ApplicationInfo appInfo = lastAppInfo.get(i);
			ImageView imgV = new ImageView(this);
			try {
				imgV.setImageDrawable(pm.getApplicationIcon(appInfo));
			} catch (NullPointerException npe) {
				imgV.setImageResource(R.drawable.no_icon_app);
			}
	
			ll.addView(imgV);
		}

		WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
				100, 300, WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mParams.gravity = Gravity.RIGHT | Gravity.CENTER;

		mWindowManager.addView(ll, mParams);

		appBoxes.add(ll);
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

			if (!appSelected && distance > 200) {
				createAppBox();
				appSelected = true;
			} else if(appSelected && distance < 200) {
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
		cleanBoxes();
		appSelected = false;
	}

}