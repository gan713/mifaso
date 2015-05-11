package com.mifashow;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mifashow.data.Constance;
import com.mifashow.data.Constance.MESSAGETYPE;
import com.mifashow.domain.Client;
import com.mifashow.domain.Message;
import com.mifashow.domain.MessageBox;
import com.mifashow.tool.ImageHelper;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class Service extends android.app.Service {
	App app;
	int messageWaitingSecond=0;
	OnServiceListener listener;
	private NotificationManager notificationManager;
	DownloadCompleteReceiver downloadCompleteReceiver;
	private final IBinder binder = new Binder();
	Thread messageThread;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {  
			listener.onNewMessages(msg.what);
			super.handleMessage(msg);
		}  

	};
    private Runnable messageRunnable = new Runnable() {
        @Override
		public void run() {
        	Log.d("-service","messageRunnable:run");
        	AndroidHttpClient client=AndroidHttpClient.newInstance("checkMessage");
        	if(app.getLoginUser()!=null && app.getLoginUser().getUserId()!=0){
        	try {
    			String sql=Constance.SERVER_URL+"message";
    			HttpGet request = new HttpGet(sql);
    			request.addHeader(BasicScheme.authenticate(
    					 new UsernamePasswordCredentials(app.getLoginUser().getSign(), app.getLoginUser().getPassword()),
    					 "UTF-8", false));
    			String response=client.execute(request,new BasicResponseHandler());
    				List<Message> newMessages=new Gson().fromJson(response, new TypeToken<List<Message>>() {}.getType());
    				Log.d("-service","gotMessages:"+newMessages.size());
    				if(newMessages!=null && newMessages.size()>0){
    					for (Message m : newMessages) {
    						long userId=m.getFromUserId();
    						if(userId==app.getLoginUser().getUserId()){
    							userId=m.getToUserId();
    						}
    					    if (!app.getMessageBoxes().containsKey(userId)) {
    					    	MessageBox mb = new MessageBox();
    					    	mb.addMessage(m, true);
    					    	app.getMessageBoxes().put(userId, mb);
    					    } else {
    					    	app.getMessageBoxes().get(userId).addMessage(m, true);
    					    }
    				     }
    					app.setMessageBoxes(app.getMessageBoxes());
    				     if(listener==null){
    				    	 Message lastMessage=newMessages.get(0);
//    				    	 Bitmap figure=BitmapFactory.decodeFile(ImageFetcher.downloadBitmap(getBaseContext(), lastMessage.getFromUserFigure()).getAbsolutePath());
    				    	 Bitmap figure=ImageHelper.loadBitmap(getBaseContext(), lastMessage.getFromUserFigure(), 61);
    				    	 String content=lastMessage.getFromUserName()+":"+lastMessage.getContent();
    				    	 if(lastMessage.getMessageType()==MESSAGETYPE.BOOK){
    				    		 SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd,E,HH:mm",Locale.getDefault());
    				    		 try{
    				 				String[] objectValues=lastMessage.getObjectValues().split(",");
    				 				content=getResources().getString(R.string.messagebox_tv_content_book, format.format(new Date(objectValues[5])),objectValues[4]);
    				 				}catch(Exception e){
    				 					Log.e("-Service",Log.getStackTraceString(e));
    				 				}
    					     }else if(lastMessage.getMessageType()==MESSAGETYPE.COMMIT){
    						     content=getResources().getString(R.string.messagebox_tv_content_commit, lastMessage.getFromUserName());
    					     }else if(lastMessage.getMessageType()==MESSAGETYPE.COMMENT){
    						     content=getResources().getString(R.string.messagebox_tv_content_comment, lastMessage.getFromUserName());
    					     }else if(lastMessage.getMessageType()==MESSAGETYPE.EXPLAIN){
    					         content=getResources().getString(R.string.messagebox_tv_content_explain, lastMessage.getFromUserName());
    					     }else if(lastMessage.getMessageType()==MESSAGETYPE.ARBITRATE){
    					    	 content=getResources().getString(R.string.messagebox_tv_content_arbitrate, lastMessage.getFromUserName());
    						 }else if(lastMessage.getMessageType()==MESSAGETYPE.FOLLOW){
    					         content=getResources().getString(R.string.messagebox_tv_content_follow, lastMessage.getFromUserName());
    					     }else if(lastMessage.getMessageType()==MESSAGETYPE.REPORT){
    					    	 content=getResources().getString(R.string.messagebox_tv_content_report, lastMessage.getFromUserName(),getResources().getStringArray(R.array.enum_reportType)[Constance.REPORTTYPE.valueOf(lastMessage.getContent()).ordinal()]);
    						 }else if(lastMessage.getMessageType()==MESSAGETYPE.CANCEL){
    							 content=getResources().getString(R.string.messagebox_tv_content_cancel, lastMessage.getFromUserName());
    						 }else if(lastMessage.getMessageType()==MESSAGETYPE.INVITE){
    							 content=getResources().getString(R.string.messagebox_tv_content_invite, lastMessage.getObjectValues());
    						 }
    					     @SuppressWarnings("deprecation")
							Notification notification = new Notification.Builder(getApplicationContext())
    						 .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(),MainActivity.class), 0))
    				         .setContentTitle(getResources().getString(R.string.notification_title, newMessages.size()))
    				         .setContentText(content)
    				         .setLargeIcon(figure)
    				         .setSmallIcon(R.drawable.icon_bow)
    				         .setAutoCancel(true)
    				         .setSound(Uri.parse("android.resource://" + getPackageName() + "/" +R.raw.mifashow))
//    				         .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
    				         .getNotification();
    				    	 notificationManager.notify(1, notification);
//    						 Log.d("-Service","android.resource://" + getPackageName() + "/" +R.raw.mifashow);
    					}else{
    					     handler.sendEmptyMessage(newMessages.size());
    				    }
    			    }
    			} catch (Exception e) {
    				Log.e("-service",Log.getStackTraceString(e));
    			}finally {
    				client.close();
    	        }
        	}
        		try {
        			if(listener==null){
        				Log.d("-service","messageRunnable:sleep10min");
        				messageWaitingSecond=300;
        				Thread.sleep(1000*60*10);
        			}else{
        				Log.d("-service","messageRunnable:sleep1min");
        				messageWaitingSecond=30;
        				Thread.sleep(1000*60*1);
        			}
				} catch (InterruptedException e) {
					Log.d("-service","messageRunnable:Interrupted");
				}finally{
					messageWaitingSecond=0;
					this.run();
				}
        }
    };
    public void setListener(OnServiceListener listener){
    	this.listener=listener;
    	if(messageThread==null && listener!=null){
			messageThread=new Thread(messageRunnable);
			messageThread.start();
		}else if(messageWaitingSecond>30 && listener!=null){
			messageThread.interrupt();
		}
    }

	public class Binder extends android.os.Binder {
		Service getService() {
			return Service.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d("-service","onBind");
		return binder;
	}
	@Override
	public boolean onUnbind(Intent arg0) {
		Log.d("-service","onunBind");
		return false;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		app=(App) getApplication();
		Log.d("-service","onCreate");
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		new Thread(checkVersionRunnable).start();
	}
	private Runnable checkVersionRunnable=new Runnable(){
		@Override
		public void run() {
			Log.d("-service","checkVersionRunnable:run");
		  	AndroidHttpClient client=AndroidHttpClient.newInstance("checkVersion");
		  	try{
		  		SharedPreferences updatableVersionSharedPreferences = getSharedPreferences("update", 0);
				int downloadedVersionCode=updatableVersionSharedPreferences.getInt("versionCode", 0);
				long downloadedId=updatableVersionSharedPreferences.getLong("downloadId", 0);
				PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
	  			int currentVersion = info.versionCode;
		  		HttpGet get = new HttpGet(Constance.SERVER_URL+"client/android");
		  		HttpResponse res = client.execute(get);
		  		if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
		  			Client c=new Gson().fromJson(EntityUtils.toString(res.getEntity()),Client.class);
		  			int maxVersion=c.getVersionCode();
		  			if(maxVersion==downloadedVersionCode && maxVersion>currentVersion){
		  				Log.d("-service","DownloadUpdateTask:didDownload");
		  				installUpdate(c,downloadedId);
		  			}else if(maxVersion>currentVersion){
		  				Log.d("-service","DownloadUpdateTask:doDownload");
		  				DownloadManager downloadManager=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
		  				DownloadManager.Request downloadRequest=new DownloadManager.Request (Uri.parse(c.getUrl()));
		  				downloadRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
		  				downloadRequest.setDestinationInExternalFilesDir(getApplicationContext(), null, c.getUrl().substring(c.getUrl().lastIndexOf('/')));
		  				long downloadId=downloadManager.enqueue(downloadRequest);
		  				downloadCompleteReceiver=new DownloadCompleteReceiver(c,downloadId);
		  				registerReceiver(downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		  			}
		  		}
		  	  }catch (Exception e) {
		  		Log.e("-service",Log.getStackTraceString(e));
			}finally {
				client.close();
	        }
		  	try {
				Thread.sleep(24*60*60*1000);
				this.run();
			} catch (InterruptedException e) {
				Log.e("-service",Log.getStackTraceString(e));
			}
		}
	};

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("-service","onStart");
		if(messageThread==null){
			messageThread=new Thread(messageRunnable);
			messageThread.start();
		}
	}

	@Override
	public void onDestroy() {
		Log.d("-service","onDestroy");
		if(downloadCompleteReceiver!=null)unregisterReceiver(downloadCompleteReceiver);
		super.onDestroy();
	}
	
	public static abstract interface OnServiceListener {
		public abstract void onNewMessages(int num);
	}
	private void installUpdate(Client c,long downloadId){
		DownloadManager downloadManager=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(downloadManager.getUriForDownloadedFile(downloadId), "application/vnd.android.package-archive"); 
		Notification notification = new Notification.Builder(getApplicationContext())
				 .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, i, Intent.FLAG_ACTIVITY_NEW_TASK))
		         .setContentTitle(getResources().getString(R.string.dialog_update_title))
		         .setContentText(getResources().getString(R.string.dialog_update_message,c.getVersionName()))
		         .setSmallIcon(R.drawable.icon_bow)
		         .setAutoCancel(false)
		         .setSound(Uri.parse("android.resource://" + getPackageName() + "/" +R.raw.mifashow))
		         .getNotification();
				notificationManager.notify(2, notification);
	}
	class DownloadCompleteReceiver extends BroadcastReceiver {
		Client c;
		long downloadId;
		public DownloadCompleteReceiver(Client c,long downloadId){
			this.c=c;
			this.downloadId=downloadId;
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){
				Log.d("-service","DownloadCompleteReceiver:downloadComplete");
				SharedPreferences updatableVersionSharedPreferences = getSharedPreferences("update", 0);
				Editor editor=updatableVersionSharedPreferences.edit();
				editor.putInt("versionCode", c.getVersionCode());
				editor.putLong("downloadId", downloadId).commit();
				editor.commit();
				installUpdate(c,downloadId);
				}
			} 
	}


	
	
	

}
