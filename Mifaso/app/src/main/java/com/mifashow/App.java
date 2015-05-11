package com.mifashow;

import android.app.Application;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;

import com.baidu.mapapi.BMapManager;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mifashow.data.Constance;
import com.mifashow.domain.Booking;
import com.mifashow.domain.MessageBox;
import com.mifashow.domain.Posting;
import com.mifashow.domain.Salon;
import com.mifashow.domain.User;
import com.mifashow.tool.LocationHelper;
import com.mifashow.tool.LocationHelper.OnLocationListener;
import com.mifashow.tool.Storage;
import com.mifashow.tool.image.ImageCache;

public class App extends Application{
  private List<User> discoveryUsers;
  private List<Salon> discoverySalons;
  private List<User> followedUsers;
  private List<User> followingUsers;
  private List<User> near100kmUsers;
  private List<User> near10kmUsers;
  private List<Salon> near100kmSalons;
  private List<Salon> near10kmSalons;
  private LocationHelper locationHelper;
  private float latitude=0;
  private float longitude=0;
  private String address="";
  private User loginUser;
//  private long loginUserId;
  private LinkedHashMap<Long, MessageBox> messageBoxes;
  private List<Posting> myPostings;
  private List<Booking> myBookings;
  private List<Posting> near100kmPostings;
  private List<Posting> near10kmPostings;
  private List<Posting> followingPostings;
  private List<Posting> markingPostings;
  private List<Posting> suitablePostings;
  private List<Posting> postings;
  private BMapManager mapManager;
  private ImageCache imageCache;
  public void init(){
	  setLoginUser(null);
	  loginUser=null;
	  followedUsers=null;
	  followingUsers=null;
	  near100kmUsers=null;
	  near10kmUsers=null;
	  messageBoxes=null;
	  myPostings=null;
	  myBookings=null;
	  near100kmPostings=null;
	  near10kmPostings=null;
	  followingPostings=null;
	  markingPostings=null;
	  suitablePostings=null;
	  postings=null;
  }
  @Override
  public void onCreate (){
	  mapManager=new BMapManager(this);
	  mapManager.init(null);
	  super.onCreate();
  }
  @Override
public void onTerminate (){
	  if(mapManager!=null){
		  mapManager.destroy();
		  mapManager=null;
  }
	  super.onTerminate();
  }
  public BMapManager getBMapManager(){
	  return mapManager;
  }
  public void setLoginUser(User user){
    loginUser = user;
    Storage.saveObject(getBaseContext(), "login", "lastUser", user);
    if(user!=null){
    	SharedPreferences loginSharedPreferences = getSharedPreferences("login", 0);
    	HashSet<String> loginSigns=(HashSet<String>) loginSharedPreferences.getStringSet("loginSigns", new HashSet<String>());
    	loginSigns.add(user.getSign());
    	loginSharedPreferences.edit().putStringSet("loginSigns", loginSigns).commit();
    }
  }
  public User getLoginUser(){
	  if(loginUser==null)loginUser=(User) Storage.getObjectInfo(getBaseContext(), "login", "lastUser");
	return loginUser;
  }
  public String[] getLoginSigns(){
	  SharedPreferences loginSharedPreferences = getSharedPreferences("login", 0);
	  HashSet<String> loginSigns=(HashSet<String>) loginSharedPreferences.getStringSet("loginSigns", new HashSet<String>());
	  return loginSigns.toArray(new String[loginSigns.size()]);
  }

  

  

  @SuppressWarnings("unchecked")
public List<User> getFollowingUsers(){
	  if (this.followingUsers == null)
	    {
	      this.followingUsers = ((List<User>)Storage.getObjectInfo(getBaseContext(), ""+getLoginUser().getUserId(), "followingUsers"));
	      if (this.followingUsers == null)
	        this.followingUsers = new ArrayList<User>();
	    }
	    return this.followingUsers;
  }

  public void setFollowingUsers(List<User> followingUsers){
    this.followingUsers = followingUsers;
    Storage.saveObject(getBaseContext(), ""+getLoginUser().getUserId(), "followingUsers", followingUsers);
  }
  
  @SuppressWarnings("unchecked")
  public List<User> getFollowedUsers(){
  	  if (this.followedUsers == null){
  	      this.followedUsers = ((List<User>)Storage.getObjectInfo(getBaseContext(), ""+getLoginUser().getUserId(), "followedUsers"));
  	      if (this.followedUsers == null)
  	        this.followedUsers = new ArrayList<User>();
  	    }
  	    return this.followedUsers;
    }

  public void setFollowedUsers(List<User> followedUsers){
    this.followedUsers = followedUsers;
    Storage.saveObject(getBaseContext(), ""+getLoginUser().getUserId(), "followedUsers", followedUsers);
  }
  @SuppressWarnings("unchecked")
  public List<User> getDiscoveryUsers(){
  	  if (this.discoveryUsers == null){
  	      this.discoveryUsers = ((List<User>)Storage.getObjectInfo(getBaseContext(), ""+getLoginUser().getUserId(), "discoveryUsers"));
  	      if (this.discoveryUsers == null)
  	        this.discoveryUsers = new ArrayList<User>();
  	    }
  	    return this.discoveryUsers;
    }

  public void setDiscoveryUsers(List<User> discoveryUsers){
    this.discoveryUsers = discoveryUsers;
    Storage.saveObject(getBaseContext(), ""+getLoginUser().getUserId(), "discoveryUsers", discoveryUsers);
  }
  
  @SuppressWarnings("unchecked")
  public List<User> getNear10kmUsers(){
  	  if (this.near10kmUsers == null)
  	    {
  	      this.near10kmUsers = ((List<User>)Storage.getObjectInfo(getBaseContext(), "location", "near10kmUsers"));
  	      if (this.near10kmUsers == null)
  	        this.near10kmUsers = new ArrayList<User>();
  	    }
  	    return this.near10kmUsers;
    }

    public void setNear10kmUsers(List<User> near10kmUsers){
      this.near10kmUsers = near10kmUsers;
      Storage.saveObject(getBaseContext(), "location", "near10kmUsers", near10kmUsers);
    }
    
    @SuppressWarnings("unchecked")
    public List<User> getNear100kmUsers(){
    	  if (this.near100kmUsers == null)
    	    {
    	      this.near100kmUsers = ((List<User>)Storage.getObjectInfo(getBaseContext(), "location", "near100kmUsers"));
    	      if (this.near100kmUsers == null)
    	        this.near100kmUsers = new ArrayList<User>();
    	    }
    	    return this.near100kmUsers;
      }
    public void setNear100kmUsers(List<User> near100kmUsers){
        this.near100kmUsers = near100kmUsers;
        Storage.saveObject(getBaseContext(), "location", "near100kmUsers", near100kmUsers);
      }

    @SuppressWarnings("unchecked")
    public List<Salon> getDiscoverySalons(){
    	  if (this.discoverySalons == null){
    	      this.discoverySalons = ((List<Salon>)Storage.getObjectInfo(getBaseContext(), ""+getLoginUser().getUserId(), "discoverySalons"));
    	      if (this.discoverySalons == null)
    	        this.discoverySalons = new ArrayList<Salon>();
    	    }
    	    return this.discoverySalons;
      }

    public void setDiscoverySalons(List<Salon> discoverySalons){
      this.discoverySalons = discoverySalons;
      Storage.saveObject(getBaseContext(), ""+getLoginUser().getUserId(), "discoverySalons", discoverySalons);
    }
      
      @SuppressWarnings("unchecked")
      public List<Salon> getNear100kmSalons(){
      	  if (this.near100kmSalons == null)
      	    {
      	      this.near100kmSalons = ((List<Salon>)Storage.getObjectInfo(getBaseContext(), "location", "near100kmSalons"));
      	      if (this.near100kmSalons == null)
      	        this.near100kmSalons = new ArrayList<Salon>();
      	    }
      	    return this.near100kmSalons;
        }
      public void setNear100kmSalons(List<Salon> near100kmSalons){
          this.near100kmSalons = near100kmSalons;
          Storage.saveObject(getBaseContext(), "location", "near100kmSalons", near100kmSalons);
        }
      
      @SuppressWarnings("unchecked")
      public List<Salon> getNear10kmSalons(){
      	  if (this.near10kmSalons == null)
      	    {
      	      this.near10kmSalons = ((List<Salon>)Storage.getObjectInfo(getBaseContext(), "location", "near10kmSalons"));
      	      if (this.near10kmSalons == null)
      	        this.near10kmSalons = new ArrayList<Salon>();
      	    }
      	    return this.near10kmSalons;
        }
      public void setNear10kmSalons(List<Salon> near10kmSalons){
          this.near10kmSalons = near10kmSalons;
          Storage.saveObject(getBaseContext(), "location", "near10kmSalons", near10kmSalons);
        }

        

  public LocationHelper getLocationHelper(){
	  if(locationHelper==null){
		  locationHelper = new LocationHelper(this);
		    locationHelper.setListener(new OnLocationListener(){
				@Override
				public void onLocationChanged() {
					updateLocation();
				}
		    });
	  }
    return locationHelper;
  }

  

//  public long getLoginUserId()
//  {
//    if (this.loginUserId == 0L)
//      this.loginUserId = getSharedPreferences("login", 0).getLong("userId", 0L);
//    return this.loginUserId;
//  }

@SuppressWarnings("unchecked")
public LinkedHashMap<Long, MessageBox> getMessageBoxes(){
    if (this.messageBoxes == null){
      this.messageBoxes = ((LinkedHashMap<Long, MessageBox>)Storage.getObjectInfo(getBaseContext(), ""+getLoginUser().getUserId(), "messageBoxes"));
      if (this.messageBoxes == null)
        this.messageBoxes = new LinkedHashMap<Long, MessageBox>();
    }
    return this.messageBoxes;
  }
public void setMessageBoxes(LinkedHashMap<Long, MessageBox> messageBoxes){
    this.messageBoxes = messageBoxes;
    Storage.saveObject(getBaseContext(), ""+getLoginUser().getUserId(), "messageBoxes", messageBoxes);
  }  

  @SuppressWarnings("unchecked")
public List<Posting> getSuitablePostings() {
	  if (this.suitablePostings == null){
	      this.suitablePostings = ((List<Posting>)Storage.getObjectInfo(getBaseContext(), ""+getLoginUser().getUserId(), "suitablePostings"));
	      if (this.suitablePostings == null)
	        this.suitablePostings = new ArrayList<Posting>();
	    }
	  return suitablePostings;
}
public void setSuitablePostings(List<Posting> suitablePostings) {
	this.suitablePostings = suitablePostings;
	Storage.saveObject(getBaseContext(), ""+getLoginUser().getUserId(), "suitablePostings", suitablePostings);
}
@SuppressWarnings("unchecked")
public List<Posting> getMarkingPostings() {
	  if (this.markingPostings == null){
	      this.markingPostings = ((List<Posting>)Storage.getObjectInfo(getBaseContext(), ""+getLoginUser().getUserId(), "markingPostings"));
	      if (this.markingPostings == null)
	        this.markingPostings = new ArrayList<Posting>();
	    }
	    return this.markingPostings;
}
public void setMarkingPostings(List<Posting> markingPostings) {
	this.markingPostings = markingPostings;
	Storage.saveObject(getBaseContext(), ""+getLoginUser().getUserId(), "markingPostings", markingPostings);
}


  

//  @Override
//public void onCreate(){
//    super.onCreate();
//  }
  public void updateLocation(){
	  if(getLoginUser()==null||(getLatitude()==0&&getLongitude()==0))return;
	  float oldLat=getLoginUser().getLatitude();
	  float oldLon=getLoginUser().getLongitude();
	  float distance=0;
	  if(!(oldLat==0&&oldLon==0) && !(getLatitude()==0&&getLongitude()==0)){
	  float[] distanceResults=new float[1];
	  Location.distanceBetween(getLatitude(), getLongitude(),oldLat, oldLon, distanceResults);
	  distance=distanceResults[0];
	  }
		if((oldLat==0 && oldLon==0)||distance>500){
			AsyncHttpClient client=new AsyncHttpClient();
			client.setBasicAuth(getLoginUser().getSign(), getLoginUser().getPassword());
			RequestParams params = new RequestParams();
			params.add("latitude", ""+getLatitude());
			params.add("longitude", ""+getLongitude());
			client.put(Constance.SERVER_URL+"user/location", params, new AsyncHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode,Header[] headers,byte[] content){
					Log.d("-App","updateLocation:success");
					User u =new Gson().fromJson(new String(content), User.class);
					if (u != null){
						setLoginUser(u);
					}
				}
				@Override
				public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
					Log.d("-App","updateLocation:failure");
					Log.getStackTraceString(error);
				}
			});
			}
	}

  @SuppressWarnings("unchecked")
public List<Posting> getMyPostings() {
	  if (this.myPostings == null){
	      this.myPostings = ((List<Posting>)Storage.getObjectInfo(getBaseContext(), ""+getLoginUser().getUserId(), "myPostings"));
	      if (this.myPostings == null)
	        this.myPostings = new ArrayList<Posting>();
	    }
	    return this.myPostings;
}
public void setMyPostings(List<Posting> myPostings) {
	this.myPostings = myPostings;
	Storage.saveObject(getBaseContext(), ""+getLoginUser().getUserId(), "myPostings", myPostings);
}


@SuppressWarnings("unchecked")
public List<Posting> getFollowingPostings(){
	if (this.followingPostings == null){
	      this.followingPostings = ((List<Posting>)Storage.getObjectInfo(getBaseContext(), ""+getLoginUser().getUserId(), "followingPostings"));
	      if (this.followingPostings == null)
	        this.followingPostings = new ArrayList<Posting>();
	    }
	return this.followingPostings;
  }
public void setFollowingPostings(List<Posting> postings){
    this.followingPostings = postings;
	Storage.saveObject(getBaseContext(), ""+getLoginUser().getUserId(), "followingPostings", followingPostings);
  }
@SuppressWarnings("unchecked")
public List<Booking> getMyBookings() {
	if (this.myBookings == null){
	      this.myBookings = ((List<Booking>)Storage.getObjectInfo(getBaseContext(), ""+getLoginUser().getUserId(), "myBookings"));
	      if (this.myBookings == null)
	        this.myBookings = new ArrayList<Booking>();
	    }
	return myBookings;
}
public void setMyBookings(List<Booking> myBookings) {
	this.myBookings = myBookings;
	Storage.saveObject(getBaseContext(), ""+getLoginUser().getUserId(), "myBookings", myBookings);
}
@SuppressWarnings("unchecked")
public List<Posting> getNear100kmPostings(){
	if (this.near100kmPostings == null){
	      this.near100kmPostings = ((List<Posting>)Storage.getObjectInfo(getBaseContext(), ""+getLoginUser().getUserId(), "near100kmPostings"));
	      if (this.near100kmPostings == null)
	        this.near100kmPostings = new ArrayList<Posting>();
	    }
    return this.near100kmPostings;
  }
  public void setNear100kmPostings(List<Posting> postings){
    this.near100kmPostings = postings;
	Storage.saveObject(getBaseContext(), ""+getLoginUser().getUserId(), "near100kmPostings", near100kmPostings);
  }

  @SuppressWarnings("unchecked")
public List<Posting> getNear10kmPostings(){
	  if (this.near10kmPostings == null){
	      this.near10kmPostings = ((List<Posting>)Storage.getObjectInfo(getBaseContext(), ""+getLoginUser().getUserId(), "near10kmPostings"));
	      if (this.near10kmPostings == null)
	        this.near10kmPostings = new ArrayList<Posting>();
	    }  
	  return this.near10kmPostings;
	  }
  public void setNear10kmPostings(List<Posting> postings){
    this.near10kmPostings = postings;
	Storage.saveObject(getBaseContext(), ""+getLoginUser().getUserId(), "near10kmPostings", near10kmPostings);
  }

  @SuppressWarnings("unchecked")
public List<Posting> getPostings(){
	  if (this.postings == null){
	      this.postings = ((List<Posting>)Storage.getObjectInfo(getBaseContext(), ""+getLoginUser().getUserId(), "postings"));
	      if (this.postings == null)
	        this.postings = new ArrayList<Posting>();
	    }  
	    return this.postings;
	  }
  public void setPostings(List<Posting> postings){
    this.postings = postings;
	Storage.saveObject(getBaseContext(), ""+getLoginUser().getUserId(), "postings", postings);
  }
public void refreshUser(){
	  new RefreshLoginUserTask().execute((Void)null);
  }
  
  class RefreshLoginUserTask extends AsyncTask<Void, Void, Void>{
		User userGot;

		@Override
		protected Void doInBackground(Void... params) {
			HttpGet request = new HttpGet(Constance.SERVER_URL+"user/"+getLoginUser().getUserId());
			request.addHeader(BasicScheme.authenticate(
					 new UsernamePasswordCredentials(getLoginUser().getSign(), getLoginUser().getPassword()),
					 "UTF-8", false));

			AndroidHttpClient client=AndroidHttpClient.newInstance("appGetUser");
			String response = null;
			try {
				response=client.execute(request,new BasicResponseHandler());
				userGot=new Gson().fromJson(response, User.class);
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				client.close();
	        } 
			return null;
		}

		@Override
		protected void onPostExecute(Void param) {
			if(userGot!=null){
				setLoginUser(userGot);
			}
		}
		}
    public float getLatitude(){
    	if(latitude==0){
    	SharedPreferences locationSharedPreferences = getSharedPreferences("location", 0);
    	latitude=locationSharedPreferences.getFloat("latitude", 0);
    	}
    	return latitude;
	}
	public float getLongitude(){
		if(longitude==0){
		SharedPreferences locationSharedPreferences = getSharedPreferences("location", 0);
		longitude=locationSharedPreferences.getFloat("longitude", 0);
		}
		return longitude;
	}
	public String getAddress() {
		if(address==null){
		SharedPreferences locationSharedPreferences = getSharedPreferences("location", 0);
		address=locationSharedPreferences.getString("address", null);
		}
		return address;
	}
	public void setLatitude(float latitude){
		SharedPreferences locationSharedPreferences = getSharedPreferences("location", 0);
    	locationSharedPreferences.edit().putFloat("latitude", latitude).commit();
		this.latitude=latitude;
	}
	public void setLongitude(float longitude){
		SharedPreferences locationSharedPreferences = getSharedPreferences("location", 0);
    	locationSharedPreferences.edit().putFloat("longitude", longitude).commit();
		this.longitude=longitude;
	}
	public void setAddress(String address) {
		SharedPreferences locationSharedPreferences = getSharedPreferences("location", 0);
    	locationSharedPreferences.edit().putString("address", address).commit();
		this.address=address;
	}
	public ImageCache getImageCache() {
		if (imageCache == null) {
            imageCache = new ImageCache(this, "bitmaps");
        }
		return imageCache;
	}
}