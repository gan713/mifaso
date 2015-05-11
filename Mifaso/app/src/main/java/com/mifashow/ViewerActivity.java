package com.mifashow;

import com.mifashow.data.Constance.AGETYPE;
import com.mifashow.data.Constance.FACESHAPE;
import com.mifashow.data.Constance.SERVICETYPE;
import com.mifashow.domain.Posting;
import com.mifashow.tool.ImageHelper;
import com.mifashow.ui.TouchImageView;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewerActivity extends Activity{
	ViewPager vp;
	TextView tv_page,tv_sumpage,tv_ageType,tv_faceShape,tv_hairType,tv_serviceType;
//	ImageView[] views;
	String[] renderingUrl;
	Posting posting;
	int initPage;

	public ViewerActivity() {
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewer);
		getActionBar().hide();
		renderingUrl=getIntent().getStringArrayExtra("renderingUrl");
		posting=(Posting) getIntent().getSerializableExtra("posting");
		initPage=getIntent().getIntExtra("init", 0);
		if(posting!=null){
			renderingUrl=posting.getImages();
			tv_ageType=(TextView) findViewById(R.id.viewer_tv_ageType);
			StringBuilder sb_ageType=new StringBuilder();
			for(AGETYPE ageType:posting.getAgeTypes()){
				sb_ageType.append(getResources().getStringArray(R.array.enum_age)[ageType.ordinal()]).append(',').append(' ');
			}
			sb_ageType.delete(sb_ageType.length()-2, sb_ageType.length()-1);
			SpannableStringBuilder ageTypeBuilder = new SpannableStringBuilder(getResources().getString(R.string.viewer_tv_ageType,sb_ageType.toString()));
			ageTypeBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.shadow_medium)), 0, getResources().getString(R.string.viewer_tv_ageType,"").length(), 33);
			tv_ageType.setText(ageTypeBuilder);
			tv_faceShape=(TextView) findViewById(R.id.viewer_tv_faceShape);
			StringBuilder sb_faceShape=new StringBuilder();
			for(FACESHAPE shapeShape:posting.getFaceShapes()){
				sb_faceShape.append(getResources().getStringArray(R.array.enum_faceShape)[shapeShape.ordinal()]).append(',').append(' ');
			}
			sb_faceShape.delete(sb_faceShape.length()-2,sb_faceShape.length()-1);
			SpannableStringBuilder faceShapeBuilder = new SpannableStringBuilder(getResources().getString(R.string.viewer_tv_faceShape,sb_faceShape.toString()));
			faceShapeBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.shadow_medium)), 0, getResources().getString(R.string.viewer_tv_faceShape,"").length(), 33);
			tv_faceShape.setText(faceShapeBuilder);
			tv_hairType=(TextView) findViewById(R.id.viewer_tv_hairType);
			StringBuilder sb_hairType=new StringBuilder();
			sb_hairType.append(getResources().getStringArray(R.array.enum_bangType)[posting.getBangType().ordinal()]).append(" ").append(getResources().getStringArray(R.array.enum_curlyType)[posting.getCurlyType().ordinal()]).append(" ").append(getResources().getStringArray(R.array.enum_hairLength)[posting.getHairLength().ordinal()]);
			SpannableStringBuilder hairTypeBuilder = new SpannableStringBuilder(getResources().getString(R.string.viewer_tv_hairType,sb_hairType.toString()));
			hairTypeBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.shadow_medium)), 0, getResources().getString(R.string.viewer_tv_hairType,"").length(), 33);
			tv_hairType.setText(hairTypeBuilder);
			if(posting.getServiceTypes()!=null){
			tv_serviceType=(TextView) findViewById(R.id.viewer_tv_serviceType);
			StringBuilder sb_serviceType=new StringBuilder();
			for(SERVICETYPE serviceType:posting.getServiceTypes()){
				sb_serviceType.append(getResources().getStringArray(R.array.enum_services)[serviceType.ordinal()]).append(',').append(' ');
			}
			sb_serviceType.delete(sb_serviceType.length()-2,sb_serviceType.length()-1);
			SpannableStringBuilder servictTypeBuilder = new SpannableStringBuilder(getResources().getString(R.string.viewer_tv_serviceType,sb_serviceType.toString()));
			servictTypeBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.shadow_medium)), 0, getResources().getString(R.string.viewer_tv_serviceType,"").length(), 33);
			tv_serviceType.setText(servictTypeBuilder);
			}
		}
//		views = new ImageView[renderingUrl.length];  
//		for(int i=0; i<renderingUrl.length; i++){
//			TouchImageView imageView = new TouchImageView(this);
//			ImageHelper.loadImg(imageView,renderingUrl[i],0,true);
//			views[i] = imageView;
//		}
		tv_page=(TextView) findViewById(R.id.viewer_tv_page);
		tv_page.setText("1");
		tv_sumpage=(TextView) findViewById(R.id.viewer_tv_sumpage);
		tv_sumpage.setText("/"+renderingUrl.length);
		vp=(ViewPager) findViewById(R.id.viewer_vp_view);
		vp.setOnPageChangeListener(new OnPageChangeListener(){

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

				tv_page.setText(""+(arg0+1));
				
			}
			
		});
		vp.setAdapter(new PagerAdapter(){

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return renderingUrl.length;
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0==arg1;
			}
			
			@Override
	    	public Object instantiateItem(ViewGroup container, int position){
				TouchImageView imageView = new TouchImageView(ViewerActivity.this);
//				imageView.setImageResource(R.drawable.placeholder);
				ImageHelper.loadImg(imageView,renderingUrl[position],0,true);
	    		container.addView(imageView, 0);
	    		return imageView;
	    	}
	    	@Override
	    	public void destroyItem(ViewGroup container, int position, Object object){
//	    		container.removeViewAt(position);
	    	}
			
		});
		vp.setCurrentItem(initPage);
	}
	

}
