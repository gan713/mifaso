package com.mifashow;

import android.annotation.TargetApi;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		setupActionBar();
		TextView tv_version=(TextView) findViewById(R.id.about_tv_version);
		try{
		PackageManager manager = this.getPackageManager();
		PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
		String version = info.versionName;
		tv_version.setText(version);
		}catch(Exception e){
			e.printStackTrace();
			tv_version.setVisibility(View.GONE);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}
	

}
