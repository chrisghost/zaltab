package com.zenexity.zaltab;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class TouchService extends Service implements OnTouchListener {

	private String TAG = this.getClass().getSimpleName();
	// window manager 
	private WindowManager mWindowManager;
	// linear layout will use to detect touch event
	private LinearLayout touchLayout;
	
	private List<LinearLayout> appBoxes = new ArrayList<LinearLayout>();
	
	ActivityManager manager;
	
	private float startX;
	private float startY;
	
	private boolean appSelected;
	 
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	 @Override
	 public void onCreate() {
	  super.onCreate();
	  
	  manager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
	  
	  // create linear layout
	  touchLayout = new LinearLayout(this);
	  // set layout width 30 px and height is equal to full screen
	  LayoutParams lp = new LayoutParams(30, LayoutParams.MATCH_PARENT);
	  touchLayout.setLayoutParams(lp);
	  // set color if you want layout visible on screen
	  touchLayout.setBackgroundColor(Color.CYAN); 
	  // set on touch listener
	  touchLayout.setOnTouchListener(this);

	  // fetch window manager object 
	   mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
	   // set layout parameter of window manager
	   WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
	           30, // width of layout 30 px
	           WindowManager.LayoutParams.MATCH_PARENT, // height is equal to full screen
	                 WindowManager.LayoutParams.TYPE_PHONE, // Type Phone, These are non-application windows providing user interaction with the phone (in particular incoming calls).
	                 WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // this window won't ever get key input focus  
	                 PixelFormat.TRANSLUCENT);      
		mParams.gravity = Gravity.RIGHT | Gravity.TOP;
		
		mWindowManager.addView(touchLayout, mParams);
	 }
	 
	 private void launchLastApp() {
		List<RecentTaskInfo> rtis = manager.getRecentTasks(5,ActivityManager.RECENT_WITH_EXCLUDED);
		Log.d(TAG, "apps:"+rtis.size());
		
		if(rtis.size() > 1) {
			RecentTaskInfo rti = rtis.get(1);
			Log.d(TAG, rti.toString());
			startActivity(rti.baseIntent);
		}
	 }
	 
	 private void createAppBox() {
		LinearLayout ll = new LinearLayout(this);

		LayoutParams lp = new LayoutParams(100, 100);
		ll.setLayoutParams(lp);
		ll.setBackgroundColor(Color.RED);

		// set layout parameter of window manager
		WindowManager.LayoutParams mParams =
				new WindowManager.LayoutParams(
						100, 100,
						WindowManager.LayoutParams.TYPE_PHONE,
						WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
						PixelFormat.TRANSLUCENT);
		mParams.gravity = Gravity.RIGHT | Gravity.CENTER;

		mWindowManager.addView(ll, mParams);
		appBoxes.add(ll);
	 }

	 public void cleanBoxes() {
		 if(mWindowManager != null) {
			 for(int i=0; i< appBoxes.size(); i++) {
				 mWindowManager.removeView(appBoxes.get(i));
			 }
			 appBoxes.clear();
		 }
	 }
	 
	 @Override
	 public void onDestroy() {
	   if(mWindowManager != null) {
		   if(touchLayout != null)
			   mWindowManager.removeView(touchLayout);
	   }
	   cleanBoxes();
	   super.onDestroy();
	 }

	 @Override
	 public boolean onTouch(View v, MotionEvent event) {
		 if(event.getAction() == MotionEvent.ACTION_MOVE) {
			float x = event.getRawX();
			float y = event.getRawY();
			double distance = Math.sqrt(
						Math.pow(Math.abs(x-startX), 2)
					+ 	Math.pow(Math.abs(y-startY), 2));

			if(distance > 300) {
				touchLayout.setBackgroundColor(Color.GREEN);
				createAppBox();
				appSelected = true;
			} else {
				 cleanInterface();
			}
		 }
		 if(event.getAction() == MotionEvent.ACTION_DOWN) {
			startX = event.getRawX();
			startY = event.getRawY();
		 }
		 if(event.getAction() == MotionEvent.ACTION_UP) {
			 if(appSelected) {
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