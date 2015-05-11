package com.mifashow;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mifashow.data.Constance;
import com.mifashow.data.Constance.FACESHAPE;
import com.mifashow.data.Constance.POSTINGTYPE;
import com.mifashow.domain.Message;
import com.mifashow.domain.MessageBox;
import com.mifashow.domain.Posting;
import com.mifashow.tool.AlertHelper;
import com.mifashow.tool.ImageHelper;
import com.mifashow.tool.image.DensityUtil;
import com.mifashow.tool.image.ImageFetcher;
import com.mifashow.tool.image.Utils;
import com.mifashow.ui.RenderView;
import com.mifashow.ui.ResizedImageView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.Header;

/**
 * TODO: document your custom view class.
 */
public class PostingView extends RelativeLayout {
	FragmentActivity activity;
	  private App app;
	  private LinearLayout lo_booking;
	  private TextView tv_booking;
	  private LinearLayout lo_comment;
	  private TextView tv_comment;
	  private LinearLayout lo_handle;
	  private LinearLayout lo_marking;
	  private TextView tv_marking;
	  private FragmentManager fm;
	  private ResizedImageView iv_figure;
	  private Posting posting;
	  private RenderView rv;
	  private TextView tv_text;
	  private TextView tv_createrName;
	  private TextView tv_distance;
	  private OnHandleListener listener;
	

	  public PostingView(Context context, AttributeSet attrs)
	  {
	    super(context, attrs);
	    init(context, attrs, 0);
	  }

	  public PostingView(Context context, AttributeSet attrs, int defStyle)
	  {
	    super(context, attrs, defStyle);
	    init(context, attrs, defStyle);
	  }

	  public PostingView(Context context, Posting posting)
	  {
	    super(context);
	    this.activity = ((FragmentActivity)context);
	    this.app = ((App)this.activity.getApplication());
	    this.posting = posting;
	    init(context, null, 0);
	  }


	private void init(final Context context,AttributeSet attrs, int defStyle) {
		LayoutInflater.from(context).inflate(R.layout.view_posting, this,true);
		fm = ((FragmentActivity)context).getSupportFragmentManager();
		iv_figure=(ResizedImageView) findViewById(R.id.posting_iv_figure);
//		iv_figure.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//		    public boolean onPreDraw() {
//		    	iv_figure.getViewTreeObserver().removeOnPreDrawListener(this);
//		    	ImageHelper.loadImg(iv_figure,posting.getCreaterFigure(),0,true);
//		        return true;
//		    }
//		});
		ImageHelper.loadImg(iv_figure,posting.getCreaterFigure(),0,true);
		iv_figure.setOnClickListener(new OnClickListener(){
	      @Override
		public void onClick(View view){
	    	  ProfileFragment profileFragment = new ProfileFragment();
				Bundle bundle = new Bundle();
			      bundle.putLong("userId",posting.getCreaterId());
			      profileFragment.setArguments(bundle);
	        profileFragment.show(fm, ""+profileFragment.getId());
	      }
	    });
		tv_createrName=(TextView) findViewById(R.id.posting_tv_createrName);
		tv_text=(TextView) findViewById(R.id.posting_tv_text);
		tv_distance=(TextView) findViewById(R.id.posting_tv_distance);
		tv_createrName.setText(posting.getCreaterName());
		lo_comment=(LinearLayout)findViewById(R.id.posting_lo_comment);
		tv_comment=(TextView)findViewById(R.id.posting_tv_comment);
		lo_booking=(LinearLayout)findViewById(R.id.posting_lo_booking);
		tv_booking=(TextView)findViewById(R.id.posting_tv_booking);
		lo_marking=(LinearLayout) findViewById(R.id.posting_lo_marking);
		tv_marking=(TextView)findViewById(R.id.posting_tv_marking);
		if(posting.getPostingType()==POSTINGTYPE.POSTER){
			if(posting.getCommentedNum()>0){
				tv_comment.setText(""+posting.getCommentedNum());
			}

			if(app.getLoginUser().getUserId()!=0)
		lo_comment.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View paramAnonymousView){
				if(posting.getCommentedNum()>0){
				CommentDialog commentDialog = new CommentDialog();
				Bundle bundle = new Bundle();
				bundle.putSerializable("sourceType", CommentDialog.SOURCETYPE.POSTING);
				bundle.putLong("sourceId",posting.getPostingId());
				commentDialog.setArguments(bundle);
				commentDialog.show(activity.getSupportFragmentManager(), ""+commentDialog.getId());
				}else{
					AlertHelper.showToast(activity,R.string.info_noComment);
				}
	          }
	        });
		if(posting.getBookedNum()>0){
			tv_booking.setText(""+posting.getBookedNum());
		}
		if(app.getLoginUser().getUserId()!=-1)
		lo_booking.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view){
				if ((posting.getBookingDay() != null) && (posting.getBookingDay().length > 0)){
					BookDialog bookDialog = BookDialog.newInstance(posting);
					bookDialog.show(fm, ""+bookDialog.getId());
				}else{
		        	AlertHelper.showToast(activity,R.string.info_noBookingSet);
		        }
			}
		});
		}else{
			lo_comment.setVisibility(View.GONE);
			lo_booking.setVisibility(View.GONE);
		}
		if(posting.getPostingType()==POSTINGTYPE.POSTER && posting.getSalonName()!=null){
			tv_text.setText(posting.getSalonName()+"-"+posting.getAddress());
		}else{
			long between=System.currentTimeMillis()-posting.getCreateTime();
			String s = null;
			if(DateUtils.isToday(posting.getCreateTime())){
				if(between<(60*60*1000)){
					int min=(int)between/1000/60;
					if(min>0){
						s=getResources().getString(R.string.time_min,min);
					}else{
						s=getResources().getString(R.string.time_now);
					}
				}else if(between<24*60*60*1000){
					int hour=(int)between/1000/60/60;
					s=getResources().getString(R.string.time_hour,hour);
				}
			}else if(DateUtils.isToday(posting.getCreateTime()+24*60*60*1000)){
				s=getResources().getString(R.string.time_yesterday);
			}else{
				s=new SimpleDateFormat("MM-dd",Locale.getDefault()).format(new Date(posting.getCreateTime()));
			}
			tv_text.setText(s);
		}
		if(posting.getMarkedNum()>0){
			tv_marking.setText(""+posting.getMarkedNum());
		}

		if(app.getLoginUser().getUserId()!=0)
		this.lo_marking.setOnClickListener(new View.OnClickListener()
	    {
	      @Override
		public void onClick(View view){
	    	  if(posting.getCreaterId()==app.getLoginUser().getUserId()){
	    		  AlertHelper.showToast(activity,R.string.error_markYourself);
	    		  return;
	    	  }
	    	  boolean isMarked=false;
	    	  for(Posting p:app.getMarkingPostings()){
	    		  if(p.getPostingId()==posting.getPostingId()){
	    			  isMarked=true;
//	    			  AlertHelper.showToast(activity, R.drawable.ic_umbrella_32, getResources().getString(R.string.error_markAlready));
		    		  break;
	    		  }
	    	  }
	    	  if(isMarked){
	    		  AlertHelper.showAlert(activity, getResources().getString(R.string.dialog_dismarkPosting_title), getResources().getString(R.string.dialog_dismarkPosting_message), getResources().getString(R.string.action_ok), new DialogInterface.OnClickListener() {
	    	            @Override
	    				public void onClick(DialogInterface dialog, int id) {
	    	            	AsyncHttpClient client=new AsyncHttpClient();
							client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
	    		    		  client.delete(Constance.SERVER_URL+"marking/"+posting.getPostingId(), new AsyncHttpResponseHandler(){
	    		    			  @Override
	    		    			  public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
	    		    				  AlertHelper.showToast(context, R.string.error_serverError);
	    		    				  }
	    		    			  @Override
	    		    			  public void onSuccess(int statusCode,Header[] headers,byte[] content){
	    		    				  AlertHelper.showToast(context, R.string.info_dismarkDone);
	    		    				  posting.setMarkedNum(posting.getMarkedNum()-1);
	    		    				  tv_marking.setText(posting.getMarkedNum()==0?null:""+posting.getMarkedNum());
	    		    				  if(listener!=null)listener.onDismark();
	    		    				  app.refreshUser();
	    		    				  }
	    		    			  });
	    	            }
	    	        }, getResources().getString(R.string.action_cancel), 
	    	        new DialogInterface.OnClickListener() {
	    	            @Override
	    				public void onClick(DialogInterface dialog, int id) {}
	    	        });
	    	  }else{
	    		  AsyncHttpClient client=new AsyncHttpClient();
					client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
	    		  client.post(Constance.SERVER_URL+"marking/"+posting.getPostingId(), new AsyncHttpResponseHandler(){
	    			  @Override
	    			  public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
	    				  AlertHelper.showToast(context, R.string.error_serverError);
	    				  }
	    			  @Override
	    			  public void onSuccess(int statusCode,Header[] headers,byte[] content){
	    				  AlertHelper.showToast(context, R.string.info_markDone);
	    				  posting.setMarkedNum(posting.getMarkedNum()+1);
	    				  tv_marking.setText(""+posting.getMarkedNum());
	    				  if(listener!=null)listener.onMark();
	    				  }
	    			  });
	    		  }
	    	  }
	      });
		lo_handle=(LinearLayout)findViewById(R.id.posting_lo_handle);
		lo_handle.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view){
				AlertDialog dialog = null;
				if(app.getLoginUser().getUserId() == posting.getCreaterId() && posting.getPostingType()==POSTINGTYPE.POSTER){
					dialog=new AlertDialog.Builder(context).setItems(getResources().getStringArray(R.array.action_posterCreaterHandle), new PosterCreaterListener()).create();
				}else if(app.getLoginUser().getUserId() == posting.getCreaterId() && posting.getPostingType()==POSTINGTYPE.SHOW){
					dialog=new AlertDialog.Builder(context).setItems(getResources().getStringArray(R.array.action_showCreaterHandle), new ShowCreaterListener()).create();	  
			    }else{
			    	dialog=new AlertDialog.Builder(context).setItems(getResources().getStringArray(R.array.action_postingHandle), new PostingListener()).create();
			    }
				dialog.show();
			}
		});
		rv = ((RenderView)findViewById(R.id.posting_lo_rendering));
	    rv.setImages(posting.getImages());
	    rv.setOnClickListener(new View.OnClickListener(){
	        @Override
			public void onClick(View view){
	          Intent intent = new Intent();
	          intent.putExtra("renderingUrl", posting.getImages());
	          intent.putExtra("posting", posting);
	          intent.setClass(context, ViewerActivity.class);
	          context.startActivity(intent);
	        }
	      });
//	    float lat=app.getLocationHelper().getLatitude();
//	    float lon=app.getLocationHelper().getLongitude();
//	    if(!(lat==0 && lon==0) && !(posting.getLongitude()==0 && posting.getLatitude()==0)){
//	    float[] distanceResults=new float[1];
//		Location.distanceBetween(posting.getLatitude(), posting.getLongitude(),lat,lon, distanceResults);
//		if(distanceResults[0]>=1000){
//			distance=""+(int)distanceResults[0]/1000+"km";
//		}else{
//			distance=""+(int)distanceResults[0]+"m";
//		}
//		tv_distance.setText(distance);
		app.getLocationHelper().showCityInTextView(posting.getLatitude(), posting.getLongitude(), tv_distance);
//		if(city==null)new CityTask().executeOnExecutor(Executors.newSingleThreadExecutor(),(Void)null);
//		}else{
//			tv_distance.setVisibility(View.GONE);
//		}
		
//		else{
//			Geocoder geocoder = new Geocoder(context);
//	        List<Address> places = null;
//	        try
//	        {
//	            places = geocoder.getFromLocation(posting.getLatitude(), posting.getLongitude(), 1);           
//	        }catch(Exception e){
//	            e.printStackTrace();
//	        }
//	       
//	        if(places != null && places.size() > 0)
//	        {
//	        	distanceStr = places.get(0).getAddressLine(0);
//	        }
//		}
//		tv_distance.setText(distanceStr);
		
		

        
//        ListView commentList=(ListView) findViewById(R.id.posting_lv_comment);
//        if(commentList!=null && posting.getComments()!=null){
//        	CommentAdapter adapter=new CommentAdapter(posting.getComments());
//        	commentList.setAdapter(adapter);
//        	}
	}
	
	private void deletePosting(){
		AlertHelper.showAlert(activity, getResources().getString(R.string.dialog_deletePosting_title), getResources().getString(R.string.dialog_deletePosting_message), getResources().getString(R.string.action_ok), new DialogInterface.OnClickListener() {
            @Override
			public void onClick(DialogInterface dialog, int id) {
            	AsyncHttpClient client=new AsyncHttpClient();
				client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
    			client.delete(Constance.SERVER_URL+"posting/"+posting.getPostingId(), new AsyncHttpResponseHandler(){
    				@Override
    				public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
    					AlertHelper.showToast(activity, R.string.error_serverError);
    				}
    				@Override
    				public void onSuccess(int statusCode,Header[] headers,byte[] content){
    					if(listener!=null)listener.onDelete();
    				}
    			});
            }
        }, getResources().getString(R.string.action_cancel), 
        new DialogInterface.OnClickListener() {
            @Override
			public void onClick(DialogInterface dialog, int id) {}
        });
	}
	class PosterCreaterListener implements DialogInterface.OnClickListener{
		PosterCreaterListener(){}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch(which){
			case 0:
				showSimilars();
				break;
			case 1:
				new Thread(){
					@Override
					public void run(){
						share();
					}
				}.start();
				break;
			case 2:
			      listener.onBookingSet();
			      break;
			case 3:
				deletePosting();
				break;
			}
		}
	}
	class ShowCreaterListener implements DialogInterface.OnClickListener{
		ShowCreaterListener(){}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch(which){
			case 0:
				showSimilars();
				break;
			case 1:
				new Thread(){
					@Override
					public void run(){
						share();
					}
				}.start();
				break;
			case 2:
				deletePosting();
				break;
			}
		}
	}
	class ReportListener implements DialogInterface.OnClickListener{
		ReportListener(){}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			RequestParams params = new RequestParams();
			params.put("reportType", Constance.REPORTTYPE.values()[which].name());
			AsyncHttpClient client=new AsyncHttpClient();
			client.setBasicAuth(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
	        client.post(getContext(), Constance.SERVER_URL+"report/"+posting.getPostingId(), params, new AsyncHttpResponseHandler(){
	        	  @Override
					public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
		              AlertHelper.showToast(activity, R.string.error_serverError);
		            }

		            @Override
					public void onSuccess(int statusCode,Header[] headers,byte[] content){
		            	Message message=new Gson().fromJson(new String(content), Message.class);
		            	AlertHelper.showToast(activity, R.string.info_reportDone);
		            	if(app.getMessageBoxes().containsKey(message.getToUserId())){
		            		app.getMessageBoxes().get(message.getToUserId()).addMessage(message, false);
		            	}else{
		            		MessageBox mb=new MessageBox();
		            		mb.addMessage(message, false);
		            		app.getMessageBoxes().put(message.getToUserId(), mb);
		            	}
//		            	app.getMessageBoxes().get(0).addMessage(message, false);
		        		app.setMessageBoxes(app.getMessageBoxes());
		            }
	          });
		}
	}
	class PostingListener implements DialogInterface.OnClickListener{
		PostingListener(){}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch(which){
			case 0:
				showSimilars();
				break;
			case 1:
				new Thread(){
					@Override
					public void run(){
						share();
					}
				}.start();
//				new Thread(runnable).start();
				break;
			case 2:
				if(app.getLoginUser().getUserId()!=0){
				AlertDialog reportDialog=new AlertDialog.Builder(getContext()).setItems(getResources().getStringArray(R.array.enum_reportType), new ReportListener()).create();
				reportDialog.show();
				}
				break;
			}
		}
	}
	public void setOnHandleListener(OnHandleListener listener) {
	this.listener = listener;
}
//	private Runnable runnable = new Runnable() {
//        public void run() {
//        	handler.sendMessage(new android.os.Message());
//        }
//    }; 
//    Handler handler = new Handler(){
//        @Override
//        public void handleMessage(android.os.Message msg) {
//            super.handleMessage(msg);
//            share();
//        }
//    };
	
	private void showSimilars(){
		PostingFragment postingFragment = new PostingFragment();
	      Bundle bundle = new Bundle();
	      bundle.putSerializable("sourceType", PostingFragment.SOURCETYPE.SIMILAR);
	      bundle.putLong("sourceId", posting.getPostingId());
	      postingFragment.setArguments(bundle);
	      postingFragment.show(activity.getSupportFragmentManager(), ""+postingFragment.getId());
	}

	private void share(){
		StringBuilder sb_faceShape=new StringBuilder();
		for(FACESHAPE shapeShape:posting.getFaceShapes()){
			sb_faceShape.append(getResources().getStringArray(R.array.enum_faceShape)[shapeShape.ordinal()]).append(',').append(' ');
		}
		sb_faceShape.delete(sb_faceShape.length()-2,sb_faceShape.length()-1);
		StringBuilder sb_hairType=new StringBuilder();
		sb_hairType.append(getResources().getStringArray(R.array.enum_bangType)[posting.getBangType().ordinal()]).append(" ").append(getResources().getStringArray(R.array.enum_curlyType)[posting.getCurlyType().ordinal()]).append(" ").append(getResources().getStringArray(R.array.enum_hairLength)[posting.getHairLength().ordinal()]);
		Intent intent=new Intent(Intent.ACTION_SEND_MULTIPLE);
		intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_text,sb_hairType.toString(),sb_faceShape.toString()));
        intent.putExtra("Kdescription", getResources().getString(R.string.share_text,sb_hairType.toString(),sb_faceShape.toString()));
//        intent.putExtra("notif_icon", R.drawable.icon_bow);
//        intent.putExtra("notif_title", getResources().getString(R.string.app_name));
//        intent.putExtra("titleUrl", "http://www.mifashow.com");
//        intent.putExtra("url", "http://a.app.qq.com/o/simple.jsp?pkgname=com.mifashow&g_f=992943");
//        intent.putExtra("siteUrl", "http://www.mifashow.com");
//        intent.putExtra("title", getResources().getString(R.string.share_subject));
//        intent.putExtra("text", getResources().getString(R.string.share_text,sb_hairType.toString(),sb_faceShape.toString(),getResources().getStringArray(R.array.enum_sex)[posting.getSex().ordinal()]));
//        intent.putExtra("site", getResources().getString(R.string.app_name));
//        intent.putExtra("silent", false);
        ArrayList<Uri> uris=new ArrayList<Uri>();
        Bitmap watermarkBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_bow);
//        int width = watermarkBitmap.getWidth();
//        int height = watermarkBitmap.getHeight();
//        int newWidth = 50;
//        int newHeight = 50;
//        float scaleWidth = ((float) newWidth) / width;
//        float scaleHeight = ((float) newHeight) / height;
//        Matrix matrix=new Matrix();
//        matrix.postScale(scaleWidth, scaleHeight);
//        watermarkBitmap=Bitmap.createBitmap(watermarkBitmap,0,0,watermarkBitmap.getWidth(),
//        		watermarkBitmap.getHeight(),matrix,true);
        watermarkBitmap=Bitmap.createScaledBitmap(watermarkBitmap, 50, 50, true);
//        watermarkBitmap=ImageHelper.drawImageDropShadow(watermarkBitmap);

//        watermarkBitmap=Bitmap.createBitmap(watermarkBitmap, 0, 0, 80, 80);
        
//        Log.e("-share","water="+watermarkBitmap.getWidth());
			for(int i=0;i<posting.getImages().length;i++){
				try {
					Bitmap targetBitmap = ImageHelper.loadBitmap(activity, posting.getImages()[i], DensityUtil.px2dip(activity, 360));
					if (targetBitmap != null) {
//					File sourceFile = ImageFetcher.downloadBitmap(activity, posting.getImages()[i]);
//					if (sourceFile != null) {
//						BitmapFactory.Options options = new BitmapFactory.Options();
//						options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//						Bitmap targetBitmap = BitmapFactory.decodeFile(sourceFile.getAbsolutePath(), options);
			        int w = targetBitmap.getWidth();
			        int h = targetBitmap.getHeight();
			        int wh = watermarkBitmap.getHeight();
			        Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);
			        Canvas cv = new Canvas(newb);
			        cv.drawBitmap(targetBitmap, 0, 0, null);
			        cv.drawBitmap(watermarkBitmap, 5, h - wh - 5, null);
			        Paint textPaintForUserName = new Paint( Paint.ANTI_ALIAS_FLAG); 
			        textPaintForUserName.setTextSize(14);  
			        textPaintForUserName.setColor(getResources().getColor(R.color.lightest));
			        Paint textPaintForAppName = new Paint( Paint.ANTI_ALIAS_FLAG); 
			        textPaintForAppName.setTextSize(14);  
			        textPaintForAppName.setColor(getResources().getColor(R.color.lightest_transparent));
			        Paint rectPaintForBackground = new Paint( Paint.ANTI_ALIAS_FLAG); 
			        rectPaintForBackground.setColor(getResources().getColor(R.color.darkest_transparent));
			        cv.drawRect(wh+2, h - wh - 3, wh+16+Math.max(textPaintForUserName.measureText(posting.getCreaterName()),textPaintForAppName.measureText(getResources().getString(R.string.app_name))), h-7, rectPaintForBackground);
			        cv.drawText(getResources().getString(R.string.app_name), 5+wh+5, h-wh/2-10, textPaintForAppName);
			        cv.drawText(posting.getCreaterName(), 5+wh+5, h-15, textPaintForUserName);
			        cv.save(Canvas.ALL_SAVE_FLAG);
			        cv.restore();
//			        File file = File.createTempFile("share", ".png");
//			        file.deleteOnExit();
			        String cachePath =Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED ||!Utils.isExternalStorageRemovable() ?
			                        Utils.getExternalCacheDir(getContext()).getPath() :
			                        	getContext().getCacheDir().getPath();
			        File file =new File(cachePath + File.separator + "share_"+i+".png");
			        FileOutputStream outputStream= new FileOutputStream(file);
			        newb.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
			        outputStream.flush();
			        outputStream.close();
			        uris.add(Uri.fromFile(file));
			        }
			    } catch (Exception e) {
			        e.printStackTrace();
			    }
			}
        uris.add(Uri.fromFile(ImageFetcher.fetchBitmap(getContext(),R.raw.qrcode)));
        Log.e("-share","qrfile="+ImageFetcher.fetchBitmap(getContext(),R.raw.qrcode).length());
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        activity.startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_title)));
	}
	
//	class CityTask extends AsyncTask<Void, Void, String>{
//		@Override
//		protected String doInBackground(Void... params) {
//			String cityName=LocationHelper.getCity(posting.getLatitude(), posting.getLongitude());
//			return cityName;
//		}
//
//		@Override
//		protected void onPostExecute(String cityName) {
//			city=cityName;
//			if(tv_distance!=null && city!=null && city.length()>0){
//				tv_distance.setText(city+distance);
//			}
//			try {
//				this.finalize();
//			} catch (Throwable e) {
//				Log.e("-UserFragment", Log.getStackTraceString(e));
//			}
//		}
//		}

public static abstract interface OnHandleListener {
	public abstract void onDelete();
	public abstract void onMark();
	public abstract void onDismark();
	public abstract void onBookingSet();
}
}
