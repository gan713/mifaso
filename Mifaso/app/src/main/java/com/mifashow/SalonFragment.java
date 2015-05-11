package com.mifashow;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mifashow.data.Constance;
import com.mifashow.domain.Salon;
import com.mifashow.tool.ImageHelper;
import com.mifashow.ui.BorderListView;
import com.mifashow.ui.ResizedImageView;

import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.AsyncTask.Status;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SalonFragment extends DialogFragment implements OnItemClickListener{

	static enum SOURCETYPE{DISCOVERY,SEARCH}
	SalonAdapter adapter;
	  App app;
	  boolean isEnd;
	  BorderListView lv;
	  LinearLayout lo_info;
	  TextView tv_info;
	  long sourceId;
	  SOURCETYPE sourceType;
	  String sourceKeyword;
	  String url;
	  String keyword;
	  List<Salon> salons;
	  View view;
	  LoadSalonTask loadSalonTask;
//	  LruCache<Long,View> memViews; 
	  private Handler handler = new Handler();
	    private Runnable runnable = new Runnable() {
	        @Override
			public void run() {
	            if(view!=null && lv.getLastVisiblePosition()<=39){
	            	if(loadSalonTask!=null && loadSalonTask.getStatus()==Status.RUNNING){
	            		loadSalonTask.cancel(true);
	        		}
	            	loadSalonTask=new LoadSalonTask();
	            	loadSalonTask.execute(-1L);
	            }
	            handler.postDelayed(this, 1000 * 300);
	        }
	    }; 
	  
//	  @Override  
//		public Dialog onCreateDialog(Bundle savedInstanceState) {
//			Dialog dialog = super.onCreateDialog(savedInstanceState);
//			setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_NoActionBar);
////	    	dialog.setTitle(R.string.profile_bt_following);
//			return dialog;
//		}  
	  @Override  
	    public void onDestroy(){
			Log.d("-UserFragment", "onDestroy");
			if(loadSalonTask!=null && loadSalonTask.getStatus()==Status.RUNNING){
				loadSalonTask.cancel(true);
			}
			handler.removeCallbacks(runnable); 
//			if(sourceType==SOURCETYPE.DISCOVERY && memViews!=null)memViews.evictAll();
	        super.onDestroy();
		}
	  @Override
	public void onCreate(Bundle bundle){
	    super.onCreate(bundle);
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar);
//	    memViews=new LruCache<Long,View>(20);
	    app = ((App)getActivity().getApplication());
	    isEnd=false;
	    Bundle b=getArguments();
		if(b!=null){
			sourceType=(SOURCETYPE)b.getSerializable("sourceType");
			sourceId = b.getLong("sourceId", -1L);
			sourceKeyword=b.getString("sourceKeyword");
		}
	    
        if(sourceType == SOURCETYPE.SEARCH){
//        	try{
//        		sourceKeyword=URLEncoder.encode(sourceKeyword,"utf-8");
//        	}catch(Exception e){}
        	url = Constance.SERVER_URL+"salon/search";
            salons=null;
        }else{
        	sourceType=SOURCETYPE.DISCOVERY;
        	salons=app.getDiscoverySalons();
        	url = Constance.SERVER_URL+"salon/discovery";
        }
        if(salons==null)salons=new ArrayList<Salon>();
        keyword="";
    }
	  @Override  
	    public View onCreateView(final LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			if(this.view==null){
				view = inflater.inflate(R.layout.fragment_salon, null);
				lo_info=(LinearLayout) view.findViewById(R.id.salons_lo_info);
				tv_info=(TextView) view.findViewById(R.id.salons_tv_info);
				lv = ((BorderListView)view.findViewById(R.id.salons_lv));
			    adapter = new SalonAdapter();
				lv.setAdapter(adapter);
		        lv.setOnItemClickListener(this);
		        lv.setOnBorderListener(new BorderListView.OnBorderListener(){
		          @Override
				  public void onBottom(){
//		        	  Log.d("-UserFragment", "LastVisiblePosition:"+lv.getLastVisiblePosition());
		              if(!isEnd&&url!=null&&salons!=null && salons.size()>0){
		            	  if(loadSalonTask!=null && loadSalonTask.getStatus()==Status.RUNNING){
		            		  loadSalonTask.cancel(true);
			        		}
		            	  loadSalonTask=new LoadSalonTask();
			        		loadSalonTask.execute();
		              }
		          }
		          @Override
		  		  public void onRoll(){}
		        });
		        if(salons==null||salons.size()==0){
		        	tv_info.setText(R.string.info_null);
		        	lo_info.setVisibility(View.VISIBLE);
		        }else{
		        	lo_info.setVisibility(View.GONE);
		        }
		        adapter.notifyDataSetChanged();
		        if(sourceType==SOURCETYPE.DISCOVERY){
		        	runnable.run();
		        }else{
		        	if(loadSalonTask!=null && loadSalonTask.getStatus()==Status.RUNNING){
		        		loadSalonTask.cancel(true);
	        		}
		        	loadSalonTask=new LoadSalonTask();
		        	loadSalonTask.execute(-1L);
		        }
			}else{
				ViewGroup parent = (ViewGroup) view.getParent();
				if (parent != null) {
					parent.removeView(view);
				}
			}
			return view;
		}
	  
	  class LoadSalonTask extends AsyncTask<Long,Void,String>{
		  long maxId=-1;
		  @Override
			protected void onPreExecute(){
			    tv_info.setText(R.string.status_loading);
			    lo_info.setVisibility(View.VISIBLE);
			}
		@Override
		protected String doInBackground(Long... p) {
			HttpGet get = null;
			AndroidHttpClient client = null;
			String content = null;
			float lat=app.getLatitude();
			float lon=app.getLongitude();
			try {
				maxId = p[0];
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				if (sourceType == SOURCETYPE.SEARCH)
					params.add(new BasicNameValuePair("keyword", sourceKeyword));
				else if (sourceType == SOURCETYPE.DISCOVERY &&!(lat==0 && lon==0)) {
					params.add(new BasicNameValuePair("latitude",""+lat));
					params.add(new BasicNameValuePair("longitude", ""+lon));
				}
				if (maxId > 0)
					params.add(new BasicNameValuePair("maxId", "" + maxId));
		        String urlParam="";
		        if(params.size()>0)
					try {
						urlParam="?"+EntityUtils.toString(new UrlEncodedFormEntity(params,"utf-8"));
					} catch (Exception e) {
						Log.e("-SalonFragment",Log.getStackTraceString(e));
					}
				get = new HttpGet(url+urlParam);
				HttpParams httpParameters = new BasicHttpParams();
			    HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
			    HttpConnectionParams.setSoTimeout(httpParameters, 7000);
			    get.setParams(httpParameters);
				Log.d("-UserFragment",url+urlParam);
				client = AndroidHttpClient.newInstance("loadSalons");
				if(app.getLoginUser().getUserId()!=0)
				get.addHeader(BasicScheme.authenticate(
						new UsernamePasswordCredentials(app.getLoginUser()
								.getSign(), app.getLoginUser().getPassword()),
						"UTF-8", false));
				HttpResponse res = client.execute(get);
				if (res != null&& res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					content = EntityUtils.toString(res.getEntity());
				}
			} catch (Exception e) {
				Log.e("-SalonFragment", Log.getStackTraceString(e));
			} finally {
				if (get != null)
					get.abort();
				if (client != null)
					client.close();
			}
			if (this.isCancelled() || content == null || "".equals(content)) {
				return null;
			}
			return content;
		}
		@Override
		protected void onPostExecute(String content) {
			if(content!=null){
			List<Salon> ss = (new Gson()).fromJson(content, new TypeToken<List<Salon>>() {}.getType());
			  isEnd = (ss==null||ss.size() < 20);
            if(maxId!=-1){
          	  salons.addAll(ss);
            }else{
            	salons=ss;
            }
            if(sourceType==SOURCETYPE.DISCOVERY){
            	  app.setDiscoverySalons(salons);
              }
            adapter.notifyDataSetChanged();
			}
			  if(salons==null || salons.size()==0){
				  tv_info.setText(R.string.info_null);
				  lo_info.setVisibility(View.VISIBLE);
			  }else{
				  lo_info.setVisibility(View.GONE);
			  }
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//		AsyncHttpClient client=new AsyncHttpClient();
//		client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
//        client.get(Constance.SERVER_URL+"salon/"+arg3, new AsyncHttpResponseHandler()
//        {
//          @Override
//		public void onFailure(Throwable throwable, String content)
//          {
//        	  if(SalonFragment.this.isAdded())AlertHelper.showToast(getActivity(), R.string.error_serverError);
//          }
//
//          @Override
//		public void onSuccess(String content){
//        	  Salon salon=new Gson().fromJson(content, Salon.class);
//        	  Intent intent = new Intent();
//        	  intent.putExtra("salon", salon);
//        	  intent.setClass(getActivity(), SalonActivity.class);
//	    	  startActivity(intent);
//          }
//        });
		Intent intent = new Intent();
  	    intent.putExtra("salonId", arg3);
  	    intent.setClass(getActivity(), SalonActivity.class);
    	startActivity(intent);
	}
	
	class SalonAdapter extends BaseAdapter{
		int count;
		SalonAdapter(){}

	    @Override
		public int getCount(){
	      return salons == null?0:salons.size();
	    }

	    @Override
		public Object getItem(int paramInt)
	    {
	      return salons.get(paramInt);
	    }

	    @Override
		public long getItemId(int paramInt)
	    {
	      return ((Salon)getItem(paramInt)).getSalonId();
	    }

	    @Override
		public View getView(int position, View convertView, ViewGroup parent){
	    	Salon salon=salons.get(position);
	      View v = SalonFragment.this.getActivity().getLayoutInflater().inflate(R.layout.list_salon, null);
	      ResizedImageView iv_image = (ResizedImageView)v.findViewById(R.id.salons_iv_image);
	      TextView tv_salonName = (TextView)v.findViewById(R.id.salons_tv_salonName);
	      TextView tv_distance = (TextView)v.findViewById(R.id.salons_tv_distance);
	      TextView tv_discount = (TextView)v.findViewById(R.id.salons_tv_discount);
	      TextView tv_price = (TextView)v.findViewById(R.id.salons_tv_price);
	      int minPrice=salon.getCut();
	      minPrice=Math.min(minPrice, salon.getBraid());
	      minPrice=Math.min(minPrice, salon.getColor());
	      minPrice=Math.min(minPrice, salon.getPermanent());
	      minPrice=Math.min(minPrice, salon.getTreatment());
		  tv_price.setText(getResources().getString(R.string.salons_tv_price,minPrice));
	      tv_salonName.setText(salon.getName());
	      tv_discount.setText(salon.getDiscount());
			app.getLocationHelper().showCityInTextView(salon.getLatitude(), salon.getLongitude(), tv_distance);
	      if(salon.getImages()!=null && salon.getImages().length>0){
	    	  ImageHelper.loadImg(iv_image, salon.getImages()[0], 0,false);
	      }else{
	    	  ImageHelper.loadImg(iv_image, salon.getManagerFigure(), 0,false);
	      }
	      return v;
	    }
	  }

}
