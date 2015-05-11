package com.mifashow.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class ResizedImageButton extends ImageButton {

	public ResizedImageButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ResizedImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ResizedImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	
	}

}
