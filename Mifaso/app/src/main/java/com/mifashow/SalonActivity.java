package com.mifashow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mifashow.CountDialog.CountListener;
import com.mifashow.InputDialog.InputListener;
import com.mifashow.data.Constance;
import com.mifashow.data.Constance.USERTYPE;
import com.mifashow.domain.Salon;
import com.mifashow.domain.User;
import com.mifashow.tool.AlertHelper;
import com.mifashow.tool.DateHelper;
import com.mifashow.tool.ImageHelper;
import com.mifashow.tool.image.DensityUtil;
import com.mifashow.ui.RoundAngleImageView;
import com.mifashow.ui.WrappedViewGroup;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class SalonActivity extends FragmentActivity implements OnClickListener,OnLongClickListener{
	private App app;
	private Menu menu;
	private Salon salon;
	private long salonId;
	private Button bt_attach,bt_invite,bt_withdraw,bt_join;
	private LinearLayout lo_stylist,lo_stylists;
	private TextView tv_name,tv_address,tv_area,tv_environment,tv_phone,tv_cut,tv_permanent,tv_color,tv_braid,tv_treatment,tv_discount,tv_recruitment;
	private WrappedViewGroup wv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_salon);
		app=(App) getApplication();
		salonId=getIntent().getLongExtra("salonId", 0);
		if(salonId!=0L){
			List<Salon> cachedSalons=app.getDiscoverySalons();
			for(Salon s:cachedSalons){
				if(s.getSalonId()==salonId){
					salon=s;
					break;
				}
			}
		}
		setupActionBar();
		wv=(WrappedViewGroup) findViewById(R.id.salon_wv);
		bt_attach=(Button) findViewById(R.id.salon_bt_attach);
		bt_attach.setOnClickListener(this);
		tv_name=(TextView) findViewById(R.id.salon_tv_name);
		tv_name.setOnClickListener(this);
		tv_address=(TextView) findViewById(R.id.salon_tv_address);
		tv_address.setOnClickListener(this);
		tv_area=(TextView) findViewById(R.id.salon_tv_area);
		tv_area.setOnClickListener(this);
		tv_environment=(TextView) findViewById(R.id.salon_tv_environment);
		tv_environment.setOnClickListener(this);
		tv_phone=(TextView) findViewById(R.id.salon_tv_phone);
		tv_phone.setOnClickListener(this);
		tv_cut=(TextView) findViewById(R.id.salon_tv_cut);
		tv_cut.setOnClickListener(this);
		tv_permanent=(TextView) findViewById(R.id.salon_tv_permanent);
		tv_permanent.setOnClickListener(this);
		tv_color=(TextView) findViewById(R.id.salon_tv_color);
		tv_color.setOnClickListener(this);
		tv_braid=(TextView) findViewById(R.id.salon_tv_braid);
		tv_braid.setOnClickListener(this);
		tv_treatment=(TextView) findViewById(R.id.salon_tv_treatment);
		tv_treatment.setOnClickListener(this);
		tv_discount=(TextView) findViewById(R.id.salon_tv_discount);
		tv_discount.setOnClickListener(this);
		lo_stylist= (LinearLayout) findViewById(R.id.salon_lo_stylist);
		lo_stylist.setVisibility(View.GONE);
		lo_stylists= (LinearLayout) findViewById(R.id.salon_lo_stylists);
		bt_invite=(Button) findViewById(R.id.salon_bt_invite);
		bt_invite.setOnClickListener(this);
		bt_withdraw=(Button) findViewById(R.id.salon_bt_withdraw);
		bt_withdraw.setOnClickListener(this);
		bt_join=(Button) findViewById(R.id.salon_bt_join);
		bt_join.setOnClickListener(this);
		tv_recruitment=(TextView) findViewById(R.id.salon_tv_recruitment);
		tv_recruitment.setOnClickListener(this);
		if(salonId==0){
			edit(null);
		}else if(salon==null){
			new GetTask().execute();
		}else{
			renderSalon();
		}
//		else{
//		    new GetTask().execute();
//		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(menu!=null && menu.getItem(1).isVisible()){
				if(getChangedSalon()!=null){
				AlertHelper.showAlert(this, getResources().getString(R.string.salon_alert_back_title), getResources().getString(R.string.salon_alert_back_content), getResources().getString(R.string.action_yes), new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if(salon==null){
							SalonActivity.this.finish();
						}else{
							SalonActivity.this.renderSalon();
						}
					}
				}, getResources().getString(R.string.action_no), new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
				}else{
					SalonActivity.this.renderSalon();
				}
			}else{
				this.finish();
			}
			return true;
		}else{
			return false;
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if(menu!=null && !menu.getItem(1).isVisible()||getChangedSalon()==null){
				finish();
			}else{
			AlertHelper.showAlert(this, getResources().getString(R.string.salon_alert_back_title), getResources().getString(R.string.salon_alert_back_content), getResources().getString(R.string.action_yes), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					finish();
				}
			}, getResources().getString(R.string.action_no), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
				}
			});
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private Salon getChangedSalon(){
		String name=tv_name.getText().toString().trim();
		String address=tv_address.getText().toString().trim();
		int area=Integer.valueOf("0"+tv_area.getText().toString());
		String environment=tv_environment.getText().toString().trim();
		String phone=tv_phone.getText().toString().replaceAll(" ", "");
		int cut=Integer.valueOf("0"+tv_cut.getText().toString());
		int permanent=Integer.valueOf("0"+tv_permanent.getText().toString());
		int color=Integer.valueOf("0"+tv_color.getText().toString());
		int braid=Integer.valueOf("0"+tv_braid.getText().toString());
		int treatment=Integer.valueOf("0"+tv_treatment.getText().toString());
		String discount=tv_discount.getText().toString().trim();
		String recruitment=tv_recruitment.getText().toString().trim();
		Salon s=new Salon();
		if(salon!=null)s.setSalonId(salon.getSalonId());
		s.setName(name);
		s.setAddress(address);
		s.setLatitude(app.getLatitude());
		s.setLongitude(app.getLongitude());
		s.setArea(area);
		s.setPhone(phone);
		s.setEnvironment(environment);
		s.setCut(cut);
		s.setPermanent(permanent);
		s.setColor(color);
		s.setBraid(braid);
		s.setTreatment(treatment);
		s.setDiscount(discount);
		s.setRecruitment(recruitment);
		if(salon==null
				||(!(app.getLatitude()==0&&app.getLongitude()==0)&&app.getLocationHelper()
				.getDistanceByMeter(
						salon.getLatitude()
						,salon.getLongitude())>500)
				||!salon.getName().equals(name)
				||!salon.getAddress().equals(address)
				||salon.getArea()!=area
				||!salon.getEnvironment().equals(environment)
				||!salon.getPhone().equals(phone)
				||salon.getCut()!=cut
				||salon.getPermanent()!=permanent
				||salon.getBraid()!=braid
				||salon.getColor()!=color
				||salon.getTreatment()!=treatment
				||!salon.getDiscount().equals(discount)
				||!salon.getRecruitment().equals(recruitment)){
			return s;
		}else{
			return null;
		}
	}
	private Salon getValidateSalon(Salon s){
		if(s==null){
			return null;
		}else if(s.getName()==null||"".equals(s.getName())){
			tv_name.performClick();
		}else if(s.getAddress()==null||"".equals(s.getAddress())){
			tv_address.performClick();
		}else if(s.getArea()<=0){
			tv_area.performClick();
		}else if(s.getEnvironment()==null||"".equals(s.getEnvironment())){
			tv_environment.performClick();
		}else if(s.getPhone()==null||"".equals(s.getPhone())||!s.getPhone().matches("\\d{5,15}")){
			tv_phone.performClick();
		}else if(s.getCut()<=0){
			tv_cut.performClick();
		}else if(s.getPermanent()<=0){
			tv_permanent.performClick();
		}else if(s.getColor()<=0){
			tv_color.performClick();
		}else if(s.getBraid()<=0){
			tv_braid.performClick();
		}else if(s.getTreatment()<=0){
			tv_treatment.performClick();
		}else if(s.getLatitude()==0&&s.getLongitude()==0){
			AlertHelper.showToast(this, R.string.error_locationError);
		}else{
			return s;
		}
		return null;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.salon, menu);
		this.menu=menu;
		if(app.getLoginUser().isSalonManager() && salon!=null && salon.getSalonId()==app.getLoginUser().getSalonId()){
		   menu.getItem(0).setVisible(true);
		   menu.getItem(1).setVisible(false);
		}else if(app.getLoginUser().getUserType()==USERTYPE.STYLIST && salon==null){
			menu.getItem(0).setVisible(false);
			menu.getItem(1).setVisible(true);
		}
		if(salon!=null && salon.getLatitude()!=0){
			menu.getItem(2).setVisible(true);
		}else{
			menu.getItem(2).setVisible(false);
		}
		return super.onCreateOptionsMenu(menu);
	}
	private void setEditable(TextView view,boolean editable){
		view.setClickable(editable);
		view.setCompoundDrawablePadding(editable?3:0);
		view.setCompoundDrawablesWithIntrinsicBounds(editable?R.drawable.selector_edit:0, 0, 0, 0);
	}
	public void edit(MenuItem item){
		if(menu!=null){
			menu.getItem(0).setVisible(false);
			menu.getItem(1).setVisible(true);
			menu.getItem(2).setVisible(false);
		}
		setEditable(tv_name,true);
		setEditable(tv_address,true);
		setEditable(tv_area,true);
		setEditable(tv_environment,true);
		setEditable(tv_phone,true);
		setEditable(tv_cut,true);
		setEditable(tv_permanent,true);
		setEditable(tv_color,true);
		setEditable(tv_braid,true);
		setEditable(tv_treatment,true);
		setEditable(tv_discount,true);
		setEditable(tv_recruitment,true);
		lo_stylist.setVisibility(View.GONE);
		wv.setVisibility(View.GONE);
	}
	public void save(MenuItem item){
		Salon s=getChangedSalon();
		if(s==null){
			renderSalon();
		}else{
			s=getValidateSalon(s);
			if(s!=null){
				new PostTask().execute(s);
			}
		}
	}
	public void map(MenuItem item){
		if(salon!=null && salon.getLatitude()!=0){
			Intent mapIntent = new Intent();
			mapIntent.putExtra("salon", salon);
		      mapIntent.setClass(this, MapActivity.class);
		      startActivity(mapIntent);
		}
	}
	private void renderImages(){
		if(salon==null)return;
		wv.setVisibility(View.VISIBLE);
		if(salon.getManagerId()==app.getLoginUser().getUserId() && (salon.getImages()==null || salon.getImages().length<10)){
			bt_attach.setVisibility(View.VISIBLE);
		}else{
			bt_attach.setVisibility(View.GONE);
		}
		wv.removeViews(0, wv.getChildCount()-1);
		String[] images=salon.getImages();
		if(images==null)return;
		for(int i=0;i<images.length;i++){
			final int page=i;
			final RoundAngleImageView iv=new RoundAngleImageView(this);
			iv.setAdjustViewBounds(true);
			iv.setMinimumWidth(DensityUtil.dip2px(this, 80));
			iv.setMinimumHeight(DensityUtil.dip2px(this, 80));
			iv.setMaxWidth(DensityUtil.dip2px(this, 80));
			iv.setMaxHeight(DensityUtil.dip2px(this, 80));
			ImageHelper.loadImg(iv, images[i], 0, false);
//			Bitmap b=ImageHelper.loadBitmap(getApplication(), images[i], 72);
//			try{
//				Log.e("-SalonActivity","image="+images[i]+",b="+b);
//			iv.setImageBitmap(Bitmap.createScaledBitmap(b, DensityUtil.dip2px(this, 72), DensityUtil.dip2px(this, 72), false));
//			iv.setTag(images[i]);
//          	b.recycle();
//			}catch(Exception e){
//				iv.setBackgroundResource(R.drawable.shape_rectangle_stroke);
//				e.printStackTrace();
//			}
         	 wv.addView(iv,i);
         	 if(app.getLoginUser().isSalonManager() && app.getLoginUser().getSalonId()==salon.getSalonId())
         	 iv.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View arg0) {
					new AlertDialog.Builder(SalonActivity.this)
					.setItems(getResources().getStringArray(R.array.action_salonImageHandle), new ImageHandleListener(page))
					.create().show();
					return false;
				}
         		 
         	 });
         	 iv.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent();
			        intent.putExtra("renderingUrl", salon.getImages());
			        intent.putExtra("init", page);
			        intent.setClass(SalonActivity.this, ViewerActivity.class);
			        startActivity(intent);
				}
         	 });
		}
//		wv.invalidate();
	}
	private void renderSytlists(){
		if(salon==null||salon.getStylists()==null)return;
		lo_stylist.setVisibility(View.VISIBLE);
		if(salon.getSalonId()==app.getLoginUser().getSalonId()){
			bt_withdraw.setVisibility(View.VISIBLE);
			if(app.getLoginUser().isSalonManager()){
				bt_invite.setVisibility(View.VISIBLE);
			}else{
				bt_invite.setVisibility(View.GONE);
			}
		}else{
			bt_invite.setVisibility(View.GONE);
			bt_withdraw.setVisibility(View.GONE);
		}
		if(salon.getManagerId()==app.getLoginUser().getUserId()){
			bt_join.setVisibility(View.GONE);
			bt_withdraw.setVisibility(View.VISIBLE);
			bt_invite.setVisibility(View.VISIBLE);
		}else if(salon.getSalonId()==app.getLoginUser().getSalonId()){
			bt_join.setVisibility(View.GONE);
			bt_withdraw.setVisibility(View.VISIBLE);
			bt_invite.setVisibility(View.GONE);
		}else if(app.getLoginUser().getUserType()==USERTYPE.STYLIST&&app.getLoginUser().getSalonId()==0){
			bt_join.setVisibility(View.VISIBLE);
			bt_invite.setVisibility(View.GONE);
			bt_withdraw.setVisibility(View.GONE);
		}else{
			bt_join.setVisibility(View.GONE);
			bt_invite.setVisibility(View.GONE);
			bt_withdraw.setVisibility(View.GONE);
		}
		lo_stylists.removeAllViews();
		for(final User user:salon.getStylists()){
			View v = SalonActivity.this.getLayoutInflater().inflate(R.layout.list_stylist, null);
		      RoundAngleImageView iv_figure = (RoundAngleImageView)v.findViewById(R.id.stylist_iv_figure);
		      TextView tv_userName = (TextView)v.findViewById(R.id.stylist_tv_userName);
		      RatingBar rb=(RatingBar) v.findViewById(R.id.stylist_rb_rating);
		      rb.setNumStars(5);
		      rb.setRating(user.getGrade()*rb.getNumStars());
		      rb.setIsIndicator(true);
		      TextView tv_age = (TextView)v.findViewById(R.id.stylist_tv_age);
		      TextView tv_about = (TextView)v.findViewById(R.id.stylist_tv_about);
			  TextView tv_level = (TextView)v.findViewById(R.id.stylist_tv_level);
			  tv_level.setText(getResources().getString(R.string.profile_tv_level_text,Constance.getLevel(user)));
		      tv_userName.setText(user.getUserName());
		      if(user.isSalonManager())tv_userName.setTextColor(getResources().getColor(R.color.emphasize_dark));
		      tv_age.setText(""+DateHelper.getAge(user.getBirthday()));
		      if (user.getSex() == Constance.SEX.MALE)
		    	  tv_age.setBackgroundResource(R.drawable.shape_rectangle_radius_blue);
		      String about=user.getAbout();
		      if(about==null || "".equals(about)){
		    	  tv_about.setVisibility(View.GONE);
		      }else{
		    	  tv_about.setText(user.getAbout());
		      }
		      ImageHelper.loadImg(iv_figure, user.getFigure(), 0,false);v.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						ProfileFragment profileFragment = new ProfileFragment();
						Bundle bundle = new Bundle();
					      bundle.putLong("userId",user.getUserId());
					      profileFragment.setArguments(bundle);
					      profileFragment.show(SalonActivity.this.getSupportFragmentManager(), ""+profileFragment.getId());
					}
			    	  
			      });
		      if(user.getUserId()!=app.getLoginUser().getUserId() && salon.getSalonId()==app.getLoginUser().getSalonId() &&  app.getLoginUser().isSalonManager())v.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View arg0) {
					new AlertDialog.Builder(SalonActivity.this)
					.setItems(getResources().getStringArray(R.array.action_salonStylistHandle), new StylistHandleListener(user.getUserId()))
					.create().show();
					return false;
				}
		      });
		      lo_stylists.addView(v);
		      TextView tv_line=new TextView(this);
		      tv_line.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		      tv_line.setHeight(1);
		      tv_line.setBackgroundResource(R.color.shadow_medium);
		      lo_stylists.addView(tv_line);
		}
	}
	private void renderSalon(){
		if(salon==null)return;
		tv_name.setText(salon.getName());
		tv_address.setText(salon.getAddress());
		tv_area.setText(""+salon.getArea());
		tv_environment.setText(salon.getEnvironment());
		tv_phone.setText(salon.getPhone());
		tv_cut.setText(""+salon.getCut());
		tv_permanent.setText(""+salon.getPermanent());
		tv_color.setText(""+salon.getColor());
		tv_braid.setText(""+salon.getBraid());
		tv_treatment.setText(""+salon.getTreatment());
		tv_discount.setText(salon.getDiscount());
		tv_recruitment.setText(salon.getRecruitment());
		if(menu!=null){
			menu.getItem(0).setVisible(true);
			menu.getItem(1).setVisible(false);
			if(salon!=null && salon.getLatitude()!=0){
				menu.getItem(2).setVisible(true);
			}else{
				menu.getItem(2).setVisible(false);
			}
		}
		setEditable(tv_name,false);
		setEditable(tv_address,false);
		setEditable(tv_area,false);
		setEditable(tv_environment,false);
		setEditable(tv_phone,false);
		setEditable(tv_cut,false);
		setEditable(tv_permanent,false);
		setEditable(tv_color,false);
		setEditable(tv_braid,false);
		setEditable(tv_treatment,false);
		setEditable(tv_discount,false);
		setEditable(tv_recruitment,false);
		renderImages();
		if(salon.getStylists()==null){
			new GetStylistTask().execute();
		}else{
			renderSytlists();
		}
	}

	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
		case R.id.salon_bt_attach:
			new AlertDialog.Builder(this)
			.setItems(getResources().getStringArray(R.array.action_takePhoto), new UploadListener())
			.create().show();
			break;
		case R.id.salon_tv_name:
			InputDialog inputDialog = InputDialog.newInstance(getResources().getString(R.string.salon_tv_name),tv_name.getText().toString(),null,InputType.TYPE_TEXT_VARIATION_PERSON_NAME,15);
			inputDialog.addListener(new InputListener(){

				@Override
				public void OnFinish(String inputText) {
					tv_name.setText(inputText);
				}
				
			});
			inputDialog.show(getSupportFragmentManager().beginTransaction(), ""+tv_name.getId());
			break;
		case R.id.salon_tv_address:
			final float lat=app.getLatitude();
			final float lon=app.getLongitude();
		    if (!(lat==0 && lon==0)){
		    	String defaultAddress = app.getAddress().trim();
		    	String savedAddress=tv_address.getText().toString();
		    	if(savedAddress!=null)savedAddress=savedAddress.trim();
		      if (!"".equals(savedAddress)){
		    	  defaultAddress=savedAddress;
		      }
		        
		      InputDialog addressDialog = InputDialog.newInstance(getResources().getString(R.string.salon_tv_address), defaultAddress, null,InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE, 100);
		      addressDialog.addListener(new InputDialog.InputListener()
		      {
		        @Override
				public void OnFinish(String input)
		        {
		        	tv_address.setText(input);
		        }
		      });
		      addressDialog.show(getSupportFragmentManager(), ""+tv_address.getId());
		    }else{
		  	  AlertHelper.showToast(this,R.string.status_noLocation);
		    }
			break;
		case R.id.salon_tv_area:
			int j=Integer.parseInt("0"+tv_area.getText().toString());
			CountDialog areaPicker=CountDialog.newInstance(j<5?70:j, 5, 500,5);
			areaPicker.addListener(new CountListener(){

				@Override
				public void OnFinish(int num) {
					tv_area.setText(""+num);
				}
				
			});
			areaPicker.show(getFragmentManager().beginTransaction(), ""+tv_area.getId());
			break;
		case R.id.salon_tv_environment:
			InputDialog environmentDialog = InputDialog.newInstance(getResources().getString(R.string.salon_tv_environment),tv_environment.getText().toString(),null,InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE, 280);
			environmentDialog.addListener(new InputListener(){

				@Override
				public void OnFinish(String inputText) {
					tv_environment.setText(inputText);
				}
			});
			environmentDialog.show(getSupportFragmentManager().beginTransaction(), ""+tv_environment.getId());
			break;
		case R.id.salon_tv_phone:
			InputDialog callDialog = InputDialog.newInstance(getResources().getString(R.string.salon_tv_call),tv_phone.getText().toString(),null,InputType.TYPE_CLASS_PHONE, 15);
			callDialog.addListener(new InputListener(){

				@Override
				public void OnFinish(String inputText) {
					tv_phone.setText(inputText);
				}
			});
			callDialog.show(getSupportFragmentManager().beginTransaction(), ""+tv_phone.getId());
			break;
		case R.id.salon_tv_cut:
			InputDialog cutDialog = InputDialog.newInstance(getResources().getString(R.string.salon_tv_cut),""+Integer.parseInt("0"+tv_cut.getText().toString()),null,InputType.TYPE_CLASS_NUMBER, 5);
			cutDialog.addListener(new InputDialog.InputListener()
		      {
		        @Override
				public void OnFinish(String content)
		        {
		          tv_cut.setText(content);
		        }
		      });
			cutDialog.show(getSupportFragmentManager(),""+tv_cut.getId());
			break;
		case R.id.salon_tv_permanent:
			InputDialog salon_tv_permanent = InputDialog.newInstance(getResources().getString(R.string.salon_tv_permanent),""+Integer.parseInt("0"+tv_permanent.getText().toString()),null,InputType.TYPE_CLASS_NUMBER, 5);
			salon_tv_permanent.addListener(new InputDialog.InputListener()
		      {
		        @Override
				public void OnFinish(String content)
		        {
		        	tv_permanent.setText(content);
		        }
		      });
			salon_tv_permanent.show(getSupportFragmentManager(),""+tv_permanent.getId());
			break;
		case R.id.salon_tv_color:
			InputDialog salon_tv_color = InputDialog.newInstance(getResources().getString(R.string.salon_tv_color),""+Integer.parseInt("0"+tv_color.getText().toString()),null,InputType.TYPE_CLASS_NUMBER, 5);
			salon_tv_color.addListener(new InputDialog.InputListener()
		      {
		        @Override
				public void OnFinish(String content)
		        {
		        	tv_color.setText(content);
		        }
		      });
			salon_tv_color.show(getSupportFragmentManager(),""+tv_color.getId());
			break;
		case R.id.salon_tv_braid:
			InputDialog salon_tv_braid = InputDialog.newInstance(getResources().getString(R.string.salon_tv_braid),""+Integer.parseInt("0"+tv_braid.getText().toString()),null,InputType.TYPE_CLASS_NUMBER, 5);
			salon_tv_braid.addListener(new InputDialog.InputListener()
		      {
		        @Override
				public void OnFinish(String content)
		        {
		        	tv_braid.setText(content);
		        }
		      });
			salon_tv_braid.show(getSupportFragmentManager(),""+tv_braid.getId());
			break;
		case R.id.salon_tv_treatment:
			InputDialog salon_tv_treatment = InputDialog.newInstance(getResources().getString(R.string.salon_tv_treatment),""+Integer.parseInt("0"+tv_treatment.getText().toString()),null,InputType.TYPE_CLASS_NUMBER, 5);
			salon_tv_treatment.addListener(new InputDialog.InputListener()
		      {
		        @Override
				public void OnFinish(String content)
		        {
		        	tv_treatment.setText(content);
		        }
		      });
			salon_tv_treatment.show(getSupportFragmentManager(),""+tv_treatment.getId());
			break;
		case R.id.salon_tv_discount:
			InputDialog discountDialog = InputDialog.newInstance(getResources().getString(R.string.salon_tv_discount),tv_discount.getText().toString(),null,InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE, 280);
			discountDialog.addListener(new InputListener(){

				@Override
				public void OnFinish(String inputText) {
					tv_discount.setText(inputText);
				}
			});
			discountDialog.show(getSupportFragmentManager().beginTransaction(), ""+tv_discount.getId());
			break;
		case R.id.salon_bt_invite:
			InputDialog inviteDialog = InputDialog.newInstance(getResources().getString(R.string.salon_tv_invite),"",getResources().getString(R.string.salon_bt_invite_hint),InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, 80);
			inviteDialog.addListener(new InputListener(){

				@Override
				public void OnFinish(String inputText) {
					if(inputText.matches("\\w+@\\w+\\.\\w+")){
						new InviteTask().execute(inputText);
					}else{
						AlertHelper.showToast(SalonActivity.this, R.string.err_signInvalid);
					}
				}
			});
			inviteDialog.show(getSupportFragmentManager().beginTransaction(), ""+bt_invite.getId());
			break;
		case R.id.salon_bt_join:
			AsyncHttpClient client=new AsyncHttpClient();
		    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
			    Header header = BasicScheme.authenticate(credentials, "UTF-8", false);
			    client.addHeader(header.getName(), header.getValue());
		    client.post(Constance.SERVER_URL+"message/join/"+salon.getSalonId(), new AsyncHttpResponseHandler(){
		   	 @Override
			     public void onStart() {
			     }

			     @Override
			     public void onSuccess(int statusCode,Header[] headers,byte[] content) {
			    	 AlertHelper.showToast(SalonActivity.this, getResources().getString(R.string.salon_join_sent));
			     }

			     @Override
			     public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
			    	 AlertHelper.showToast(SalonActivity.this,R.string.error_serverError);
			     }
			     @Override
			     public void onFinish() {
			     }
		    });
			break;
		case R.id.salon_bt_withdraw:
			int withdrawAlertTitle=R.string.salon_alert_withdraw_title;
			int withdrawAlertContent=R.string.salon_alert_withdraw_content;
			if(app.getLoginUser().isSalonManager()){
				withdrawAlertTitle=R.string.salon_alert_dissolve_title;
				withdrawAlertContent=R.string.salon_alert_dissolve_content;
			}
			AlertHelper.showAlert(this, getResources().getString(withdrawAlertTitle), getResources().getString(withdrawAlertContent), getResources().getString(R.string.action_yes), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					AsyncHttpClient client=new AsyncHttpClient();
				    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
					    Header header = BasicScheme.authenticate(credentials, "UTF-8", false);
					    client.addHeader(header.getName(), header.getValue());
				    client.delete(Constance.SERVER_URL+"/salon/stylist/"+app.getLoginUser().getUserId(), new AsyncHttpResponseHandler(){
				   	 @Override
					     public void onStart() {
					     }

					     @Override
					     public void onSuccess(int statusCode,Header[] headers,byte[] content) {
					    	 app.getLoginUser().setSalonAddress(null);
					    	 app.getLoginUser().setSalonId(0);
					    	 app.getLoginUser().setSalonImages(null);
					    	 app.getLoginUser().setSalonLatitude(0);
					    	 app.getLoginUser().setSalonLongitude(0);
					    	 app.getLoginUser().setSalonManager(false);
					    	 app.getLoginUser().setSalonName(null);
					    	 app.getLoginUser().setSalonPhone(null);
					    	 app.refreshUser();
					         SalonActivity.this.finish();
					     }

					     @Override
					     public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
					    	 AlertHelper.showToast(SalonActivity.this,R.string.error_serverError);
					     }
					     @Override
					     public void onFinish() {
					     }
				    });
				}
			}, getResources().getString(R.string.action_no), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
				}
			});
				
			break;
		case R.id.salon_tv_recruitment:
			InputDialog recruitmentDialog = InputDialog.newInstance(getResources().getString(R.string.salon_tv_recruitment),tv_recruitment.getText().toString(),null,InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE, 280);
			recruitmentDialog.addListener(new InputListener(){

				@Override
				public void OnFinish(String inputText) {
					tv_recruitment.setText(inputText);
				}
			});
			recruitmentDialog.show(getSupportFragmentManager().beginTransaction(), ""+tv_recruitment.getId());
			break;
		}
		
	}
	class PostTask extends AsyncTask<Salon, Void, Void>{
		Salon resultSalon;
		@Override
		protected Void doInBackground(Salon... arg0) {
			StringEntity se;
			try{
				se = new StringEntity(new Gson().toJson(arg0[0]), "UTF-8");
				}catch (UnsupportedEncodingException e){
					e.printStackTrace();
					return null;
					}
			HttpPost request = new HttpPost(Constance.SERVER_URL+"salon");
			request.addHeader(BasicScheme.authenticate(
					 new UsernamePasswordCredentials(((App)getApplication()).getLoginUser().getSign(), ((App)getApplication()).getLoginUser().getPassword()),
					 "UTF-8", false));
			request.setEntity(se);
			AndroidHttpClient client=AndroidHttpClient.newInstance("salonPost");
			String response = null;
			try {
				response=client.execute(request,new BasicResponseHandler());
				resultSalon = new Gson().fromJson(response, Salon.class);
				
			} catch (Exception e) {
//				Log.e("-SalonActivity",response);
				Log.e("SalonActivity",Log.getStackTraceString(e));
			}finally {
				client.close();
	        } 
			return null;
		}
		@Override
		protected void onPostExecute(Void param) {
			if(resultSalon!=null){
				app.refreshUser();
				salon=resultSalon;
				for(int i=0;i<app.getDiscoverySalons().size();i++){
					if(app.getDiscoverySalons().get(i).getSalonId()==salon.getSalonId()){
						app.getDiscoverySalons().set(i, salon);
						app.setDiscoverySalons(app.getDiscoverySalons());
						break;
					}
				}
				Log.d("-SalonActivity","salon.treatment="+salon.getTreatment());
				renderSalon();
				AlertHelper.showToast(SalonActivity.this, R.string.info_saveDone);
			}else{
				AlertHelper.showToast(SalonActivity.this, R.string.error_serverError);
			}
		}
		
	}
//	class UserAdapter extends BaseAdapter{
//		int count;
//	    UserAdapter(){}
//
//	    @Override
//		public int getCount(){
//	      return salon.getStylists() == null?0:salon.getStylists().length;
//	    }
//
//	    @Override
//		public Object getItem(int paramInt)
//	    {
//	      return salon.getStylists()[paramInt];
//	    }
//
//	    @Override
//		public long getItemId(int paramInt)
//	    {
//	      return ((User)getItem(paramInt)).getUserId();
//	    }
//
//	    @Override
//		public View getView(int position, View convertView, ViewGroup parent){
//	    	final User user=(User)getItem(position);
//	      View v = SalonActivity.this.getLayoutInflater().inflate(R.layout.list_stylist, null);
//	      ResizedImageView iv_figure = (ResizedImageView)v.findViewById(R.id.stylist_iv_figure);
//	      TextView tv_userName = (TextView)v.findViewById(R.id.stylist_tv_userName);
//	      RatingBar rb=(RatingBar) findViewById(R.id.stylist_rb_rating);
//	      rb.setNumStars(5);
//	      rb.setRating(1);
//	      rb.setIsIndicator(true);
//	      Log.d("-SalonActivity", user.getUserName()+"grade="+user.getGrade());
//	      TextView tv_age = (TextView)v.findViewById(R.id.stylist_tv_age);
//	      TextView tv_about = (TextView)v.findViewById(R.id.stylist_tv_about);
//		  TextView tv_level = (TextView)v.findViewById(R.id.stylist_tv_level);
//		  tv_level.setText(getResources().getString(R.string.profile_tv_level_text,Constance.getLevel(user)));
//	      tv_userName.setText(user.getUserName());
//	      tv_age.setText(""+DateHelper.getAge(user.getBirthday()));
//	      if (user.getSex() == Constance.SEX.MALE)
//	    	  tv_age.setBackgroundResource(R.drawable.shape_rectangle_radius_blue);
//	      String about=user.getAbout();
//	      if(about==null || "".equals(about)){
//	    	  tv_about.setVisibility(View.GONE);
//	      }else{
//	    	  tv_about.setText(user.getAbout());
//	      }
//	      ImageHelper.loadImg(iv_figure, user.getFigure(), 0,false);
////	      memViews.put(user.getUserId(), v);
//	      
//	      return v;
//	    }
//	  }
	class GetTask extends AsyncTask<Void, Void, Void>{
		Salon salonGot;

		@Override
		protected Void doInBackground(Void... params) {
			AndroidHttpClient client = null;
			try {
			HttpGet request = new HttpGet(Constance.SERVER_URL+"salon/"+salonId);
			request.addHeader(BasicScheme.authenticate(
					 new UsernamePasswordCredentials(app.getLoginUser().getSign(), app.getLoginUser().getPassword()),
					 "UTF-8", false));

			client=AndroidHttpClient.newInstance("getSalonById");
			String response=client.execute(request,new BasicResponseHandler());
			salonGot=new Gson().fromJson(response, Salon.class);
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				if(client!=null)client.close();
	        } 
			return null;
		}

		@Override
		protected void onPostExecute(Void param) {
			if(salonGot!=null){
				salon=salonGot;
				renderSalon();
			}
		}
		}

	class GetStylistTask extends AsyncTask<Void, Void, Void>{
		User[] us;

		@Override
		protected Void doInBackground(Void... params) {
			AndroidHttpClient client = null;
			try {
			HttpGet request = new HttpGet(Constance.SERVER_URL+"user/salon/"+salon.getSalonId());
			if(app.getLoginUser().getUserId()!=0)
			request.addHeader(BasicScheme.authenticate(
					 new UsernamePasswordCredentials(app.getLoginUser().getSign(), app.getLoginUser().getPassword()),
					 "UTF-8", false));

			client=AndroidHttpClient.newInstance("getUserBySalonId");
			String response=client.execute(request,new BasicResponseHandler());
			us=new Gson().fromJson(response, new TypeToken<User[]>() {}.getType());
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				if(client!=null)client.close();
	        } 
			return null;
		}
		@Override
		protected void onPostExecute(Void param) {
			if(us!=null&&us.length>0){
				salon.setStylists(us);
				renderSytlists();
			}
		}
	}
	class InviteTask extends AsyncTask<String, Void, Void>{
		String sign;
		HttpResponse response;

		@Override
		protected Void doInBackground(String... params) {
			sign=params[0];
			AndroidHttpClient client = null;
			try {
			HttpPost request = new HttpPost(Constance.SERVER_URL+"message/invite");
//			request.getParams().setParameter("sign", sign);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
	        nvps.add(new BasicNameValuePair("sign", sign));  
	        request.setEntity(new UrlEncodedFormEntity(nvps));
			request.addHeader(BasicScheme.authenticate(
					 new UsernamePasswordCredentials(app.getLoginUser().getSign(), app.getLoginUser().getPassword()),
					 "UTF-8", false));
			client=AndroidHttpClient.newInstance("inviteStylistBySign");
			response=client.execute(request);
			} catch (Exception e) {
				Log.e("-SalonActivity",Log.getStackTraceString(e));
			}finally {
				if(client!=null)client.close();
	        } 
			return null;
		}
		@Override
		protected void onPostExecute(Void param) {
			if(response.getStatusLine().getStatusCode()==HttpStatus.SC_NOT_FOUND){
				AlertHelper.showToast(SalonActivity.this, getResources().getString(R.string.salon_invite_notFound,sign));
			}else{
				AlertHelper.showToast(SalonActivity.this, getResources().getString(R.string.salon_invite_sent));
			}
		}
	}
	
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
	         if (resultCode == RESULT_OK) {
	        	 Bitmap bmp = BitmapFactory.decodeFile(Constance.sdcardTempFile.getAbsolutePath());
	             int uploadBitmapPx=(DensityUtil.dip2px(getBaseContext(), 360));
	             int reviewBitmapPx=(DensityUtil.dip2px(getBaseContext(), 72));
	             Bitmap reviewBmp = Bitmap.createScaledBitmap(bmp ,reviewBitmapPx,reviewBitmapPx, false);
	             Bitmap uploadBitmap = Bitmap.createScaledBitmap(bmp ,uploadBitmapPx,uploadBitmapPx, false);
	             RoundAngleImageView iv=new RoundAngleImageView(this);
	             if(iv!=null && reviewBmp!=null){
	            	 iv.setImageBitmap(reviewBmp);
//	            	 iv.setMinWidth(72);
//	            	 iv.setMinHeight(72);
//	            	 iv.setWidth(72);
//	            	 iv.setHeight(72);
	            	 iv.setAlpha(0.6f);
//	            	 iv.setBackgroundDrawable(new BitmapDrawable(reviewBmp));
//	            	 LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(72, 72);
	            	 wv.addView(iv,wv.getChildCount()-1);
	            	 bt_attach.setVisibility(View.GONE);
//	            	 LayoutParams params=iv.getLayoutParams();
//	            	 params.width=72;
//	            	 params.height=72;
//	            	 iv.setLayoutParams(params);
//	            	 iv.invalidate();
	             }
	                 ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
	                 uploadBitmap.compress(CompressFormat.JPEG, 75, bos);  
	                 byte[] data = bos.toByteArray();
	                 postImage(data);
//	             bitmapHash.put(requestCode, data);
	             bmp.recycle();
	         }
	     }
	 class ImageHandleListener implements DialogInterface.OnClickListener{
		 String image;
		 int place;
		 public ImageHandleListener(int place){
			 this.image=salon.getImages()[place];
			 this.place=place;
		 }

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			switch(arg1){
			case 0:
				placeImageAt(image,Math.max(place-1,0));
				break;
			case 1:
				placeImageAt(image,Math.min(place+1,salon.getImages().length-1));
				break;
			case 2:
				placeImageAt(image,0);
				break;
			case 3:
				placeImageAt(image,salon.getImages().length-1);
				break;
			case 4:
				placeImageAt(image,-1);
				break;
			}
			
		}
		 
	 }
	 private void placeImageAt(String img,int i){
			AsyncHttpClient client=new AsyncHttpClient();
		    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
			    Header header = BasicScheme.authenticate(credentials, "UTF-8", false);
			    client.addHeader(header.getName(), header.getValue());
//			client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
		    RequestParams params=new RequestParams();
		    params.put("image", img);
		    params.put("place", ""+i);
		    client.put(Constance.SERVER_URL+"/salon/image", params, new AsyncHttpResponseHandler(){
		   	 @Override
			     public void onStart() {
			     }

			     @Override
			     public void onSuccess(int statusCode,Header[] headers,byte[] content) {
			         Salon s=new Gson().fromJson(new String(content), Salon.class);
			         if(s!=null){
			        	 salon=s;
			        	 for(int i=0;i<app.getDiscoverySalons().size();i++){
								if(app.getDiscoverySalons().get(i).getSalonId()==salon.getSalonId()){
									app.getDiscoverySalons().set(i, salon);
									app.setDiscoverySalons(app.getDiscoverySalons());
									break;
								}
							}
			        	 renderImages();
			        	 app.refreshUser();
			         }
			     }

			     @Override
			     public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
			    	 AlertHelper.showToast(SalonActivity.this,R.string.error_serverError);
			     }
			     @Override
			     public void onFinish() {
			     }
		    });
		}
	 class StylistHandleListener implements DialogInterface.OnClickListener{
		 long stylistId;
		 public StylistHandleListener(long stylistId){
			 this.stylistId=stylistId;
		 }

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			switch(arg1){
			case 0:
				AsyncHttpClient client=new AsyncHttpClient();
			    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
				    Header header = BasicScheme.authenticate(credentials, "UTF-8", false);
				    client.addHeader(header.getName(), header.getValue());
			    client.delete(Constance.SERVER_URL+"/salon/stylist/"+stylistId, new AsyncHttpResponseHandler(){
			   	 @Override
				     public void onStart() {
				     }

				     @Override
				     public void onSuccess(int statusCode,Header[] headers,byte[] content) {
				    	 new GetStylistTask().execute();
				     }

				     @Override
				     public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
				    	 AlertHelper.showToast(SalonActivity.this,R.string.error_serverError);
				     }
				     @Override
				     public void onFinish() {
				     }
			    });
				break;
			case 1:
				AlertHelper.showAlert(SalonActivity.this, getResources().getString(R.string.salon_alert_discharge_title), getResources().getString(R.string.salon_alert_discharge_content), getResources().getString(R.string.action_yes), new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						AsyncHttpClient managerClient=new AsyncHttpClient();
					    UsernamePasswordCredentials managerCredentials = new UsernamePasswordCredentials(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
						    Header managerHeader = BasicScheme.authenticate(managerCredentials, "UTF-8", false);
						    managerClient.addHeader(managerHeader.getName(), managerHeader.getValue());
						    managerClient.put(Constance.SERVER_URL+"/salon/manager/"+stylistId, new AsyncHttpResponseHandler(){
					   	 @Override
						     public void onStart() {
						     }

						     @Override
						     public void onSuccess(int statusCode,Header[] headers,byte[] content) {
						         app.refreshUser();
						         SalonActivity.this.finish();
						     }

						     @Override
						     public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
						    	 AlertHelper.showToast(SalonActivity.this,R.string.error_serverError);
						     }
						     @Override
						     public void onFinish() {
						     }
					    });
					}
				}, getResources().getString(R.string.action_no), new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
				
				break;
			}
			
		}
		 
	 }
	 class UploadListener implements DialogInterface.OnClickListener{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == 0) {
				Intent intent = new Intent(
						"android.media.action.IMAGE_CAPTURE");
				intent.putExtra(
						"output",
						Uri.fromFile(Constance.sdcardTempFile));
				intent.putExtra("crop", "true");
				intent.putExtra("aspectX", 1);// �ü������
				intent.putExtra("aspectY", 1);
				intent.putExtra("outputX", 360);// ���ͼƬ��С
				intent.putExtra("outputY", 360);
				startActivityForResult(intent, 0);
			} else if(which==1){
				Intent intent = new Intent(
						"android.intent.action.PICK");
				intent.setDataAndType(
						MediaStore.Images.Media.INTERNAL_CONTENT_URI,
						"image/*");
				intent.putExtra(
						"output",
						Uri.fromFile(Constance.sdcardTempFile));
				intent.putExtra("crop", "true");
				intent.putExtra("aspectX", 1);// �ü������
				intent.putExtra("aspectY", 1);
				intent.putExtra("outputX", 360);// ���ͼƬ��С
				intent.putExtra("outputY", 360);
				startActivityForResult(intent, 0);
			}
			
		}
		 
	 }
	 private void postImage(byte[] data){
			AsyncHttpClient client=new AsyncHttpClient();
		    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
			    Header header = BasicScheme.authenticate(credentials, "UTF-8", false);
			    client.addHeader(header.getName(), header.getValue());
//			client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
		    RequestParams params=new RequestParams();
		    params.put("image", new ByteArrayInputStream(data));
		    client.post(Constance.SERVER_URL+"/salon/image", params, new AsyncHttpResponseHandler(){
		   	 @Override
			     public void onStart() {
			     }

			     @Override
			     public void onSuccess(int statusCode,Header[] headers,byte[] content) {
			         Salon s=new Gson().fromJson(new String(content), Salon.class);
			         if(s!=null){
			        	 salon=s;
			        	 for(int i=0;i<app.getDiscoverySalons().size();i++){
								if(app.getDiscoverySalons().get(i).getSalonId()==salon.getSalonId()){
									app.getDiscoverySalons().set(i, salon);
									app.setDiscoverySalons(app.getDiscoverySalons());
									break;
								}
							}
			         }
			     }

			     @Override
			     public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
			    	 Log.e("-SalonActivity", Log.getStackTraceString(error));
			    	 Log.e("-SalonActivity", new String(content));
			    	 AlertHelper.showToast(SalonActivity.this,R.string.error_serverError);
			     }
			     @Override
			     public void onFinish() {
		        	 renderImages();
		        	 app.refreshUser();
			     }
		    });
		}

	@Override
	public boolean onLongClick(View arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
