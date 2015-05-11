package com.mifashow;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class WelcomeActivity extends Activity implements OnPageChangeListener{

	private App app;
	private long backKeyExitTime;
	private ImageView[] imageViewsOfDot;
	private int[] arrayOfImageId={
//		R.drawable.welcome_1,
//		R.drawable.welcome_2,
//		R.drawable.welcome_3,
//		R.drawable.welcome_4,
//		R.drawable.welcome_5
};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	getActionBar().hide();
        setContentView(R.layout.activity_welcome);
        app=(App)getApplication();
        ViewPager vp_welcome=(ViewPager) this.findViewById(R.id.welcome_vp);
        PagerAdapter adapter=new PagerAdapterForImage(this,arrayOfImageId);
        vp_welcome.setAdapter(adapter);
        vp_welcome.setCurrentItem(0);
        imageViewsOfDot=new ImageView[arrayOfImageId.length];
		for(int i=0;i<arrayOfImageId.length;i++){
			ImageView imageView=new ImageView(this);
			imageViewsOfDot[i]=imageView;
			if(i==0)imageViewsOfDot[i].setBackgroundResource(R.drawable.shape_dot_down);
			else imageViewsOfDot[i].setBackgroundResource(R.drawable.shape_dot_up);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 15;
			layoutParams.rightMargin = 15;
			layoutParams.width=12;
			layoutParams.height=12;
			((LinearLayout)this.findViewById(R.id.welcome_lo_indicator)).addView(imageView,layoutParams);
		}
        vp_welcome.setOnPageChangeListener(this);
//    	if(app.getLoginUser() !=null){
//    		app.setCurrentUserId(app.getLoginUserId());
//			Intent intent = new Intent();
//			intent.setClass(WelcomeActivity.this, MainActivity.class);
//			startActivity(intent);
//			finish();
//    	}else 
    		
    }
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPageSelected(int arg0) {
		for(int i=0; i<imageViewsOfDot.length; i++){
			if(i == arg0){
				imageViewsOfDot[i].setBackgroundResource(R.drawable.shape_dot_down);
				}else{
					imageViewsOfDot[i].setBackgroundResource(R.drawable.shape_dot_up);
					}
			}		
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


    public void onClick_register(View view){
    	Intent intent=new Intent();
    	intent.setClass(this, RegisterActivity.class);
    	this.startActivity(intent);
    	finish();
    }
    public void onClick_login(View view){
    	Intent intent=new Intent();
    	intent.setClass(this, LoginActivity.class);
    	this.startActivity(intent);
    	finish();
    }
    class PagerAdapterForImage extends PagerAdapter {
    	private ImageView[] imageViewsOfWelcome;
    	public PagerAdapterForImage(Context context,int[] arrayOfImageId){
    		imageViewsOfWelcome = new ImageView[arrayOfImageId.length];  
    		for(int i=0; i<arrayOfImageId.length; i++){
    			ImageView imageView = new ImageView(context);
    			imageView.setImageResource(arrayOfImageId[i]);
    			imageView.setScaleType(ScaleType.CENTER_CROP);
    			imageViewsOfWelcome[i] = imageView;
    		}
    	}
    	@Override
    	public Object instantiateItem(ViewGroup container, int position){
    		container.addView(imageViewsOfWelcome[position], 0);
    		return imageViewsOfWelcome[position];
    	}
    	@Override
    	public void destroyItem(ViewGroup container, int position, Object object){
    		container.removeView(imageViewsOfWelcome[position]);
    	}


    	@Override
    	public int getCount() {
    		// TODO Auto-generated method stub
    		return imageViewsOfWelcome.length;
    	}

    	@Override
    	public boolean isViewFromObject(View arg0, Object arg1) {
    		// TODO Auto-generated method stub
    		return arg0==arg1;
    	}

    }

}
