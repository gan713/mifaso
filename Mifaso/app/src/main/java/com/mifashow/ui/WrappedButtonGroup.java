package com.mifashow.ui;

import java.util.ArrayList;

import com.mifashow.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;
import android.view.View.OnClickListener;


public class WrappedButtonGroup extends ViewGroup implements OnClickListener{
	ToggleButton[] buttons;
	private CharSequence[] texts;
	int width,height,margin;
	TypedArray ta;
	boolean single;
	ArrayList<Integer> check;
	int select=-1;
	
	
	public WrappedButtonGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context,attrs,defStyle);
		
	}
	public WrappedButtonGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context,attrs,0);
	}
	public WrappedButtonGroup(Context context) {
		super(context);
		init(context,null,0);
	}
	private void init(Context context, AttributeSet attrs, int defStyle){
		check=new ArrayList<Integer>();
		ta=context.obtainStyledAttributes(attrs,R.styleable.WrappedButtonGroup,defStyle,0);
		width = ta.getDimensionPixelSize(R.styleable.WrappedButtonGroup_layout_width,0);
        height = ta.getDimensionPixelSize(R.styleable.WrappedButtonGroup_layout_height,0);
        margin = ta.getDimensionPixelSize(R.styleable.WrappedButtonGroup_layout_margin,5);
        single=ta.getBoolean(R.styleable.WrappedButtonGroup_single, true);
        addToggleButtons();
        ta.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
	    int childCount = getChildCount();
	    int x = 0;
	    int y = 0;
	    int row = 0;

	    for (int index = 0; index < childCount; index++) {
	        final View child = getChildAt(index);
	        if (child.getVisibility() != View.GONE) {
	            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
	            int width = child.getMeasuredWidth();
	            int height = child.getMeasuredHeight();
	            x += width+margin;
	            y = row * (height+margin) + height+margin;
	            if (x > maxWidth) {
	                x = width+margin;
	                row++;
	                y = row * (height+margin) + height+margin;
	            }
	        }
	    }
	    setMeasuredDimension(maxWidth, y+margin);

		}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int childCount = getChildCount();
	    int maxWidth = r - l;
	    int x = 0;
	    int y = 0;
	    int row = 0;
	    for (int i = 0; i < childCount; i++) {
	        final View child = this.getChildAt(i);
	        if (child.getVisibility() != View.GONE) {
	            int width = child.getMeasuredWidth();
	        	int height = child.getMeasuredHeight();
	        	x += width+margin;
	            y = row * (height+margin) + height+margin;
	            if (x > maxWidth) {
	                x = width+margin;
	                row++;
	                y = row * (height+margin) + height+margin;
	            }
	            child.layout(x - width, y - height, x, y);
	        }
	    }
	}
	private void addToggleButtons(){
		texts= ta.getTextArray(R.styleable.WrappedButtonGroup_texts);
		if(texts==null)return;
		buttons=new ToggleButton[texts.length];
		for(int i=0;i<texts.length;i++){
			final String text=texts[i].toString();
			ToggleButton tb=new ToggleButton(this.getContext());
			if(width!=0){
//				tb.setIncludeFontPadding(false);
				tb.setWidth(width);
				tb.setMinimumWidth(width);
			}
			if(height!=0){
				tb.setHeight(height);
				tb.setMinimumHeight(height);
			}
			tb.setText(text);
			tb.setTextOn(text);
			tb.setTextOff(text);
			tb.setPadding(5, 0, 5, 0);
			tb.setBackgroundResource(R.drawable.selector_5);
			tb.setOnClickListener(this);	
			buttons[i]=tb;
			this.addView(tb, width, height);
		}
	}
	public int getSelect(){
		return select;
	}
	public ArrayList<Integer> getCheck(){
		return check;
	}
	public void setSelect(int select){
		this.select=select;
		this.check.clear();
		this.check.add(select);
		updateCheck();
	}
	public void setCheck(ArrayList<Integer> check){
		this.check=check;
		updateCheck();
	}
	private void updateCheck(){
		for(int k=0;k<texts.length;k++){
			buttons[k].setChecked(check.contains(k));
		}
	}
	@Override
	public void onClick(View v) {
		check.clear();
		for(int i=0;i<buttons.length;i++){
			if(single){
				boolean b=buttons[i]==v;
				if(b)select=i;
				buttons[i].setChecked(b);
			}else if(buttons[i].isChecked()){
				check.add(i);
			}
		}
		
	}
	
}
