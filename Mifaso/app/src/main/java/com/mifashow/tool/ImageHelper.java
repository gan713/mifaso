package com.mifashow.tool;
import java.io.File;

import com.mifashow.App;
import com.mifashow.data.Constance;
import com.mifashow.tool.image.DensityUtil;
import com.mifashow.tool.image.ImageFetcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class ImageHelper {
	public static void loadImg(final ImageView imageView, final String fileName,final int shadow,final boolean fadeIn){
		if(imageView==null ||fileName==null || "".equals(fileName))return;
		imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
		    @Override
			public boolean onPreDraw() {
		    	imageView.getViewTreeObserver().removeOnPreDrawListener(this);
		    	ImageFetcher imageFetcher = new ImageFetcher(imageView.getContext(), imageView.getWidth());
			    imageFetcher.setShadow(shadow);
			    imageFetcher.setImageFadeIn(fadeIn);
//			    Log.d("-ImageHelper","context="+(imageView.getContext()==null));
			    imageFetcher.setImageCache(((App)imageView.getContext().getApplicationContext()).getImageCache());
//			    if(imageView.getDrawable()!=null)
//			    	imageFetcher.setLoadingImage(BitmapFactory.decodeResource(imageView.getContext().getResources(), R.drawable.placeholder));
			    imageFetcher.loadImage(Constance.SERVER_URL+"image/"+fileName.substring(fileName.lastIndexOf("/")+1)+"/"+(imageView.getWidth()), imageView);
//			    Log.d("-ImageHelper",Constance.SERVER_URL+"image/"+fileName.substring(fileName.lastIndexOf("/")+1)+"/"+DensityUtil.px2dip(imageView.getContext(), imageView.getWidth()));
		        return true;
		    }
		});
		
	  }
	public static Bitmap loadBitmap(Context context,String fileName,int size){
		File f = ImageFetcher.downloadBitmap(context, Constance.SERVER_URL+"image/"+fileName.substring(fileName.lastIndexOf("/")+1)+"/"+DensityUtil.dip2px(context, size));
		Log.d("-ImageHelper",Constance.SERVER_URL+"image/"+fileName.substring(fileName.lastIndexOf("/")+1)+"/"+DensityUtil.dip2px(context, size));
		if(f!=null){
			return BitmapFactory.decodeFile(f.getAbsolutePath());
		}
		return null;
	}
	public static Drawable getRoundedCornerBitmap(Context context,Bitmap bitmap){
		Bitmap output=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Config.ARGB_8888);
		Canvas canvas=new Canvas(output);
		final int color=0xff424242;
		final Paint paint=new Paint();
		final Rect rect=new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF=new RectF(rect);
		final float roundPx=bitmap.getWidth()/2;
//				DensityUtil.px2dip(context, 12);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect,paint);
		Drawable outputDrawable=new BitmapDrawable(output);
		return outputDrawable;
		}

	public static Bitmap rotateBitmap(Bitmap bm, final int orientationDegree) {
		Matrix m = new Matrix();
		m.setRotate(orientationDegree, (float) bm.getWidth() / 2,
				(float) bm.getHeight() / 2);
		float targetX, targetY;
		if (orientationDegree == 90) {
			targetX = bm.getHeight();
			targetY = 0;
		} else {
			targetX = bm.getHeight();
			targetY = bm.getWidth();
		}
		final float[] values = new float[9];
		m.getValues(values);
		float x1 = values[Matrix.MTRANS_X];
		float y1 = values[Matrix.MTRANS_Y];
		m.postTranslate(targetX - x1, targetY - y1);
		Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(),Bitmap.Config.ARGB_8888);
		Paint paint = new Paint();
		Canvas canvas = new Canvas(bm1);
		canvas.drawBitmap(bm, m, paint);
		return bm1;
	}


}
