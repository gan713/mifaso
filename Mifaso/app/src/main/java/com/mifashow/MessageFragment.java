package com.mifashow;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mifashow.InputDialog.InputListener;
import com.mifashow.data.Constance;
import com.mifashow.data.Constance.MESSAGETYPE;
import com.mifashow.domain.Message;
import com.mifashow.domain.MessageBox;
import com.mifashow.tool.AlertHelper;
import com.mifashow.tool.ImageHelper;
import com.readystatesoftware.viewbadger.BadgeView;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MessageFragment extends Fragment {
	App app;
	View view;
	ListView listView;
	RelativeLayout lv_manager;
	MessageAdapter adapter;
	LayoutInflater inflater;
	boolean isRefreshing;
	ActionBar ab;
	@Override 
	public void onCreate (Bundle savedInstanceState){
		app=((App) MessageFragment.this.getActivity().getApplication());
		adapter=new MessageAdapter();
		Log.d("-messageFragment","onCreate");
		super.onCreate(savedInstanceState);
	}
	@Override  
    public void onDestroy(){
        super.onDestroy();
	}
	@Override  
    public void onResume(){
		super.onResume();
		adapter.notifyDataSetChanged();     
	}
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) { 
		if(view==null){  
			this.inflater=inflater;
			view=inflater.inflate(R.layout.fragment_message,container, false);
			listView=(ListView) view.findViewById(R.id.message_lv_list);
			View headerView  = inflater.inflate(R.layout.list_message, null);
			ImageView iv_figure=(ImageView) headerView.findViewById(R.id.user_iv_figure);
			TextView tv_username=(TextView) headerView.findViewById(R.id.message_tv_username);
			TextView tv_time=(TextView) headerView.findViewById(R.id.message_tv_time);
			TextView tv_review=(TextView) headerView.findViewById(R.id.message_tv_review);
			iv_figure.setImageResource(R.drawable.icon_bow);
			tv_username.setText(R.string.messagebox_tv_manager_username);
			tv_review.setText(R.string.messagebox_tv_manager_message);
			tv_time.setVisibility(View.GONE);
			if(app.getLoginUser().getUserId()!=0)
			headerView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					InputDialog inputDialog = InputDialog.newInstance(getResources().getString(R.string.main_input_title),"",null,InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE,100);
					inputDialog.addListener(new InputListener(){

						@Override
						public void OnFinish(String inputText) {
							if(inputText==null || inputText.trim().length()==0)return;
							Message m=new Message();
							m.setMessageType(MESSAGETYPE.CHAT);
							m.setFromUserId(app.getLoginUser().getUserId());
							m.setToUserId(-1);
							m.setContent(inputText);
							AsyncHttpClient client=new AsyncHttpClient();
							client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
							StringEntity se = null;
							try{
				    			se = new StringEntity(new Gson().toJson(m), "UTF-8");
				    			}catch (UnsupportedEncodingException e){
				    				e.printStackTrace();
				    				return;
				    				}
				    		client.post(getActivity(), Constance.SERVER_URL+"message", se, "application/json;charset=utf-8", new AsyncHttpResponseHandler(){
				            @Override
							public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
				            	Log.d("-mainActivity","home:onFailure:"+content);
				            	AlertHelper.showToast(getActivity(), R.string.error_serverError);
				            }

				            @Override
							public void onSuccess(int statusCode,Header[] headers,byte[] content){
				            	Log.d("-mainActivity","home:onSuccess");
				              Message message = new Gson().fromJson(new String(content), Message.class);
				              if(app.getMessageBoxes().containsKey(message.getToUserId())){
				            	  app.getMessageBoxes().get(message.getToUserId()).addMessage(message, false);
				              }else{
				            	  MessageBox mb=new MessageBox();
				            	  mb.addMessage(message, false);
				            	  app.getMessageBoxes().put(message.getToUserId(), mb);
				              }
				              app.setMessageBoxes(app.getMessageBoxes());
				              AlertHelper.showToast(getActivity(), R.string.info_sendDone);
				            }
				          });
						}
						
					});
					inputDialog.show(getFragmentManager().beginTransaction(), ""+android.R.id.home);
				}
			});
			listView.addHeaderView(headerView);
			listView.setAdapter(adapter);
			Log.d("-messageFragment","onCreateView:view==null"+adapter.getCount());
			listView.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
					if(arg2==0)return;
					if(app.getMessageBoxes().get(arg3).getNewNum()>0){
						adapter.notifyDataSetChanged();
						app.getMessageBoxes().get(arg3).zeroNewNum();
						app.setMessageBoxes(app.getMessageBoxes());
					}
					Intent intent = new Intent();
					intent.putExtra("userId", arg3);
					intent.setClass(MessageFragment.this.getActivity(), ChatActivity.class);
					startActivity(intent);
					}
				});
			listView.setOnItemLongClickListener(new OnItemLongClickListener(){
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int arg2, final long arg3) {
					new AlertDialog.Builder(MessageFragment.this.getActivity()).setItems(getResources().getStringArray(R.array.action_messageBoxHandle), new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch(which){
							case 0:
								AlertHelper.showAlert(getActivity(), getResources().getString(R.string.dialog_clearMessageBox_title), getResources().getString(R.string.dialog_clearMessageBox_message), getResources().getString(R.string.action_ok), new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int id) {
										app.getMessageBoxes().remove(arg3);
										app.setMessageBoxes(app.getMessageBoxes());
										adapter.notifyDataSetChanged();
										}
									}, getResources().getString(R.string.action_cancel), 
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int id) {}
										});
								break;
							case 1:
								AlertHelper.showAlert(getActivity(), getResources().getString(R.string.dialog_clearHandledMessageBoxes_title), getResources().getString(R.string.dialog_clearHandledMessageBoxes_message), getResources().getString(R.string.action_ok), 
										new DialogInterface.OnClickListener() {
							    	@Override
							    	public void onClick(DialogInterface dialog, int id) {
							    		Set<Long> keys=app.getMessageBoxes().keySet();
							    		LinkedHashMap<Long, MessageBox> newMessageBoxes=new LinkedHashMap<Long, MessageBox>();
							    		for(Long key:keys){
							    			ArrayList<Message> ms=app.getMessageBoxes().get(key).getHandleable();
							    			if(ms.size()>0){
							    				MessageBox mb=new MessageBox();
							    				mb.setMessages(ms);
							    				newMessageBoxes.put(key, mb);
							    			}
							    		}
							    		app.setMessageBoxes(newMessageBoxes);
							    		adapter.notifyDataSetChanged();
							    		}
							    	}, getResources().getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
								    	@Override
								    	public void onClick(DialogInterface dialog, int id) {}
								    	});
							    break;
							case 2:
								AlertHelper.showAlert(getActivity(), getResources().getString(R.string.dialog_clearMessageBoxes_title), getResources().getString(R.string.dialog_clearMessageBoxes_message), getResources().getString(R.string.action_ok), 
										new DialogInterface.OnClickListener() {
							    	@Override
							    	public void onClick(DialogInterface dialog, int id) {
							    		app.getMessageBoxes().clear();
							    		app.setMessageBoxes(app.getMessageBoxes());
							    		adapter.notifyDataSetChanged();
							    		}
							    	}, getResources().getString(R.string.action_cancel), 
							    	new DialogInterface.OnClickListener() {
								    	@Override
								    	public void onClick(DialogInterface dialog, int id) {}
								    	});
							    break;
							    }
							}
						}).create().show();
					return false;
					}
				});
			}else{
				Log.d("-messageFragment", "onCreateView:view!=null");
				ViewGroup parent = (ViewGroup) view.getParent();
				if (parent != null) {
					parent.removeView(view);
				}
			}
		return view; 
	}
	private ArrayList<MessageBox> getOrderBox(){
		ArrayList<MessageBox> boxList=new ArrayList<MessageBox>();
		for (MessageBox m : app.getMessageBoxes().values()) {
			if (boxList.size() == 0) {
				boxList.add(0, m);
			} else {
				for (int i = 0; i < boxList.size(); i++) {
					if (m.getLast().getMessageId() > boxList.get(i).getLast().getMessageId()) {
						boxList.add(i, m);
						break;
					}
				}
			}
		}
		return boxList;
	}
	class MessageAdapter extends BaseAdapter{
		int size;

		@Override
		public int getCount() {
			size= getOrderBox().size();
			return size;
		}

		@Override
		public Object getItem(int arg0) {
			return getOrderBox().get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			Long userId=getOrderBox().get(arg0).getLast().getFromUserId();
			if(userId==app.getLoginUser().getUserId())userId=getOrderBox().get(arg0).getLast().getToUserId();
			return userId;
		}

		@Override
		public View getView(final int arg0, View arg1, ViewGroup arg2) {
			Log.d("-messageFragment","getView");
			View view=inflater.inflate(R.layout.list_message,null);
			ImageView iv_figure=(ImageView) view.findViewById(R.id.user_iv_figure);
			TextView tv_username=(TextView) view.findViewById(R.id.message_tv_username);
			TextView tv_time=(TextView) view.findViewById(R.id.message_tv_time);
			TextView tv_review=(TextView) view.findViewById(R.id.message_tv_review);
			final Message m=((MessageBox)getItem(arg0)).getLast();
//			m.setHandlable(m.getMessageType()==MESSAGETYPE.BOOK && m.getBookingStatus()==BOOKINGSTATUS.COMMITTING && m.getAgreedTime()>Calendar.getInstance().getTimeInMillis());			
			if(m.getToUserId()==app.getLoginUser().getUserId()){
				ImageHelper.loadImg(iv_figure, m.getFromUserFigure(), 0,false);
				tv_username.setText(m.getFromUserName());
			}else{
				ImageHelper.loadImg(iv_figure, m.getToUserFigure(), 0,false);
				tv_username.setText(m.getToUserName());
			}
			if(m.getMessageType()==MESSAGETYPE.BOOK){
				SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd,E,HH:mm",Locale.getDefault());
				try{
				String[] objectValues=m.getObjectValues().split(",");
				m.setContent(getResources().getString(R.string.messagebox_tv_content_book, format.format(new Date(objectValues[5])),objectValues[4]));
				}catch(Exception e){
					Log.e("-MessageFragment",Log.getStackTraceString(e));
				}
				}else if(m.getMessageType()==MESSAGETYPE.COMMIT){
					m.setContent(getResources().getString(R.string.messagebox_tv_content_commit, m.getFromUserName()));
				}else if(m.getMessageType()==MESSAGETYPE.COMMENT){
					m.setContent(getResources().getString(R.string.messagebox_tv_content_comment, m.getFromUserName()));
				}else if(m.getMessageType()==MESSAGETYPE.EXPLAIN){
					m.setContent(getResources().getString(R.string.messagebox_tv_content_explain, m.getFromUserName()));
				}else if(m.getMessageType()==MESSAGETYPE.ARBITRATE){
					m.setContent(getResources().getString(R.string.messagebox_tv_content_arbitrate, m.getFromUserName()));
				}else if(m.getMessageType()==MESSAGETYPE.FOLLOW){
					m.setContent(getResources().getString(R.string.messagebox_tv_content_follow, m.getFromUserName()));
				}else if(m.getMessageType()==MESSAGETYPE.REPORT){
					m.setContent(getResources().getString(R.string.messagebox_tv_content_report, m.getFromUserName(),getResources().getStringArray(R.array.enum_reportType)[Constance.REPORTTYPE.valueOf(m.getObjectValues()).ordinal()]));
				}else if(m.getMessageType()==MESSAGETYPE.CANCEL){
					m.setContent(getResources().getString(R.string.messagebox_tv_content_cancel, m.getFromUserName()));
				}else if(m.getMessageType()==MESSAGETYPE.INVITE){
					m.setContent(getResources().getString(R.string.messagebox_tv_content_invite, m.getObjectValues()));
				}else if(m.getMessageType()==MESSAGETYPE.JOIN){
					m.setContent(getResources().getString(R.string.messagebox_tv_content_join, m.getObjectValues()));
				}
			tv_review.setText(m.getContent());
			long publishTime=m.getCreateTime();
			Calendar pc=Calendar.getInstance();
			pc.setTimeInMillis(publishTime);
			Calendar nc=Calendar.getInstance();
			String publishTimeStr="";
			if(DateUtils.isToday(publishTime)){
				SimpleDateFormat f = new SimpleDateFormat("HH:mm",Locale.getDefault());
				publishTimeStr=f.format(pc.getTime());
			}else if(pc.get(Calendar.YEAR)==nc.get(Calendar.YEAR)){
				SimpleDateFormat f = new SimpleDateFormat("MM/dd",Locale.getDefault());
				publishTimeStr=f.format(pc.getTime());
			}else{
				SimpleDateFormat f = new SimpleDateFormat("yy/MM/dd",Locale.getDefault());
				publishTimeStr=f.format(pc.getTime());
			}
			tv_time.setText(publishTimeStr);
			int newNum=((MessageBox)getItem(arg0)).getNewNum();
			if(newNum>0){
			BadgeView badge=new BadgeView(getActivity(),tv_review);
			badge.setIncludeFontPadding(false);
			badge.setText(""+getOrderBox().get(arg0).getNewNum());
			badge.show();
			}
			return view;
		}
		
	}

}
