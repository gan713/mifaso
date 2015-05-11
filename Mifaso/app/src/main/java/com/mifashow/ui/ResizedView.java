package com.mifashow.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class ResizedView extends ViewGroup {

	public ResizedView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ResizedView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ResizedView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
//		int measuredHeight=(int) (widthMeasureSpec*ratio);
//		this.setMeasuredDimension(measuredWidth, measuredHeight);
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		
	}

}
