package com.mifashow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mifashow.data.Constance;
import com.mifashow.data.Constance.DISCOUNT;
import com.mifashow.data.Constance.WEEKDAY;
import com.mifashow.domain.Posting;
import com.mifashow.tool.AlertHelper;
import com.mifashow.ui.WrappedButtonGroup;
import com.mifashow.ui.WrappedViewGroup;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.http.Header;

public class BookingsetActivity extends FragmentActivity implements View.OnClickListener{
	App app;
	private ImageButton bt_sumbit;
	Posting posting;
	private TextView tv_listPrice;
	private Button[] bt_times;
	private WrappedViewGroup wv_time;
	private WrappedButtonGroup wv_discount,wv_day;
	String[] str_days,str_discounts;

	public BookingsetActivity() {
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
		if (this.wv_discount != null)
			outState.putInt("discount", this.wv_discount.getSelect());
		    super.onSaveInstanceState(outState);
    }
	 @Override   
	 public void onRestoreInstanceState(Bundle savedInstanceState) {
		 super.onRestoreInstanceState(savedInstanceState);
		 this.wv_discount.setSelect(savedInstanceState.getInt("discount", 0));
	 } 
	 
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_bookingset);
		getActionBar().hide();
		app = ((App)getApplication());
		posting=(Posting) getIntent().getSerializableExtra("posting");
		str_days=getResources().getStringArray(R.array.enum_weekday);
		str_discounts=getResources().getStringArray(R.array.enum_discount);
		bt_sumbit = ((ImageButton)findViewById(R.id.bookingset_bt_submit));
	    bt_sumbit.setOnClickListener(this);
	    tv_listPrice = ((TextView)findViewById(R.id.bookingset_tv_listPrice));
	    tv_listPrice.setText(getResources().getString(R.string.bookingset_tv_listPrice, posting.getPrice()));
	    tv_listPrice.setOnClickListener(this);
	    wv_discount=(WrappedButtonGroup) findViewById(R.id.bookingset_wv_discount);
	    wv_discount.setSelect(0);
	    wv_day=(WrappedButtonGroup) findViewById(R.id.bookingset_wv_day);
		wv_time=(WrappedViewGroup) findViewById(R.id.bookingset_wv_time);
		WEEKDAY[] bookingDay = posting.getBookingDay();
		ArrayList<Integer> tmpArr=new ArrayList<Integer>();
		if(bookingDay!=null)
		for(WEEKDAY w:bookingDay){
			tmpArr.add(w.ordinal());
		}
		if(tmpArr!=null)wv_day.setCheck(tmpArr);
		DISCOUNT[] bookingTime = posting.getBookingTime();
		if (bookingTime == null){
			bookingTime = new DISCOUNT[48];
			for(int i=0;i<48;i++)bookingTime[i] =DISCOUNT.NO;
		    posting.setBookingTime(bookingTime);
		}
		bt_times = new Button[48];
		updateTime();
		
		
	}
	private void updateTime(){
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		SimpleDateFormat f=new SimpleDateFormat("HH:mm",Locale.getDefault());
		wv_time.removeAllViews();
		for(int i=0;i<48;i++){
			Calendar c=((Calendar)cal.clone());
			c.add(Calendar.MINUTE, 30*i);
			Button b=new Button(BookingsetActivity.this);
//			b.setWidth(114);
			b.setText(f.format(c.getTime())+"\n"+str_discounts[posting.getBookingTime()[i].ordinal()]);
			b.setBackgroundResource(R.drawable.selector_5);
			b.setOnClickListener(new TimeButtonListener(i));
			wv_time.addView(b);
			bt_times[i]=b;	
			if (this.posting.getBookingTime()[i] == Constance.DISCOUNT.NO){
			      b.setBackgroundColor(getResources().getColor(R.color.lightest));
			}else{
				b.setBackgroundColor(getResources().getColor(R.color.shadow_light));
			      b.setTextColor(getResources().getColor(R.color.emphasize_medium));
			}
		}
	}
	class TimeButtonListener implements OnClickListener{
		int i;
		public TimeButtonListener(int i){
			this.i=i;
		}

		@Override
		public void onClick(View v) {
			DISCOUNT[] d=posting.getBookingTime();
			for(int j=i;j<48;j++){
				d[j]=DISCOUNT.values()[wv_discount.getSelect()];
			}
			posting.setBookingTime(d);
			updateTime();
		}
		
	}
	
	
	@Override
	public void onClick(View view)
	  {
	    switch (view.getId())
	    {
	    case R.id.bookingset_tv_listPrice:
	      InputDialog inputDialog = InputDialog.newInstance(getResources().getString(R.string.bookingset_tv_listPrice_input), ""+posting.getPrice(), null,InputType.TYPE_CLASS_NUMBER, 5);
	      inputDialog.addListener(new InputDialog.InputListener()
	      {
	        @Override
			public void OnFinish(String content)
	        {
	          posting.setPrice(Integer.parseInt(content));
	          tv_listPrice.setText(getResources().getString(R.string.bookingset_tv_listPrice, posting.getPrice()));
	        }
	      });
	      inputDialog.show(getSupportFragmentManager(),""+tv_listPrice.getId());
	      break;
	    case R.id.bookingset_bt_submit:
	    if (posting.getPrice() == 0){
	      onClick(this.tv_listPrice);
	      break;
	    }
	    ArrayList<Integer> dayArr=wv_day.getCheck();
		WEEKDAY[] bookingDay=new WEEKDAY[dayArr.size()];
		for(int i=0;i<dayArr.size();i++){
			bookingDay[i]=WEEKDAY.values()[dayArr.get(i)];
		}
		posting.setBookingDay(bookingDay);
	        RequestParams params = new RequestParams();
	        params.put("price", ""+posting.getPrice());
	        params.put("bookingDay", Constance.buildSet(posting.getBookingDay()));
	        params.put("bookingTime", Constance.buildSet(posting.getBookingTime()));
	        AsyncHttpClient client=new AsyncHttpClient();
	        client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
	        client.put(this, Constance.SERVER_URL+"posting/" + posting.getPostingId() + "/bookingset", params, new AsyncHttpResponseHandler()
	        {
	          @Override
			public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error)
	          {
	        	  AlertHelper.showToast(BookingsetActivity.this,R.string.error_serverError);
	          }

	          @Override
			public void onSuccess(int statusCode,Header[] headers,byte[] content){
	        	  Posting p=new Gson().fromJson(new String(content), Posting.class);
//	        	  List<Posting> myPostings=app.getMyPostings();
//	        	  for(int i=0;i<myPostings.size();i++){
//	        		  if(myPostings.get(i).getPostingId()==p.getPostingId()){
//	        			  myPostings.remove(i);
//	        			  myPostings.add(i, p);
//	        			  break;
//	        		  }
//	        	  }
//	        	  app.setMyPostings(myPostings);
	        	  Intent intent = new Intent();  
	        	  intent.putExtra("posting",p);  
	        	  setResult(Activity.RESULT_OK, intent);

	            finish();
	          }
	        });
	        break;
	      }
	    }

}
