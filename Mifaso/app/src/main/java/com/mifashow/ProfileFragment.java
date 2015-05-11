package com.mifashow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mifashow.data.Constance;
import com.mifashow.data.Constance.USERTYPE;
import com.mifashow.domain.User;
import com.mifashow.tool.AlertHelper;
import com.mifashow.tool.DateHelper;
import com.mifashow.tool.ImageHelper;
import com.mifashow.tool.image.DensityUtil;
import com.mifashow.ui.RoundAngleImageView;
import com.readystatesoftware.viewbadger.BadgeView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class ProfileFragment extends DialogFragment implements OnClickListener,OnLongClickListener {
	  App app;
	  TextView tv_booking;
	  Button bt_chat;
	  TextView tv_comment;
	  Button bt_follow;
	  TextView tv_follower;
	  TextView tv_following;
	  TextView tv_marking;
	  Button bt_post,bt_salon,bt_set,bt_signup,bt_login;
	  TextView tv_posting;
	  RoundAngleImageView iv_figure;
	  ImageView iv_top,iv_usertype;
	  LinearLayout lo_salon,lo_rating;
	  RatingBar rb_rating;
	  TextView tv_rating;
	  TextView tv_about;
	  ImageButton bt_salonPhone;
	  TextView tv_salonName;
	  TextView tv_salonAddress;
	  TextView tv_salonDistance;
	  TextView tv_age;
	  TextView tv_faceShape;
	  TextView tv_hairStyle;
	  TextView tv_username;
	  TextView tv_level;
	  TextView tv_city;
	  User user;
	  long userId;
	  View view;
	  BadgeView bv_booked,bv_commented,bv_follower;
	  static final int TAKE_PHOTO_FOR_FIGURE=1;
	  static final int TAKE_PHOTO_FOR_SHOP=2;
	  boolean isRotating=false;
	  private Handler handler = new Handler();
	    private Runnable runnable = new Runnable() {
	        @Override
			public void run() {
	            if(view!=null){
	            	new UserTask().execute((Void)null);
	            }
	            handler.postDelayed(this, 1000 * 60);// ���60��
	        }
	    }; 
//	@Override  
//	public Dialog onCreateDialog(Bundle savedInstanceState) {
//		Dialog dialog = super.onCreateDialog(savedInstanceState);
//		dialog.getWindow().requestFeature(STYLE_NO_TITLE);
//    	dialog.getWindow().requestFeature(STYLE_NO_FRAME);
//		dialog.getWindow().setBackgroundDrawableResource(R.color.lightest);
//		return dialog;
//	}  

	@Override  
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
    	setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar);
		Log.d("-ProfileFragment","ProfileFragment:onCreate:userId:"+userId);
		app=(App)(this.getActivity().getApplication());
		Bundle bundle=getArguments();
		if(bundle!=null){
			userId=bundle.getLong("userId");
		}
		if(userId==0L){
			user=app.getLoginUser();
			userId=app.getLoginUser().getUserId();
		}else{
			List<User> cachedUsers=new ArrayList<User>();
			cachedUsers.add(app.getLoginUser());
			cachedUsers.addAll(app.getFollowingUsers());
			cachedUsers.addAll(app.getFollowedUsers());
			cachedUsers.addAll(app.getNear100kmUsers());
			cachedUsers.addAll(app.getDiscoveryUsers());
			for(User u:cachedUsers){
				if(u.getUserId()==userId){
					user=u;
					break;
				}
			}
		}
	}
	@Override  
    public void onDestroy(){
		handler.removeCallbacks(runnable); 
        super.onDestroy();
	}
	@Override  
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		if(this.view==null){
			Log.d("-ProfileFragment","ProfileFragment:onCreateView:view=null");
		this.view = inflater.inflate(R.layout.fragment_profile, null);
		this.lo_salon=((LinearLayout)this.view.findViewById(R.id.profile_lo_salon));
		this.iv_top=((ImageView)view.findViewById(R.id.profile_iv_top));
	    this.tv_salonName = ((TextView)this.view.findViewById(R.id.profile_tv_salonName));
	    this.tv_salonAddress = ((TextView)this.view.findViewById(R.id.profile_tv_salonAddress));
	    this.tv_salonDistance = ((TextView)this.view.findViewById(R.id.profile_tv_salonDistance));
	    this.bt_salonPhone = ((ImageButton)this.view.findViewById(R.id.profile_bt_salonPhone));
	    bt_salonPhone.setOnClickListener(this);
	    this.tv_posting = ((TextView)this.view.findViewById(R.id.profile_tv_posting));
	    this.tv_follower = ((TextView)this.view.findViewById(R.id.profile_tv_follower));
	    this.tv_hairStyle = ((TextView)this.view.findViewById(R.id.profile_tv_hairStyle));
	    this.tv_faceShape = ((TextView)this.view.findViewById(R.id.profile_tv_faceShape));
	    this.iv_figure = ((RoundAngleImageView)this.view.findViewById(R.id.profile_iv_figure));
	    this.tv_username = ((TextView)this.view.findViewById(R.id.profile_tv_username));
	    this.tv_about = ((TextView)this.view.findViewById(R.id.profile_tv_about));
	    this.tv_age = ((TextView)this.view.findViewById(R.id.profile_tv_age));
	    this.tv_level = ((TextView)this.view.findViewById(R.id.profile_tv_level));
	    this.tv_city = ((TextView)this.view.findViewById(R.id.profile_tv_city));
	    this.tv_marking = ((TextView)this.view.findViewById(R.id.profile_tv_marking));
	    this.tv_comment = ((TextView)this.view.findViewById(R.id.profile_tv_comment));
	    this.tv_booking = ((TextView)this.view.findViewById(R.id.profile_tv_booking));
	    this.tv_following = ((TextView)this.view.findViewById(R.id.profile_tv_following));
		this.iv_usertype=((ImageView)view.findViewById(R.id.profile_iv_usertype));
	    this.lo_rating=((LinearLayout)this.view.findViewById(R.id.profile_lo_rating));
	    this.rb_rating = ((RatingBar)this.view.findViewById(R.id.profile_rb_rating));
	    this.tv_rating=(TextView) this.view.findViewById(R.id.profile_tv_rating);
	    this.bt_chat = ((Button)this.view.findViewById(R.id.profile_bt_chat));
	    this.bt_follow = ((Button)this.view.findViewById(R.id.profile_bt_follow));
	    this.bt_post = ((Button)this.view.findViewById(R.id.profile_bt_post));
	    this.bt_salon = ((Button)this.view.findViewById(R.id.profile_bt_salon));
	    this.bt_set = ((Button)this.view.findViewById(R.id.profile_bt_set));
	    this.bt_signup = ((Button)this.view.findViewById(R.id.profile_bt_signup));
	    this.bt_login = ((Button)this.view.findViewById(R.id.profile_bt_login));
	    if(bv_follower==null)bv_follower=new BadgeView(getActivity(),tv_follower);
		bv_follower.setIncludeFontPadding(false);
		bv_follower.hide();
		if(bv_booked==null)bv_booked=new BadgeView(getActivity(),tv_booking);
		bv_booked.setIncludeFontPadding(false);
	    bv_booked.hide();
	    if(bv_commented==null)bv_commented=new BadgeView(getActivity(),tv_comment);
	    bv_commented.setIncludeFontPadding(false);
	    bv_commented.hide();
	    if(user!=null){
	    	renderData();
	    	new UserTask().execute((Void)null);
	    }else if(userId==app.getLoginUser().getUserId()){
			handler.post(runnable);
	    }else{
	    	new UserTask().execute((Void)null);
	    }
//	    handler.post(runnable);
		}else{
			Log.d("-ProfileFragment","ProfileFragment:onCreateView:view!=null");
			ViewGroup parent = (ViewGroup) this.view.getParent();
			if (parent != null) {
				parent.removeView(this.view);
				}
		}
	    return this.view; 
	}
	public void renderData(){
		Log.d("-ProfileFragment","renderData:"+(user!=null));
		if(user==null || !ProfileFragment.this.isAdded())return;
		if(app.getLoginUser().getUserId()==0){
			this.bt_chat.setVisibility(View.GONE);
		      this.bt_follow.setVisibility(View.GONE);
		      this.bt_set.setVisibility(View.GONE);
		      this.bt_post.setVisibility(View.GONE);
		      this.bt_salon.setVisibility(View.GONE);
		      if(user.getUserId()==0){
		      this.bt_signup.setVisibility(View.VISIBLE);
		      this.bt_signup.setOnClickListener(this);
		      this.bt_login.setVisibility(View.VISIBLE);
		      this.bt_login.setOnClickListener(this);
		      }else{
		    	  this.bt_signup.setVisibility(View.GONE);
		    	  this.bt_login.setVisibility(View.GONE);
		      }
		      this.tv_level.setVisibility(View.GONE);
		      this.iv_figure.setImageResource(R.drawable.icon_bow);
		}else if (user.getUserId() == app.getLoginUser().getUserId()){
	      this.iv_figure.setOnLongClickListener(this);
	      this.tv_about.setOnLongClickListener(this);
	      this.tv_username.setOnLongClickListener(this);
	      this.bt_chat.setVisibility(View.GONE);
	      this.bt_follow.setVisibility(View.GONE);
	      this.bt_set.setVisibility(View.VISIBLE);
	      this.bt_signup.setVisibility(View.GONE);
	      this.bt_login.setVisibility(View.GONE);
	      this.bt_set.setOnClickListener(this);
	      if (user.getUserType() == Constance.USERTYPE.STYLIST){
	        if (user.getSalonId()==0){
	        	this.bt_salon.setVisibility(View.VISIBLE);
		        this.bt_salon.setOnClickListener(this);
		        this.bt_post.setVisibility(View.GONE);
	        }else{
	        	this.bt_salon.setVisibility(View.GONE);
		        this.bt_post.setVisibility(View.VISIBLE);
		        this.bt_post.setOnClickListener(this);
	        }
	      }else{
	    	  this.bt_salon.setVisibility(View.GONE);
	    	  this.bt_post.setVisibility(View.VISIBLE);
	    	  this.bt_post.setOnClickListener(this);
	      }
	    }else{
	    	this.bt_signup.setVisibility(View.GONE);
		    this.bt_login.setVisibility(View.GONE);
	      this.bt_chat.setVisibility(View.VISIBLE);
	      this.bt_chat.setOnClickListener(this);
	      boolean isFollowed=false;
	      for(User u:app.getFollowingUsers()){
	    	  if(u.getUserId()==userId){
	    		  isFollowed=true;
	    		  break;
	    	  }
	      }
	      if(!isFollowed){
	    	  this.bt_follow.setVisibility(View.VISIBLE);
		      this.bt_follow.setOnClickListener(this);
	      }else{
	    	  this.bt_follow.setVisibility(View.GONE);
	      }
	      this.bt_post.setVisibility(View.GONE);
	      this.bt_salon.setVisibility(View.GONE);
	      this.bt_set.setVisibility(View.GONE);
	      tv_about.setHint(null);
	    }
	    if (user!=null && user.getUserType() == Constance.USERTYPE.STYLIST){
//	      tv_age.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_scissor_16, 0, 0, 0);
//	      tv_age.setCompoundDrawablePadding(5);
	      lo_rating.setVisibility(View.VISIBLE);
	      rb_rating.setNumStars(5);
	      rb_rating.setRating(user.getGrade()*rb_rating.getNumStars());
	      rb_rating.setIsIndicator(true);
	      tv_rating.setText(getResources().getString(R.string.profile_tv_rating,Math.round(user.getGrade())));
	      tv_booking.setText(getResources().getString(R.string.profile_bt_booking, user.getBookedNum()));
	      tv_comment.setText(getResources().getString(R.string.profile_bt_comment, user.getCommentedNum()));
	      if(user.getSalonId()!=0){
	    	  if(user.getSalonImages()!=null && user.getSalonImages().split(",")!=null&&user.getSalonImages().split(",").length>0){
	    		  ImageHelper.loadImg(iv_top, user.getSalonImages().split(",")[0], 0, false);
	    	  }
	    	  bt_salon.setVisibility(View.GONE);
	    	  lo_salon.setVisibility(View.VISIBLE);
		      lo_salon.setOnClickListener(this);
		      tv_salonName.setText(user.getSalonName());
		      tv_salonAddress.setText(user.getSalonAddress());
		      if(app.getLatitude()!=0&&app.getLongitude()!=0)
		      tv_salonDistance.setText(app.getLocationHelper().getDistance(user.getLatitude(), user.getLongitude()));
	      }else{
	    	  lo_salon.setVisibility(View.GONE);
	      }
	      tv_faceShape.setVisibility(View.GONE);
	      tv_hairStyle.setVisibility(View.GONE);
	      if (user.getBookedNum() > 0)
	        tv_booking.setOnClickListener(this);
	      if (user.getCommentedNum() > 0)
	        tv_comment.setOnClickListener(this);
	    }else{
	    	lo_rating.setVisibility(View.GONE);
	    	  lo_salon.setVisibility(View.GONE);
	    	  if(user.getUserId()==0){
		      tv_faceShape.setVisibility(View.GONE);
		      tv_hairStyle.setVisibility(View.GONE);
	    	  }
		      tv_booking.setText(getResources().getString(R.string.profile_bt_booking, user.getBookingNum()));
		      tv_comment.setText(getResources().getString(R.string.profile_bt_comment, user.getCommentingNum()));
		      if (user.getBookingNum() > 0)
		        tv_booking.setOnClickListener(this);
		      if (user.getCommentingNum() > 0)
		        tv_comment.setOnClickListener(this);
		      if (user.getFaceShape() != null)
		        tv_faceShape.setText(getResources().getStringArray(R.array.enum_faceShape)[this.user.getFaceShape().ordinal()]);
		      if ((this.user.getHairLength() != null) && (this.user.getBangType() != null) && (this.user.getCurlyType() != null))
		        tv_hairStyle.setText(getResources().getStringArray(R.array.enum_bangType)[this.user.getBangType().ordinal()] + " "+getResources().getStringArray(R.array.enum_curlyType)[this.user.getCurlyType().ordinal()]+" "+getResources().getStringArray(R.array.enum_hairLength)[this.user.getHairLength().ordinal()]);
	    }
	      if ((user.getFigure() != null) && (!"".equals(user.getFigure()))){
	    	  ImageHelper.loadImg(this.iv_figure, this.user.getFigure(),0,true);
	    	  iv_figure.setOnClickListener(this);
	      }
	      tv_username.setText(this.user.getUserName());
	      if(user.getUserType()==USERTYPE.STYLIST){
//	          tv_age.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_scissors_16, 0,0 , 0);
//	          tv_age.setCompoundDrawablePadding(5);
	    	  iv_usertype.setVisibility(View.VISIBLE);
	      }else{
	    	  iv_usertype.setVisibility(View.GONE);
	      }
	      if(user.getUserId()!=app.getLoginUser().getUserId() && (user.getAbout()==null||"".equals(user.getAbout()))){
	    	  tv_about.setVisibility(View.GONE);
	      }else{
	    	  tv_about.setVisibility(View.VISIBLE);
	    	  tv_about.setText(this.user.getAbout());
	      }
	      tv_age.setBackgroundResource(user.getSex() == Constance.SEX.MALE?R.drawable.shape_rectangle_radius_blue:R.drawable.shape_rectangle_radius_pink);
	      tv_age.setText(""+DateHelper.getAge(user.getBirthday()));
	      int level=Constance.getLevel(user);
	      if(user.getUserId()==app.getLoginUser().getUserId() && level<7){
	          tv_level.setText(getResources().getString(R.string.profile_tv_levelTask_text,level,getResources().getStringArray(user.getUserType()==USERTYPE.STYLIST?R.array.enum_stylistLevel:R.array.enum_level)[level-1]));
	      }else{
	    	  tv_level.setText(getResources().getString(R.string.profile_tv_level_text,level));
	      }
	      if(user.getUserId()!=app.getLoginUser().getUserId()){
	    	  tv_city.setVisibility(View.VISIBLE);
	    	  app.getLocationHelper().showCityInTextView(user.getLatitude(), user.getLongitude(), tv_city);
	    	  if(user.getLatitude()!=0){
	    		  tv_city.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						Intent mapIntent = new Intent();
						mapIntent.putExtra("user", user);
					      mapIntent.setClass(getActivity(), MapActivity.class);
					      startActivity(mapIntent);
					} 
	    		  });
	    	  }
//	    	  new CityTask(getActivity(),tv_city,user).executeOnExecutor(Executors.newSingleThreadExecutor(), (Void)null);
	      }else{
	    	  tv_city.setVisibility(View.GONE);
	      }
	      tv_posting.setText(getResources().getString(R.string.profile_bt_posting, user.getPostingNum()));
	      if (user.getPostingNum() > 0)
	        tv_posting.setOnClickListener(this);
	      tv_follower.setText(getResources().getString(R.string.profile_bt_follower, user.getFollowerNum()));
	      if (user.getFollowerNum() > 0)
	        tv_follower.setOnClickListener(this);
		  
	      tv_marking.setText(getResources().getString(R.string.profile_bt_marking, user.getMarkingNum()));
	      if (user.getMarkingNum() > 0)
	        tv_marking.setOnClickListener(this);
	      tv_following.setText(getResources().getString(R.string.profile_bt_following, user.getFollowingNum()));
	      if (user.getFollowingNum() > 0)
	        tv_following.setOnClickListener(this);
//	      view.invalidate();
	  }
	@Override
	public void onResume(){
		Log.d("--ProfileFragment","onResume");
		super.onResume();
		if(user!=null && user.getUserId()==app.getLoginUser().getUserId() && !user.toString().equals(app.getLoginUser().toString())){
			int bookedNum=app.getLoginUser().getBookedNum()-user.getBookedNum();
			int commentedNum=app.getLoginUser().getCommentedNum()-user.getCommentedNum();
			int followerNum=app.getLoginUser().getFollowerNum()-user.getFollowerNum();
			int changeNum=bookedNum+commentedNum+followerNum;
			user=app.getLoginUser();
			renderData();
			if(user.getUserType()==USERTYPE.STYLIST){
				if(bookedNum>0){
					bv_booked.setText(""+bookedNum);
					bv_booked.toggle();
				}
				if(commentedNum>0){
					bv_commented.setText(""+commentedNum);
					bv_commented.toggle();
				}
				if(followerNum>0){
					bv_follower.setText(""+followerNum);
					bv_follower.toggle();
				}
				if(changeNum>0){
					((MainActivity)getActivity()).bv_me.setText(""+changeNum);
					((MainActivity)getActivity()).bv_me.toggle();
				}
			}
		}
	}
@Override
public void onClick(View v) {
	switch(v.getId()){
	case R.id.profile_iv_figure:
	      Intent figureIntent = new Intent();
	      figureIntent.putExtra("renderingUrl", new String[]{user.getFigure()});
	      figureIntent.setClass(getActivity(), ViewerActivity.class);
	      startActivity(figureIntent);
	      break;
//	    case R.id.profile_iv_shop:
//	      if ((user.getUserId() ==app.getLoginUser().getUserId()) && ((user.getShopImage() == null) || ("".equals(user.getShopImage())))){
//	        onLongClick(this.iv_shop);
//	      }else if(user.getShopImage()!=null && !"".equals(user.getShopImage())){
//	      Intent shopIntent = new Intent();
//	      shopIntent.putExtra("renderingUrl", new String[]{user.getShopImage()});
//	      shopIntent.setClass(getActivity(), ViewerActivity.class);
//	      startActivity(shopIntent);
//	      }
//	      break;
//	    case R.id.profile_tv_address:
//		      Intent mapIntent = new Intent();
//		      mapIntent.putExtra("user", user);
//		      mapIntent.setClass(getActivity(), MapActivity.class);
//		      startActivity(mapIntent);
//		      break;
	    case R.id.profile_bt_salonPhone:
	    	AlertHelper.showAlert(getActivity(), getResources().getString(R.string.profile_alert_phone_title), getResources().getString(R.string.profile_alert_phone_content,user.getSalonPhone()), getResources().getString(R.string.action_yes), new DialogInterface.OnClickListener(){
	    		@Override
				public void onClick(DialogInterface arg0, int arg1) {
	    		Intent callIntent = new Intent();
	    	callIntent.setAction("android.intent.action.CALL");
	    	callIntent.setData(Uri.parse("tel:"+user.getSalonPhone()));
    		startActivity(callIntent);
	    	}
	    	}, getResources().getString(R.string.action_no), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
				}
			});
    		break;
	    case R.id.profile_tv_booking:
	    		bv_booked.setText("0");
		    	bv_booked.hide();
	    	Intent bookingIntent = new Intent();
	          bookingIntent.putExtra("userId", userId);
	          bookingIntent.putExtra("userType", user.getUserType());
	          bookingIntent.setClass(getActivity(), BookingActivity.class);
	          if(ProfileFragment.this.isAdded())startActivity(bookingIntent);
	      break;
	    case R.id.profile_tv_posting:
	      PostingFragment postingFragment = new PostingFragment();
	      Bundle bundle = new Bundle();
	      bundle.putSerializable("sourceType", PostingFragment.SOURCETYPE.CREATER);
	      bundle.putLong("sourceId", user.getUserId());
	      postingFragment.setArguments(bundle);
	      postingFragment.show(getFragmentManager(), ""+postingFragment.getId());
	      break;
	    case R.id.profile_tv_comment:
	    	bv_commented.setText("0");
	    	bv_commented.hide();
	    	CommentDialog commentDialog = new CommentDialog();
  	      Bundle commentBundle = new Bundle();
  	    commentBundle.putSerializable("sourceType", user.getUserType()==USERTYPE.STYLIST?CommentDialog.SOURCETYPE.STYLIST:CommentDialog.SOURCETYPE.CUSTOMER);
  	  commentBundle.putLong("sourceId",user.getUserId());
  	      commentDialog.setArguments(commentBundle);
  	      commentDialog.show(getFragmentManager(), ""+commentDialog.getId());
	      break;
	    case R.id.profile_tv_marking:
	      PostingFragment postingFragment1 = new PostingFragment();
	      Bundle bundle1 = new Bundle();
	      bundle1.putSerializable("sourceType", PostingFragment.SOURCETYPE.MARKINGUSER);
	      bundle1.putLong("sourceId", user.getUserId());
	      postingFragment1.setArguments(bundle1);
	      postingFragment1.show(getFragmentManager(), ""+postingFragment1.getId());
	      break;
	    case R.id.profile_tv_following:
	    	UserFragment followingFragment = new UserFragment();
		    Bundle followingFragmentBundle = new Bundle();
		    followingFragmentBundle.putSerializable("sourceType", UserFragment.SOURCETYPE.FOLLOWINGUSER);
		    followingFragmentBundle.putLong("sourceId",user.getUserId());
		    followingFragment.setArguments(followingFragmentBundle);
		    followingFragment.show(getFragmentManager(), ""+followingFragment.getId());
	      break;
	    case R.id.profile_tv_follower:
	    	bv_follower.setText("0");
	    	bv_follower.hide();
	    	UserFragment followerFragment = new UserFragment();
		    Bundle followierFragmentBundle = new Bundle();
		    followierFragmentBundle.putSerializable("sourceType", UserFragment.SOURCETYPE.FOLLOWEDUSER);
		    followierFragmentBundle.putLong("sourceId",user.getUserId());
		    followerFragment.setArguments(followierFragmentBundle);
		    followerFragment.show(getFragmentManager(), ""+followerFragment.getId());
	      break;
	    case R.id.profile_bt_post:
	    	if(user.getUserType()==USERTYPE.STYLIST && user.getSalonId()==0){
	    		bt_salon.performClick();
	    	}else{
	    		Intent postIntent = new Intent();
	    		postIntent.setClass(getActivity(), app.getLoginUser().getUserType()==USERTYPE.STYLIST?PosterActivity.class:ShowActivity.class);
	    		startActivity(postIntent);
	    	}
	      break;
	    case R.id.profile_bt_set:
	    	if(user.getUserId()==app.getLoginUser().getUserId()){
	    		new AlertDialog.Builder(this.getActivity())
	    		.setItems(getResources().getStringArray(R.array.action_userProfile), new ProfileListener())
	    		.create().show();
	    	}
	      break;
	    case R.id.profile_bt_signup:
	    	Intent signupIntent=new Intent();
	    	signupIntent.setClass(getActivity(), RegisterActivity.class);
	    	this.startActivity(signupIntent);
	    	getActivity().finish();
	      break;
	    case R.id.profile_bt_login:
	    	Intent loginIntent=new Intent();
	    	loginIntent.setClass(getActivity(), LoginActivity.class);
	    	this.startActivity(loginIntent);
	    	getActivity().finish();
		      break;
	    case R.id.profile_bt_salon:
	    	Intent newSalonIntent = new Intent();
    		newSalonIntent.setClass(getActivity(), SalonActivity.class);
    		startActivity(newSalonIntent);
	    	break;
	    case R.id.profile_lo_salon:
//	    	if(user.getSalonId()>0){
//	    		AsyncHttpClient client=new AsyncHttpClient();
//				client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
//		        client.get(Constance.SERVER_URL+"salon/"+user.getSalonId(), new AsyncHttpResponseHandler()
//		        {
//		          @Override
//				public void onFailure(Throwable throwable, String content)
//		          {
//		        	  if(ProfileFragment.this.isAdded())AlertHelper.showToast(getActivity(), R.string.error_serverError);
//		          }
//
//		          @Override
//				public void onSuccess(String content){
//		        	  Salon salon=new Gson().fromJson(content, Salon.class);
//		        	  Intent intent = new Intent();
//		        	  intent.putExtra("salon", salon);
//		        	  intent.setClass(getActivity(), SalonActivity.class);
//			    	  startActivity(intent);
//		          }
//		        });
//	    	}
	    	Intent intent = new Intent();
      	    intent.putExtra("salonId", user.getSalonId());
      	    intent.setClass(getActivity(), SalonActivity.class);
	    	startActivity(intent);
	      break;
	    case R.id.profile_bt_chat:
	      Intent chatIntent = new Intent();
	      chatIntent.putExtra("userId", user.getUserId());
	      chatIntent.setClass(getActivity(), ChatActivity.class);
	      startActivity(chatIntent);
	      break;
	    case R.id.profile_bt_follow:
	    	AsyncHttpClient client=new AsyncHttpClient();
			client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
	        client.post(Constance.SERVER_URL+"following/"+user.getUserId(), new AsyncHttpResponseHandler()
	        {
	          @Override
			public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error)
	          {
	        	  if(ProfileFragment.this.isAdded())AlertHelper.showToast(getActivity(), R.string.error_serverError);
	          }

	          @Override
			public void onSuccess(int statusCode,Header[] headers,byte[] content)
	          {
	        	  if(ProfileFragment.this.isAdded())AlertHelper.showToast(getActivity(), R.string.info_followDone);
	              app.refreshUser();
	          }
	        });
	        break;
	}
	
}
@Override
public boolean onLongClick(View v) {
	switch(v.getId()){
	case R.id.profile_iv_figure:
		new AlertDialog.Builder(this.getActivity()).setTitle(R.string.profile_dl_figure_title)
		.setItems(getResources().getStringArray(R.array.action_takePhoto), new TakePhotoListener(TAKE_PHOTO_FOR_FIGURE))
		.create().show();
		break;
	case R.id.profile_tv_username:
		new AlertDialog.Builder(this.getActivity())
		.setItems(getResources().getStringArray(R.array.action_userProfile), new ProfileListener())
		.create().show();
		break;
	case R.id.profile_tv_about:
		InputDialog aboutDialog = InputDialog.newInstance(getResources().getString(R.string.profile_tv_about_text), tv_about.getText().toString(), null,InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE, 40);
		aboutDialog.addListener(new InputDialog.InputListener()
        {
          @Override
		public void OnFinish(String content)
          {
        	  AsyncHttpClient client=new AsyncHttpClient();
  			client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
            RequestParams params = new RequestParams();
            params.put("about", content);
            client.put(Constance.SERVER_URL+"user/about", params, new AsyncHttpResponseHandler(){
              @Override
			public void onSuccess(int statusCode,Header[] headers,byte[] content){
                User u = new Gson().fromJson(new String(content), User.class);
                app.setLoginUser(u);
                user = u;
                renderData();
              }
                @Override
                public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
                    Log.d("-ProfileFragment","updateAbout:failure");
                    Log.getStackTraceString(error);
                }
            });
          }
        });
		aboutDialog.show(getFragmentManager(), ""+tv_about.getId());
		break;
	}
	return false;
}
//private void setAddress(){
//	final float lat=app.getLatitude();
//	final float lon=app.getLongitude();
//    if (!(lat==0 && lon==0)){
//    	String defaultAddress = app.getAddress().trim();
//    	String savedAddress=tv_address.getText().toString();
//    	if(savedAddress!=null)savedAddress=savedAddress.trim();
//      if (!"".equals(savedAddress)){
//    	  defaultAddress=savedAddress;
//      }
//        
//      InputDialog addressDialog = InputDialog.newInstance(getResources().getString(R.string.profile_tv_address_input), defaultAddress, InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE, 100);
//      addressDialog.addListener(new InputDialog.InputListener()
//      {
//        @Override
//		public void OnFinish(String input)
//        {
//        	if(input==null||input.trim().length()==0)return;
//      	  AsyncHttpClient client=new AsyncHttpClient();
//			client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
//          RequestParams params = new RequestParams();
//          params.add("address", input.trim());
//          params.add("latitude", ""+lat);
//          params.add("longitude", ""+lon);
//          client.put(Constance.SERVER_URL+"user/address", params, new AsyncHttpResponseHandler(){
//            @Override
//			public void onFailure(Throwable throwable, String content){
//          	  if(ProfileFragment.this.isAdded())AlertHelper.showToast(getActivity(), R.string.error_serverError);
//            }
//
//            @Override
//			public void onSuccess(String content)
//            {
//              User u =new Gson().fromJson(content, User.class);
//              if (u != null)
//              {
//                user = u;
//                app.setLoginUser(u);
//              }
//              renderData();
//            }
//          });
//        }
//      });
//      addressDialog.show(getFragmentManager(), ""+tv_about.getId());
//    }else{
//  	  if(ProfileFragment.this.isAdded())AlertHelper.showToast(getActivity(),R.string.status_noLocation);
//    }
//}
class ProfileListener implements DialogInterface.OnClickListener{
	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch(which){
		case 0:
//			app.setLoginUser(null);
			app.init();
			Intent intent=new Intent();
			intent.setClass(getActivity(), MainActivity.class);
			startActivity(intent);
			getActivity().finish();
			break;
		case 1:
			Intent profileIntent=new Intent();
			profileIntent.setClass(getActivity(), ProfileEditorActivity.class);
			startActivity(profileIntent);
			break;
		case 2:
			Intent passwordIntent=new Intent();
			passwordIntent.setClass(getActivity(), PasswordActivity.class);
			startActivity(passwordIntent);
			break;
		case 3:
			Intent aboutIntent=new Intent();
			aboutIntent.setClass(getActivity(), AboutActivity.class);
			startActivity(aboutIntent);
			break;
		}
	}
}
class TakePhotoListener implements DialogInterface.OnClickListener{
	int i;
	 public TakePhotoListener(int i){
		 this.i=i;
	 }

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == 0) {
			Intent intent = new Intent(
					"android.media.action.IMAGE_CAPTURE");
			intent.putExtra(
					"output",
					Uri.fromFile(Constance.sdcardTempFile));
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);// �ü������
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 360);// ���ͼƬ��С
			intent.putExtra("outputY", 360);
			startActivityForResult(intent,i);
		} else if(which==1){
			Intent intent = new Intent(
					"android.intent.action.PICK");
			intent.setDataAndType(
					MediaStore.Images.Media.INTERNAL_CONTENT_URI,
					"image/*");
			intent.putExtra(
					"output",
					Uri.fromFile(Constance.sdcardTempFile));
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);// �ü������
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 360);// ���ͼƬ��С
			intent.putExtra("outputY", 360);
			startActivityForResult(intent, i);
		}else if(which==2 && !isRotating){
			new LoadBitmapTask().execute(i);
		}
	}	 
}
private class LoadBitmapTask extends AsyncTask<Integer, Void, Bitmap> {
	int i;

	@Override
	protected Bitmap doInBackground(Integer... arg0) {
		isRotating=true;
		i=arg0[0];
		if(i==TAKE_PHOTO_FOR_FIGURE)
		 return ImageHelper.loadBitmap(getActivity(), user.getFigure(), DensityUtil.px2dip(getActivity(), 360));
		else if(i==TAKE_PHOTO_FOR_SHOP)
			return ImageHelper.loadBitmap(getActivity(), user.getShopImage(), DensityUtil.px2dip(getActivity(), 360));
		return null;
	}
	@Override
    protected void onPostExecute(Bitmap ob) {
		if(ob==null)return;
		Bitmap nb=ImageHelper.rotateBitmap(ob, 90);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();  
         nb.compress(CompressFormat.JPEG, 100, bos);  
         if(i==TAKE_PHOTO_FOR_FIGURE){
        	 uploadFigure(bos.toByteArray());
         }else if(i==TAKE_PHOTO_FOR_SHOP){
        	 uploadShop(bos.toByteArray());
         }
         isRotating=false;
	}
}
@Override
public void onActivityResult (int requestCode, int resultCode, Intent data) {
         if (requestCode==TAKE_PHOTO_FOR_SHOP && resultCode == Activity.RESULT_OK) {
             Bitmap bmp = BitmapFactory.decodeFile(Constance.sdcardTempFile.getAbsolutePath());
             int pix=DensityUtil.dip2px(getActivity(), 360);
        	 bmp = Bitmap.createScaledBitmap(bmp ,pix,pix, false);
             ByteArrayOutputStream bos = new ByteArrayOutputStream();  
             bmp.compress(CompressFormat.JPEG, 75, bos);
             uploadShop(bos.toByteArray());
                 
         }else if(requestCode==TAKE_PHOTO_FOR_FIGURE && resultCode == Activity.RESULT_OK){
        	 Bitmap bmp = BitmapFactory.decodeFile(Constance.sdcardTempFile.getAbsolutePath());
        	 int pix=DensityUtil.dip2px(getActivity(), 360);
        	 bmp = Bitmap.createScaledBitmap(bmp ,pix,pix, false);
        	 ByteArrayOutputStream bos = new ByteArrayOutputStream();  
             bmp.compress(CompressFormat.JPEG, 75, bos);  
             uploadFigure(bos.toByteArray());
         }
     }
private void uploadFigure(byte[] data){
    AsyncHttpClient client=new AsyncHttpClient();
//	client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
	    Header header = BasicScheme.authenticate(credentials, "UTF-8", false);
	    client.addHeader(header.getName(), header.getValue());
    RequestParams params=new RequestParams();
    params.put("figure", new ByteArrayInputStream(data));
    client.post(Constance.SERVER_URL+"/user/figure", params, new AsyncHttpResponseHandler(){
   	 @Override
	     public void onStart() {
	     }

	     @Override
	     public void onSuccess(int statusCode,Header[] headers,byte[] content) {
	         User user=new Gson().fromJson(new String(content), User.class);
	         if(user!=null){
//	        	 ProfileFragment.this.user=user;
		         app.setLoginUser(user);
//		         ImageHelper.loadImg(iv_figure, user.getFigure(),0,true);
		         onResume();
	         }
	     }

	     @Override
	     public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
            Log.e("-ProfileFragment","postFigure:onFailure:"+Log.getStackTraceString(error));
	    	 AlertHelper.showToast(getActivity(), R.string.error_serverError);
	     }
	     @Override
	     public void onFinish() {
	     }
    });
}
private void uploadShop(byte[] data){
	AsyncHttpClient client=new AsyncHttpClient();
    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
	    Header header = BasicScheme.authenticate(credentials, "UTF-8", false);
	    client.addHeader(header.getName(), header.getValue());
//	client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
    RequestParams params=new RequestParams();
    params.put("shopImage", new ByteArrayInputStream(data));
    client.post(Constance.SERVER_URL+"/user/shopImage", params, new AsyncHttpResponseHandler(){
   	 @Override
	     public void onStart() {
	     }

	     @Override
	     public void onSuccess(int statusCode,Header[] headers,byte[] content) {
	         User user=new Gson().fromJson(new String(content), User.class);
	         if(user!=null){
//	        	 ProfileFragment.this.user=user;
		         app.setLoginUser(user);
//		         ImageHelper.loadImg(iv_shop, user.getShopImage(),0,true);
		         onResume();
	         }
	     }

	     @Override
	     public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
	    	 if(ProfileFragment.this.isAdded())AlertHelper.showToast(getActivity(),R.string.error_serverError);
	     }
	     @Override
	     public void onFinish() {
	     }
    });
}
class UserTask extends AsyncTask<Void, Void, Void>{
	User userGot;

	@Override
	protected Void doInBackground(Void... params) {
		Log.d("-ProfileFragment","UserTask:doInBackground");
		AndroidHttpClient client = null;
		try {
		HttpGet request = new HttpGet(Constance.SERVER_URL+"user/"+userId);
		if(app.getLoginUser().getUserId()!=0)
		request.addHeader(BasicScheme.authenticate(
				 new UsernamePasswordCredentials(app.getLoginUser().getSign(), app.getLoginUser().getPassword()),
				 "UTF-8", false));

		client=AndroidHttpClient.newInstance("profileGetUser");
		String response=client.execute(request,new BasicResponseHandler());
			userGot=new Gson().fromJson(response, User.class);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(client!=null)client.close();
        } 
		return null;
	}

	@Override
	protected void onPostExecute(Void param) {
		if(userGot!=null){
			user=userGot;
			if(userGot.getUserId()==app.getLoginUser().getUserId()){
				int bookedNum=userGot.getBookedNum()-user.getBookedNum();
				int commentedNum=userGot.getCommentedNum()-user.getCommentedNum();
				int followerNum=userGot.getFollowerNum()-user.getFollowerNum();
				int changeNum=bookedNum+commentedNum+followerNum;
				if(userGot.getUserType()==USERTYPE.STYLIST){
					if(bookedNum>0){
						bv_booked.setText(""+bookedNum);
						bv_booked.toggle();
					}
					if(commentedNum>0){
						bv_commented.setText(""+commentedNum);
						bv_commented.toggle();
					}
					if(followerNum>0){
						bv_follower.setText(""+followerNum);
						bv_follower.toggle();
					}
					if(changeNum>0){
						((MainActivity)getActivity()).bv_me.setText(""+changeNum);
						((MainActivity)getActivity()).bv_me.toggle();
					}
				}
				app.setLoginUser(userGot);
				}
				renderData();
			}
	}
	}
//class CityTask extends AsyncTask<Void, Void, String>{
//	TextView tv;
//	Context context;
//	User user;
//	public CityTask(Context context,TextView tv,User user){
//		this.context=context;
//		this.tv=tv;
//		this.user=user;
//	}
//	@Override
//	protected String doInBackground(Void... params) {
////		Log.d("-UserFragment", "CityTask:"+user.getLatitude()+","+user.getLongitude());
//		String cityName=LocationHelper.getCity(user.getLatitude(), user.getLongitude());
//		return cityName;
//	}
//
//	@Override
//	protected void onPostExecute(String cityName) {
//		if(tv_city!=null && cityName!=null && cityName.length()>0){
//			tv_city.setText(cityName);
//			tv_city.setVisibility(View.VISIBLE);
//			try {
//				this.finalize();
//			} catch (Throwable e) {
//				Log.e("-ProfileFragment", Log.getStackTraceString(e));
//			}
//		}
//	}
//	}
}
