package com.mifashow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mifashow.data.Constance;
import com.mifashow.tool.AlertHelper;
import com.mifashow.tool.ImageHelper;
import com.mifashow.tool.TextLengthInputFilter;
import com.mifashow.tool.image.DensityUtil;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

public class CommentActivity extends FragmentActivity {
	App app;
	  private HashMap<Integer, byte[]> bitmapHash;
	  long bookingId;
	  RatingBar rb;
	  TextView tv_content;

	  public void input(View view)
	  {
	    InputDialog localInputDialog = InputDialog.newInstance(getResources().getString(R.string.comment_input_title), this.tv_content.getText().toString(),null, InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE, 1000);
	    localInputDialog.addListener(new InputDialog.InputListener()
	    {
	      @Override
		public void OnFinish(String content)
	      {
	        tv_content.setText(content);
	      }
	    });
	    localInputDialog.show(getSupportFragmentManager(), ""+tv_content.getId());
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
				send();
				break;
			}
			return super.onOptionsItemSelected(item);
			}

	  @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	  {
	    if (resultCode == RESULT_OK)
	    {
	    	switch(requestCode){
	    	case (short)R.id.comment_iv_front:
	    		requestCode = R.id.comment_iv_front;
	    	    break;
	    	case (short)R.id.comment_iv_back:
	    		requestCode = R.id.comment_iv_back;
	    	    break;
	    	case (short)R.id.comment_iv_side:
	    		requestCode = R.id.comment_iv_side;
	    	    break;
	    	case (short)R.id.comment_iv_more:
	    		requestCode = R.id.comment_iv_more;
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

	  @Override
	protected void onCreate(Bundle bundle)
	  {
	    super.onCreate(bundle);
	    setContentView(R.layout.activity_comment);
	    setupActionBar();
	    this.bookingId = getIntent().getLongExtra("bookingId", 0L);
	    this.tv_content = ((TextView)findViewById(R.id.comment_tv_content));
	    this.rb = ((RatingBar)findViewById(R.id.comment_rb));
	    this.app = ((App)getApplication());
	    this.bitmapHash = new HashMap<Integer,byte[]>();
	  }

	  public void send()
	  {
	    String content = this.tv_content.getText().toString();
	    if ((content == null) || (content.length() == 0) || (TextLengthInputFilter.getChineseCount(content) + content.length() > 1000)){
	      input(null);
	    }
	      if ((this.bitmapHash == null) || (this.bitmapHash.size() == 0)){
	        uploadImg(findViewById(R.id.comment_iv_front));
	        return;
	      }
	      RequestParams params = new RequestParams();
	      params.put("comment", content);
	      params.put("rating", ""+(int)(this.rb.getRating() - 3.0F));
	    for(int i: bitmapHash.keySet()){
				String name="";
				switch(i){
				case R.id.comment_iv_front:name="front";break;
				case R.id.comment_iv_back:name="back";break;
				case R.id.comment_iv_side:name="side";break;
				case R.id.comment_iv_more:name="more";break;
				} 
				params.put(name,  new ByteArrayInputStream(bitmapHash.get(i)));
	    }
	    Log.d("-commentActivity","send:"+bitmapHash.size());
	    AsyncHttpClient client=new AsyncHttpClient();
	    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
	    Header header = BasicScheme.authenticate(credentials, "UTF-8", false);
//	    Header[] headers=new Header[]{header};
	    client.addHeader(header.getName(), header.getValue());
//	    client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
	    client.post(this, Constance.SERVER_URL+"booking/" + bookingId + "/comment", params, new AsyncHttpResponseHandler()
	      {
	        @Override
			public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error)
	        {
	        	Log.d("-commentActivity","onFailure:"+Log.getStackTraceString(error));
	          AlertHelper.showToast(CommentActivity.this, R.string.error_serverError);
	        }

	        @Override
			public void onSuccess(int statusCode,Header[] headers,byte[] content)
	        {
	        	AlertHelper.showToast(CommentActivity.this, R.string.info_commentDone);
	          finish();
	        }
	      });
	  }

	  public void uploadImg(View paramView)
	  {
	    new AlertDialog.Builder(this).setItems(getResources().getStringArray(R.array.action_takeOrRotateOrClearPhoto), new UploadListener(paramView)).create().show();
	  }

	  class UploadListener
	    implements DialogInterface.OnClickListener
	  {
	    View v;

	    public UploadListener(View v)
	    {
	      this.v = v;
	    }

	    @Override
		public void onClick(DialogInterface dialog, int which)
	    {
	      if (which == 0){
	        Intent localIntent1 = new Intent("android.media.action.IMAGE_CAPTURE");
	        localIntent1.putExtra("output", Uri.fromFile(Constance.sdcardTempFile));
	        localIntent1.putExtra("crop", "true");
	        localIntent1.putExtra("aspectX", 1);
	        localIntent1.putExtra("aspectY", 1);
	        localIntent1.putExtra("outputX", 360);
	        localIntent1.putExtra("outputY", 360);
	        startActivityForResult(localIntent1, (short)v.getId());
	      }else if (which == 1){
	        Intent localIntent2 = new Intent("android.intent.action.PICK");
	        localIntent2.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
	        localIntent2.putExtra("output", Uri.fromFile(Constance.sdcardTempFile));
	        localIntent2.putExtra("crop", "true");
	        localIntent2.putExtra("aspectX", 1);
	        localIntent2.putExtra("aspectY", 1);
	        localIntent2.putExtra("outputX", 360);
	        localIntent2.putExtra("outputY", 360);
	        startActivityForResult(localIntent2, (short)v.getId());
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
	      bitmapHash.remove(Integer.valueOf(v.getId()));
	      }
	    }
	  }
}
