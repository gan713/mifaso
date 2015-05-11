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


//import cn.domob.android.ads.DomobAdEventListener;
//import cn.domob.android.ads.DomobAdManager.ErrorCode;
//import cn.domob.android.ads.DomobAdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mifashow.PostingView.OnHandleListener;
import com.mifashow.data.Constance;
import com.mifashow.domain.Posting;
import com.mifashow.tool.AlertHelper;
import com.mifashow.ui.BorderListView;
import com.qq.e.ads.AdListener;
import com.qq.e.ads.AdRequest;
import com.qq.e.ads.AdSize;
import com.qq.e.ads.AdView;

import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PostingFragment extends DialogFragment {
	public static enum SOURCETYPE{NAVIGATION,ID,CREATER,MARKINGUSER,SIMILAR}
	private static final int BOOKINGSET = 1; 
	View view;
	  App app;
	  boolean isEnd;
	  LinearLayout lo_info;
	  TextView tv_info;
	  int navigationItemSelected;
	  private List<Posting> postings;
	  SOURCETYPE sourceType;
	  long sourceId;
//多盟
//	  DomobAdView adView;
//广点通
	  AdView adView;
	  BorderListView lv;
	  PostingAdapter adapter;
	  LoadPostingTask loadPostingTask;
	  private Handler handler = new Handler();
	    private Runnable runnable = new Runnable() {
	        @Override
			public void run() {
	        	try{
	        		if(loadPostingTask!=null && loadPostingTask.getStatus()==Status.RUNNING){
	        			loadPostingTask.cancel(true);
	        		}
	        		loadPostingTask=new LoadPostingTask();
	        		loadPostingTask.execute(0L,(postings==null || postings.size()==0)?0L:postings.get(0).getPostingId());
	        	}catch(Exception e){
	        		Log.e("-PostingFragment",Log.getStackTraceString(e));
	        	}
	        	handler.postDelayed(this, 1000 * 120);// ���120��
	        }
	    }; 
	@Override
	public void onResume(){
		super.onResume();
		if(postings!=null && postings.size()!=lv.getChildCount())refreshUI();
		
	}
	@Override  
    public void onDestroy(){
		Log.d("-PostingFragment", "onDestroy");
		if(loadPostingTask!=null && loadPostingTask.getStatus()==Status.RUNNING){
			loadPostingTask.cancel(true);
		}
		handler.removeCallbacks(runnable); 
        super.onDestroy();
	}
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar);
		Bundle bundle=getArguments();
		if(bundle!=null){
		sourceType=(SOURCETYPE) bundle.getSerializable("sourceType");
		sourceId=bundle.getLong("sourceId");
		}
	    app = ((App)getActivity().getApplication());
	    if (this.sourceType == null){
	      this.sourceType = SOURCETYPE.NAVIGATION;
	      this.navigationItemSelected = -1;
	      handler.postDelayed(runnable, 1000*120);
	    }
	    isEnd=false;
//多盟
//	    adView=new DomobAdView(getActivity(),"56OJwnp4uNLDCtnLJb","16TLuOGlApfgYNUfZglkzgos",DomobAdView.INLINE_SIZE_FLEXIBLE);
//	    adView.setKeyword("����,����,��ױ,����,hair,woman,fashion");
//	    adView.setUserGender("female");
//	    adView.setBackgroundResource(R.drawable.ic_320_50);
//	    adView.setAdEventListener(new DomobAdEventListener(){
//
//			@Override
//			public void onDomobAdClicked(DomobAdView arg0) {
//				Log.d("-PostingFragment", "onDomobAdClicked");
//				
//			}
//
//			@Override
//			public void onDomobAdOverlayDismissed(DomobAdView arg0) {
//				Log.d("-PostingFragment", "onDomobAdOverlayDismissed");
//			}
//
//			@Override
//			public void onDomobAdOverlayPresented(DomobAdView arg0) {
//				Log.d("-PostingFragment", "onDomobAdOverlayPresented");
//			}
//
//			@Override
//			public Context onDomobAdRequiresCurrentContext() {
//				return getActivity();
//			}
//
//			@Override
//			public void onDomobAdReturned(DomobAdView arg0) {
//				Log.d("-PostingFragment", "onDomobAdReturned");
//			}
//
//			@Override
//			public void onDomobLeaveApplication(DomobAdView arg0) {
//				Log.d("-PostingFragment", "onDomobLeaveApplication");
//			}
//
//			@Override
//			public void onDomobAdFailed(DomobAdView arg0, ErrorCode arg1) {
//				Log.d("-PostingFragment", "onDomobAdFailed");
//				
//			}
//	    });

	}

	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) { 
		Log.d("-PostingFragment", "onCreateView");
//多盟
//		adView.requestRefreshAd();
		if(view==null){
			view=inflater.inflate(R.layout.fragment_posting, null);
			lo_info=(LinearLayout) view.findViewById(R.id.posting_lo_info);
			tv_info= (TextView) view.findViewById(R.id.posting_tv_info);
			lv = ((BorderListView)view.findViewById(R.id.posting_lv));
//广点通
			adView = new AdView(getActivity(), AdSize.BANNER, "1101262522", "9079537216003289103");
		    adView.setBackgroundResource(R.drawable.ic_320_50);
		    AdRequest adRequest = new AdRequest();
		    adRequest.setShowCloseBtn(false);
		    adView.fetchAd(adRequest);
		    adView.setAdListener(new AdListener() {
		      @Override
		      public void onNoAd() {}
		      @Override
		      public void onBannerClosed() {}
		      @Override
		      public void onAdReceiv() {}
		    });
		    FrameLayout lo_ad=new FrameLayout(getActivity());
		    lo_ad.addView(adView);
		    lv.addHeaderView(lo_ad);

		    adapter = new PostingAdapter();
			lv.setAdapter(adapter);
			lv.setOnBorderListener(new BorderListView.OnBorderListener(){
				@Override
				public void onBottom(){
					Log.d("-PostingFragment", "onBottom");
					if (postings!=null && postings.size()!=0 && !isEnd){
						if(loadPostingTask!=null && loadPostingTask.getStatus()==Status.RUNNING){
		        			loadPostingTask.cancel(true);
		        		}
		        		loadPostingTask=new LoadPostingTask();
						loadPostingTask.execute(postings.get(postings.size()-1).getPostingId(),0L);
					}
				}

				@Override
				public void onRoll() {
					// TODO Auto-generated method stub
					
				}
			});
		}else{
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null) {
				parent.removeView(view);
			}
		}
		boolean needToLoad=true;
		switch(sourceType){
		case NAVIGATION:
			int selectedNavigationIndex=getActivity().getActionBar().getSelectedNavigationIndex();
			if(selectedNavigationIndex!=navigationItemSelected){
				navigationItemSelected=selectedNavigationIndex;
				isEnd=false;
				float latitude=app.getLatitude();
				float longitude=app.getLongitude();
				switch(navigationItemSelected){
				case 0:
					postings=app.getPostings();
		    		break;
		    	case 1:
		    		postings=app.getNear100kmPostings();
		    		if(latitude==0 && longitude==0)needToLoad=false;
		    		break;
		    	case 2:
		    		postings=app.getNear10kmPostings();
		    		if(latitude==0 && longitude==0)needToLoad=false;
		    		break;
		    	case 3:
		    		postings=app.getFollowingPostings();
		    		break;
		    	case 4:
		    		postings=app.getSuitablePostings();
		    		break;
		    	default:
		    		postings=null;
		    		break;
				}
			}else{
				needToLoad=false;
			}
			break;
		case CREATER:
			if(sourceId==app.getLoginUser().getUserId()){
    			postings=app.getMyPostings();
			}
			break;
		case MARKINGUSER:
			if(sourceId==app.getLoginUser().getUserId()){
	    	    postings=app.getMarkingPostings();
			}
			break;
		case ID:
			break;
		case SIMILAR:
			break;
		}
		if(postings==null||postings.size()==0){
        	tv_info.setText(R.string.info_null);
        	lo_info.setVisibility(View.VISIBLE);
        }else{
        	lo_info.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
		if(needToLoad){
			if(loadPostingTask!=null && loadPostingTask.getStatus()==Status.RUNNING){
    			loadPostingTask.cancel(true);
    		}
    		loadPostingTask=new LoadPostingTask();
			loadPostingTask.execute(0L,0L);
		}
        return view;        
    }
	class PostingAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return postings==null?0:postings.size();
		}

		@Override
		public Object getItem(int arg0) {
			return postings.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return postings.get(arg0).getPostingId();
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			PostingView pv = new PostingView(getActivity(), (Posting)getItem(arg0));
			pv.setOnHandleListener(new HandleListener((Posting)getItem(arg0),pv));
			return pv;
		}
		
	}
	class LoadPostingTask extends AsyncTask<Long,Void,String>{
		
		long maxId,sinceId;
		ArrayList<NameValuePair> params;
		private String parseSql(long maxId,long sinceId){
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			String url = null;
			float latitude=app.getLatitude();
			float longitude=app.getLongitude();
			switch(sourceType){
			case NAVIGATION:
				switch(navigationItemSelected){
					case 0:
						url = Constance.SERVER_URL+"posting";
			    		break;
			    	case 1:
			    		url = Constance.SERVER_URL+"posting/100km";
			    		params.add(new BasicNameValuePair("latitude", ""+latitude));
			    		params.add(new BasicNameValuePair("longitude", ""+longitude));
			    		break;
			    	case 2:
			    		url = Constance.SERVER_URL+"posting/10km";
			    		params.add(new BasicNameValuePair("latitude", ""+latitude));
			    		params.add(new BasicNameValuePair("longitude", ""+longitude));
			    		break;
			    	case 3:
			    		url = Constance.SERVER_URL+"posting/followingUser/" + app.getLoginUser().getUserId();
			    		break;
			    	case 4:
			    		url = Constance.SERVER_URL+"posting/suitableUser/" + app.getLoginUser().getUserId();
			    		break;
			    	default:
			    		break;
				}
				break;
			case ID:
				url = Constance.SERVER_URL+"posting/" + sourceId;
				break;
			case SIMILAR:
				url=Constance.SERVER_URL+"posting/similar/"+sourceId;
				break;
			case CREATER:
				url = Constance.SERVER_URL+"posting/creater/" + sourceId;
				break;
			case MARKINGUSER:
				url = Constance.SERVER_URL+"posting/markingUser/" + sourceId;
				break;
			}
			if(maxId>0){
				params.add(new BasicNameValuePair("maxId", ""+maxId));
			}
			if(sinceId>0){
				params.add(new BasicNameValuePair("sinceId", ""+sinceId));
			}
			if(params.size()>0)
			try {
				url+="?"+EntityUtils.toString(new UrlEncodedFormEntity(params,"utf-8"));
			} catch (Exception e) {
				Log.e("-postingFragment",Log.getStackTraceString(e));
			}
			return url;
			
		}
		@Override
		protected void onPreExecute(){
		    tv_info.setText(R.string.status_loading);
		    lo_info.setVisibility(View.VISIBLE);
		}
		@Override
		protected String doInBackground(Long... arg0) {
			HttpGet get = null;
			AndroidHttpClient client = null;
			String content = null;
			try{
			maxId=arg0[0];
			sinceId=arg0[1];
			String sql=parseSql(maxId,sinceId);
			if(postings==null)postings=new ArrayList<Posting>();
			Log.d("-PostingFragment", "update:sql="+sql);
			client=AndroidHttpClient.newInstance("loadPosting");
			get = new HttpGet(sql);
			HttpParams httpParameters = new BasicHttpParams();
		    HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
		    HttpConnectionParams.setSoTimeout(httpParameters, 7000);
			get.setParams(httpParameters);
			if(app.getLoginUser().getUserId()!=0)
			get.addHeader(BasicScheme.authenticate(
					 new UsernamePasswordCredentials(app.getLoginUser().getSign(), app.getLoginUser().getPassword()),
					 "UTF-8", false));
			HttpResponse res = client.execute(get);
			if (res!=null && res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			    	content=EntityUtils.toString(res.getEntity());
			    }
	  		}catch(Exception e){
	  			Log.e("-postingFragment",Log.getStackTraceString(e));
	  		}finally{
	  			if(get!=null)get.abort();
	  			if(client!=null)client.close();
	  		}
			if(this.isCancelled() || content==null ||"".equals(content)){
				return null;
			}
			return content;
		}
		@Override
		protected void onPostExecute(String content) {
			if (content!=null) {
	  			Log.d("-PostingFragment", "update:success");
	  			List<Posting> ps=new ArrayList<Posting>();
	        	if(sourceType == SOURCETYPE.ID){
	        		Posting p=new Gson().fromJson(content, Posting.class);
	        		isEnd = true;
	        		ps.add(p);
	        	}else{
	                ps = new Gson().fromJson(content, new TypeToken<List<Posting>>(){}.getType());
	            }
	        	if(maxId>0){
	        		if(ps == null || ps.size() < 20){
		                isEnd = true;
		            }
	        	}
	          if (ps != null && ps.size()>0){
	        	  if(maxId==0 && sinceId==0){
		        		postings=new ArrayList<Posting>();
		        	}
	        	  postings.addAll(maxId==0?0:postings.size(),ps);
		          switch(sourceType){
		            case NAVIGATION:
		            	switch (navigationItemSelected){
			            case 0:
			            	app.setPostings(postings);
			                break;
			            case 1:
			                app.setNear100kmPostings(postings);
			                break;
			            case 2:
			                app.setNear10kmPostings(postings);
			                break;
			            case 3:
			                app.setFollowingPostings(postings);
			                break; 
			            case 4:
			                app.setSuitablePostings(postings);
			                break; 
			            }
		            	break;
		            case MARKINGUSER:
		            	 app.setMarkingPostings(postings);
		            	 break;
		            case CREATER:
		    			if(sourceId==app.getLoginUser().getUserId()){
		        			app.setMyPostings(postings);
		    			}
		    			break;
					default:
						break;
		            }
		      }
        	  refreshUI();
	  		}
    		Log.d("-PostingFragment", "update:finish");
		}
	}
	private void refreshUI(){
		Log.d("-PostingFragment", "refreshUI");
//		lv.smoothScrollToPosition(0);
		if(postings==null)postings=new ArrayList<Posting>();
		if(postings.size()==0){
			lo_info.setVisibility(View.VISIBLE);
			lv.setBackgroundResource(android.R.color.transparent);
		}else{
			lo_info.setVisibility(View.GONE);
			lv.setBackgroundResource(R.drawable.shape_background);
		}
		adapter.notifyDataSetChanged();
	}
	class HandleListener implements OnHandleListener{
		Posting posting;
		PostingView postingView;
		public HandleListener(Posting posting,PostingView postingView){
			this.posting=posting;
			this.postingView=postingView;
		}

		@Override
		public void onDelete() {
			AlertHelper.showToast(getActivity(), R.string.info_deleteDone);
			postings.remove(posting);
			refreshUI();
			app.refreshUser();
		}

		@Override
		public void onDismark() {
			if(sourceType==SOURCETYPE.MARKINGUSER){
				postings.remove(posting);
				refreshUI();
			}
			app.refreshUser(); 
			
		}

		@Override
		public void onMark() {
			app.refreshUser();
		}

		@Override
		public void onBookingSet() {
			Intent intent = new Intent();
			intent.putExtra("posting", posting);
			intent.setClass(getActivity(), BookingsetActivity.class);
		      startActivityForResult(intent, BOOKINGSET);
			
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(requestCode==BOOKINGSET && intent!=null){
			Posting p=(Posting) intent.getSerializableExtra("posting");
			if(p!=null && postings!=null){
				for(int i=0;i<postings.size();i++){
	        		  if(postings.get(i).getPostingId()==p.getPostingId()){
	        			  postings.remove(i);
	        			  postings.add(i, p);
	        			  refreshUI();
	        			  break;
	        		  }
	        	  }
			}
		}
	}
}
