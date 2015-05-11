package com.mifashow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpStatus;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mifashow.data.Constance;
import com.mifashow.domain.User;
import com.mifashow.tool.AlertHelper;
import com.mifashow.tool.ImageHelper;
import com.mifashow.tool.TextLengthInputFilter;
import com.mifashow.tool.image.DensityUtil;
import com.mifashow.ui.WrappedButtonGroup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputFilter;
import android.text.LoginFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends FragmentActivity {

	App app;
	static User user;
//	boolean isValidPhone;
	static Bitmap figure;
//	private boolean b_firstShow;
//	private int countrycodeItemNum;
	private int i_pageBeforeScroll;
	private long backKeyExitTime;
//	private UserSignUpTask task;
	private View mRegisterStatusView;
	private TextView mRegisterStatusMessageView;
	
	private SectionsPagerAdapter mSectionsPagerAdapter;

	private ViewPager vp_mViewPager;
	private static Button bt_figure;
	private EditText et_birthday,et_height,et_weight;
	private static EditText et_sign,et_password,et_username;
	static WrappedButtonGroup wv_hairlength,wv_bangtype,wv_curlytype,wv_sex,wv_usertype;

	static final int[] fragementIDs = {  R.layout.fragment_register_faceshape,R.layout.fragment_register_hair,R.layout.fragment_register_figure,R.layout.fragment_register_username };
	static final int[] titleIDs = {  R.string.register_pt_faceshape,R.string.register_pt_hair,R.string.register_pt_figure,R.string.register_pt_username };


	
	@Override
    public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("user", user);
//		outState.putBoolean("isValidPhone", isValidPhone);
		super.onSaveInstanceState(outState);
    }
	 @Override   
	 public void onRestoreInstanceState(Bundle savedInstanceState) {
		 super.onRestoreInstanceState(savedInstanceState);
		 user=(User) savedInstanceState.getSerializable("user");
//		 isValidPhone = savedInstanceState.getBoolean("isValidPhone",false);
	 }   

	
	private void init(){
		user=new User();
		user.setBangType(Constance.BANGTYPE.LONGSIDE);
		user.setCurlyType(Constance.CURLYTYPE.STRAIGHT);
		user.setFaceShape(Constance.FACESHAPE.STANDARD);
		user.setHairLength(Constance.HAIRLENGTH.LONG);
		user.setSex(Constance.SEX.FEMALE);
		user.setUserType(Constance.USERTYPE.CUSTOMER);
//		b_firstShow = true;
//		countrycodeItemNum = 0;
		i_pageBeforeScroll = 0;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app=(App) getApplication();
		init();
		setContentView(R.layout.activity_register);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mRegisterStatusView = findViewById(R.id.register_status);
		mRegisterStatusMessageView = (TextView) findViewById(R.id.register_status_message);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		vp_mViewPager = (ViewPager) findViewById(R.id.register_pg);
		vp_mViewPager.setAdapter(mSectionsPagerAdapter);
		vp_mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {
				if (arg0 == 1) {
					i_pageBeforeScroll = vp_mViewPager.getCurrentItem();
					}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}

			@Override
			public void onPageSelected(int arg0) {
				if(i_pageBeforeScroll<arg0)
				switch (i_pageBeforeScroll) {
//				case 0: {
//					et_phone = (EditText) findViewById(R.id.register_et_phone);
//					if(et_phone!=null){
//					et_phone.setError(null);
//					boolean b_cancel = false;
//					String s_err = "";
//					user.setPhone(et_phone.getText().toString());
//					if (user.getPhone().isEmpty()) {
//						s_err = getString(R.string.register_err_null_phone);
//						b_cancel = true;
//					} else if (user.getPhone().length() < 5 || user.getPhone().length() > 11) {
//						s_err = getString(R.string.register_err_length_phone);
//						b_cancel = true;
//					}
//					
//					
//					if (b_cancel) {
//						vp_mViewPager.setCurrentItem(i_pageBeforeScroll);
//						et_phone.requestFocus();
//						if (b_firstShow) {
//							b_firstShow = false;
//						} else {
//							et_phone.setError(s_err);
//						}
//					}
//					}
//					break;
//				}
//				case 1:{
//					et_validcode = (EditText) findViewById(R.id.register_et_validCode);
//					if(et_validcode!=null){
//						et_validcode.setError(null);
//						boolean b_cancel = false;
//						String s_err = "";
//						String s_validcode=et_validcode.getText().toString();
//						if (s_validcode.isEmpty()) {
//							s_err = getString(R.string.register_err_null_validCode);
//							b_cancel = true;
//						}
//						if (b_cancel) {
//							isValidPhone=false;
//							vp_mViewPager.setCurrentItem(i_pageBeforeScroll);
//							et_validcode.requestFocus();
//							et_validcode.setError(s_err);	
//						}else{
//							isValidPhone=true;
//						}
//						}
//					break;
//				}
				case 1:{
					wv_hairlength=(WrappedButtonGroup)findViewById(R.id.register_wv_hairLenght);
					wv_bangtype=(WrappedButtonGroup)findViewById(R.id.register_wv_bangType);
					wv_curlytype=(WrappedButtonGroup)findViewById(R.id.register_wv_curlyType);
					if(wv_hairlength!=null && wv_bangtype!=null && wv_curlytype!=null){
						user.setHairLength(Constance.HAIRLENGTH.values()[wv_hairlength.getSelect()]);
						user.setBangType(Constance.BANGTYPE.values()[wv_bangtype.getSelect()]);
						user.setCurlyType(Constance.CURLYTYPE.values()[wv_curlytype.getSelect()]);
					}
					break;
				}
				case 2:{
					et_birthday = (EditText) findViewById(R.id.register_et_birthday);
					et_height = (EditText) findViewById(R.id.register_et_height);
					et_weight = (EditText) findViewById(R.id.register_et_weight);
					wv_sex=(WrappedButtonGroup)findViewById(R.id.register_wv_sex);
					if(et_birthday!=null && et_height!=null && et_weight!=null && wv_sex!=null){
						user.setSex(Constance.SEX.values()[wv_sex.getSelect()]);
						et_birthday.setError(null);
						et_height.setError(null);
						et_weight.setError(null);
						boolean b_cancel = false;
						String s_height=et_height.getText().toString();
						String s_weight=et_weight.getText().toString();
						int i_height=Integer.valueOf(s_height==null||"".equals(s_height)?"0":s_height);
						int i_weight=Integer.valueOf(s_weight==null||"".equals(s_weight)?"0":s_weight);
						Calendar cal=Calendar.getInstance();
						long now=cal.getTimeInMillis();
						cal.add(Calendar.YEAR, -125);
						long oldest=cal.getTimeInMillis();
						
						if(s_height==null || "".equals(s_height)){
							et_height.setError(getString(R.string.register_err_null_height));
							et_height.requestFocus();
							b_cancel = true;
						}else if(i_height<100 ||i_height>250){
							et_height.setError(getString(R.string.register_err_invalid_height));
							et_height.requestFocus();
							b_cancel = true;
						}else
						if(s_weight==null || "".equals(s_weight)){
							et_weight.setError(getString(R.string.register_err_null_weight));
							et_weight.requestFocus();
							b_cancel = true;
							}else if(i_weight<3 ||i_weight>200){
							et_weight.setError(getString(R.string.register_err_invalid_weight));
							et_weight.requestFocus();
							b_cancel = true;
						}else
						if (user.getBirthday()==0) {
							et_birthday.setError(getString(R.string.register_err_null_birthday));
							et_birthday.requestFocus();
							b_cancel = true;
						}else if(user.getBirthday()>now || user.getBirthday()<oldest){
							et_birthday.setError(getString(R.string.register_err_invalid_birthday));
							et_birthday.requestFocus();
							b_cancel = true;
						}else
						if(figure==null){
							Toast.makeText(RegisterActivity.this, getResources().getString(R.string.register_err_null_figure), Toast.LENGTH_SHORT).show();
							b_cancel=true;
						}
						if (b_cancel) {
							vp_mViewPager.setCurrentItem(i_pageBeforeScroll);	
						}else{
							user.setHeight(i_height);
							user.setWeight(i_weight);
						}
						}
					break;
				}
				}
				}

		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			AlertHelper.showAlert(this, getResources().getString(R.string.register_alert_back_title), getResources().getString(R.string.register_alert_back_content), getResources().getString(R.string.action_yes), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent intent = new Intent();
					intent.setClass(getBaseContext(), MainActivity.class);
					startActivity(intent);
					RegisterActivity.this.finish();
				}
			}, getResources().getString(R.string.action_no), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
				}
			});
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return fragementIDs.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			return getString(titleIDs[position]).toUpperCase(l);
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		public static final String BD_CHOSED_FACESAHPE = "chosed_faceshape";
//		EditText et_phone;
//		TextView tv_phonenumber_l;
//		EditText et_countrycode;

		@Override
		public View onCreateView(LayoutInflater inflater,final ViewGroup container, Bundle savedInstanceState) {
			int pageNum = this.getArguments().getInt(ARG_SECTION_NUMBER);
			View view = inflater.inflate(fragementIDs[pageNum], container,false);
				if(pageNum==0){
				Button bt_chosedFace = (Button) view.findViewById(R.id.register_bt_chosedFace);
				if (bt_chosedFace != null) {
					bt_chosedFace.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(user.getFaceShape().getDrawableID()),null, null);
					
				}
			}
			else if(pageNum==1){
				wv_hairlength=(WrappedButtonGroup)view.findViewById(R.id.register_wv_hairLenght);
				wv_bangtype=(WrappedButtonGroup)view.findViewById(R.id.register_wv_bangType);
				wv_curlytype=(WrappedButtonGroup)view.findViewById(R.id.register_wv_curlyType);
				if(wv_hairlength!=null && wv_bangtype!=null && wv_curlytype!=null){

					wv_hairlength.setSelect(user.getHairLength().ordinal());
					wv_bangtype.setSelect(user.getBangType().ordinal());
					wv_curlytype.setSelect(user.getCurlyType().ordinal());

//					Toast.makeText(view.getContext(), "onCreate:"+chosedHairlength, Toast.LENGTH_SHORT).show();
				}
			}else if(pageNum==2){
				bt_figure=(Button) view.findViewById(R.id.register_bt_figure);
				wv_sex=(WrappedButtonGroup)view.findViewById(R.id.register_wv_sex);
				if(wv_sex!=null){
					wv_sex.setSelect(user.getSex().ordinal());
					
		            if(bt_figure!=null && figure!=null){
		            	int pix=DensityUtil.dip2px(getActivity(), 360);
		            	figure = Bitmap.createScaledBitmap(figure ,pix,pix, false);
		            	bt_figure.setText(null);
		            	bt_figure.setCompoundDrawables(null, null, null, null);
		            	bt_figure.setBackgroundDrawable(new BitmapDrawable(figure));
		            }
				}
			}else if(pageNum==3){
				et_sign=(EditText) view.findViewById(R.id.register_et_sign);
//				et_sign.setFilters(new InputFilter[]{new RegExInputFilter("[a-zA-Z0-9@._-]{7,}")});
//				et_sign.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
				et_username=(EditText)view.findViewById(R.id.register_et_userName);
				et_username.setFilters(new InputFilter[]{new TextLengthInputFilter(15)});
				et_password=(EditText)view.findViewById(R.id.register_et_password);
				et_password.setFilters(new InputFilter[]{new TextLengthInputFilter(15),new LoginFilter.PasswordFilterGMail()});
				wv_usertype=(WrappedButtonGroup)view.findViewById(R.id.register_wv_userType);
				wv_usertype.setSelect(user.getSex().ordinal());
			}
			return view;
		}
	}
	public void uploadImg(View v) {
		new AlertDialog.Builder(this)
				.setItems(getResources().getStringArray(R.array.action_takePhoto), new UploadListener(v))
				.create().show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(System.currentTimeMillis()-backKeyExitTime>2000){
				Toast.makeText(this, getString(R.string.register_to_exit), Toast.LENGTH_SHORT).show();
				backKeyExitTime=System.currentTimeMillis();
			}else{
				Intent intent = new Intent();
				intent.setClass(this, MainActivity.class);
				this.startActivity(intent);
				RegisterActivity.this.finish();
			}
			return true;
		}else{
			return false;
		}
	}


	public void chooseface(View v) {
		for(Constance.FACESHAPE faceShape:Constance.FACESHAPE.values()){
			if(faceShape.getButtonID()==v.getId()){
				user.setFaceShape(faceShape);
				break;
			}
		}
		Button bt_chosedFace = (Button) findViewById(R.id.register_bt_chosedFace);
		if (bt_chosedFace != null)
			bt_chosedFace.setCompoundDrawablesWithIntrinsicBounds(
					null,getResources().getDrawable(user.getFaceShape().getDrawableID()), null, null);
	}
	public void chooseBirthday(View v){
		Calendar defaultCal=Calendar.getInstance();
		if(user.getBirthday()==0){
			defaultCal.add(Calendar.YEAR, -22);
		}else{
			defaultCal.setTimeInMillis(user.getBirthday());
		}
		new DatePickerDialog(v.getContext(),new DatePickerDialog.OnDateSetListener(){
			@Override
			public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
				Calendar cal=Calendar.getInstance();
				cal.set(arg1, arg2, arg3);
				EditText et_birthday=(EditText)findViewById(R.id.register_et_birthday);
				et_birthday.setText(arg1+"-"+(arg2+1)+"-"+arg3);
				user.setBirthday(cal.getTimeInMillis());
				et_birthday.setError(null);
			}},defaultCal.get(Calendar.YEAR), defaultCal.get(Calendar.MONTH), defaultCal.get(Calendar.DAY_OF_MONTH)).show();


	}
	public void finish(View v){
		user.setUserType(Constance.USERTYPE.values()[wv_usertype.getSelect()]);
		user.setUserName(et_username.getText().toString());
		user.setPassword(et_password.getText().toString());
		user.setSign(et_sign.getText().toString());
		et_sign.setError(null);
		et_username.setError(null);
		et_password.setError(null);
		boolean b_cancel = false;
		EditText errView=null;
		
		if(user.getUserName()==null || "".equals(user.getUserName())){
			b_cancel=true;
			errView=et_username;
			et_username.setError(getString(R.string.register_err_null_userName));
		}
		if(user.getPassword()==null || "".equals(user.getPassword())){
			b_cancel=true;
			errView=et_password;
			et_password.setError(getString(R.string.register_err_null_password));
		}
		if(user.getSign()==null||!user.getSign().matches("\\w+@\\w+\\.\\w+")){
			b_cancel=true;
			errView=et_sign;
			et_sign.setError(getString(R.string.err_signInvalid));
		}
		if(b_cancel){
			errView.requestFocus();
		}else{
			mRegisterStatusMessageView.setText("");
			showProgress(true);
			Gson gson = new Gson();
			String str = gson.toJson(user);
			RequestParams params = new RequestParams();
			try{
				str=new String(str.getBytes(),"UTF-8");
			}catch(Exception e){
				e.printStackTrace();
				}
			params.put("user", str);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();  
            figure.compress(CompressFormat.JPEG, 75, bos);  
            byte[] data = bos.toByteArray();   
			params.put("figure",  new ByteArrayInputStream(data));
			AsyncHttpClient client = new AsyncHttpClient();
			client.post(this,Constance.SERVER_URL+"user", params,new AsyncHttpResponseHandler(){
				@Override
				   public void onSuccess(int statusCode,Header[] headers,byte[] content) {
					User user=new Gson().fromJson(new String(content), User.class);
					app.setLoginUser(user);
					Intent intent = new Intent();
					intent.setClass(RegisterActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				   }
				@Override
				public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
					if(statusCode==HttpStatus.SC_NOT_ACCEPTABLE){
					    Toast.makeText(RegisterActivity.this, R.string.error_signExistsError, Toast.LENGTH_SHORT).show();
					}else{
//						Toast.makeText(RegisterActivity.this, R.string.error_serverError, Toast.LENGTH_SHORT).show();
                        Toast.makeText(RegisterActivity.this, new String(content), Toast.LENGTH_SHORT).show();
					}
					
				}
				@Override
			     public void onFinish() {
					showProgress(false);
			     }
			});
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mRegisterStatusView.setVisibility(View.VISIBLE);
			mRegisterStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mRegisterStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			vp_mViewPager.setVisibility(View.VISIBLE);
			vp_mViewPager.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							vp_mViewPager.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			mRegisterStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			vp_mViewPager.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
        	
            Bitmap bmp = BitmapFactory.decodeFile(new File("/mnt/sdcard","mifaso_tmp_pic"+ ".jpg").getAbsolutePath());

        	int uploadBitmapPx=(DensityUtil.dip2px(getBaseContext(), 360));
            int reviewBitmapPx=(DensityUtil.dip2px(getBaseContext(), 100));
            Bitmap reviewBmp = Bitmap.createScaledBitmap(bmp ,reviewBitmapPx,reviewBitmapPx, false);
            Bitmap uploadBitmap = Bitmap.createScaledBitmap(bmp ,uploadBitmapPx,uploadBitmapPx, false);
            if(bt_figure!=null && reviewBmp!=null){
            	bt_figure.setText(null);
            	bt_figure.setCompoundDrawables(null, null, null, null);
            	bt_figure.setBackgroundDrawable(new BitmapDrawable(reviewBmp)); 
            }
                figure=uploadBitmap;  
        }
    }
	class UploadListener implements DialogInterface.OnClickListener{

		private File sdcardTempFile;
		 View v;
		 public UploadListener(View v){
			 sdcardTempFile=new File("/mnt/sdcard","mifaso_tmp_pic"+ ".jpg");
			 this.v=v;
		 }

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == 0) {
				Intent intent = new Intent(
						"android.media.action.IMAGE_CAPTURE");
				intent.putExtra(
						"output",
						Uri.fromFile(sdcardTempFile));
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
						Uri.fromFile(sdcardTempFile));
				intent.putExtra("crop", "true");
				intent.putExtra("aspectX", 1);// �ü������
				intent.putExtra("aspectY", 1);
				intent.putExtra("outputX", 360);// ���ͼƬ��С
				intent.putExtra("outputY", 360);
				startActivityForResult(intent, 0);
			}else if(which==2 && figure!=null){
				figure=ImageHelper.rotateBitmap(figure, 90);
				int reviewBitmapPx=(DensityUtil.dip2px(getBaseContext(), 100));
	             Bitmap reviewBmp = Bitmap.createScaledBitmap(figure ,reviewBitmapPx,reviewBitmapPx, false);
	            	bt_figure.setText(null);
	            	bt_figure.setCompoundDrawables(null, null, null, null);
				bt_figure.setBackgroundDrawable(new BitmapDrawable(reviewBmp));
			}
			
		}
	
	}
}
