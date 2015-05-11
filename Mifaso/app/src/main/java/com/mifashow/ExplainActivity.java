package com.mifashow;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mifashow.data.Constance;
import com.mifashow.domain.Booking;
import com.mifashow.tool.AlertHelper;
import com.mifashow.tool.ImageHelper;
import com.mifashow.tool.TextLengthInputFilter;
import com.mifashow.ui.RenderView;
import com.mifashow.ui.ResizedImageView;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.apache.http.Header;

public class ExplainActivity extends FragmentActivity {
	  App app;
	  Booking booking;
	  ResizedImageView iv_figure;
	  RenderView rv_comment;
	  TextView tv_comment;
	  TextView tv_explanation;
	  
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
				explain();
				break;
			}
			return super.onOptionsItemSelected(item);
			}

	  public void arbitrate(MenuItem menuItem)
	  {
	    String str = this.tv_explanation.getText().toString();
	    if ((str == null) || (str.length() == 0) || (TextLengthInputFilter.getChineseCount(str) + str.length() > 1000))
	    {
	      input(null);
	      return;
	    }
	    RequestParams params = new RequestParams();
	    params.put("explanation", str);
	    AsyncHttpClient client=new AsyncHttpClient();
		client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
	    client.put(this, Constance.SERVER_URL+"booking/" + booking.getBookingId() + "/arbitrate", params, new AsyncHttpResponseHandler()
	    {
	      @Override
		public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error)
	      {
	        AlertHelper.showToast(ExplainActivity.this, R.string.error_serverError);
	      }

	      @Override
		public void onSuccess(int statusCode,Header[] headers,byte[] content)
	      {
	    	  AlertHelper.showToast(ExplainActivity.this, R.string.info_arbitrateDone);
	        finish();
	      }
	    });
	  }

	  public void explain()
	  {
	    String str = tv_explanation.getText().toString();
	    RequestParams params = new RequestParams();
	    params.put("explanation", str);
	    AsyncHttpClient client=new AsyncHttpClient();
		client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
	    client.put(this, Constance.SERVER_URL+"booking/" + booking.getBookingId() + "/explain", params, new AsyncHttpResponseHandler()
	    {
	      @Override
		public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error)
	      {
	    	  AlertHelper.showToast(ExplainActivity.this, R.string.error_serverError);
	      }

	      @Override
		public void onSuccess(int statusCode,Header[] headers,byte[] content)
	      {
	    	  AlertHelper.showToast(ExplainActivity.this, R.string.info_explainDone);
	    	  app.refreshUser();
	        finish();
	      }
	    });
	  }

	  public void input(View view)
	  {
	    InputDialog inputDialog = InputDialog.newInstance(getResources().getString(R.string.explain_input_title), tv_explanation.getText().toString(), null,InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE, 1000);
	    inputDialog.addListener(new InputDialog.InputListener()
	    {
	      @Override
		public void OnFinish(String content)
	      {
	        ExplainActivity.this.tv_explanation.setText(content);
	      }
	    });
	    inputDialog.show(getSupportFragmentManager(), ""+tv_explanation.getId());
	  }

	  @Override
	protected void onCreate(Bundle bundle)
	  {
	    super.onCreate(bundle);
	    setContentView(R.layout.activity_explain);
	    setupActionBar();
//	    getActionBar().hide();
	    this.booking = ((Booking)getIntent().getSerializableExtra("booking"));
	    this.iv_figure = ((ResizedImageView)findViewById(R.id.explain_iv_figure));
	    this.tv_comment = ((TextView)findViewById(R.id.explain_tv_comment));
	    this.tv_explanation = ((TextView)findViewById(R.id.explain_tv_explanation));
	    this.rv_comment = ((RenderView)findViewById(R.id.explain_rv_comment));
	    this.app = ((App)getApplication());
	    ImageHelper.loadImg(iv_figure, booking.getCustomerFigure(), 0,true);
	    this.tv_comment.setText(booking.getComment());
	    if(booking.getCommentRendering()!=null){
	    	this.rv_comment.setImages(booking.getCommentRendering().split(","));
	    	this.rv_comment.setOnClickListener(new View.OnClickListener(){
	  	      @Override
	  		public void onClick(View paramAnonymousView)
	  	      {
	  	        Intent intent = new Intent();
	  	        intent.putExtra("renderingUrl", booking.getCommentRendering().split(","));
	  	        intent.setClass(ExplainActivity.this, ViewerActivity.class);
	  	        startActivity(intent);
	  	      }
	  	    });
	    }   
	  }

	  @Override
	public boolean onCreateOptionsMenu(Menu menu)
	  {
	    getMenuInflater().inflate(R.menu.expain, menu);
	    return true;
	  }
}
