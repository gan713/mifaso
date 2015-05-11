package com.mifashow;

import java.util.ArrayList;

import com.baidu.location.LocationClient;
import com.mifashow.Service.OnServiceListener;
import com.mifashow.data.Constance.BANGTYPE;
import com.mifashow.data.Constance.CURLYTYPE;
import com.mifashow.data.Constance.FACESHAPE;
import com.mifashow.data.Constance.HAIRLENGTH;
import com.mifashow.data.Constance.SEX;
import com.mifashow.data.Constance.USERTYPE;
import com.mifashow.domain.User;
import com.mifashow.tool.AlertHelper;
import com.mifashow.tool.image.ImageFetcher;
import com.readystatesoftware.viewbadger.BadgeView;

import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;

public class MainActivity extends FragmentActivity{
	private App app;
	protected Service service;
	protected BadgeView bv_me,bv_message;
	private int navigationItemSelected;
	private long backKeyExitTime;
	private String keyword;
	public LocationClient mLocationClient;
	private FragmentTabHost mTabHost;
	private String tabText[];
	private int tabIconNormal[]={R.drawable.ic_hairstyle_normal,R.drawable.ic_salon_normal,R.drawable.ic_user_normal,R.drawable.ic_profile_normal,R.drawable.ic_chat_normal};
	private int tabIconOver[]={R.drawable.ic_hairstyle_over,R.drawable.ic_salon_over,R.drawable.ic_user_over,R.drawable.ic_profile_over,R.drawable.ic_chat_over};
	private Class<?> tapClass[]={PostingFragment.class,SalonFragment.class,UserFragment.class,ProfileFragment.class,MessageFragment.class};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("-MainActivity","onCreate");
		super.onCreate(savedInstanceState);
		app=(App) getApplication();
		if(app.getLoginUser()==null||app.getLoginUser().getUserId()<=0){
			User u=new User();
			u.setUserId(0);
			u.setUserName(getResources().getString(R.string.default_user_name));
			u.setAbout(getResources().getString(R.string.default_user_about));
			u.setUserType(USERTYPE.CUSTOMER);
			u.setBangType(BANGTYPE.LONGSIDE);
			u.setBirthday(System.currentTimeMillis());
			u.setBookedNum(0);
			u.setBookingNum(0);
			u.setCommentedNum(0);
			u.setCommentingNum(0);
			u.setCreateTime(System.currentTimeMillis());
			u.setCurlyType(CURLYTYPE.STRAIGHT);
			u.setFaceShape(FACESHAPE.STANDARD);
			u.setFollowerNum(0);
			u.setFollowingNum(0);
			u.setGrade(0);
			u.setHairLength(HAIRLENGTH.LONG);
			u.setHeight(165);
			u.setMarkedNum(0);
			u.setMarkingNum(0);
			u.setPassword("");
			u.setPostingNum(0);
			u.setRanking(0);
			u.setRating(0);
			u.setSex(SEX.FEMALE);
			u.setSign("");
			u.setWeight(55);
			app.setLoginUser(u);
		}
//		app.updateLocation();
		setContentView(R.layout.activity_main);
		
//		mLocationClient = new LocationClient(getApplicationContext());
//        mLocationClient.registerLocationListener(new MyLocationListener()); 
//        LocationClientOption option = new LocationClientOption();
//        option.setLocationMode(LocationMode.Hight_Accuracy);
//        option.setCoorType("bd09ll");
//        option.setScanSpan(60*1000);
//        option.setIsNeedAddress(true);
//        option.setNeedDeviceDirect(false);
//        mLocationClient.setLocOption(option);
//        if (mLocationClient != null){
//        	mLocationClient.start();
//        	mLocationClient.requestLocation();
//        	Log.d("-MainActivity","requestLocation");
//        }
		
		navigationItemSelected=-1;
		mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        tabText=getResources().getStringArray(R.array.main_tapText);
        for(int i=0;i<tabText.length;i++){
        	FrameLayout lo=new FrameLayout(this);
        	Button tabBt=new Button(mTabHost.getContext());
        	lo.setClickable(false);
        	lo.setFocusable(false);
        	tabBt.setClickable(false);
        	tabBt.setFocusable(false);
        	tabBt.setText(tabText[i]);
        	tabBt.setTextColor(getResources().getColor(R.color.shadow_medium));
        	tabBt.setCompoundDrawablePadding(3);
        	tabBt.setCompoundDrawablesWithIntrinsicBounds(0, tabIconNormal[i], 0, 0);
        	tabBt.setPadding(0, 10, 0, 10);
        	tabBt.setBackgroundResource(0);
        	tabBt.setTag(0);
    		lo.addView(tabBt);
        	mTabHost.addTab(mTabHost.newTabSpec(tabText[i]).setIndicator(lo), tapClass[i], null);
        	mTabHost.setOnTabChangedListener(new OnTabChangeListener(){

				@Override
				public void onTabChanged(String arg0) {
					for(int i=0;i<tabText.length;i++){
						if(mTabHost.getCurrentTab()==i){
							((Button)mTabHost.getTabWidget().getChildTabViewAt(i).findViewWithTag(0)).setCompoundDrawablesWithIntrinsicBounds(0, tabIconOver[i], 0, 0);
							((Button)mTabHost.getTabWidget().getChildTabViewAt(i).findViewWithTag(0)).setTextColor(getResources().getColor(R.color.hot));
							mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.shape_tab_on);
						}else{
							((Button)mTabHost.getTabWidget().getChildTabViewAt(i).findViewWithTag(0)).setCompoundDrawablesWithIntrinsicBounds(0, tabIconNormal[i], 0, 0);
							((Button)mTabHost.getTabWidget().getChildTabViewAt(i).findViewWithTag(0)).setTextColor(getResources().getColor(R.color.shadow_medium));
							mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(0);
						}
					}
					if(mTabHost.getCurrentTab()==3){
						bv_me.setText("0");
						bv_me.hide();
					}else if(mTabHost.getCurrentTab()==4){
						bv_message.setText("0");
						bv_message.hide();
					}
					
				}
        		
        	});
        }
        ((Button)mTabHost.getTabWidget().getChildTabViewAt(0).findViewWithTag(0)).setCompoundDrawablesWithIntrinsicBounds(0, tabIconOver[0], 0, 0);
		((Button)mTabHost.getTabWidget().getChildTabViewAt(0).findViewWithTag(0)).setTextColor(getResources().getColor(R.color.hot));
        mTabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.shape_tab_on);
        bv_me = new BadgeView(this, mTabHost.getTabWidget(), 3);
        bv_me.setIncludeFontPadding(false);
        bv_me.hide();
        bv_message=new BadgeView(this, mTabHost.getTabWidget(), 4);
        bv_message.setIncludeFontPadding(false);
        bv_message.hide();
        keyword="";
		setupActionBar();
	}
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("-MainActivity","onResume");
		Intent intent=new Intent(MainActivity.this,Service.class);
		startService(intent);
		bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE);
	}
	@Override
	protected void onDestroy() {
		Log.d("-MainActivity","onDestroy");
		super.onDestroy();
		service.setListener(null);  
		this.unbindService(serviceConnection);
	}
	
	private void setupActionBar(){
		Log.d("-MainActivity","setupActionBar");
		ActionBar ab = getActionBar();
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//			ab.setDisplayHomeAsUpEnabled(true);
//		}
	    ab.setDisplayShowTitleEnabled(false);
//	    ab..setHomeButtonEnabled(true);
//	    getSupportActionBar().setHomeButtonEnabled(true);
	    ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	    ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, app.getLoginUser().getUserId()==0?R.array.action_list_visitor:R.array.action_list, android.R.layout.simple_spinner_dropdown_item);
	    ActionBar.OnNavigationListener navigationListener = new ActionBar.OnNavigationListener(){
	    	@Override
	    	public boolean onNavigationItemSelected(int itemPosition, long itemId){
	    		Log.d("-MainActivity","onNavigationItemSelected");
	    		if(navigationItemSelected!=-1 && itemPosition!=navigationItemSelected){
	    			if(mTabHost.getCurrentTab()==0)mTabHost.setCurrentTab(1);
	    			mTabHost.setCurrentTab(0);
	    		}
    			navigationItemSelected=itemPosition;
	    		return false;
			}
	    };
	    ab.setListNavigationCallbacks(arrayAdapter, navigationListener);
	}
	public void map(MenuItem item){
		if(app.getLatitude()==0&& app.getLongitude()==0){
			AlertHelper.showToast(this, R.string.error_locationError);
			return;
		}
		Intent mapIntent = new Intent();
	      mapIntent.setClass(this, MapActivity.class);
	      startActivity(mapIntent);
	}
	public void search(MenuItem item){
		InputDialog searchDialog = InputDialog.newInstance(getResources().getString(mTabHost.getCurrentTab()==1?R.string.dialog_searchSalon_title:R.string.dialog_searchUser_title), this.keyword,getResources().getString(mTabHost.getCurrentTab()==1?R.string.dialog_searchSalon_hint:R.string.dialog_searchUser_hint), InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE, 50);
		searchDialog.addListener(new InputDialog.InputListener(){

			@Override
			public void OnFinish(String inputText) {
				inputText=inputText.trim();
				if(inputText==null || inputText.length()<2){
					AlertHelper.showToast(MainActivity.this, R.string.error_searchKeywordError);
					return;
				}
				keyword=inputText;
				if(mTabHost.getCurrentTab()==1){
					SalonFragment searchFragment = new SalonFragment();
				    Bundle searchFragmentBundle = new Bundle();
				    searchFragmentBundle.putSerializable("sourceType", SalonFragment.SOURCETYPE.SEARCH);
				    searchFragmentBundle.putString("sourceKeyword",inputText);
				    searchFragment.setArguments(searchFragmentBundle);
				    searchFragment.show(getSupportFragmentManager(), ""+searchFragment.getId());
				}else{
					UserFragment searchFragment = new UserFragment();
				    Bundle searchFragmentBundle = new Bundle();
				    searchFragmentBundle.putSerializable("sourceType", UserFragment.SOURCETYPE.SEARCH);
				    searchFragmentBundle.putString("sourceKeyword",inputText);
				    searchFragment.setArguments(searchFragmentBundle);
				    searchFragment.show(getSupportFragmentManager(), ""+searchFragment.getId());
				}
				
			}
	    	  
	      });
		searchDialog.show(this.getSupportFragmentManager(), "search");
	}
	public void share(MenuItem item){
		Intent intent=new Intent(Intent.ACTION_SEND_MULTIPLE);
		intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.download_link));
        intent.putExtra("Kdescription", getResources().getString(R.string.download_link));
        ArrayList<Uri> uris=new ArrayList<Uri>();
        uris.add(Uri.fromFile(ImageFetcher.fetchBitmap(this,R.raw.screenshot_1)));
        uris.add(Uri.fromFile(ImageFetcher.fetchBitmap(this,R.raw.screenshot_2)));
        uris.add(Uri.fromFile(ImageFetcher.fetchBitmap(this,R.raw.screenshot_3)));
        uris.add(Uri.fromFile(ImageFetcher.fetchBitmap(this,R.raw.screenshot_4)));
        uris.add(Uri.fromFile(ImageFetcher.fetchBitmap(this,R.raw.screenshot_5)));
        uris.add(Uri.fromFile(ImageFetcher.fetchBitmap(this,R.raw.screenshot_6)));
        uris.add(Uri.fromFile(ImageFetcher.fetchBitmap(this,R.raw.qrcode)));
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_title)));
	}
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case android.R.id.home:
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case android.R.id.home:
//			Intent itent=new Intent(this,AboutActivity.class);
//			startActivity(itent);
//			break;
//		}
//		return super.onOptionsItemSelected(item);
//		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(System.currentTimeMillis()-backKeyExitTime>2000){
				Toast.makeText(this, getString(R.string.welcome_to_text_exit), Toast.LENGTH_SHORT).show();
				backKeyExitTime=System.currentTimeMillis();
			}else{
				finish();
			}
			return true;
		}else{
			return false;
		}
	}
	private ServiceConnection serviceConnection = new ServiceConnection() { 
        @Override
		public void onServiceConnected(ComponentName className, IBinder iBinder) { 
                service = ((Service.Binder)iBinder).getService();
                service.setListener(new OnServiceListener(){

					@Override
					public void onNewMessages(int num) {
						bv_message.setText(""+num);
						bv_message.toggle();
					}
                });
//                service.checkVersion();
        } 

        @Override
		public void onServiceDisconnected(ComponentName className) { 
        	service.setListener(null);    
//        	service = null; 
        } 
}; 
//public class MyLocationListener implements BDLocationListener {
//	@Override
//	public void onReceiveLocation(BDLocation bdl) {
//		Log.d("-MainActivity", "onReceiveLocation");
//		if (bdl == null)return ;
//		app.setLatitude((float)bdl.getLatitude());
//		app.setLongitude((float)bdl.getLongitude());
//		app.setAddress(bdl.getAddrStr());
//		app.updateLocation();
//	}
//	@Override
//	public void onReceivePoi(BDLocation bdLocation) {
//		onReceiveLocation(bdLocation);
//	}
//}
}
