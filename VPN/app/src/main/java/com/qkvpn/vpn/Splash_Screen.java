package com.qkvpn.vpn;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;



public class Splash_Screen extends AppCompatActivity {
	private Dialog dialog_home_Activity;
	private View progress_bar_home_Activity;

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@SuppressLint("ObsoleteSdkInt")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_window);
		if (getWindow() != null) {
			Window window = getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		}



			new Handler().postDelayed(() -> {
				startActivity(new Intent(Splash_Screen.this, MainActivity.class));
				finish();
			}, 3000);


	}

}