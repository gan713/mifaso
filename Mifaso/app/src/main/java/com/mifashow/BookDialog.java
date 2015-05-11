package com.mifashow;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mifashow.data.Constance;
import com.mifashow.data.Constance.DISCOUNT;
import com.mifashow.data.Constance.USERTYPE;
import com.mifashow.data.Constance.WEEKDAY;
import com.mifashow.domain.Booking;
import com.mifashow.domain.Posting;
import com.mifashow.tool.AlertHelper;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class BookDialog extends DialogFragment {
	
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

	// TODO: Rename and change types of parameters
	private App app;
	private Posting posting;
	private String[] titles;
	LayoutInflater inflater;
	private HashMap<Integer,ListView> lists;
	private HashMap<Integer,Booking[]> bookings;
	
	public static BookDialog newInstance(Posting posting){
		BookDialog bd=new BookDialog();
		Bundle bundle=new Bundle();
		bundle.putSerializable("posting", posting);
		bd.setArguments(bundle);
		return bd;
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light);
		app=(App) getActivity().getApplication();
		this.posting=(Posting) getArguments().getSerializable("posting");
		lists=new HashMap<Integer,ListView>();
		bookings=new HashMap<Integer,Booking[]>();
			Calendar cal=Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			ArrayList<String> titleList=new ArrayList<String>();
			WEEKDAY[] bookingDay=posting.getBookingDay();
			for(int i=0;i<7;i++){
				cal.add(Calendar.DATE, 1);
				boolean isClosed=true;
				for(WEEKDAY day:bookingDay){
					if((cal.get(Calendar.DAY_OF_WEEK)-1)==day.ordinal()){
						isClosed=false;
						break;
					}
				}
				if(isClosed)continue;
				
				titleList.add(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())+"  "+cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())+" "+cal.get(Calendar.DAY_OF_MONTH));
				ArrayList<Booking> bookingArray=new ArrayList<Booking>();
				DISCOUNT[] bookingTime=posting.getBookingTime();
					for(int j=0;j<bookingTime.length;j++){
						if(bookingTime[j].ordinal()>0){
						Booking booking=new Booking();
//						DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
						Calendar agreedCal=Calendar.getInstance();
						agreedCal.setTimeInMillis(cal.getTimeInMillis());
						agreedCal.add(Calendar.MINUTE, 30*j);
//						long agreedTime = 0;
//						try {
//							agreedTime = format.parse(""+cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.DATE)+" "+getResources().getStringArray(R.array.booking_time)[j]).getTime();
//						} catch (NotFoundException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} catch (ParseException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						booking.setPostingId(posting.getPostingId());
						booking.setCustomerId(app.getLoginUser().getUserId());
						booking.setStylistId(posting.getCreaterId());
						booking.setAgreedTime(agreedCal.getTimeInMillis());
						booking.setDiscount(bookingTime[j]);
						booking.setListPrice(posting.getPrice());
						booking.setAgreedPrice(posting.getPrice()*(11-bookingTime[j].ordinal())/10);
						bookingArray.add(booking);
						}
				}
				Booking[] bs=new Booking[bookingArray.size()];
				bookings.put(titleList.size()-1,  bookingArray.toArray(bs));
			}
			titles=titleList.toArray(new String[titleList.size()]);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater=inflater;
		this.getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		this.getDialog().getWindow().requestFeature(STYLE_NO_FRAME);
    	this.getDialog().getWindow().requestFeature(STYLE_NO_TITLE);
		// Inflate the layout for this fragment
		View view=inflater.inflate(R.layout.fragment_book, container, false);
		ViewPager vp=(ViewPager) view.findViewById(R.id.book_pg_datetime);
		BookingPageAdapter adapter=new BookingPageAdapter(this.getDialog().getContext());
		vp.setAdapter(adapter);
		return view;
	}

	// TODO: Rename method, update argument and hook method into UI event
//	public void onButtonPressed(Uri uri) {
//		if (mListener != null) {
//			mListener.onFragmentInteraction(uri);
//		}
//	}

//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		try {
//			mListener = (OnFragmentInteractionListener) activity;
//		} catch (ClassCastException e) {
//			throw new ClassCastException(activity.toString()
//					+ " must implement OnFragmentInteractionListener");
//		}
//	}

//	@Override
//	public void onDetach() {
//		super.onDetach();
//		mListener = null;
//	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
//	public interface OnFragmentInteractionListener {
//		// TODO: Update argument type and name
//		public void onFragmentInteraction(Uri uri);
//	}
	class BookingPageAdapter extends PagerAdapter{
//		private Context context;
		public BookingPageAdapter(Context context){
//			this.context=context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return titles.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0==arg1;
		}
		
		@Override  
        public void destroyItem(ViewGroup container, int position,  
                Object object) {  
            container.removeView(lists.get(position));
            lists.remove(position);

        } 
		
		@Override  
        public int getItemPosition(Object object) {  

            return super.getItemPosition(object);  
        } 
		
		@Override  
        public CharSequence getPageTitle(int position) {  

            return titles[position];  
        } 
		
		@Override  
        public Object instantiateItem(ViewGroup container, int position) {
			ListView list=new ListView(container.getContext());
			LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
			list.setLayoutParams(params);
			BookingListAdapter adapter=new BookingListAdapter(container.getContext(),bookings.get(position));
			list.setAdapter(adapter); 
			container.addView(list);
			lists.put(position,list);
            return list;  
        } 
		
	}
	class BookingListAdapter extends BaseAdapter{
		Context context;
		Booking[] bs;
		public BookingListAdapter(Context context,Booking[] bs){
			this.context=context;
			this.bs=bs;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return bs.length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return bs[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return bs[arg0].getBookingId();
		}

		@Override
		public View getView(final int arg0, View arg1, ViewGroup arg2) {
			boolean isDiscount=bs[arg0].getDiscount().ordinal()>1;
			View view=inflater.inflate(R.layout.list_book, null);
			TextView listPrice_tv=(TextView) view.findViewById(R.id.book_tv_listPrice);
			TextView agreedPrice_tv=(TextView) view.findViewById(R.id.book_tv_agreedPrice);
			TextView discount_tv=(TextView) view.findViewById(R.id.book_tv_discount);
			TextView time_tv = (TextView) view.findViewById(R.id.book_tv_time);
			Button book_bt=(Button) view.findViewById(R.id.book_bt_book);
//			if(bs[arg0].getStylistId()==bs[arg0].getCustomerId()){
//				book_bt.setEnabled(false);
//			}else{
			book_bt.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					if(app.getLoginUser().getUserId()==bs[arg0].getStylistId()){
						AlertHelper.showToast(getActivity(), R.string.error_bookYourself);
						return;
					}
					if(app.getLoginUser().getUserType()==USERTYPE.STYLIST){
			    		  AlertHelper.showToast(getActivity(),R.string.error_bookByStylist);
			    		  return;
			    	  }
					AlertHelper.showAlert(getActivity(), getResources().getString(R.string.dialog_book_title), getResources().getString(R.string.dialog_book_message), getResources().getString(R.string.action_ok), 
							new DialogInterface.OnClickListener() {
			            @Override
						public void onClick(DialogInterface dialog, int id) {
			            	AsyncHttpClient client=new AsyncHttpClient();
							client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
							StringEntity se = null;
							try {
								se=new StringEntity(new Gson().toJson(bs[arg0]),"UTF-8");
//								se.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING, "UTF-8"));  
//								se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));  
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							client.post(getActivity(), Constance.SERVER_URL+"booking", se, "application/json", new AsyncHttpResponseHandler(){
								@Override
								   public void onSuccess(int statusCode,Header[] headers,byte[] content) {
									try{
									AlertHelper.showToast(getActivity(), R.string.info_bookDone);
									BookDialog.this.dismiss();
									app.refreshUser();
									}catch(Exception e){}
								   }
								@Override
								public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
									try{
									AlertHelper.showToast(getActivity(),  R.string.error_serverError);
									}catch(Exception e){}
								}
							});
			            }
			        }, getResources().getString(R.string.action_cancel), 
			        new DialogInterface.OnClickListener() {
			            @Override
						public void onClick(DialogInterface dialog, int id) {}
			        });
//					AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
//					builder1.setMessage(R.string.dialog_book_message).setTitle(R.string.dialog_book_title);
//					builder1.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
//			            @Override
//						public void onClick(DialogInterface dialog, int id) {
//			            	AsyncHttpClient client=new AsyncHttpClient();
//							client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
//							StringEntity se = null;
//							try {
//								se=new StringEntity(new Gson().toJson(bs[arg0]),"UTF-8");
////								se.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING, "UTF-8"));  
////								se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));  
//							} catch (UnsupportedEncodingException e) {
//								e.printStackTrace();
//							}
//							client.post(getActivity(), Constance.SERVER_URL+"booking", se, "application/json", new AsyncHttpResponseHandler(){
//								@Override
//								   public void onSuccess(String response) {
//									try{
//									AlertHelper.showToast(getActivity(), R.string.info_bookDone);
//									BookDialog.this.dismiss();
//									}catch(Exception e){}
//								   }
//								@Override
//								public void onFailure(int statusCode,java.lang.Throwable error,java.lang.String content){
//									try{
//									AlertHelper.showToast(getActivity(),  R.string.error_serverError);
//									}catch(Exception e){}
//								}
//							});
//			            }
//			        });
//					builder1.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
//			            @Override
//						public void onClick(DialogInterface dialog, int id) {}
//			        });
//					builder1.create().show();
					
					
					
					
				}				
			});
//			}
			if (isDiscount) {
				discount_tv.setText(getResources().getStringArray(R.array.enum_discount)[bs[arg0].getDiscount().ordinal()]);
				listPrice_tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			}
			listPrice_tv.setText(getResources().getString(R.string.booking_tv_agreedPrice_text, bs[arg0].getListPrice()));
			agreedPrice_tv.setText(getResources().getString(R.string.booking_tv_agreedPrice_text,bs[arg0].getAgreedPrice()));
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm",Locale.getDefault());
			time_tv.setText(formatter.format(new Date(bs[arg0].getAgreedTime())));
			return view;
		}
		
	}

}
