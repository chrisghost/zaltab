package com.zenexity.zaltab;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Intent;
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
	  final TextView vTextView2 = (TextView)findViewById(R.id.textView2);
	  final TextView vTextView1 = (TextView)findViewById(R.id.textView1);
	  final ImageView imgV = (ImageView)findViewById(R.id.imageView1);
	  ActivityManager manager = 
			    (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
			List<RunningTaskInfo> rtinfo = manager.getRunningTasks(1);
			List<RecentTaskInfo> rtis = manager.getRecentTasks(5,ActivityManager.RECENT_WITH_EXCLUDED);
			List<RunningTaskInfo> taskInfos = null;
			//GET recent Application
			//for (RecentTaskInfo rti : rtis)
			//{
				//vTextView2.setText(vTextView2.getText() + "\n" + rti.id );
	 		//}
			for (RunningTaskInfo rt : rtinfo) {
				imgV.setImageBitmap(rt.thumbnail);
			}
			//}
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
