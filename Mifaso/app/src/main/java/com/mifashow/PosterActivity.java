package com.mifashow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mifashow.data.Constance;
import com.mifashow.domain.Posting;
import com.mifashow.tool.AlertHelper;
import com.mifashow.tool.ImageHelper;
import com.mifashow.tool.image.DensityUtil;
import com.mifashow.ui.WrappedButtonGroup;
import com.mifashow.ui.WrappedViewGroup;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;

public class PosterActivity extends FragmentActivity {
	private App app;
	private Posting posting;
	private ScrollView sv;
//	private TextView tv_address;
	private HashMap<Integer,byte[]> bitmapHash;
	private WrappedViewGroup wv_upload;
	private WrappedButtonGroup wv_services,wv_hairlength,wv_bangtype,wv_curlytype,wv_sex,wv_age,wv_faceshape;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poster);
		setupActionBar();
		app=(App) getApplication();
		bitmapHash=new HashMap<Integer,byte[]>();
		sv=(ScrollView) findViewById(R.id.poster_sv);
//		tv_address=(TextView) findViewById(R.id.poster_tv_address);
		wv_upload=(WrappedViewGroup) findViewById(R.id.poster_wv_upload);
		wv_services=(WrappedButtonGroup)findViewById(R.id.poster_wv_services);
		wv_hairlength=(WrappedButtonGroup)findViewById(R.id.poster_wv_hairlength);
		wv_bangtype=(WrappedButtonGroup)findViewById(R.id.poster_wv_bantype);
		wv_curlytype=(WrappedButtonGroup)findViewById(R.id.poster_wv_curlytype);
		wv_sex=(WrappedButtonGroup)findViewById(R.id.poster_wv_sex);
		wv_age=(WrappedButtonGroup)findViewById(R.id.poster_wv_age);
		wv_faceshape=(WrappedButtonGroup)findViewById(R.id.poster_wv_faceshape);
//		if (app.getLoginUser().getAddress() != null)
//		      tv_address.setText(app.getLoginUser().getAddress());
//		    tv_address.setOnClickListener(new View.OnClickListener()
//		    {
//		      @Override
//			public void onClick(View view)
//		      {
//		        if (app.getLocationHelper().getLocation() != null)
//		        {
//		          if ("".equals(tv_address.getText().toString().trim()))
//		            tv_address.setText(app.getLocationHelper().getAddress());
//		          InputDialog inputDialog = InputDialog.newInstance(getResources().getString(R.string.profile_tv_address_input), tv_address.getText().toString(), InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE, 100);
//		          inputDialog.addListener(new InputDialog.InputListener()
//		          {
//		            @Override
//					public void OnFinish(String content)
//		            {
//		              tv_address.setText(content);
//		            }
//		          });
//		          inputDialog.show(PosterActivity.this.getSupportFragmentManager(), ""+tv_address.getId());
//		          return;
//		        }
//		        AlertHelper.showToast(PosterActivity.this,R.string.status_noLocation);
//		      }
//		    });
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
			if (isGoodPost()) {
				RequestParams params = new RequestParams();
				String s=new Gson().toJson(posting);
				try{
					s=new String(s.getBytes(),"UTF-8");
				}catch(Exception e){
					e.printStackTrace();
					return false;
					}
				params.put("posting", s);
				for(int i: bitmapHash.keySet()){
					String name="";
					switch(i){
					case R.id.poster_iv_front:name="front";break;
					case R.id.poster_iv_back:name="back";break;
					case R.id.poster_iv_side:name="side";break;
					case R.id.poster_iv_more:name="more";break;
					} 
					params.put(name,  new ByteArrayInputStream(bitmapHash.get(i)));
				}
					AsyncHttpClient client=new AsyncHttpClient();
					UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
				    Header header = BasicScheme.authenticate(credentials, "UTF-8", false);
				    client.addHeader(header.getName(), header.getValue());
//					client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
				client.post(this, Constance.SERVER_URL+"posting", params, new AsyncHttpResponseHandler(){
					@Override
					public void onStart(){
						getActionBar().setDisplayHomeAsUpEnabled(false);
						getActionBar().setTitle(R.string.status_sending);
					}
					@Override
					   public void onSuccess(int statusCode,Header[] headers,byte[] content) {
						AlertHelper.showToast(PosterActivity.this, R.string.info_postDone);
						app.refreshUser();
						finish();
					   }
					@Override
					public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
						Log.e("-PosterActivity",Log.getStackTraceString(error));
						AlertHelper.showToast(PosterActivity.this, R.string.error_serverError);
					}
					@Override
					public void onFinish(){
						getActionBar().setDisplayHomeAsUpEnabled(true);
						getActionBar().setTitle(R.string.poster_at_label);
					}
				});
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private boolean isGoodPost(){
		posting=new Posting();
//		String address=tv_address.getText().toString();
//		Location location=app.getLocationHelper().getLocation();
		ArrayList<Integer> services=wv_services.getCheck();
		int hairLength=wv_hairlength.getSelect();
		int bangType=wv_bangtype.getSelect();
		int curlyType=wv_curlytype.getSelect();
		int sex=wv_sex.getSelect();
		ArrayList<Integer> faceshape=wv_faceshape.getCheck();
		ArrayList<Integer> age=wv_age.getCheck();
		View errView = null;
//		if(location==null || address==null || address.length()<5){
//			errView=tv_address;
//		}else 
			if(bitmapHash.get(R.id.poster_iv_front)==null){
        	errView=wv_upload;
        }else if(services.size()==0){
        	errView=wv_services;
        }else if(hairLength==-1){
        	errView=wv_hairlength;
        }else if(bangType==-1){
        	errView=wv_bangtype;
        }else if(curlyType==-1){
        	errView=wv_curlytype;
        }else if(sex==-1){
        	errView=wv_sex;
        }else if(age.size()==0){
        	errView=wv_age;
        }else if(faceshape.size()==0){
        	errView=wv_faceshape;
        }
        if(errView!=null){
        	sv.scrollTo(0,errView.getTop()-DensityUtil.dip2px(this, 44));
        	return false;
        }
        posting.setCreaterId(app.getLoginUser().getUserId());
//        posting.setLatitude((float) location.getLatitude());
//		posting.setLongitude((float) location.getLongitude());
//		posting.setAddress(address);
		Constance.SERVICETYPE[] serviceTypes=new Constance.SERVICETYPE[services.size()];
		for(int i=0;i<services.size();i++){
			serviceTypes[i]=Constance.SERVICETYPE.values()[services.get(i)];
		}
		posting.setServiceTypes(serviceTypes);
		posting.setHairLength(Constance.HAIRLENGTH.values()[hairLength]);
		posting.setBangType(Constance.BANGTYPE.values()[bangType]);
		posting.setCurlyType(Constance.CURLYTYPE.values()[curlyType]);
		posting.setSex(Constance.SEX.values()[sex]);Constance.FACESHAPE[] faceShapes=new Constance.FACESHAPE[faceshape.size()];
		for(int i=0;i<faceshape.size();i++){
			faceShapes[i]=Constance.FACESHAPE.values()[faceshape.get(i)];
		}
		posting.setFaceShapes(faceShapes);Constance.AGETYPE[] ageTypes=new Constance.AGETYPE[age.size()];
		for(int i=0;i<age.size();i++){
			ageTypes[i]=Constance.AGETYPE.values()[age.get(i)];
		}
		posting.setAgeTypes(ageTypes);
		return true;
	}

	public void uploadImg(View v) {
		new AlertDialog.Builder(this)
				.setItems(getResources().getStringArray(R.array.action_takeOrRotateOrClearPhoto), new UploadListener(v))
				.create().show();
	}
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
	         if (resultCode == RESULT_OK) {
	        	 switch(requestCode){
	             case (short)R.id.poster_iv_front:
	            	 requestCode=R.id.poster_iv_front;
	            	 break;
	             case (short)R.id.poster_iv_back:
	            	 requestCode=R.id.poster_iv_back;
	            	 break;
	             case (short)R.id.poster_iv_side:
	            	 requestCode=R.id.poster_iv_side;
	            	 break;
	             case (short)R.id.poster_iv_more:
	            	 requestCode=R.id.poster_iv_more;
	            	 break;
	             }
	             Bitmap bmp = BitmapFactory.decodeFile(Constance.sdcardTempFile.getAbsolutePath());
	             int uploadBitmapPx=(DensityUtil.dip2px(getBaseContext(), 360));
	             int reviewBitmapPx=(DensityUtil.dip2px(getBaseContext(), 140));
	             Bitmap reviewBmp = Bitmap.createScaledBitmap(bmp ,reviewBitmapPx,reviewBitmapPx, false);
	             Bitmap uploadBitmap = Bitmap.createScaledBitmap(bmp ,uploadBitmapPx,uploadBitmapPx, false);
	             ImageButton vb=(ImageButton)findViewById(requestCode);
	             if(vb!=null && reviewBmp!=null)vb.setImageBitmap(reviewBmp);
	                 ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
	                 uploadBitmap.compress(CompressFormat.JPEG, 75, bos);  
	                 byte[] data = bos.toByteArray();
	             bitmapHash.put(requestCode, data);
	             bmp.recycle();
	         }
	     }
	 class UploadListener implements DialogInterface.OnClickListener{
		 View v;
		 public UploadListener(View v){
			 this.v=v;
		 }

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
				startActivityForResult(intent, (short)v.getId());
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
				startActivityForResult(intent, (short)v.getId());
			}else if(which==2 && bitmapHash.containsKey(v.getId())){
				byte[] obs=bitmapHash.get(v.getId());
				Bitmap nb=ImageHelper.rotateBitmap(BitmapFactory.decodeByteArray(obs, 0, obs.length), 90);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				nb.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				bitmapHash.put(v.getId(),baos.toByteArray());
				int reviewBitmapPx=(DensityUtil.dip2px(getBaseContext(), 140));
	             Bitmap reviewBmp = Bitmap.createScaledBitmap(nb ,reviewBitmapPx,reviewBitmapPx, false);
				((ImageButton)v).setImageBitmap(reviewBmp);
			}else{
				((ImageButton)v).setImageResource(R.drawable.selector_6);
				bitmapHash.remove(v.getId());
			}
			
		}
		 
	 }

}
