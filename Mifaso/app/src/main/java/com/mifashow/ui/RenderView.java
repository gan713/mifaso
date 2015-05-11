package com.mifashow.ui;
import com.mifashow.tool.ImageHelper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class RenderView extends LinearLayout {
	private float ratio;

	public RenderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public RenderView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RenderView(Context context) {
		super(context);
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (ratio == 0.0f)
			ratio = 1.0f;
		float measuredWidth = View.MeasureSpec.getSize(widthMeasureSpec);
		super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(
				(int) (ratio * measuredWidth), MeasureSpec.EXACTLY));

	}

	public void setImages(String[] images) {
		ImageView iv1, iv2, iv3, iv4;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		LinearLayout.LayoutParams params_v = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, 0);
		params_v.weight = 1;
		LinearLayout.LayoutParams params_h = new LinearLayout.LayoutParams(0,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		params_h.weight = 1;
		LinearLayout.LayoutParams params_h_b = new LinearLayout.LayoutParams(0,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		params_h_b.weight = 2;
		LinearLayout.LayoutParams params_h_s = new LinearLayout.LayoutParams(0,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		params_h_s.weight = 1;
		switch (images.length) {
		case 1: {
			iv1 = new ImageView(getContext());
			iv1.setLayoutParams(params);
			addView(iv1);
//			iv1.setImageResource(R.drawable.placeholder);
			ImageHelper.loadImg(iv1, images[0],0,true);
			break;
		}
		case 2: {
			this.ratio = 0.5f;
			iv1 = new ImageView(getContext());
			iv2 = new ImageView(getContext());
			iv1.setLayoutParams(params_h);
			iv2.setLayoutParams(params_h);
			addView(iv1);
			addView(iv2);
//			iv1.setImageResource(R.drawable.placeholder);
//			iv2.setImageResource(R.drawable.placeholder);
			ImageHelper.loadImg(iv1, images[0],0,true);
			ImageHelper.loadImg(iv2, images[1],0,true);
			break;
		}
		case 4: {
			iv1 = new ImageView(getContext());
			iv2 = new ImageView(getContext());
			iv3 = new ImageView(getContext());
			iv4 = new ImageView(getContext());
			LinearLayout lo_l = new LinearLayout(getContext());
			lo_l.setOrientation(LinearLayout.VERTICAL);
			LinearLayout lo_r = new LinearLayout(getContext());
			lo_r.setOrientation(LinearLayout.VERTICAL);
			iv1.setLayoutParams(params_v);
			iv2.setLayoutParams(params_v);
			iv3.setLayoutParams(params_v);
			iv4.setLayoutParams(params_v);
			lo_l.setLayoutParams(params_h);
			lo_r.setLayoutParams(params_h);
			addView(lo_l);
			addView(lo_r);
//			iv1.setImageResource(R.drawable.placeholder);
//			iv2.setImageResource(R.drawable.placeholder);
//			iv3.setImageResource(R.drawable.placeholder);
//			iv4.setImageResource(R.drawable.placeholder);
			lo_l.addView(iv1);
			lo_l.addView(iv2);
			lo_r.addView(iv3);
			lo_r.addView(iv4);
			ImageHelper.loadImg(iv1, images[0],0,true);
			ImageHelper.loadImg(iv2, images[1],0,true);
			ImageHelper.loadImg(iv3, images[2],0,true);
			ImageHelper.loadImg(iv4, images[3],0,true);
			break;
		}
		default: {
			ratio = 0.666667f;
			iv1 = new ImageView(getContext());
			iv1.setLayoutParams(params_h_b);
			addView(iv1);
			LinearLayout lo_r = new LinearLayout(getContext());
			lo_r.setOrientation(LinearLayout.VERTICAL);
			lo_r.setLayoutParams(params_h_s);
			addView(lo_r);
			iv2 = new ImageView(lo_r.getContext());
			iv3 = new ImageView(lo_r.getContext());
			iv2.setLayoutParams(params_v);
			iv3.setLayoutParams(params_v);
			lo_r.addView(iv2);
			lo_r.addView(iv3);
//			iv1.setImageResource(R.drawable.placeholder);
//			iv2.setImageResource(R.drawable.placeholder);
//			iv3.setImageResource(R.drawable.placeholder);
			ImageHelper.loadImg(iv1, images[0],0,true);
			ImageHelper.loadImg(iv2, images[1],0,true);
			ImageHelper.loadImg(iv3, images[2],0,true);
			break;
		}
		}
	}

}
