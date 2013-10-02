package com.zenexity.zaltab;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	 Intent globalService;
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.activity_main);
	  globalService = new Intent(this, TouchService.class);
	  
	 }


	 public void buttonClicked(View v){

		 final ImageView imgV1 = (ImageView)findViewById(R.id.imageView1);

		 ActivityManager manager = 
			    (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

		 PackageManager pm = getPackageManager();

		 List<RunningTaskInfo> list = manager.getRunningTasks(10);
		 List<RunningTaskInfo> listFiltered = new ArrayList<ActivityManager.RunningTaskInfo>();
		 // Pour avoir les ident r.id = RecentTask.id
		 for(RunningTaskInfo r : list) {
			 if(!"com.android.launcher".equals(r.baseActivity.getPackageName()) 
			    && !"com.android.systemui".equals(r.baseActivity.getPackageName()) ){
				 listFiltered.add(r);
			 }
		 }
		 
	    for (int i=0; i< listFiltered.size(); i++)
	    {
	        ApplicationInfo appInfo;
			try {
				appInfo = pm.getApplicationInfo(list.get(i).baseActivity.getPackageName(), 0);
		        String packageName = appInfo.packageName;
		        String appLabel = (String) pm.getApplicationLabel(appInfo);
		        Drawable icon = pm.getApplicationIcon(appInfo);
		        System.out.println("appLabel ==> " +appLabel);
		        System.out.println("packageName ==> " +packageName);
		        System.out.println("Icon ==> " +icon);
		        imgV1.setImageDrawable(icon);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	  
	  if(v.getTag() == null){
	   startService(globalService);
	   v.setTag("on");
	   Toast.makeText(this, "Start Service", Toast.LENGTH_SHORT).show();
	  }
	  else{
	   stopService(globalService);
	   v.setTag(null);
	   Toast.makeText(this, "Stop Service", Toast.LENGTH_SHORT).show();
	  }
	  
	 }


}
