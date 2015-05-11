package com.mifashow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mifashow.HairDialog.HairListener;
import com.mifashow.data.Constance;
import com.mifashow.data.Constance.BANGTYPE;
import com.mifashow.data.Constance.CURLYTYPE;
import com.mifashow.data.Constance.HAIRLENGTH;
import com.mifashow.data.Constance.POSTINGTYPE;
import com.mifashow.domain.Posting;
import com.mifashow.tool.AlertHelper;
import com.mifashow.tool.DateHelper;
import com.mifashow.tool.ImageHelper;
import com.mifashow.tool.image.DensityUtil;

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
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class ShowActivity extends FragmentActivity {
	private App app;
	private Posting posting;
	private TextView tv_hair;
	private HashMap<Integer,byte[]> bitmapHash;
	private HAIRLENGTH hairLength;
	private CURLYTYPE curlyType;
	private BANGTYPE bangType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show);
		setupActionBar();
		app=(App) getApplication();
		bitmapHash=new HashMap<Integer,byte[]>();
		hairLength=app.getLoginUser().getHairLength();
		curlyType=app.getLoginUser().getCurlyType();
		bangType=app.getLoginUser().getBangType();
		tv_hair=(TextView) findViewById(R.id.show_tv_hair);
		tv_hair.setText(getResources().getStringArray(R.array.enum_bangType)[bangType.ordinal()] + " "+getResources().getStringArray(R.array.enum_curlyType)[curlyType.ordinal()]+" "+getResources().getStringArray(R.array.enum_hairLength)[hairLength.ordinal()]);
		tv_hair.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				HairDialog hairPicker=HairDialog.newInstance(hairLength,curlyType,bangType);
				hairPicker.addListener(new HairListener(){

					@Override
					public void OnFinish(HAIRLENGTH nHairLength,CURLYTYPE nCurlyType,BANGTYPE nBangType) {
						hairLength=nHairLength;
						curlyType=nCurlyType;
						bangType=nBangType;
						tv_hair.setText(getResources().getStringArray(R.array.enum_bangType)[bangType.ordinal()] + " "+getResources().getStringArray(R.array.enum_curlyType)[curlyType.ordinal()]+" "+getResources().getStringArray(R.array.enum_hairLength)[hairLength.ordinal()]);
					}
					
				});
				hairPicker.show(getFragmentManager().beginTransaction(), ""+tv_hair.getId());
			}
		});
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
					case R.id.show_iv_front:name="front";break;
					case R.id.show_iv_back:name="back";break;
					case R.id.show_iv_side:name="side";break;
					case R.id.show_iv_more:name="more";break;
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
						AlertHelper.showToast(ShowActivity.this, R.string.info_postDone);
						app.refreshUser();
						finish();
					   }
					@Override
					public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
//						Log.e("-ShowActivity",Log.getStackTraceString(error));
						Log.e("-ShowActivity",error.getMessage());
						AlertHelper.showToast(ShowActivity.this, R.string.error_serverError);
					}
					@Override
					public void onFinish(){
						getActionBar().setDisplayHomeAsUpEnabled(true);
						getActionBar().setTitle(R.string.show_at_label);
					}
				});
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private boolean isGoodPost(){
		posting=new Posting();
//		Location location=app.getLocationHelper().getLocation();
//		if(location==null){
//			AlertHelper.showToast(this, getResources().getString(R.string.error_locationError));
//			return false;
//		}
		if(bitmapHash.get(R.id.show_iv_front)==null){
			AlertHelper.showToast(this, getResources().getString(R.string.error_noImageError));
			return false;
        }
		posting.setPostingType(POSTINGTYPE.SHOW);
        posting.setCreaterId(app.getLoginUser().getUserId());
        posting.setHairLength(hairLength);
		posting.setBangType(bangType);
		posting.setCurlyType(curlyType);
		posting.setSex(app.getLoginUser().getSex());
		posting.setFaceShapes(new Constance.FACESHAPE[]{app.getLoginUser().getFaceShape()});
		posting.setAgeTypes(new Constance.AGETYPE[]{DateHelper.getAgeType(app.getLoginUser().getBirthday())});
//		posting.setLatitude((float)location.getLatitude());
//		posting.setLongitude((float)location.getLongitude());
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
	             case (short)R.id.show_iv_front:
	            	 requestCode=R.id.show_iv_front;
	            	 break;
	             case (short)R.id.show_iv_back:
	            	 requestCode=R.id.show_iv_back;
	            	 break;
	             case (short)R.id.show_iv_side:
	            	 requestCode=R.id.show_iv_side;
	            	 break;
	             case (short)R.id.show_iv_more:
	            	 requestCode=R.id.show_iv_more;
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
