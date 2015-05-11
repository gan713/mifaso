package com.mifashow;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.mifashow.domain.Salon;
import com.mifashow.domain.User;
import com.mifashow.ui.HeadOverlay;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MapActivity extends FragmentActivity {
	App app;
	MapView mv = null;
	Button bt_user=null;
	Button bt_salon=null;
	MKSearch mMKSearch=null;
	GeoPoint endPoint;
	RouteOverlay routeOverlay;
	MapController mMapController;
	MyLocationOverlay myLocationOverlay,targetLocationOverLayout;
	HeadOverlay userOverlay,salonOverlay;
	Salon salon;
	User user;
	float myLat,myLon;
	@Override  
    public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		app=(App)getApplication();
		user=(User) getIntent().getSerializableExtra("user");
		salon=(Salon) getIntent().getSerializableExtra("salon");
		setContentView(R.layout.activity_map);
		bt_user=(Button) findViewById(R.id.map_bt_user);
		bt_salon=(Button) findViewById(R.id.map_bt_salon);
		mv=(MapView)findViewById(R.id.map_mv);
		mv.setBuiltInZoomControls(true);
		mMapController=mv.getController();
		mMapController.setZoom(13);
		init();
    }
	@Override
	protected void onDestroy(){
		mv.destroy();
	    super.onDestroy();
	}
	@Override
	protected void onPause(){
		mv.onPause();
	    super.onPause();
	}
	@Override
	protected void onResume(){
		mv.onResume();
	    super.onResume();
	}
	public class MySearchListener implements MKSearchListener {
        @Override  
        public void onGetAddrResult(MKAddrInfo result, int iError) {
        }  
        @Override
        public void onGetDrivingRouteResult(MKDrivingRouteResult result, int iError) {
                Log.d("-MapActivity","onGetDrivingRouteResult");
                if(mv!=null && result!=null){
                	routeOverlay = new RouteOverlay(MapActivity.this, mv);
                	routeOverlay.setData(result.getPlan(0).getRoute(0));
                    if(mv.getOverlays().contains(routeOverlay)){
                    	mv.getOverlays().remove(routeOverlay);
                    }
                    mv.getOverlays().add(routeOverlay);
                    mv.refresh(); 
                }
            	try {
                    mMKSearch.destory();
    				this.finalize();
    			} catch (Throwable e) {
    				Log.e("-MapActivity",Log.getStackTraceString(e));
    			}
        }
        @Override  
        public void onGetPoiResult(MKPoiResult result, int type, int iError) {
        }  
        @Override  
        public void onGetTransitRouteResult(MKTransitRouteResult result, int iError) {
        }  
        @Override  
        public void onGetWalkingRouteResult(MKWalkingRouteResult result, int iError) {
        }  
        @Override      
        public void onGetBusDetailResult(MKBusLineResult result, int iError) {
        }  
         @Override 
         public void onGetShareUrlResult(MKShareUrlResult result , int type, int error) {
               //�ڴ˴���̴����󷵻ؽ��. 
        }
		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		} 
	}

	public void target(View v){
		if(endPoint!=null){
//			mMapController.setCenter(endPoint);
			mMapController.animateTo(endPoint);//���õ�ͼ���ĵ�
		}
	}
	
	private void init(){
		int nearUserNum=app.getNear100kmUsers().size();
		int nearSalonNum=app.getNear100kmSalons().size();
		if(nearUserNum>0)bt_user.setText(""+nearUserNum);
		if(nearSalonNum>0)bt_salon.setText(""+nearSalonNum);
		myLat = app.getLatitude();
		myLon = app.getLongitude();
		if ((salon!=null && !(salon.getLatitude() == 0 && salon.getLongitude() == 0)) || (user!=null && !(user.getLatitude() == 0 && user.getLongitude() == 0))) {
			
			targetLocationOverLayout = new MyLocationOverlay(mv);
			LocationData locData = new LocationData();
			//�ֶ���λ��Դ��Ϊ�찲�ţ���ʵ��Ӧ���У���ʹ�ðٶȶ�λSDK��ȡλ����Ϣ��Ҫ��SDK����ʾһ��λ�ã���Ҫʹ�ðٶȾ�γ����꣨bd09ll��
			locData.latitude = salon==null?user.getLatitude():salon.getLatitude();
			locData.longitude = salon==null?user.getLongitude():salon.getLongitude();
			locData.direction = 2.0f;
			targetLocationOverLayout.setData(locData);
			if(mv.getOverlays().contains(targetLocationOverLayout)){
				mv.getOverlays().remove(targetLocationOverLayout);
			}
			mv.getOverlays().add(targetLocationOverLayout);
			if(!(myLat==0&&myLon==0)){
				endPoint = new GeoPoint((int) (locData.latitude * 1E6),(int) (locData.longitude* 1E6));
				MKPlanNode start = new MKPlanNode();
				start.pt = new GeoPoint((int) (myLat * 1E6),(int) (myLon * 1E6));
				MKPlanNode end = new MKPlanNode();
				end.pt = endPoint;
				if(mMKSearch!=null)mMKSearch.destory();
				mMKSearch = new MKSearch();
				mMKSearch.init(app.getBMapManager(), new MySearchListener());
				mMKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
				mMKSearch.drivingSearch(null, start, null, end);
			}
		}else{
			myLocationOverlay = new MyLocationOverlay(mv);
			LocationData locData = new LocationData();
			//�ֶ���λ��Դ��Ϊ�찲�ţ���ʵ��Ӧ���У���ʹ�ðٶȶ�λSDK��ȡλ����Ϣ��Ҫ��SDK����ʾһ��λ�ã���Ҫʹ�ðٶȾ�γ����꣨bd09ll��
			locData.latitude = myLat;
			locData.longitude = myLon;
			locData.direction = 2.0f;
			myLocationOverlay.setData(locData);
			if(!(myLat==0&&myLon==0)){
				endPoint = new GeoPoint((int) (myLat * 1E6),(int) (myLon * 1E6));
			}
			if(mv.getOverlays().contains(myLocationOverlay)){
				mv.getOverlays().remove(myLocationOverlay);
			}
			mv.getOverlays().add(myLocationOverlay);
		}
		mv.refresh();
		target(null);
	}
	public void showNearUsers(View v){
		if(userOverlay==null){
			userOverlay=new HeadOverlay(this,mv,bt_user,User.class);
		}
		if(userOverlay!=null){
			if (mv.getOverlays().contains(userOverlay)) {
				mv.getOverlays().remove(userOverlay);
			} else {
				mv.getOverlays().add(userOverlay);
				userOverlay.refreshUI();
				userOverlay.reloadData();
			}
		}
//		mv.refresh();
	}
	
	public void showNearSalons(View v){
		if(salonOverlay==null){
			salonOverlay=new HeadOverlay(this,mv,bt_salon,Salon.class);
		}
		if(salonOverlay!=null){
			if (mv.getOverlays().contains(salonOverlay)) {
				mv.getOverlays().remove(salonOverlay);
			} else {
			    mv.getOverlays().add(salonOverlay);
			    salonOverlay.refreshUI();
			    salonOverlay.reloadData();
		    }
		}
//		mv.refresh();
	}
}
