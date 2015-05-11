package com.mifashow;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.NumberPicker;

public class CountDialog extends DialogFragment {
	private CountListener listener;
	private NumberPicker np;
	private ImageButton bt_confirm;
	private int num,min,max,step;
	public static CountDialog newInstance(int num,int min,int max,int step){
		CountDialog dialog=new CountDialog();
		Bundle bundle=new Bundle();
		bundle.putInt("num", num);
		bundle.putInt("min", min);
		bundle.putInt("max", max);
		bundle.putInt("step", step);
		dialog.setArguments(bundle);
		return dialog;
		
	}
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.num=getArguments().getInt("num");
		this.min=getArguments().getInt("min");
		this.max=getArguments().getInt("max");
		this.step=getArguments().getInt("step");
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_count, container, false);
		this.getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    	this.getDialog().getWindow().requestFeature(STYLE_NO_FRAME);
    	this.getDialog().getWindow().requestFeature(STYLE_NO_TITLE);
    	np=(NumberPicker) v.findViewById(R.id.count_np_picker);
    	np.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		final String[] displayValues = new String[(max-min-1)/step+2];
		for (int i = 0; i <displayValues.length; i++) {
			int j=min+step*i;
				displayValues[i]=""+(j<max?(min+step*i):max);
		}
		np.setDisplayedValues(displayValues);
		np.setMinValue(0);
		np.setMaxValue(displayValues.length-1);
		np.setValue((num-min)/step);
		bt_confirm=(ImageButton) v.findViewById(R.id.input_bt_confirm);
		bt_confirm.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(listener!=null)listener.OnFinish(Integer.parseInt(displayValues[np.getValue()]));
				CountDialog.this.dismiss();
			}
			
		});
		return v;
	}
	public void addListener(CountListener listener){
		this.listener=listener;
	}
	public interface CountListener {
	    void OnFinish(int num);
	}

}
