package com.mifashow.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class BorderScrollView extends ScrollView {
	private View contentView;
	private OnBorderListener onBorderListener;

	public BorderScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public BorderScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public BorderScrollView(Context context) {
		super(context);
	}
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t,oldl,oldt);
		if ((this.contentView != null) && (t < oldt) && (this.contentView.getMeasuredHeight() <= t + getHeight()) && (this.onBorderListener != null))
			this.onBorderListener.onBottom();
	}

	public void setOnBorderListener(OnBorderListener onBorderListener) {
		this.onBorderListener = onBorderListener;
		if(onBorderListener != null && this.contentView == null)this.contentView = getChildAt(0);
	}

	public static abstract interface OnBorderListener {
		public abstract void onBottom();
	}
	

}
