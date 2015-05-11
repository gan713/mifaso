package com.mifashow.ui;

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
import org.apache.http.util.EntityUtils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.AsyncTask.Status;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Button;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mifashow.App;
import com.mifashow.ProfileFragment;
import com.mifashow.R;
import com.mifashow.SalonActivity;
import com.mifashow.data.Constance;
import com.mifashow.domain.Salon;
import com.mifashow.domain.User;
import com.mifashow.tool.ImageHelper;
import com.mifashow.tool.image.DensityUtil;

public class HeadOverlay extends ItemizedOverlay<OverlayItem>{
	App app;
	FragmentActivity activity;
	MapView mapView;
	Class<?> c;
	List<?> items;
	Button bt;
	private GetTask getTask;
	private LoadMarkerTask loadMarkerTask;
	private Handler handler = new Handler();
	private Runnable getRunnable = new Runnable() {
        @Override
		public void run() {
        	if(getTask!=null && getTask.getStatus()==Status.RUNNING){
        		getTask.cancel(true);
        	}
            if(loadMarkerTask!=null && loadMarkerTask.getStatus()==Status.RUNNING){
                loadMarkerTask.cancel(true);
            }
        	getTask=new GetTask();
        	getTask.execute();
        }
    }; 
    private Runnable loadMarkerRunnable = new Runnable() {
        @Override
		public void run() {
        	if(loadMarkerTask!=null && loadMarkerTask.getStatus()==Status.RUNNING){
        		loadMarkerTask.cancel(true);
        	}
        	loadMarkerTask=new LoadMarkerTask();
        	loadMarkerTask.execute();
        }
    }; 

	public HeadOverlay(FragmentActivity activity,MapView mapView,Button bt,Class<?> c) {
		super(activity.getResources().getDrawable(R.drawable.ic_location_over), mapView);
		this.c=c;
		this.activity=activity;
		this.app=(App) activity.getApplication();
		this.bt=bt;
		this.mapView=mapView;
		if(c==User.class){
			items=app.getNear100kmUsers();
		}else if(c==Salon.class){
			items=app.getNear100kmSalons();
		}
	}
	@Override
	public boolean onTap(int index){
		if (c == User.class) {
			ProfileFragment profileFragment = new ProfileFragment();
			Bundle bundle = new Bundle();
			bundle.putLong("userId",Long.parseLong(this.getItem(index).getTitle()));
			profileFragment.setArguments(bundle);
			profileFragment.show(activity.getSupportFragmentManager(), ""+ profileFragment.getId());
		} else if (c == Salon.class) {
//			AsyncHttpClient client=new AsyncHttpClient();
//			client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
//	        client.get(Constance.SERVER_URL+"salon/"+Long.parseLong(this.getItem(index).getTitle()), new AsyncHttpResponseHandler()
//	        {
//	          @Override
//			public void onFailure(Throwable throwable, String content){
//	        	  AlertHelper.showToast(activity, R.string.error_serverError);
//	          }
//
//	          @Override
//			public void onSuccess(String content){
//	        	  Salon salon=new Gson().fromJson(content, Salon.class);
//	        	  Intent intent = new Intent();
//	        	  intent.putExtra("salon", salon);
//	        	  intent.setClass(activity, SalonActivity.class);
//	        	  activity.startActivity(intent);
//	          }
//	        });
			Intent intent = new Intent();
      	    intent.putExtra("salonId", Long.parseLong(this.getItem(index).getTitle()));
      	    intent.setClass(activity, SalonActivity.class);
      	    activity.startActivity(intent);
		}
		return true;
	}
	class GetTask extends AsyncTask<Void,Void,String>{
		  @Override
			protected void onPreExecute(){
			}
		@Override
		protected String doInBackground(Void... p) {
			HttpGet get = null;
			AndroidHttpClient client = null;
			String content = null;
			try {
				String tag = "";
				if(c==User.class){
					tag="user";
				}else if(c==Salon.class){
					tag="salon";
				}
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("latitude",""+app.getLatitude()));
				params.add(new BasicNameValuePair("longitude", ""+app.getLongitude()));
				get = new HttpGet(Constance.SERVER_URL+tag+"/100km"+"?"+EntityUtils.toString(new UrlEncodedFormEntity(params,"utf-8")));
				client = AndroidHttpClient.newInstance("headOverlayOf"+tag);
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
				Log.e("-HeadOverlay", Log.getStackTraceString(e));
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
				if(c==User.class){
					List<User> users=(new Gson()).fromJson(content, new TypeToken<List<User>>() {}.getType());
					if(users!=null && users.size()>0){
						app.setNear100kmUsers(users);
						items=users;
						refreshUI();
					}
				}else if(c==Salon.class){
					List<Salon> salons=(new Gson()).fromJson(content, new TypeToken<List<Salon>>() {}.getType());
					if(salons!=null && salons.size()>0){
						app.setNear100kmSalons(salons);
						items=salons;
						refreshUI();
					}
				}
			}
		}
	}
	
	private class LoadMarkerTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			for(OverlayItem item:HeadOverlay.this.getAllItem()){
				try{
					Bitmap markBM=ImageHelper.loadBitmap(activity, item.getSnippet(), 40);
					if(markBM!=null){
						int pix=DensityUtil.dip2px(activity, 40);
						if(markBM.getWidth()!=pix)markBM=Bitmap.createScaledBitmap(markBM ,pix,pix, false);
						item.setMarker(ImageHelper.getRoundedCornerBitmap(activity, markBM));
						HeadOverlay.this.updateItem(item);
					}
				}catch(Exception e){
						Log.e("-UserOverlay",Log.getStackTraceString(e));
					}
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void param) {
			if(mapView==null)return;
			mapView.refresh();
			try {
				this.finalize();
			} catch (Throwable e) {
				Log.e("-UserOverlay",Log.getStackTraceString(e));
			}
		}
	}
	
	public void reloadData(){
        handler.post(getRunnable);
	}

	public void refreshUI() {
		try {
			removeAll();
			List<OverlayItem> overlayitems = new ArrayList<OverlayItem>();
			if (items != null) {
			for (Object item : items) {
				float lat = 0,lon = 0;
				String head = null;
				long id = 0;
				if(c==User.class){
					User u=(User)item;
					id=u.getUserId();
					lat=u.getLatitude();
					lon=u.getLongitude();
					head=u.getFigure();
				}else if(c==Salon.class){
					Salon s=(Salon)item;
					id=s.getSalonId();
					lat=s.getLatitude();
					lon=s.getLongitude();
					head=(s.getImages()!=null && s.getImages().length>0)?s.getImages()[0]:null;
				}
				OverlayItem overlayItem=new OverlayItem(new GeoPoint((int) (lat * 1E6),(int) (lon * 1E6)), 
						""+id,
						head
						);
				overlayitems.add(overlayItem);
				}
					this.finalize();
			}
			addItem(overlayitems);
			bt.setText(""+overlayitems.size());
			handler.post(loadMarkerRunnable);
			} catch (Throwable e) {
				Log.e("-UserOverlay",Log.getStackTraceString(e));
			}
		}

}
