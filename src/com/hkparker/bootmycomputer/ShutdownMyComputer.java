package com.hkparker.bootmycomputer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ShutdownMyComputer extends Service {
	
	@Override
    public void onCreate() {
        super.onCreate();
    }
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		Intent i = new Intent(this, ShutdownActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
		return  START_NOT_STICKY;
	}
		
    @Override
    public void onDestroy() {
        super.onDestroy();
    }	
		
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}	
}
