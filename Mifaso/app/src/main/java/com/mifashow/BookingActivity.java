package com.mifashow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mifashow.data.Constance;
import com.mifashow.data.Constance.BOOKINGSTATUS;
import com.mifashow.data.Constance.USERTYPE;
import com.mifashow.domain.Booking;
import com.mifashow.tool.AlertHelper;
import com.mifashow.tool.ImageHelper;
import com.mifashow.ui.ResizedImageView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.AsyncTask.Status;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class BookingActivity extends FragmentActivity {
	private final int COMMENT=0;
	private final int EXPLAIN=1;
	private App app;
	private ExpandableListView elv;
	private String[] groupNames;
	private List<Booking> bookings;
	private long userId;
	private USERTYPE userType;
	public BaseExpandableListAdapter adapter;
	List<List<Booking>> groups;
	List<Booking> todayBookings,tomorrowBookings,thisWeekBookings,historyBookings;
	private Handler handler = new Handler();
	private LoadBookingTask loadBookingTask;
    private Runnable runnable = new Runnable() {
        @Override
		public void run() {
        		if(loadBookingTask!=null && loadBookingTask.getStatus()==Status.RUNNING){
        			loadBookingTask.cancel(true);
        		}
        		loadBookingTask=new LoadBookingTask();
        		loadBookingTask.execute();
            handler.postDelayed(this, 1000 * 60);
//        	AsyncHttpClient client = new AsyncHttpClient();
//        	client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
//    		String booking_sql=userType==USERTYPE.CUSTOMER?Constance.SERVER_URL+"booking/customer/"+userId:Constance.SERVER_URL+"booking/stylist/"+userId;
//    		client.get(booking_sql, new AsyncHttpResponseHandler(){
//    			@Override
//    			public void onSuccess(String response) {
//    				 List<Booking> bs = new Gson().fromJson(response, new TypeToken<List<Booking>>(){}.getType());
//    				if(bs!=null && bs.size()>0){
//    					bookings=bs;
//    					prepareGroups();
//    					adapter.notifyDataSetChanged();
//    					for(int i=0;i<adapter.getGroupCount();i++){
//    						elv.expandGroup(i);
//    					}
//    					try{
//    					if(userId==app.getLoginUser().getUserId()){
//    						app.setMyBookings(bookings);
//    					}
//    					}catch(Exception e){}
//    				}
//    			}
//    		});
//            handler.postDelayed(this, 1000 * 120);
        }
    }; 
    @Override  
    public void onDestroy(){
    	if(loadBookingTask!=null && loadBookingTask.getStatus()==Status.RUNNING){
    		loadBookingTask.cancel(true);
		}
		handler.removeCallbacks(runnable); 
        super.onDestroy();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booking);
		getActionBar().hide();
		app=(App) getApplication();
		userId=getIntent().getLongExtra("userId",-1);
		userType=(USERTYPE) getIntent().getSerializableExtra("userType");
		if(userId==-1 || userId==app.getLoginUser().getUserId()){
			bookings=app.getMyBookings();
			userId=app.getLoginUser().getUserId();
			userType=app.getLoginUser().getUserType();
		}
		if(bookings==null)bookings=new ArrayList<Booking>();
//		bookings=(List<Booking>) getIntent().getSerializableExtra("bookings");
		groups=new ArrayList<List<Booking>>();
		todayBookings=new ArrayList<Booking>();
		tomorrowBookings=new ArrayList<Booking>();
		thisWeekBookings=new ArrayList<Booking>();
		historyBookings=new ArrayList<Booking>();
		groupNames=getResources().getStringArray(R.array.booking_timeGroup);
		elv=(ExpandableListView) findViewById(R.id.booking_elv);
		elv.setGroupIndicator(null);
		prepareGroups();
//		new RefreshTask().execute();
		adapter=new BaseExpandableListAdapter (){

			@Override
			public boolean areAllItemsEnabled() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public Object getChild(int groupPosition, int childPosition) {
				return groups.get(groupPosition).get(childPosition);
			}

			@Override
			public long getChildId(int groupPosition, int childPosition) {
				return ((Booking)getChild(groupPosition,childPosition)).getBookingId();
			}

			@Override
			public View getChildView(final int groupPosition, int childPosition,
					boolean isLastChild, View convertView, ViewGroup parent) {
				LayoutInflater inflater = LayoutInflater.from(BookingActivity.this);  
	            if(convertView==null)convertView = inflater.inflate(R.layout.list_booking, null);
	            TextView tv_agreedDate=(TextView) convertView.findViewById(R.id.booking_tv_agreedDate);
	            TextView tv_agreedTime=(TextView) convertView.findViewById(R.id.booking_tv_agreedTime);
	            TextView tv_userName=(TextView) convertView.findViewById(R.id.booking_tv_userName);
	            TextView tv_agreedPrice=(TextView) convertView.findViewById(R.id.booking_tv_agreedPrice);
	            TextView tv_status=(TextView) convertView.findViewById(R.id.booking_tv_status);
	            ResizedImageView iv_postingRendering=(ResizedImageView) convertView.findViewById(R.id.booking_iv_postingRendering);
	            ResizedImageView iv_userFirgure=(ResizedImageView) convertView.findViewById(R.id.booking_iv_userFigure);
	            Button bt_cancel=(Button) convertView.findViewById(R.id.booking_bt_cancel);
	            Button bt_explain=(Button) convertView.findViewById(R.id.booking_bt_explain);
	            Button bt_comment=(Button) convertView.findViewById(R.id.booking_bt_comment);
	            final Booking b=(Booking) getChild(groupPosition,childPosition);
	            SimpleDateFormat dateFormat=new SimpleDateFormat("MM-dd",Locale.getDefault());
	            SimpleDateFormat timeFormat=new SimpleDateFormat("hh:mm",Locale.getDefault());
	            Date agreedTime=new Date(b.getAgreedTime());
//	            if(groupPosition<2){
//	            	tv_agreedDate.setVisibility(View.GONE);
//	            }else{
//	            	tv_agreedDate.setText(dateFormat.format(agreedTime));
//	            }
	            tv_agreedTime.setText(timeFormat.format(agreedTime));
	            tv_userName.setText(userId==b.getStylistId()?b.getCustomerName():b.getStylistName());
	            tv_agreedPrice.setText(getResources().getString(R.string.booking_tv_agreedPrice_text,b.getAgreedPrice()));
	            tv_status.setText(getResources().getStringArray(R.array.booking_status)[b.getStatus().ordinal()]);
	            ImageHelper.loadImg(iv_postingRendering,b.getPostingRendering()==null?"":b.getPostingRendering().split(",")[0],10,false);
	            ImageHelper.loadImg(iv_userFirgure,userId==b.getStylistId()?b.getCustomerFigure():b.getStylistFigure(),10,false);
	            iv_postingRendering.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						PostingFragment postingFragment = new PostingFragment();
						Bundle bundle = new Bundle();
					      bundle.putSerializable("sourceType", PostingFragment.SOURCETYPE.ID);
					      bundle.putLong("sourceId",b.getPostingId());
					      postingFragment.setArguments(bundle);
						postingFragment.show(getSupportFragmentManager(), ""+postingFragment.getId());
					}
	            	
	            });
	            iv_userFirgure.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						ProfileFragment profileFragment = new ProfileFragment();
						Bundle bundle = new Bundle();
					      bundle.putLong("userId",userId==b.getStylistId()?b.getCustomerId():b.getStylistId());
					      profileFragment.setArguments(bundle);
						profileFragment.show(getSupportFragmentManager(), ""+profileFragment.getId());
					}
	            	
	            });
	            bt_cancel.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						AlertHelper.showAlert(BookingActivity.this, getResources().getString(R.string.dialog_cancelBooking_title), getResources().getString(R.string.dialog_cancelBooking_message), getResources().getString(R.string.action_ok), 
								new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								new CancelBookingTask().execute(b.getBookingId(),groupPosition);
							}
						},  getResources().getString(R.string.action_cancel), 
						new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface arg0, int arg1) {}
						});
					}
	            });
	            bt_comment.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.putExtra("bookingId", b.getBookingId());
						intent.setClass(BookingActivity.this, CommentActivity.class);
			              startActivityForResult(intent,COMMENT);
					}
	            });
	            bt_explain.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.putExtra("booking", b);
						intent.setClass(BookingActivity.this, ExplainActivity.class);
			              startActivityForResult(intent,EXPLAIN);
					}
	            });
	            tv_agreedDate.setText(dateFormat.format(agreedTime));
	            if(groupPosition==3 && b.getStatus()==Constance.BOOKINGSTATUS.COMMITTING)tv_status.setText(getResources().getStringArray(R.array.booking_status)[6]);
	            if ((app.getLoginUser().getUserId() == b.getStylistId()) && (b.getStatus() == Constance.BOOKINGSTATUS.COMMENTING) && (b.getAgreedTime() > Calendar.getInstance().getTimeInMillis()))
	            {
	            	bt_cancel.setVisibility(View.VISIBLE);
	            	bt_explain.setVisibility(View.GONE);
	              tv_status.setVisibility(View.GONE);
	              bt_comment.setVisibility(View.GONE);
	            }
	            else if ((app.getLoginUser().getUserId() == b.getStylistId()) && (b.getStatus() == Constance.BOOKINGSTATUS.EXPLAINING) && (Calendar.getInstance().getTimeInMillis() - b.getCommentTime() < 1296000000L))
	            {
	              bt_explain.setVisibility(View.VISIBLE);
	              bt_cancel.setVisibility(View.GONE);
	              tv_status.setVisibility(View.GONE);
	              bt_comment.setVisibility(View.GONE);
	            }
	            else if ((app.getLoginUser().getUserId() == b.getCustomerId()) && (b.getStatus() == Constance.BOOKINGSTATUS.COMMITTING || b.getStatus() == Constance.BOOKINGSTATUS.COMMENTING) && (b.getAgreedTime() > Calendar.getInstance().getTimeInMillis()))
	            {
	            	bt_explain.setVisibility(View.GONE);
	            	bt_cancel.setVisibility(View.VISIBLE);
	              tv_status.setVisibility(View.GONE);
	              bt_comment.setVisibility(View.GONE);
	            }
	            else if ((app.getLoginUser().getUserId() == b.getCustomerId()) && (b.getStatus() == Constance.BOOKINGSTATUS.COMMENTING) && (Calendar.getInstance().getTimeInMillis() - b.getAgreedTime() < 1296000000L))
	            {
	            	bt_explain.setVisibility(View.GONE);
	            	bt_cancel.setVisibility(View.GONE);
	              tv_status.setVisibility(View.GONE);
	              bt_comment.setVisibility(View.VISIBLE);
	            }
	            else if ((app.getLoginUser().getUserId() == b.getCustomerId()) && (b.getStatus() == Constance.BOOKINGSTATUS.EXPLAINING) && (Calendar.getInstance().getTimeInMillis() - b.getCommentTime() < 1296000000L))
	            {
	            	bt_explain.setVisibility(View.GONE);
	            	bt_cancel.setVisibility(View.GONE);
	              tv_status.setVisibility(View.GONE);
	              bt_comment.setText(R.string.action_reComment);
	              bt_comment.setVisibility(View.VISIBLE);
	            }
	            else
	            {
	            	bt_cancel.setVisibility(View.GONE);
	              tv_status.setVisibility(View.VISIBLE);
	              bt_comment.setVisibility(View.GONE);
	              bt_explain.setVisibility(View.GONE);
	            }
	            return convertView;
			}

			@Override
			public int getChildrenCount(int groupPosition) {
				return groups.get(groupPosition).size();
			}

			@Override
			public long getCombinedChildId(long groupId, long childId) {
				return childId;
			}

			@Override
			public long getCombinedGroupId(long groupId) {
				return groupId;
			}

			@Override
			public Object getGroup(int groupPosition) {
				return groups.get(groupPosition);
			}

			@Override
			public int getGroupCount() {
				return groupNames.length;
			}

			@Override
			public long getGroupId(int groupPosition) {
				return groupPosition;
			}

			@Override
			public View getGroupView(int groupPosition, boolean isExpanded,
					View convertView, ViewGroup parent) {
				TextView tv_group=new TextView(parent.getContext());
				tv_group.setTextSize(40);
				tv_group.setTypeface(Typeface.SERIF);
				tv_group.setTextColor(getResources().getColor(R.color.emphasize_medium));
				tv_group.setText(groupNames[groupPosition]);
				
				return tv_group;
			}

			@Override
			public boolean hasStableIds() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isChildSelectable(int groupPosition,
					int childPosition) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return bookings==null||bookings.size()==0;
			}

			@Override
			public void onGroupCollapsed(int groupPosition) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onGroupExpanded(int groupPosition) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void registerDataSetObserver(DataSetObserver observer) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void unregisterDataSetObserver(DataSetObserver observer) {
				// TODO Auto-generated method stub
				
			}
			
		};
		elv.setAdapter(adapter);
		runnable.run();
	}
	private void prepareGroups(){
		todayBookings.clear();
		tomorrowBookings.clear();
		thisWeekBookings.clear();
		historyBookings.clear();
		for(Booking b:bookings){
			Calendar theDayBeforeAgreedDay=Calendar.getInstance();
			theDayBeforeAgreedDay.setTimeInMillis(b.getAgreedTime());
			theDayBeforeAgreedDay.add(Calendar.DATE, -1);
			if (Calendar.getInstance().getTimeInMillis() > b.getAgreedTime())
		        this.historyBookings.add(b);
		      else if (DateUtils.isToday(b.getAgreedTime()))
		        this.todayBookings.add(b);
		      else if (DateUtils.isToday(theDayBeforeAgreedDay.getTimeInMillis()))
		        this.tomorrowBookings.add(b);
		      else
		        this.thisWeekBookings.add(b);
		}
		groups.add(todayBookings);
		groups.add(tomorrowBookings);
		groups.add(thisWeekBookings);
		groups.add(historyBookings);
	}
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==COMMENT ||requestCode==EXPLAIN){
        	if(loadBookingTask!=null && loadBookingTask.getStatus()==Status.RUNNING){
    			loadBookingTask.cancel(true);
    		}
    		loadBookingTask=new LoadBookingTask();
    		loadBookingTask.execute();
        }
    }
	class LoadBookingTask extends AsyncTask<Void, Void, String>{
		@Override
		protected String doInBackground(Void... arg0) {
			Log.d("-bookingActivity","LoadBookingTask:doInBackground");
			String sql=userType==USERTYPE.CUSTOMER?Constance.SERVER_URL+"booking/customer/"+userId:Constance.SERVER_URL+"booking/stylist/"+userId;
			HttpGet request=new HttpGet(sql);
			request.addHeader(BasicScheme.authenticate(
					 new UsernamePasswordCredentials(app.getLoginUser().getSign(), app.getLoginUser().getPassword()),
					 "UTF-8", false));
			AndroidHttpClient client=AndroidHttpClient.newInstance("loadBooking");
			String result = null;
			try {
				HttpResponse httpResponse=client.execute(request);
				if (httpResponse!=null && httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					result=EntityUtils.toString(httpResponse.getEntity());
			    }
			} catch (Exception e) {
				Log.e("-bookingActivity",Log.getStackTraceString(e));
			} finally{
				request.abort();
	  			client.close();
	  		}
			return this.isCancelled()?null:result;
		}
		@Override
		protected void onPostExecute (String result){
			if(result!=null){
				Log.d("-bookingActivity","LoadBookingTask:success");
				List<Booking> bs = new Gson().fromJson(result, new TypeToken<List<Booking>>(){}.getType());
				if(bs!=null && bs.size()>0){
					bookings=bs;
					prepareGroups();
					for(int i=0;i<adapter.getGroupCount()-1;i++){
						elv.collapseGroup(i);
						elv.expandGroup(i);
					}
					try{
					if(userId==app.getLoginUser().getUserId()){
						app.setMyBookings(bookings);
					}
					}catch(Exception e){}
			}
			}
		}
		
	}
	class CancelBookingTask extends AsyncTask<Object, Void, Integer>{

		@Override
		protected Integer doInBackground(Object... arg0) {
			long bookingId=(Long)arg0[0];
			int groupPosition=(Integer)arg0[1];
			String sql=Constance.SERVER_URL+"booking/" + bookingId+ "/cancel";
			Log.d("-bookingActivity","CancelBookingTask:doInBackground:"+sql);
			HttpPut request=new HttpPut(sql);
			request.addHeader(BasicScheme.authenticate(
					 new UsernamePasswordCredentials(app.getLoginUser().getSign(), app.getLoginUser().getPassword()),
					 "UTF-8", false));
			AndroidHttpClient client=AndroidHttpClient.newInstance("cancelBooking");
			try {
				HttpResponse httpResponse=client.execute(request);
				if (httpResponse!=null && httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					for(Booking booking : bookings){
			        	  if(booking.getBookingId()==bookingId){
			        		  booking.setStatus(BOOKINGSTATUS.CANCELED);
			        		  break;
			        	  }
			          }
			          Log.d("-bookingActivity", "cancel:success:"+bookings.size());
			          app.setMyBookings(bookings);
			          prepareGroups();
			    }
			} catch (Exception e) {
				Log.e("-bookingActivity",Log.getStackTraceString(e));
			} finally{
				request.abort();
	  			client.close();
	  		}
			return groupPosition;
		}
		@Override
		protected void onPostExecute (Integer groupPosition){
			elv.collapseGroup(groupPosition);
			elv.expandGroup(groupPosition);
		}
		
	}
}
