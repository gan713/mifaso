package com.mifashow;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mifashow.CountDialog.CountListener;
import com.mifashow.FaceShapeDialog.FaceShapeListener;
import com.mifashow.HairDialog.HairListener;
import com.mifashow.InputDialog.InputListener;
import com.mifashow.data.Constance;
import com.mifashow.data.Constance.BANGTYPE;
import com.mifashow.data.Constance.CURLYTYPE;
import com.mifashow.data.Constance.FACESHAPE;
import com.mifashow.data.Constance.HAIRLENGTH;
import com.mifashow.data.Constance.SEX;
import com.mifashow.domain.User;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.TextView;


public class ProfileEditorActivity extends FragmentActivity implements OnClickListener,DialogInterface.OnClickListener {
	private App app;
	private User user;
	private TextView tv_userType,tv_userName,tv_sex,tv_birthday,tv_height,tv_weight,tv_faceShape,tv_hair;
	AlertDialog ad_sex;
	private AlertDialog ad_userType;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_editor);
		app=(App) getApplication();
		user=app.getLoginUser();
		setupActionBar();
//		tv_phone=(TextView) findViewById(R.id.profile_tv_phone);
		tv_userType=(TextView) findViewById(R.id.profileEditor_tv_userType);
		tv_userName=(TextView) findViewById(R.id.profileEditor_tv_userName);
		tv_sex=(TextView) findViewById(R.id.profileEditor_tv_sex);
		tv_birthday=(TextView) findViewById(R.id.profileEditor_tv_birthday);
		tv_height=(TextView) findViewById(R.id.profileEditor_tv_height);
		tv_weight=(TextView) findViewById(R.id.profileEditor_tv_weight);
		tv_faceShape=(TextView) findViewById(R.id.profileEditor_tv_faceShape);
		tv_hair=(TextView) findViewById(R.id.profileEditor_tv_hair);
//		tv_phone.setText(""+user.getPhone());
		tv_userType.setText(""+getResources().getStringArray(R.array.enum_userType)[user.getUserType().ordinal()]);
		tv_userType.setOnClickListener(this);
		tv_userName.setText(""+user.getUserName());
		tv_userName.setOnClickListener(this);
		tv_sex.setText(""+getResources().getStringArray(R.array.enum_sex)[user.getSex().ordinal()]);
		tv_sex.setOnClickListener(this);
		tv_birthday.setText(""+new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(new Date(user.getBirthday())));
		tv_birthday.setOnClickListener(this);
		tv_height.setText(""+user.getHeight()+getResources().getString(R.string.register_tv_height_unit_text));
		tv_height.setOnClickListener(this);
		tv_weight.setText(""+user.getWeight()+getResources().getString(R.string.register_tv_weight_unit_text));
		tv_weight.setOnClickListener(this);
		tv_faceShape.setText(""+getResources().getStringArray(R.array.enum_faceShape)[user.getFaceShape().ordinal()]);
		tv_faceShape.setOnClickListener(this);
		tv_hair.setText(getResources().getStringArray(R.array.enum_bangType)[user.getBangType().ordinal()]+" "+getResources().getStringArray(R.array.enum_curlyType)[user.getCurlyType().ordinal()]+" "+getResources().getStringArray(R.array.enum_hairLength)[user.getHairLength().ordinal()]);
		tv_hair.setOnClickListener(this);
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			AsyncHttpClient client=new AsyncHttpClient();
			client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
			String s_user=new Gson().toJson(user);
			StringEntity se = null;
			try {
//				s_user=new String(s_user.getBytes(),"UTF-8");
				se = new StringEntity(s_user,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			client.put(this, Constance.SERVER_URL+"user", se, "application/json;charset=utf-8", new AsyncHttpResponseHandler(){
				@Override
			    public void onSuccess(int statusCode,Header[] headers,byte[] content) {
			        User user=new Gson().fromJson(new String(content), User.class);
			        app.setLoginUser(user);
			        finish();
			    }
                @Override
                public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
                    Log.d("-ProfileEditorActivity", "putUser:failure");
                    Log.getStackTraceString(error);
                }
			});
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.profileEditor_tv_userName:
			InputDialog inputDialog = InputDialog.newInstance(getResources().getString(R.string.profileEditor_tv_userName_text),tv_userName.getText().toString(),null,InputType.TYPE_TEXT_VARIATION_PERSON_NAME,15);
			inputDialog.addListener(new InputListener(){

				@Override
				public void OnFinish(String inputText) {
					tv_userName.setText(inputText);
					user.setUserName(inputText);
				}
				
			});
			inputDialog.show(getSupportFragmentManager().beginTransaction(), ""+tv_userName.getId());
			break;
		case R.id.profileEditor_tv_userType:
		      this.ad_userType = new AlertDialog.Builder(this).setItems(getResources().getStringArray(R.array.enum_userType), this).create();
		      this.ad_userType.show();
		      return;
		case R.id.profileEditor_tv_birthday:
			Calendar cal=Calendar.getInstance();
			cal.setTimeInMillis(user.getBirthday());
			new DatePickerDialog(v.getContext(),new DatePickerDialog.OnDateSetListener(){
				@Override
				public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
					Calendar cal=Calendar.getInstance();
					cal.set(arg1, arg2, arg3);
					tv_birthday.setText(arg1+"-"+(arg2+1)+"-"+arg3);
					user.setBirthday(cal.getTimeInMillis());
				}},cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
			break;
		case R.id.profileEditor_tv_height:
			CountDialog heightPicker=CountDialog.newInstance(user.getHeight(), 100, 250,1);
			heightPicker.addListener(new CountListener(){

				@Override
				public void OnFinish(int num) {
					tv_height.setText(""+num+getResources().getString(R.string.register_tv_height_unit_text));
					user.setHeight(num);
				}
				
			});
			heightPicker.show(getFragmentManager().beginTransaction(), ""+tv_height.getId());
			break;
		case R.id.profileEditor_tv_weight:
			CountDialog weightPicker=CountDialog.newInstance(user.getWeight(), 2, 250,1);
			weightPicker.addListener(new CountListener(){

				@Override
				public void OnFinish(int num) {
					tv_weight.setText(""+num+getResources().getString(R.string.register_tv_weight_unit_text));
					user.setWeight(num);
				}
				
			});
			weightPicker.show(getFragmentManager().beginTransaction(), ""+tv_weight.getId());
			break;
		case R.id.profileEditor_tv_sex:
			ad_sex=new AlertDialog.Builder(this)
			.setItems(getResources().getStringArray(R.array.enum_sex), this)
			.create();
			ad_sex.show();
			break;
		case R.id.profileEditor_tv_faceShape:
			FaceShapeDialog faceShapePicker=FaceShapeDialog.newInstance();
			faceShapePicker.addListener(new FaceShapeListener(){

				@Override
				public void OnFinish(FACESHAPE faceShape) {
					tv_faceShape.setText(""+getResources().getStringArray(R.array.enum_faceShape)[faceShape.ordinal()]);
					user.setFaceShape(faceShape);
				}
				
			});
			faceShapePicker.show(getFragmentManager().beginTransaction(), ""+tv_faceShape.getId());
			break;
		case R.id.profileEditor_tv_hair:
			HairDialog hairPicker=HairDialog.newInstance(user.getHairLength(),user.getCurlyType(),user.getBangType());
			hairPicker.addListener(new HairListener(){

				@Override
				public void OnFinish(HAIRLENGTH hairLength,CURLYTYPE curlyType,BANGTYPE bangType) {
					tv_hair.setText(getResources().getStringArray(R.array.enum_bangType)[user.getBangType().ordinal()]+" "+getResources().getStringArray(R.array.enum_curlyType)[user.getCurlyType().ordinal()]+" "+getResources().getStringArray(R.array.enum_hairLength)[user.getHairLength().ordinal()]);
					user.setHairLength(hairLength);
					user.setCurlyType(curlyType);
					user.setBangType(bangType);
				}
				
			});
			hairPicker.show(getFragmentManager().beginTransaction(), ""+tv_hair.getId());
			break;
		}
		
	}
	@Override
	public void onClick(DialogInterface dialog, int which) {
		if(dialog.equals(ad_sex)){
			tv_sex.setText(""+getResources().getStringArray(R.array.enum_sex)[which]);
			user.setSex(SEX.values()[which]);
		}
		
	}

}
