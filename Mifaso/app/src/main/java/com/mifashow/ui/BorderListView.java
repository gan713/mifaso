package com.mifashow.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

public class BorderListView extends ListView {
//	private View contentView;
	private OnBorderListener onBorderListener;

	public BorderListView(Context context) {
		super(context);
	}

	public BorderListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BorderListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

//	@Override
//	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//		super.onScrollChanged(l, t,oldl,oldt);
//		if(this.onBorderListener != null){
//		if (this.getLastVisiblePosition()==(this.getChildCount()-1) && (t==0||oldt<t)){
//			this.onBorderListener.onBottom();
//		}else{
//			this.onBorderListener.onRoll();
//			}
//		}
//		
//	}

	public void setOnBorderListener(OnBorderListener onBorderListener) {
		this.onBorderListener = onBorderListener;
		this.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem , int visibleItemCount , int totalItemCount ) {
				
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState==OnScrollListener.SCROLL_STATE_IDLE && view.getLastVisiblePosition() == (view.getCount() - 1)){
//					AlertHelper.showToast(getContext(), "Bottom");
					BorderListView.this.onBorderListener.onBottom();
				}else{
					BorderListView.this.onBorderListener.onRoll();
				}
			}
			
		});
	}

	public static abstract interface OnBorderListener {
		public abstract void onBottom();
		public abstract void onRoll();
	}

}
