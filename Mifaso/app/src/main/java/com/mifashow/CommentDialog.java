package com.mifashow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mifashow.data.Constance;
import com.mifashow.data.Constance.BOOKINGSTATUS;
import com.mifashow.data.Constance.USERTYPE;
import com.mifashow.domain.Booking;
import com.mifashow.tool.ImageHelper;
import com.mifashow.ui.RenderView;
import com.mifashow.ui.ResizedImageView;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.Header;


public class CommentDialog extends DialogFragment {
	App app;
	CommentAdapter adapter;
	static enum SOURCETYPE{POSTING,BOOKING,STYLIST,CUSTOMER};
	SOURCETYPE sourceType;
	long sourceId;
	int mNum;
	private List<Booking> bookings;
	ListView commentList;
	String sql;
	LinearLayout lo_noComment;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle=getArguments();
		if(bundle!=null){
		sourceType=(SOURCETYPE) bundle.getSerializable("sourceType");
		sourceId=bundle.getLong("sourceId");
		}
		app = ((App)getActivity().getApplication());
		if(sourceType==null){
			sourceType=app.getLoginUser().getUserType()==USERTYPE.STYLIST?SOURCETYPE.STYLIST:SOURCETYPE.CUSTOMER;
			sourceId=app.getLoginUser().getUserId();
		}
		bookings=new ArrayList<Booking>();
		List<Booking> bs=app.getMyBookings();
		if(bs!=null && (sourceType == SOURCETYPE.STYLIST || sourceType==SOURCETYPE.CUSTOMER) && sourceId==app.getLoginUser().getUserId()){
			for(Booking b:bs){
	        	  if ((sourceType ==SOURCETYPE.STYLIST && b.getStatus() == BOOKINGSTATUS.TERMINATE) || (sourceType ==SOURCETYPE.CUSTOMER && b.getCommentTime() > 0L)){
	        		  bookings.add(b);
	        	  }
	          }
		}
		if(sourceType == SOURCETYPE.POSTING){
			sql=Constance.SERVER_URL+"booking/posting/" + sourceId;
		}else if(sourceType == SOURCETYPE.STYLIST){
			sql=Constance.SERVER_URL+"booking/stylist/"+sourceId;
		}else if(sourceType == SOURCETYPE.CUSTOMER){
			sql=Constance.SERVER_URL+"booking/customer/"+sourceId;
		}else if(sourceType == SOURCETYPE.BOOKING){
			sql=Constance.SERVER_URL+"booking/"+sourceId;
		}
		
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	this.getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    	this.getDialog().getWindow().requestFeature(STYLE_NO_FRAME);
    	this.getDialog().getWindow().requestFeature(STYLE_NO_TITLE);
    	View v = inflater.inflate(R.layout.fragment_comment, container, false);
    	lo_noComment=(LinearLayout) v.findViewById(R.id.comment_lo_noComment);
    	commentList=(ListView) v.findViewById(R.id.comment_lv_list);
        adapter = new CommentAdapter(inflater);
        this.commentList.setAdapter(adapter);
        AsyncHttpClient client=new AsyncHttpClient();
		client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
        client.get(sql, new AsyncHttpResponseHandler(){
	        @Override
			public void onSuccess(int statusCode,Header[] headers,byte[] content){
	        	List<Booking> bs;
	        	if(sourceType==SOURCETYPE.BOOKING){
	        		Booking b=new Gson().fromJson(new String(content), Booking.class);
	        		bs=new ArrayList<Booking>();
	        		bs.add(b);
	        	}else{
	                bs = new Gson().fromJson(new String(content), new TypeToken<List<Booking>>(){}.getType());
	        	}
	          if((sourceType == SOURCETYPE.STYLIST || sourceType==SOURCETYPE.CUSTOMER) && sourceId==app.getLoginUser().getUserId() && bs!=null && bs.size()>0)app.setMyBookings(bs);
	          bookings=new ArrayList<Booking>();
				for(Booking b:bs){
		        	  if (sourceType==SOURCETYPE.BOOKING || (b.getStatus() == BOOKINGSTATUS.TERMINATE && b.getCommentTime() > 0L)){
		        		  bookings.add(b);
		        	  }
		          }
				if(bookings.size()>0){
					commentList.setBackgroundResource(R.color.darkest_transparent);
				}else{
					commentList.setBackgroundColor(getResources().getColor(android.R.color.transparent));
				}
				adapter.notifyDataSetChanged();
	        }
            @Override
            public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
                Log.d("-CommentDialog", "getComment:failure");
                Log.getStackTraceString(error);
            }
	      });
        if(bookings.size()>0)commentList.setBackgroundResource(R.color.darkest_transparent);
        return v;
    }
    private String formatTime(long time){
      Calendar cal_time = Calendar.getInstance();
      cal_time.setTimeInMillis(time);
      Calendar cal_now = Calendar.getInstance();
      long l = cal_now.getTimeInMillis();
      if (time / 8640000L == l / 8640000L)
        return cal_time.get(Calendar.HOUR_OF_DAY) + ":" + cal_time.get(Calendar.MINUTE);
      if (cal_time.get(Calendar.YEAR) == cal_now.get(Calendar.YEAR))
        return cal_time.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) + " " + cal_time.get(Calendar.DATE);
      return cal_time.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) + " " + cal_time.get(Calendar.DATE) + "," + cal_time.get(Calendar.YEAR);
    }
    class CommentAdapter extends BaseAdapter{
    	LayoutInflater inflater;
        ResizedImageView iv_customerFigure;
        ResizedImageView iv_stylistFigure;
        LinearLayout lo_explanation;
        RenderView rv;
        TextView tv_comment;
        TextView tv_commentTime;
        TextView tv_explainTime;
        TextView tv_explanation;
        
    	public CommentAdapter(LayoutInflater inflater){
    		this.inflater=inflater;
    	}
    	@Override  
    	public boolean isEnabled(int position) {   
    	   return false;   
    	}

		@Override
		public int getCount() {
			int count=bookings.size();
			if(count==0){
				lo_noComment.setVisibility(View.VISIBLE);
			}else{

				lo_noComment.setVisibility(View.GONE);
			}
			return count;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return bookings.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return bookings.get(arg0).getBookingId();
		}

		@Override
		public View getView(final int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			final Booking booking = (Booking)getItem(arg0);
			View view=inflater.inflate(R.layout.list_comment, null);
			this.lo_explanation = ((LinearLayout)view.findViewById(R.id.commentlist_lo_explanation));
		      this.iv_customerFigure = ((ResizedImageView)view.findViewById(R.id.commentlist_iv_customerFigure));
		      this.iv_stylistFigure = ((ResizedImageView)view.findViewById(R.id.commentlist_iv_stylistFigure));
		      this.rv = ((RenderView)view.findViewById(R.id.commentlist_rv));
		      this.tv_comment = ((TextView)view.findViewById(R.id.commentlist_tv_comment));
		      this.tv_commentTime = ((TextView)view.findViewById(R.id.commentlist_tv_commentTime));
		      this.tv_explanation = ((TextView)view.findViewById(R.id.commentlist_tv_explanation));
		      this.tv_explainTime = ((TextView)view.findViewById(R.id.commentlist_tv_explainTime));
		      ImageHelper.loadImg(this.iv_customerFigure, booking.getCustomerFigure(), 5,true);
		      iv_customerFigure.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					ProfileFragment profileFragment = new ProfileFragment();
					Bundle bundle = new Bundle();
				      bundle.putLong("userId",booking.getCustomerId());
				      profileFragment.setArguments(bundle);
				      profileFragment.show(getFragmentManager(), ""+profileFragment.getId());
				}
		      });
		      if(booking.getCommentRendering()!=null){
		    	  this.rv.setImages(booking.getCommentRendering().split(","));
		    	  this.rv.setOnClickListener(new View.OnClickListener()
			      {
			        @Override
					public void onClick(View view)
			        {
			          Intent intent = new Intent();
			          intent.putExtra("renderingUrl", booking.getCommentRendering().split(","));
			          intent.setClass(view.getContext(), ViewerActivity.class);
			          CommentDialog.this.startActivity(intent);
			        }
			      });
		      }
		      this.tv_commentTime.setText(formatTime(booking.getCommentTime()));
		      SpannableStringBuilder spannableStringBuilder1 = new SpannableStringBuilder(booking.getCustomerName() + "  " + booking.getComment());
		      spannableStringBuilder1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.emphasize_medium)), 0, booking.getCustomerName().length(), 33);
		      this.tv_comment.setText(spannableStringBuilder1);
		      if ((booking.getExplanation() != null) && (!"".equals(booking.getExplanation())))
		      {
		        ImageHelper.loadImg(this.iv_stylistFigure, booking.getStylistFigure(), 0,true);
		        SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(booking.getStylistName() + "  " + booking.getExplanation());
		        spannableStringBuilder2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.emphasize_medium)), 0, booking.getStylistName().length(), 33);
		        this.tv_explanation.setText(spannableStringBuilder2);
		        this.tv_explainTime.setText(formatTime(booking.getExplainTime()));
		        this.lo_explanation.setVisibility(View.VISIBLE);
		        return view;
		      }
		      this.lo_explanation.setVisibility(View.GONE);
		      return view;
		}
    	
    }


}
