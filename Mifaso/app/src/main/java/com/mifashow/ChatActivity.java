package com.mifashow;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mifashow.Service.OnServiceListener;
import com.mifashow.data.Constance;
import com.mifashow.data.Constance.MESSAGETYPE;
import com.mifashow.data.Constance.USERTYPE;
import com.mifashow.domain.Message;
import com.mifashow.domain.MessageBox;
import com.mifashow.domain.User;
import com.mifashow.tool.AlertHelper;
import com.mifashow.tool.ImageHelper;
import com.mifashow.ui.BorderListView;
import com.mifashow.ui.RenderView;
import com.mifashow.ui.ResizedImageView;
import com.mifashow.ui.BorderListView.OnBorderListener;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.DataSetObserver;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ChatActivity extends FragmentActivity {
	App app;
	long userId;
	User user;
	Service service;
	BorderListView lv;
	LinearLayout lo_noChat;
	EditText et_send;
	ChatAdapter adapter;
	String name;
	String figure;
	ResizedImageView iv_figure;
	ResizedImageView iv_send;
	TextView tv_userName,tv_address,tv_hair,tv_face;
	RelativeLayout lo_actionBar,lo_container;
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unbindService(serviceConnection);
	}
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("-chatActivity","onResume");
		Intent intent=new Intent(ChatActivity.this,Service.class);
		startService(intent);
		bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("-chatFragmet","onCreate");
		getActionBar().hide();
		setContentView(R.layout.activity_chat);
		lo_noChat=(LinearLayout) findViewById(R.id.chat_lo_noChat);
		lo_container=(RelativeLayout) findViewById(R.id.chat_lo_container);
		lo_actionBar=(RelativeLayout) findViewById(R.id.chat_lo_actionBar);
		iv_figure=(ResizedImageView) findViewById(R.id.chat_iv_figure);
		iv_send=(ResizedImageView) findViewById(R.id.chat_iv_send);
		tv_userName=(TextView) findViewById(R.id.chat_tv_userName);
		tv_address=(TextView) findViewById(R.id.chat_tv_address);
		tv_hair=(TextView) findViewById(R.id.chat_tv_hair);
		tv_face=(TextView) findViewById(R.id.chat_tv_face);
		app=(App)getApplication();
		userId = getIntent().getLongExtra("userId", 0L);
		lv=(BorderListView) findViewById(R.id.chat_lv);
		adapter=new ChatAdapter();
		adapter.registerDataSetObserver(new DataSetObserver(){
			@Override
			public void onChanged (){
				super.onChanged();
				Log.d("-chatActivity","adapter:onChanged:"+adapter.getCount());
//				lv.invalidate();
//				lv.setBottom(lo_actionBar.getTop());
				lv.setSelection(adapter.getCount()-1);
			}
		});
		new UserTask().execute((Void)null);
		lv.setAdapter(adapter);
		Log.d("-chatFragmet","onCreate:end");
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.chat, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem)
	  {
	    switch (menuItem.getItemId())
	    {
	    case android.R.id.home:
	    	ProfileFragment profileFragment = new ProfileFragment();
			Bundle bundle = new Bundle();
		      bundle.putLong("userId",userId);
		      profileFragment.setArguments(bundle);
	      profileFragment.show(getSupportFragmentManager(), ""+profileFragment.getId());
	      break;
	    }
	    return true;
	  }
	public void rebuild(MenuItem menuItem){
		AlertHelper.showAlert(this, getResources().getString(R.string.dialog_rebuildMessageBox_title), getResources().getString(R.string.dialog_rebuildMessageBox_message), getResources().getString(R.string.action_ok), new DialogInterface.OnClickListener() {
            @Override
			public void onClick(DialogInterface dialog, int id) {
            	AsyncHttpClient client =new AsyncHttpClient();
            	client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
            	RequestParams params=new RequestParams();
            	params.put("fromUserId", ""+userId);
            	client.get(Constance.SERVER_URL+"message",params,new AsyncHttpResponseHandler(){
    	            @Override
    				public void onSuccess(int statusCode,Header[] headers,byte[] content){
    	              ArrayList<Message> ms = new Gson().fromJson(new String(content), new TypeToken<ArrayList<Message>>() {}.getType());
    	              if(ms.size()>0){
    	            	  MessageBox messageBox=new MessageBox();
    	            	  messageBox.setMessages(ms);
    	            	  app.getMessageBoxes().put(userId, messageBox);
    	            	  app.setMessageBoxes(app.getMessageBoxes());
    	            	  adapter.notifyDataSetChanged();
    	              }
    	            }
                    @Override
                    public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
                        Log.d("-ChatActivity","rebuild:failure");
                        Log.getStackTraceString(error);
                    }
            	});
            }
        }, getResources().getString(R.string.action_cancel), 
        new DialogInterface.OnClickListener() {
            @Override
			public void onClick(DialogInterface dialog, int id) {}
        });
	}
    public void clearUp(MenuItem menuItem){
    	AlertHelper.showAlert(this, getResources().getString(R.string.dialog_clearMessageBox_title), getResources().getString(R.string.dialog_clearMessageBox_message), getResources().getString(R.string.action_ok), new DialogInterface.OnClickListener() {
            @Override
			public void onClick(DialogInterface dialog, int id) {
            	app.getMessageBoxes().remove(userId);
                app.setMessageBoxes(app.getMessageBoxes());
                adapter.notifyDataSetChanged();
            }
        }, getResources().getString(R.string.action_cancel), 
        new DialogInterface.OnClickListener() {
            @Override
			public void onClick(DialogInterface dialog, int id) {}
        });
	}
    public void clearHandled(MenuItem menuItem){
    	AlertHelper.showAlert(this, getResources().getString(R.string.dialog_clearHandledMessageBox_title), getResources().getString(R.string.dialog_clearHandledMessageBox_message), getResources().getString(R.string.action_ok), new DialogInterface.OnClickListener() {
            @Override
			public void onClick(DialogInterface dialog, int id) {
            	if(app.getMessageBoxes().containsKey(userId)){
            		ArrayList<Message> ms=app.getMessageBoxes().get(userId).getHandleable();
            		if(ms.size()==0){
            			app.getMessageBoxes().remove(userId);
            		}else{
            			app.getMessageBoxes().get(userId).setMessages(ms);
            		}
            		app.setMessageBoxes(app.getMessageBoxes());
                    adapter.notifyDataSetChanged();
            	}
            }
        }, getResources().getString(R.string.action_cancel), 
        new DialogInterface.OnClickListener() {
            @Override
			public void onClick(DialogInterface dialog, int id) {}
        });
	}
	private List<Message> getMessages(){
		if(app.getMessageBoxes().get(userId)==null){
			return new ArrayList<Message>();
		}
		return app.getMessageBoxes().get(userId).getMessages();
	}
	class ChatAdapter extends BaseAdapter{
		int count;

		@Override
		public int getCount() {
			count=getMessages().size();
			if(count==0){
				lo_noChat.setVisibility(View.VISIBLE);
			}else{
				lo_noChat.setVisibility(View.GONE);
			}
			return count;
		}
		@Override  
    	public boolean isEnabled(int position) {   
    	   return false;   
    	}

		@Override
		public Object getItem(int arg0) {
			return getMessages().get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return getMessages().get(arg0).getMessageId();
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			final Message m=getMessages().get(arg0);
			boolean isRight=m.getFromUserId()==app.getLoginUser().getUserId();
			View view;
			if(m.getMessageType()==MESSAGETYPE.BOOK || m.getMessageType()==MESSAGETYPE.REPORT || m.getMessageType()==MESSAGETYPE.ARBITRATE || m.getMessageType()==MESSAGETYPE.INVITE || m.getMessageType()==MESSAGETYPE.JOIN){
				view=getLayoutInflater().inflate(isRight?R.layout.list_chat_rv_right:R.layout.list_chat_rv_left,null);
				RenderView renderView = (RenderView)view.findViewById(R.id.chat_rv);
				Button bt_handle=(Button) view.findViewById(R.id.chat_bt_handle);
				Log.d("-chatActivity","photos="+m.getPhotos());
				if(m.getPhotos()!=null && !"".equals(m.getPhotos())){
					renderView.setVisibility(View.VISIBLE);
				renderView.setImages(m.getPhotos().split(","));
				renderView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view){
						if(m.getMessageType()==MESSAGETYPE.BOOK||m.getMessageType()==MESSAGETYPE.REPORT){
						PostingFragment postingFragment = new PostingFragment();
						Bundle bundle = new Bundle();
					      bundle.putSerializable("sourceType", PostingFragment.SOURCETYPE.ID);
					      bundle.putLong("sourceId",m.getObjectId());
					      postingFragment.setArguments(bundle);
						postingFragment.show(getSupportFragmentManager(), ""+postingFragment.getId());
						}else if(m.getMessageType()==MESSAGETYPE.ARBITRATE){
							CommentDialog commentDialog = new CommentDialog();
				    	      Bundle bundle = new Bundle();
				    	      bundle.putSerializable("sourceType", CommentDialog.SOURCETYPE.BOOKING);
				    	      bundle.putLong("sourceId",m.getObjectId());
				    	      commentDialog.setArguments(bundle);
				    	      commentDialog.show(getSupportFragmentManager(), ""+commentDialog.getId());
						}else if(m.getMessageType()==MESSAGETYPE.INVITE){
							Intent salonIntent = new Intent();
					    	salonIntent.putExtra("salonId", m.getObjectId());
				    		salonIntent.setClass(ChatActivity.this, SalonActivity.class);
				    		startActivity(salonIntent);
						}
					}
				});
				}else{
					renderView.setVisibility(View.GONE);
				}
				SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd,E,HH:mm",Locale.getDefault());
//				Log.d("-chatActitivy","isHandlable="+m.isHandlable());
				if(m.getMessageType()==MESSAGETYPE.BOOK){
					String[] objectValues=m.getObjectValues().split(",");
					long agreedTime=0;
					try{
						agreedTime=Long.valueOf(objectValues[5]);
					}catch(Exception e){
						Log.e("-ChatActivity",Log.getStackTraceString(e));
					}
					if(agreedTime>0)m.setContent(getResources().getString(R.string.chat_tv_book_committing, format.format(new Date(agreedTime)),objectValues[4],objectValues[2],getResources().getStringArray(R.array.enum_discount)[Constance.DISCOUNT.valueOf(objectValues[3]).ordinal()]));
					if(m.isProcessed() || isRight){
						bt_handle.setVisibility(View.GONE);
					}else if(m.getMessageType()==MESSAGETYPE.BOOK && agreedTime>0&&agreedTime<Calendar.getInstance().getTimeInMillis()){
						bt_handle.setVisibility(View.VISIBLE);
						bt_handle.setText(getResources().getStringArray(R.array.booking_status)[6]);
					}else{
						bt_handle.setVisibility(View.VISIBLE);
						bt_handle.setText(R.string.action_commit);
						bt_handle.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View arg0) {
								AsyncHttpClient client=new AsyncHttpClient();
								client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
								client.put(Constance.SERVER_URL+"booking/"+m.getObjectId()+"/commit", new AsyncHttpResponseHandler(){
									@Override
									public void onSuccess(int statusCode,Header[] headers,byte[] content) {
										app.getMessageBoxes().get(userId).handle(m.getMessageId());
										app.setMessageBoxes(app.getMessageBoxes());
										adapter.notifyDataSetChanged();
										app.refreshUser();
									}
									@Override
									public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
										AlertHelper.showToast(ChatActivity.this, getResources().getString(R.string.error_serverError));
									}
								});
							}					
						});
					}
				}else if(m.getMessageType()==MESSAGETYPE.REPORT){
					m.setContent(getResources().getString(R.string.chat_tv_report,m.getFromUserName(),getResources().getStringArray(R.array.enum_reportType)[Constance.REPORTTYPE.valueOf(m.getObjectValues()).ordinal()]));
					if(m.isProcessed() || isRight){
						bt_handle.setVisibility(View.GONE);
					}else{
						bt_handle.setVisibility(View.VISIBLE);
						bt_handle.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View arg0) {
								new AlertDialog.Builder(ChatActivity.this).setItems(getResources().getStringArray(R.array.action_reportHandle), new DialogInterface.OnClickListener(){

									@Override
									public void onClick(DialogInterface dialog, int which) {
										switch(which){
										case 0:
											AsyncHttpClient client=new AsyncHttpClient();
											client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
											client.put(Constance.SERVER_URL+"report/"+m.getMessageId()+"/allow", new AsyncHttpResponseHandler(){
												@Override
												public void onSuccess(int statusCode,Header[] headers,byte[] content) {
													app.getMessageBoxes().get(userId).handle(m.getMessageId());
													app.setMessageBoxes(app.getMessageBoxes());
													adapter.notifyDataSetChanged();
												}
												@Override
												public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
													AlertHelper.showToast(ChatActivity.this, getResources().getString(R.string.error_serverError));
												}
											});
											break;
										case 1:
											AsyncHttpClient client1=new AsyncHttpClient();
											client1.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
											client1.put(Constance.SERVER_URL+"report/"+m.getMessageId()+"/reject", new AsyncHttpResponseHandler(){
												@Override
												public void onSuccess(int statusCode,Header[] headers,byte[] content) {
													app.getMessageBoxes().get(userId).handle(m.getMessageId());
													app.setMessageBoxes(app.getMessageBoxes());
													adapter.notifyDataSetChanged();
												}
												@Override
												public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
													AlertHelper.showToast(ChatActivity.this, getResources().getString(R.string.error_serverError));
												}
											});
											break;
										}
									}
									
								}).show();
							}					
						});
					}
				}else if(m.getMessageType()==MESSAGETYPE.ARBITRATE){
				m.setContent(getResources().getString(R.string.chat_tv_arbitrate,m.getFromUserName()));
				if(m.isProcessed() || isRight){
					bt_handle.setVisibility(View.GONE);
				}else{
					bt_handle.setVisibility(View.VISIBLE);
					bt_handle.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View arg0) {
							new AlertDialog.Builder(ChatActivity.this).setItems(getResources().getStringArray(R.array.action_arbitrateHandle), new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface dialog, int which) {
									switch(which){
									case 0:
										AsyncHttpClient client=new AsyncHttpClient();
										client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
										client.put(Constance.SERVER_URL+"arbitrate/"+m.getMessageId()+"/reject", new AsyncHttpResponseHandler(){
											@Override
											public void onSuccess(int statusCode,Header[] headers,byte[] content) {
												app.getMessageBoxes().get(userId).handle(m.getMessageId());
												app.setMessageBoxes(app.getMessageBoxes());
												adapter.notifyDataSetChanged();
											}
											@Override
											public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
												AlertHelper.showToast(ChatActivity.this, getResources().getString(R.string.error_serverError));
											}
										});
										break;
									case 1:
										AsyncHttpClient client1=new AsyncHttpClient();
										client1.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
										client1.put(Constance.SERVER_URL+"arbitrate/"+m.getMessageId()+"/allow", new AsyncHttpResponseHandler(){
											@Override
											public void onSuccess(int statusCode,Header[] headers,byte[] content) {
												app.getMessageBoxes().get(userId).handle(m.getMessageId());
												app.setMessageBoxes(app.getMessageBoxes());
												adapter.notifyDataSetChanged();
											}
											@Override
											public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
												AlertHelper.showToast(ChatActivity.this, getResources().getString(R.string.error_serverError));
											}
										});
										break;
									}
								}
								
							}).show();
						}					
					});
				}
				}else if(m.getMessageType()==MESSAGETYPE.INVITE){
					m.setContent(getResources().getString(R.string.chat_tv_invite,m.getObjectValues()));
					if(m.isProcessed() || isRight){
						bt_handle.setVisibility(View.GONE);
					}else{
						bt_handle.setVisibility(View.VISIBLE);
						bt_handle.setText(R.string.action_commit);
						bt_handle.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View arg0) {
								if(app.getLoginUser().getSalonId()!=0){
									AlertHelper.showToast(ChatActivity.this,getResources().getString(R.string.chat_invite_salonConflict,app.getLoginUser().getSalonName()));
								}else{
									AsyncHttpClient client=new AsyncHttpClient();
									client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
									client.put(Constance.SERVER_URL+"/message/invite/"+m.getMessageId()+"/accept", new AsyncHttpResponseHandler(){
										@Override
										public void onSuccess(int statusCode,Header[] headers,byte[] content) {
											app.getMessageBoxes().get(userId).handle(m.getMessageId());
											app.setMessageBoxes(app.getMessageBoxes());
											adapter.notifyDataSetChanged();
											app.refreshUser();
										}
										@Override
										public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
											if(statusCode==HttpStatus.SC_CONFLICT){
												AlertHelper.showToast(ChatActivity.this, getResources().getString(R.string.chat_invite_salonConflict));
											}else{
												AlertHelper.showToast(ChatActivity.this, getResources().getString(R.string.error_serverError));
											}
										}
									});
								}
							}					
						});
					}
				}else if(m.getMessageType()==MESSAGETYPE.JOIN){
					m.setContent(getResources().getString(R.string.chat_tv_join,m.getObjectValues()));
					if(m.isProcessed() || isRight){
						bt_handle.setVisibility(View.GONE);
					}else{
						bt_handle.setVisibility(View.VISIBLE);
						bt_handle.setText(R.string.action_commit);
						bt_handle.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View arg0) {
								AsyncHttpClient client=new AsyncHttpClient();
									client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
									client.put(Constance.SERVER_URL+"/message/join/"+m.getMessageId()+"/accept", new AsyncHttpResponseHandler(){
										@Override
										public void onSuccess(int statusCode,Header[] headers,byte[] content) {
											app.getMessageBoxes().get(userId).handle(m.getMessageId());
											app.setMessageBoxes(app.getMessageBoxes());
											adapter.notifyDataSetChanged();
											app.refreshUser();
										}
										@Override
										public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
											if(statusCode==HttpStatus.SC_NOT_ACCEPTABLE){
												AlertHelper.showToast(ChatActivity.this, getResources().getString(R.string.chat_join_notAcceptable));
											}else if(statusCode==HttpStatus.SC_UNAUTHORIZED){
												AlertHelper.showToast(ChatActivity.this, getResources().getString(R.string.chat_join_unauthorized));
											}else{
												AlertHelper.showToast(ChatActivity.this, getResources().getString(R.string.error_serverError));
											}
										}
									});				
						}
					});
					}
				}
			}else{
				view=ChatActivity.this.getLayoutInflater().inflate(isRight?R.layout.list_chat_right:R.layout.list_chat_left,null);
				if(m.getMessageType() == MESSAGETYPE.COMMIT){
					m.setContent(getResources().getString(R.string.chat_tv_commit, m.getFromUserName()));
				} else if(m.getMessageType() == MESSAGETYPE.COMMENT){
					m.setContent(getResources().getString(R.string.chat_tv_comment, m.getFromUserName()));
				} else if(m.getMessageType() == MESSAGETYPE.EXPLAIN){
					m.setContent(getResources().getString(R.string.chat_tv_explain, m.getFromUserName()));
				} else if(m.getMessageType() == MESSAGETYPE.FOLLOW){
					m.setContent(getResources().getString(R.string.chat_tv_follow, m.getFromUserName()));
				}else if(m.getMessageType() == MESSAGETYPE.CANCEL){
					m.setContent(getResources().getString(R.string.chat_tv_cancel, m.getFromUserName()));
				}else if(m.getMessageType() == MESSAGETYPE.INVITE){
					m.setContent(getResources().getString(R.string.chat_tv_invite, m.getObjectValues()));
				}else if(m.getMessageType() == MESSAGETYPE.JOIN){
					m.setContent(getResources().getString(R.string.chat_tv_join, m.getObjectValues()));
				}
			}
			TextView tv_time=(TextView) view.findViewById(R.id.chat_tv_time);
			TextView tv_content=(TextView) view.findViewById(R.id.chat_tv_content);
			tv_content.setText(m.getContent());
			long createTime=m.getCreateTime();
			Calendar cc=Calendar.getInstance();
			cc.setTimeInMillis(createTime);
			Calendar nc=Calendar.getInstance();
//			long nowTime=nc.getTimeInMillis();
//			final long MS_OF_ONE_DAY = 8640000;
			String publishTimeStr="";
			if(DateUtils.isToday(createTime)){
				SimpleDateFormat f = new SimpleDateFormat("HH:mm",Locale.getDefault());
				publishTimeStr=f.format(cc.getTime());
//				publishTimeStr=""+pc.get(Calendar.HOUR_OF_DAY)+":"+pc.get(Calendar.MINUTE);
			}else if(cc.get(Calendar.YEAR)==nc.get(Calendar.YEAR)){
				SimpleDateFormat f = new SimpleDateFormat("MM/dd",Locale.getDefault());
				publishTimeStr=f.format(cc.getTime());
//				publishTimeStr=""+pc.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())+" "+pc.get(Calendar.DAY_OF_MONTH);
			}else{
				SimpleDateFormat f = new SimpleDateFormat("yy/MM/dd",Locale.getDefault());
				publishTimeStr=f.format(cc.getTime());
//				publishTimeStr=""+pc.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())+" "+pc.get(Calendar.DAY_OF_MONTH)+","+pc.get(Calendar.YEAR);
			}
			tv_time.setText(publishTimeStr);
			return view;
		}
		
	}
	
	private ServiceConnection serviceConnection = new ServiceConnection() { 
        @Override
		public void onServiceConnected(ComponentName className, IBinder iBinder) { 
                service = ((Service.Binder)iBinder).getService();
                service.setListener(new OnServiceListener(){

					@Override
					public void onNewMessages(int num) {
						adapter.notifyDataSetChanged();
					}
                	
                });
        } 

        @Override
		public void onServiceDisconnected(ComponentName className) { 
        	service.setListener(null);    
//        	service = null; 
        } 
}; 
class SendTask extends AsyncTask<String, Void, Void>{
	Message resultMessage;
	@Override
	protected Void doInBackground(String... arg0) {
		String content=arg0[0];
		Message m;
		if (content==null||content.length() == 0)return null;
		m = new Message();
		m.setFromUserId(app.getLoginUser().getUserId());
		m.setToUserId(userId);
		m.setContent(content);
		m.setMessageType(Constance.MESSAGETYPE.CHAT);
		StringEntity se;
		try{
			se = new StringEntity(new Gson().toJson(m), "UTF-8");
			}catch (UnsupportedEncodingException e){
				e.printStackTrace();
				return null;
				}
		HttpPost request = new HttpPost(Constance.SERVER_URL+"message");
		request.addHeader(BasicScheme.authenticate(
				 new UsernamePasswordCredentials(((App)getApplication()).getLoginUser().getSign(), ((App)getApplication()).getLoginUser().getPassword()),
				 "UTF-8", false));
		request.setEntity(se);
		AndroidHttpClient client=AndroidHttpClient.newInstance("chatSend");
		String response = null;
		try {
			response=client.execute(request,new BasicResponseHandler());
			resultMessage = new Gson().fromJson(response, Message.class);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			client.close();
        } 
		return null;
	}
	@Override
	protected void onPostExecute(Void param) {
		if(resultMessage!=null){
//			addMessage(resultMessage);
			if(app.getMessageBoxes().containsKey(userId)){
				app.getMessageBoxes().get(userId).addMessage(resultMessage, false);
			}else{
				MessageBox mb=new MessageBox();
				mb.addMessage(resultMessage, false);
				app.getMessageBoxes().put(userId, mb);
			}
			app.setMessageBoxes(app.getMessageBoxes());
			adapter.notifyDataSetChanged();
		}
	}
	
}

class UserTask extends AsyncTask<Void, Void, Void>{
	User userGot;

	@Override
	protected Void doInBackground(Void... params) {
		HttpGet request = new HttpGet(Constance.SERVER_URL+"user/"+userId);
		request.addHeader(BasicScheme.authenticate(
				 new UsernamePasswordCredentials(((App)getApplication()).getLoginUser().getSign(), ((App)getApplication()).getLoginUser().getPassword()),
				 "UTF-8", false));

		AndroidHttpClient client=AndroidHttpClient.newInstance("chatGetUser");
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
		user=userGot;
		name=user.getUserName();
		lo_actionBar.setVisibility(View.VISIBLE);
		iv_send.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
			    InputDialog inputDialog = InputDialog.newInstance(getResources().getString(R.string.chat_input, name), "",null, InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE, 280);
			    inputDialog.addListener(new InputDialog.InputListener(){
			    	@Override
			    	public void OnFinish(String content){
			    		new SendTask().execute(content);
			      }
			    });
			    inputDialog.show(getSupportFragmentManager(), ""+inputDialog.getId());
			}
		  });
		  ImageHelper.loadImg(iv_figure,user.getFigure() , 5,true);
		  iv_figure.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ProfileFragment profileFragment = new ProfileFragment();
				Bundle bundle = new Bundle();
			      bundle.putLong("userId",userId);
			      profileFragment.setArguments(bundle);
			      profileFragment.show(getSupportFragmentManager(), ""+profileFragment.getId());
			} 
		  });
			tv_userName.setText(name);
			if(user.getUserType()==USERTYPE.STYLIST){
				tv_hair.setVisibility(View.GONE);
				tv_face.setVisibility(View.GONE);
				if(user.getAddress()!=null && !"".equals(user.getAddress())){
					tv_address.setText(user.getAddress());
					tv_address.setVisibility(View.VISIBLE);
				}else{
					tv_address.setVisibility(View.GONE);
				}
			}else{
				tv_address.setVisibility(View.GONE);
				tv_hair.setVisibility(View.VISIBLE);
				tv_face.setVisibility(View.VISIBLE);
				tv_hair.setText(getResources().getStringArray(R.array.enum_bangType)[user.getBangType().ordinal()] + " "+getResources().getStringArray(R.array.enum_curlyType)[user.getCurlyType().ordinal()]+" "+getResources().getStringArray(R.array.enum_hairLength)[user.getHairLength().ordinal()]);
				tv_face.setText(getResources().getStringArray(R.array.enum_faceShape)[user.getFaceShape().ordinal()]);
			}
			lv.setOnBorderListener(new OnBorderListener(){

				@Override
				public void onBottom() {
					if(lo_actionBar.getVisibility()!=View.VISIBLE)lo_actionBar.setVisibility(View.VISIBLE);
				}

				@Override
				public void onRoll() {
					if(lo_actionBar.getVisibility()!=View.GONE)lo_actionBar.setVisibility(View.GONE);
				}
				
			});
	}
	}
}
}
