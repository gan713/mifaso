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

import android.graphics.Bitmap;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.AsyncTask.Status;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mifashow.App;
import com.mifashow.ProfileFragment;
import com.mifashow.R;
import com.mifashow.data.Constance;
import com.mifashow.data.Constance.USERTYPE;
import com.mifashow.domain.User;
import com.mifashow.tool.ImageHelper;
import com.mifashow.tool.image.DensityUtil;

public class UserOverlay extends ItemizedOverlay<OverlayItem>{
	App app;
	FragmentActivity activity;
	MapView mapView;
	List<User> users;
	TextView tv_info;
	boolean stylistOnly;
	private LoadUserTask loadUserTask;
	private LoadMarkerTask loadMarkerTask;
	private Handler handler = new Handler();
	private Runnable loadUserRunnable = new Runnable() {
        @Override
		public void run() {
        	if(loadUserTask!=null && loadUserTask.getStatus()==Status.RUNNING){
        		loadUserTask.cancel(true);
        	}
        	loadUserTask=new LoadUserTask();
			loadUserTask.execute();
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

	public UserOverlay(FragmentActivity activity,MapView mapView,TextView tv_info) {
		super(activity.getResources().getDrawable(R.drawable.ic_location_over), mapView);
		this.activity=activity;
		this.app=(App) activity.getApplication();
		this.tv_info=tv_info;
		this.mapView=mapView;
		this.users=app.getNear10kmUsers();
		refreshUI();
        handler.post(loadUserRunnable);
	}
	@Override
	public boolean onTap(int index)
	{
		ProfileFragment profileFragment = new ProfileFragment();
		Bundle bundle = new Bundle();
	    bundle.putLong("userId",Long.parseLong(this.getItem(index).getTitle()));
	    profileFragment.setArguments(bundle);
	    profileFragment.show(activity.getSupportFragmentManager(), ""+profileFragment.getId());
		return true;
	}
	class LoadUserTask extends AsyncTask<Void,Void,String>{
		  @Override
			protected void onPreExecute(){
			}
		@Override
		protected String doInBackground(Void... p) {
			HttpGet get = null;
			AndroidHttpClient client = null;
			String content = null;
			try {
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("latitude",""+app.getLatitude()));
				params.add(new BasicNameValuePair("longitude", ""+app.getLongitude()));
				get = new HttpGet(Constance.SERVER_URL+"user/10km"+"?"+EntityUtils.toString(new UrlEncodedFormEntity(params,"utf-8")));
				client = AndroidHttpClient.newInstance("loadUserBy10km");
				get.addHeader(BasicScheme.authenticate(
						new UsernamePasswordCredentials(app.getLoginUser()
								.getSign(), app.getLoginUser().getPassword()),
						"UTF-8", false));
				HttpResponse res = client.execute(get);
				if (res != null&& res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					content = EntityUtils.toString(res.getEntity());
				}
			} catch (Exception e) {
				Log.e("-UserOverlay", Log.getStackTraceString(e));
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
			users = (new Gson()).fromJson(content, new TypeToken<List<User>>() {}.getType());
			if(users!=null && users.size()>0){
				app.setNear10kmUsers(users);
				refreshUI();
			}
			}
		}
	}
	
	private class LoadMarkerTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			for(OverlayItem item:UserOverlay.this.getAllItem()){
				try{
					Bitmap markBM=ImageHelper.loadBitmap(activity, item.getSnippet(), 40);
					if(markBM!=null){
						int pix=DensityUtil.dip2px(activity, 40);
						if(markBM.getWidth()!=pix)markBM=Bitmap.createScaledBitmap(markBM ,pix,pix, false);
						item.setMarker(ImageHelper.getRoundedCornerBitmap(activity, markBM));
						UserOverlay.this.updateItem(item);
					}
				}catch(Exception e){
						Log.e("-UserOverlay",Log.getStackTraceString(e));
					}
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void param) {
			mapView.refresh();
			try {
				this.finalize();
			} catch (Throwable e) {
				Log.e("-UserOverlay",Log.getStackTraceString(e));
			}
		}
		
		
	}

	public void refreshUI() {
		stylistOnly=!stylistOnly;
			removeAll();
			List<OverlayItem> items = new ArrayList<OverlayItem>();
			if (users != null) {
			for (User user : users) {
				if(stylistOnly && user.getUserType()!=USERTYPE.STYLIST)continue;
				OverlayItem item=new OverlayItem(new GeoPoint((int) (user.getLatitude() * 1E6),(int) (user.getLongitude() * 1E6)), 
						""+user.getUserId(),
						user.getFigure()
						);
				items.add(item);
				}
				try {
					this.finalize();
				} catch (Throwable e) {
					Log.e("-UserOverlay",Log.getStackTraceString(e));
				}
			}
			addItem(items);
			mapView.refresh();
			tv_info.setText(items==null?0:items.size());
			handler.post(loadMarkerRunnable);
		}

}
