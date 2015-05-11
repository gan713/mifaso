package com.mifashow.tool;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.mifashow.App;

public class LocationHelper implements BDLocationListener
//LocationListener 
{
	public LocationClient mLocationClient;
	private OnLocationListener listener;
    private MKSearch mMKSearch;
//    private Context context;
    private App app;
    private Map<String,String> cityCache;
    public LocationHelper(Context context){
//    	this.context=context;
    	this.app=(App)context;
    	this.cityCache=new HashMap<String,String>();
    	mLocationClient = new LocationClient(context);
        mLocationClient.registerLocationListener( this ); 
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(60*1000);
        option.setIsNeedAddress(true);
        option.setNeedDeviceDirect(false);
        mLocationClient.setLocOption(option);
        if (mLocationClient != null){
        	mLocationClient.start();
        	mLocationClient.requestLocation();
        }
    }
//	public LocationHelper(Context context){
//		locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
//		location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//		if(location!=null){
//			if(listener!=null)listener.onLocationChanged(location);
//			address=getAddress((float)location.getLatitude(),(float)location.getLongitude());
//		}
//		Log.d("-LocationHelper","lastKnownLocation:"+(location==null?"no":location.toString()));
//	    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, LocationHelper.this);
//	    Log.d("-LocationHelper","updateRequested");
//	}
//	public Location getLocation(){
//		return location;
//	}
	public static abstract interface OnLocationListener {
		public abstract void onLocationChanged();
	}
	public void setListener(OnLocationListener listener){
    	this.listener=listener;
    }
//	@Override
//	public void onLocationChanged(Location location) {
//		this.location=location;
//		Log.d("-LocationHelper", "onLocationChanged");
//		if(listener!=null)listener.onLocationChanged(location);
//		address=getAddress((float)location.getLatitude(),(float)location.getLongitude());
//	}
//	@Override
//	public void onProviderDisabled(String provider) {
//		Log.d("-LocationHelper", "onProviderDisabled:"+provider.toString());
//		
//	}
//	@Override
//	public void onProviderEnabled(String provider) {
//		Log.d("-LocationHelper", "onProviderEnabled"+provider.toString());
//		
//	}
//	@Override
//	public void onStatusChanged(String provider, int status, Bundle extras) {
//		Log.d("-LocationHelper", "onStatusChanged:"+status);
//		
//	}

//	public String getAddress() {
//		return address;
//	}
	public String getDistance(float latitude,float longitude){
		float myLat=app.getLatitude();
		float myLon=app.getLongitude();
		if(!(myLat==0 && myLon==0) && !(latitude==0 && longitude==0)){
    		float[] distanceResults=new float[1];
			Location.distanceBetween(latitude, longitude,myLat, myLon, distanceResults);
			if(distanceResults[0]>=10000){
				return ""+(int)distanceResults[0]/1000+"km";
			}else{
				return ""+(int)distanceResults[0]+"m";
			}
		}
		return "";
	}
	public int getDistanceByMeter(float latitude,float longitude){
		float myLat=app.getLatitude();
		float myLon=app.getLongitude();
		if(!(myLat==0 && myLon==0) && !(latitude==0 && longitude==0)){
    		float[] distanceResults=new float[1];
			Location.distanceBetween(latitude, longitude,myLat, myLon, distanceResults);
			return (int)distanceResults[0];
		}
		return 0;
	}
	public void showCityInTextView(float latitude,float longitude,TextView tv){
		if(tv==null || (latitude==0&&longitude==0)){
			tv.setVisibility(View.GONE);
			return;
		}
		String distanceStr = getDistance(latitude,longitude);
		String city=cityCache.get(""+latitude+longitude);
		if(city==null){
			city="";
			mMKSearch = new MKSearch();
			mMKSearch.init(app.getBMapManager(), new MySearchListener(mMKSearch,tv,distanceStr,latitude,longitude));
			mMKSearch.reverseGeocode(new GeoPoint((int)(latitude*1e6),(int)(longitude*1e6)));
		}
		if("".equals(city+distanceStr)){
			tv.setVisibility(View.GONE);
		}else{
			tv.setText(city+distanceStr);
			tv.setVisibility(View.VISIBLE);
		}
//		String city = null;
//		try{
//		String uriAPI = "http://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&language=%s&sensor=%s"; 
//        //����Geocoder API������URL 
//        String url = String.format(uriAPI,""+latitude, ""+longitude,Locale.getDefault().toString(),"false"); 
//        //����request���� 
//        HttpGet request = new HttpGet(url); 
//        //����HttpClient���� 
//        HttpClient client = new DefaultHttpClient(); 
//        //�õ�������Ӧ���� 
//        HttpResponse response = client.execute(request); 
//        //��״̬��Ϊ200��˵������ɹ� 
//        if(response.getStatusLine().getStatusCode()==200){ 
//            //�����Ӧ��Ŀ--����Ŀ��json�ַ� 
//            String resultStr = EntityUtils.toString(response.getEntity()); 
//            JSONArray jsonObjs = new JSONObject(resultStr).getJSONArray("results"); 
//            for(int j=0;j<jsonObjs.length();j++){
//            JSONObject jsonObj = jsonObjs.getJSONObject(j); 
//            //������formatted_addressֵ 
//            JSONArray address_components = jsonObj.getJSONArray("address_components"); 
//            for(int i=0;i<address_components.length();i++){
//            	JSONObject address=address_components.getJSONObject(i);
//            	JSONArray types=address.getJSONArray("types");
//            	if(types.length()>0&&"locality".equals(types.getString(0))){
//            		city=address.getString("short_name").replace(" ", "").replace("City", "");
//            		break;
//            	}
//            }
//            if(city!=null)break;
//            }
//        }
//		}catch(Exception e){
//			Log.e("-LocationHelper",Log.getStackTraceString(e));
//		}
//		return city;
	}
//	public static String getAddress(float latitude,float longitude){
//		String formattedAddress = null;
//		try{
//		String uriAPI = "http://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&language=%s&sensor=%s"; 
//        //����Geocoder API������URL 
//        String url = String.format(uriAPI,""+latitude, ""+longitude,Locale.getDefault().toString(),"false"); 
//        //����request���� 
//        HttpGet request = new HttpGet(url); 
//        //����HttpClient���� 
//        HttpClient client = new DefaultHttpClient(); 
//        //�õ�������Ӧ���� 
//        HttpResponse response = client.execute(request); 
//        //��״̬��Ϊ200��˵������ɹ� 
//        if(response.getStatusLine().getStatusCode()==200){ 
//            //�����Ӧ��Ŀ--����Ŀ��json�ַ� 
//            String resultStr = EntityUtils.toString(response.getEntity()); 
//            JSONArray jsonObjs = new JSONObject(resultStr).getJSONArray("results"); 
//            for(int j=0;j<jsonObjs.length();j++){
//            JSONObject jsonObj = jsonObjs.getJSONObject(j); 
//            //������formatted_addressֵ 
//            formattedAddress = jsonObj.getString("formatted_address");
//            if(formattedAddress!=null)break;
//            }
//        }
//		}catch(Exception e){
//			Log.e("-LocationHelper",Log.getStackTraceString(e));
//		}
//		return formattedAddress;
//	}
	@Override
	public void onReceiveLocation(BDLocation bdLocation) {
		if (bdLocation == null)return ;
		Log.d("-LocationHelper", "onReceiveLocation");
		app.setLatitude((float)bdLocation.getLatitude());
		app.setLongitude((float)bdLocation.getLongitude());
		app.setAddress(bdLocation.getAddrStr());
		if(listener!=null)listener.onLocationChanged();
	}
	@Override
	public void onReceivePoi(BDLocation bdLocation) {
		onReceiveLocation(bdLocation);
	}
	public class MySearchListener implements MKSearchListener {
		MKSearch mMKSearch;
		TextView tv;
		String distance;
		float latitude;
		float longitude;
		public MySearchListener(MKSearch mMKSearch,TextView tv,String distance,float latitude,float longitude){
			this.mMKSearch=mMKSearch;
			this.tv=tv;
			this.distance=distance;
			this.latitude=latitude;
			this.longitude=longitude;
		}
        @Override  
        public void onGetAddrResult(MKAddrInfo result, int iError) {
        	String city=result.addressComponents.city;
        	if(city!=null && TextLengthInputFilter.getChineseCount(city)+city.length()>10)city=result.addressComponents.province;
    		if("".equals(distance))distance=getDistance(latitude,longitude);
        	if(!("".equals(city) && "".equals(distance))){
        	    city=city.replaceAll(" ","").replace("�ر�������", "").replace("������", "").replace("������", "").replace("ʡ", "").replace("��", "").replace("City", "");
        	    cityCache.put(""+latitude+longitude, city);
        	    tv.setText(city+distance);
    			tv.setVisibility(View.VISIBLE);
        	}
        	mMKSearch.destory();
        	try {
				this.finalize();
			} catch (Throwable e) {
				Log.e("-LocationHelper",Log.getStackTraceString(e));
			}
        }  
        @Override  
        public void onGetDrivingRouteResult(MKDrivingRouteResult result, int iError) {  
                //���ؼݳ�·���������  
        }  
        @Override  
        public void onGetPoiResult(MKPoiResult result, int type, int iError) {  
                //����poi�������  
        }  
        @Override  
        public void onGetTransitRouteResult(MKTransitRouteResult result, int iError) {  
                //���ع����������  
        }  
        @Override  
        public void onGetWalkingRouteResult(MKWalkingRouteResult result, int iError) {  
                //���ز���·���������  
        }  
        @Override      
        public void onGetBusDetailResult(MKBusLineResult result, int iError) {  
                //���ع�����������Ϣ�������  
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

}
