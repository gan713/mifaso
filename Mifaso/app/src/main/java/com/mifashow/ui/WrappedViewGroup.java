package com.mifashow.ui;

import com.mifashow.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;


public class WrappedViewGroup extends ViewGroup{
	View[] views;
	int width,height,margin;
	TypedArray ta;
	
	
	public WrappedViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context,attrs,defStyle);
		
	}
	public WrappedViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context,attrs,0);
	}
	public WrappedViewGroup(Context context) {
		super(context);
		init(context,null,0);
	}
	private void init(Context context, AttributeSet attrs, int defStyle){
		ta=context.obtainStyledAttributes(attrs,R.styleable.WrappedViewGroup,defStyle,0);
        margin = ta.getDimensionPixelSize(R.styleable.WrappedViewGroup_wvg_margin,5);
//        margin=DensityUtil.dip2px(getContext(), margin);
        ta.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
	    int childCount = getChildCount();
	    int x = 0;
	    int y = 0;
	    int aboveHeight=0;
	    int rowHeight=0;
//	    int row = 0;

	    for (int index = 0; index < childCount; index++) {
	        final View child = getChildAt(index);
	        if (child.getVisibility() != View.GONE) {
	            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
	            int width = child.getMeasuredWidth();
	            int height = child.getMeasuredHeight();
	        	if(rowHeight<height)rowHeight=height;
	            x += width+margin;
//	            y = row * (height+margin) + height+margin;
	            if (x > maxWidth) {
	            	aboveHeight+=rowHeight+margin;
	                x = width+margin;
//	                row++;
//	                y = row * (height+margin) + height+margin;
	            }
	            y=aboveHeight+height+margin;
	        }
	    }
//	    setMeasuredDimension(maxWidth, y+margin);
	    setMeasuredDimension(maxWidth, aboveHeight+rowHeight+margin);
		}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int childCount = getChildCount();
	    int maxWidth = r - l;
	    int x = 0;
	    int y = 0;
	    int aboveHeight=0;
	    int rowHeight=0;
//	    int row = 0;
	    for (int i = 0; i < childCount; i++) {
	        final View child = this.getChildAt(i);
	        if (child.getVisibility() != View.GONE) {
	            int width = child.getMeasuredWidth();
	        	int height = child.getMeasuredHeight();
	        	if(rowHeight<height)rowHeight=height;
	        	x += width+margin;
//	            y = row * (height+margin) + height+margin;
	            if (x > maxWidth) {
	            	aboveHeight+=rowHeight+margin;
	                x = width+margin;
//	                row++;
//	                y = row * (height+margin) + height+margin;
	            }
	        	y=aboveHeight+height+margin;
	            child.layout(x - width, y - height, x, y);
	        }
	    }
	}
	
}
