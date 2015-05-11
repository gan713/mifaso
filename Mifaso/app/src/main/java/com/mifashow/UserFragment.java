package com.mifashow;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mifashow.data.Constance;
import com.mifashow.domain.User;
import com.mifashow.tool.DateHelper;
import com.mifashow.tool.ImageHelper;
import com.mifashow.ui.BorderListView;
import com.mifashow.ui.ResizedImageView;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserFragment extends DialogFragment implements OnItemClickListener, OnItemLongClickListener{

	static enum SOURCETYPE{DISCOVERY,FOLLOWINGUSER,FOLLOWEDUSER,SEARCH}
	UserAdapter adapter;
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
	  List<User> users;
	  View view;
	  LoadUserTask loadUserTask;
//	  LruCache<Long,View> memViews; 
	  private Handler handler = new Handler();
	    private Runnable runnable = new Runnable() {
	        @Override
			public void run() {
	            if(view!=null && lv.getLastVisiblePosition()<=39){
	            	if(loadUserTask!=null && loadUserTask.getStatus()==Status.RUNNING){
	        			loadUserTask.cancel(true);
	        		}
	        		loadUserTask=new LoadUserTask();
					loadUserTask.execute(-1L);
	            }
	            handler.postDelayed(this, 1000 * 300);// ���300��
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
			if(loadUserTask!=null && loadUserTask.getStatus()==Status.RUNNING){
				loadUserTask.cancel(true);
			}
			handler.removeCallbacks(runnable); 
//			if(sourceType==SOURCETYPE.DISCOVERY && memViews!=null)memViews.evictAll();
	        super.onDestroy();
		}
	  @Override
	public void onCreate(Bundle bundle){
	    super.onCreate(bundle);
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar);
//	    memViews=new LruCache<Long,View>(20);//����60���û�
	    app = ((App)getActivity().getApplication());
	    isEnd=false;
	    Bundle b=getArguments();
		if(b!=null){
			sourceType=(SOURCETYPE)b.getSerializable("sourceType");
			sourceId = b.getLong("sourceId", -1L);
			sourceKeyword=b.getString("sourceKeyword");
		}
	    
        if(sourceType == SOURCETYPE.FOLLOWEDUSER){
            if(sourceId == app.getLoginUser().getUserId())
                users = app.getFollowedUsers();
            url =Constance.SERVER_URL+"user/followingUser/"+sourceId;
        }else if(sourceType == SOURCETYPE.FOLLOWINGUSER){
            if(sourceId == app.getLoginUser().getUserId())
                users = app.getFollowingUsers();
            url = Constance.SERVER_URL+"user/followedUser/"+sourceId;
        }else if(sourceType == SOURCETYPE.SEARCH){
//        	try{
//        		sourceKeyword=URLEncoder.encode(sourceKeyword,"utf-8");
//        	}catch(Exception e){}
        	url = Constance.SERVER_URL+"user/search";
            users=null;
        }else{
        	sourceType=SOURCETYPE.DISCOVERY;
        	users=app.getDiscoveryUsers();
        	url = Constance.SERVER_URL+"user/discovery";
        }
        if(users==null)users=new ArrayList<User>();
        keyword="";
    }
	  @Override  
	    public View onCreateView(final LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			if(this.view==null){
				view = inflater.inflate(R.layout.fragment_user, null);
				lo_info=(LinearLayout) view.findViewById(R.id.user_lo_info);
				tv_info=(TextView) view.findViewById(R.id.user_tv_info);
				lv = ((BorderListView)view.findViewById(R.id.user_lv));
			    adapter = new UserAdapter();
				lv.setAdapter(adapter);
				lv.setOnItemLongClickListener(this);
		        lv.setOnItemClickListener(this);
		        lv.setOnBorderListener(new BorderListView.OnBorderListener(){
		          @Override
				  public void onBottom(){
//		        	  Log.d("-UserFragment", "LastVisiblePosition:"+lv.getLastVisiblePosition());
		              if(!isEnd&&url!=null&&users!=null && users.size()>0){
		            	  if(loadUserTask!=null && loadUserTask.getStatus()==Status.RUNNING){
			        			loadUserTask.cancel(true);
			        		}
			        		loadUserTask=new LoadUserTask();
			        		Log.d("-UserFragment","maxId="+users.get(users.size()-1).getUserId());
							loadUserTask.execute(users.get(users.size()-1).getUserId());
		              }
		          }
		          @Override
		  		  public void onRoll(){}
		        });
		        if(users==null||users.size()==0){
		        	tv_info.setText(R.string.info_null);
		        	lo_info.setVisibility(View.VISIBLE);
		        }else{
		        	lo_info.setVisibility(View.GONE);
		        }
		        adapter.notifyDataSetChanged();
		        if(sourceType==SOURCETYPE.DISCOVERY){
		        	runnable.run();
		        }else{
		        	if(loadUserTask!=null && loadUserTask.getStatus()==Status.RUNNING){
	        			loadUserTask.cancel(true);
	        		}
	        		loadUserTask=new LoadUserTask();
					loadUserTask.execute(-1L);
		        }
			}else{
				ViewGroup parent = (ViewGroup) view.getParent();
				if (parent != null) {
					parent.removeView(view);
				}
			}
			return view;
		}
//	  private void update(final long maxId){
//		  Log.d("-UserFragment","update");
//		  RequestParams params=new RequestParams();
//		  if(sourceType==SOURCETYPE.SEARCH)params.put("keyword", this.sourceKeyword);
//		  else if(sourceType==SOURCETYPE.DISCOVERY){
//			  Location location=app.getLocationHelper().getLocation();
//			  params.put("latitude", ""+location.getLatitude());
//			  params.put("longitude", ""+location.getLongitude());
//		  }
//		  if(maxId>0)params.put("maxId", ""+maxId);
//		  isRefreshing=true;
//		  AsyncHttpClient client=new AsyncHttpClient();
//			client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
//		  client.get(sql, params,new AsyncHttpResponseHandler() {
//			  @Override
//			  public void onStart() {
//				  tv_info.setText(R.string.status_loading);
//				  lo_info.setVisibility(View.VISIBLE);
//			  }
//			  @Override
//			  public void onFinish(){
//				  isRefreshing = false;
//				  if(users==null || users.size()==0){
//					  tv_info.setText(R.string.info_null);
//					  lo_info.setVisibility(View.VISIBLE);
//				  }else{
//					  lo_info.setVisibility(View.GONE);
//				  }
//				  }
//			  @Override
//			  public void onFailure(Throwable e){
//			      Log.d("-UserFragment",Log.getStackTraceString(e));
//			  }
//			  @Override
//			  public void onSuccess(String s){
//				  List<User> us = (new Gson()).fromJson(s, new TypeToken<List<User>>() {}.getType());
//				  isEnd = (us==null||us.size() < 20);
//	              if(maxId!=-1){
//	            	  users.addAll(us);
//	              }else{
//	            	  users=us;
//	              }
//	              if(sourceType == SOURCETYPE.FOLLOWEDUSER){
//	            	  if(sourceId == app.getLoginUser().getUserId())app.setFollowedUsers(users);
//	              }else if(sourceType == SOURCETYPE.FOLLOWINGUSER){
//	            	  if(sourceId == app.getLoginUser().getUserId())app.setFollowingUsers(users);
//	              }else if(sourceType==SOURCETYPE.DISCOVERY){
//	            	  app.setDiscoveryUsers(users);
//	              }
//	              adapter.notifyDataSetChanged();
//	              }
//	            });
//	  }
	  
	  class LoadUserTask extends AsyncTask<Long,Void,String>{
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
				else if (sourceType == SOURCETYPE.DISCOVERY && !(lat==0 && lon==0)) {
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
						Log.e("-UFragment",Log.getStackTraceString(e));
					}
				get = new HttpGet(url+urlParam);
				HttpParams httpParameters = new BasicHttpParams();
			    HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
			    HttpConnectionParams.setSoTimeout(httpParameters, 7000);
			    get.setParams(httpParameters);
				Log.d("-UserFragment",url+urlParam);
				client = AndroidHttpClient.newInstance("loadUser");
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
				Log.e("-UserFragment", Log.getStackTraceString(e));
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
			List<User> us = (new Gson()).fromJson(content, new TypeToken<List<User>>() {}.getType());
			  isEnd = (us==null||us.size() < 20);
            if(maxId!=-1){
          	  users.addAll(us);
            }else{
          	  users=us;
            }
            if(sourceType == SOURCETYPE.FOLLOWEDUSER){
          	  if(sourceId == app.getLoginUser().getUserId())app.setFollowedUsers(users);
            }else if(sourceType == SOURCETYPE.FOLLOWINGUSER){
          	  if(sourceId == app.getLoginUser().getUserId())app.setFollowingUsers(users);
            }else if(sourceType==SOURCETYPE.DISCOVERY){
          	  app.setDiscoveryUsers(users);
            }
            adapter.notifyDataSetChanged();
			}
			  if(users==null || users.size()==0){
				  tv_info.setText(R.string.info_null);
				  lo_info.setVisibility(View.VISIBLE);
			  }else{
				  lo_info.setVisibility(View.GONE);
			  }
		}
	}
	  
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
		if(app.getLoginUser().getUserId()==0)return false;
		String[] followingHandles;
		if(sourceType==SOURCETYPE.FOLLOWINGUSER){
			followingHandles=getResources().getStringArray(R.array.action_followingHandle);
		}else{
			followingHandles=new String[]{getResources().getStringArray(R.array.action_followingHandle)[0]};
		}
		
		new AlertDialog.Builder(this.getActivity()).setItems(followingHandles, new FollowingHandleListener(arg3)).create().show();
	    return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		ProfileFragment profileFragment = new ProfileFragment();
		Bundle bundle = new Bundle();
	      bundle.putLong("userId",arg3);
	      profileFragment.setArguments(bundle);
	    profileFragment.show(getFragmentManager(), ""+profileFragment.getId());		
	}
	
	class FollowingHandleListener implements DialogInterface.OnClickListener{
    private long followedUserId;

    public FollowingHandleListener(long followedUserId){
      this.followedUserId = followedUserId;
    }

    @Override
	public void onClick(DialogInterface dialog, int which)
    {
      switch (which)
      {
      case 0:
        Intent intent = new Intent();
        intent.putExtra("userId", this.followedUserId);
        intent.setClass(getActivity(), ChatActivity.class);
        startActivity(intent);
        return;
      case 1:
    	  AsyncHttpClient client=new AsyncHttpClient();
			client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
      client.delete(Constance.SERVER_URL+"following/"+followedUserId, new AsyncHttpResponseHandler(){
        @Override
		public void onSuccess(int statusCode,Header[] headers,byte[] content){
          for(User u:users){
        	  if(u.getUserId()==followedUserId){
        		  users.remove(u);
        		  break;
        	  }
          }
          app.refreshUser();
          adapter.notifyDataSetChanged();
        }
          @Override
          public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
              Log.d("-UserFragment","deleteFollowing:failure");
              Log.getStackTraceString(error);
          }
      });
      break;
      }
    }
  }
	
	class UserAdapter extends BaseAdapter{
		int count;
	    UserAdapter(){}

	    @Override
		public int getCount(){
	      return users == null?0:users.size();
	    }

	    @Override
		public Object getItem(int paramInt)
	    {
	      return users.get(paramInt);
	    }

	    @Override
		public long getItemId(int paramInt)
	    {
	      return ((User)getItem(paramInt)).getUserId();
	    }

	    @Override
		public View getView(int position, View convertView, ViewGroup parent){
	    	User user=users.get(position);
//	    	View oldView=memViews.get(user.getUserId());
//	    	if(oldView!=null)return oldView;
	      View v = UserFragment.this.getActivity().getLayoutInflater().inflate(R.layout.list_user, null);
	      ResizedImageView iv_figure = (ResizedImageView)v.findViewById(R.id.user_iv_figure);
	      TextView tv_userName = (TextView)v.findViewById(R.id.user_tv_userName);
	      TextView tv_distance = (TextView)v.findViewById(R.id.user_tv_distance);
	      TextView tv_age = (TextView)v.findViewById(R.id.user_tv_age);
	      TextView tv_about = (TextView)v.findViewById(R.id.user_tv_about);
		  TextView tv_level = (TextView)v.findViewById(R.id.user_tv_level);
		  tv_level.setText(getResources().getString(R.string.profile_tv_level_text,Constance.getLevel(user)));
	      tv_userName.setText(user.getUserName());
//	      float[] distanceResults=new float[1];
//	      float lat=app.getLocationHelper().getLatitude();
//	      float lon=app.getLocationHelper().getLongitude();
//			if(!(lat==0 && lon==0) && !(user.getLongitude()==0 && user.getLatitude()==0)){
//			Location.distanceBetween(user.getLatitude(), user.getLongitude(),lat, lon, distanceResults);
//			String distanceStr;
//			if(distanceResults[0]>=1000){
//				distanceStr=""+(int)distanceResults[0]/1000+"km";
//			}else{
//				distanceStr=""+(int)distanceResults[0]+"m";
//			}
//			if(distanceResults[0]<=10000){
//				tv_distance.setTextColor(getResources().getColor(R.color.emphasize_dark));
//			}else if(distanceResults[0]<=100000){
//				tv_distance.setTextColor(getResources().getColor(R.color.emphasize_medium));
//			}
//			tv_distance.setText(distanceStr);
			app.getLocationHelper().showCityInTextView(user.getLatitude(), user.getLongitude(), tv_distance);
////			new CityTask(getActivity(),tv_distance,user).executeOnExecutor(Executors.newSingleThreadExecutor(),(Void)null);
//			}else{
//				tv_distance.setVisibility(View.GONE);
//			}
	      tv_age.setText(""+DateHelper.getAge(user.getBirthday()));
	      if (user.getSex() == Constance.SEX.MALE)
	    	  tv_age.setBackgroundResource(R.drawable.shape_rectangle_radius_blue);
	      if (users.get(position).getUserType() == Constance.USERTYPE.STYLIST){
	          tv_level.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_scissors_16, 0,0 , 0);
	          tv_level.setCompoundDrawablePadding(5);
	      }
	      String about=user.getAbout();
	      if(about==null || "".equals(about)){
	    	  tv_about.setVisibility(View.GONE);
	      }else{
	    	  tv_about.setText(user.getAbout());
	      }
	      ImageHelper.loadImg(iv_figure, user.getFigure(), 0,false);
//	      memViews.put(user.getUserId(), v);
	      return v;
	    }
	  }
//	class CityTask extends AsyncTask<Void, Void, String>{
//		TextView tv_distance;
//		Context context;
//		User user;
//		public CityTask(Context context,TextView tv_distance,User user){
//			this.context=context;
//			this.tv_distance=tv_distance;
//			this.user=user;
//		}
//		@Override
//		protected String doInBackground(Void... params) {
////			Log.d("-UserFragment", "CityTask:"+user.getLatitude()+","+user.getLongitude());
//			String cityName=LocationHelper.getCity(user.getLatitude(), user.getLongitude());
//			return cityName;
//		}
//
//		@Override
//		protected void onPostExecute(String cityName) {
//			if(tv_distance!=null && cityName!=null && cityName.length()>0){
//				tv_distance.setText(cityName+tv_distance.getText());
//				try {
//					this.finalize();
//				} catch (Throwable e) {
//					Log.e("-UserFragment", Log.getStackTraceString(e));
//				}
//			}
//		}
//		}

}
