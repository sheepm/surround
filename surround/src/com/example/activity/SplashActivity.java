package com.example.activity;

import com.example.surround.MainActivity;
import com.example.surround.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class SplashActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		new load().start();
	}
	
	class load extends Thread{
		@Override
		public void run() {
			super.run();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Intent intent = new Intent();
			intent.setClass(SplashActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
		
	}
	

}
