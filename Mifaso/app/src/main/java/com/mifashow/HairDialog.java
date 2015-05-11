package com.mifashow;

import com.mifashow.data.Constance.BANGTYPE;
import com.mifashow.data.Constance.CURLYTYPE;
import com.mifashow.data.Constance.HAIRLENGTH;
import com.mifashow.ui.WrappedButtonGroup;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class HairDialog extends DialogFragment {
	private HairListener listener;
	private ImageButton bt_confirm;
	private HAIRLENGTH hairLength;
	private CURLYTYPE curlyType;
	private BANGTYPE bangType;
	WrappedButtonGroup wv_hairLength,wv_bangType,wv_curlyType;
	public static HairDialog newInstance(HAIRLENGTH hairLength,CURLYTYPE curlyType,BANGTYPE bangType){
		HairDialog dialog=new HairDialog();
		Bundle bundle=new Bundle();
		bundle.putSerializable("hairLength", hairLength);
		bundle.putSerializable("curlyType", curlyType);
		bundle.putSerializable("bangType", bangType);
		dialog.setArguments(bundle);
		return dialog;
		
	}
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.hairLength=(HAIRLENGTH) getArguments().getSerializable("hairLength");
		this.curlyType=(CURLYTYPE) getArguments().getSerializable("curlyType");
		this.bangType=(BANGTYPE) getArguments().getSerializable("bangType");
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_hair, container, false);
		this.getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    	this.getDialog().getWindow().requestFeature(STYLE_NO_FRAME);
    	this.getDialog().getWindow().requestFeature(STYLE_NO_TITLE);
    	wv_hairLength=(WrappedButtonGroup) v.findViewById(R.id.wv_hairLength);
    	wv_curlyType=(WrappedButtonGroup) v.findViewById(R.id.wv_curlyType);
    	wv_bangType=(WrappedButtonGroup) v.findViewById(R.id.wv_bangType);
    	wv_hairLength.setSelect(this.hairLength.ordinal());
    	wv_curlyType.setSelect(this.curlyType.ordinal());
    	wv_bangType.setSelect(this.bangType.ordinal());
		bt_confirm=(ImageButton) v.findViewById(R.id.bt_confirm);
		bt_confirm.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(listener!=null)listener.OnFinish(HAIRLENGTH.values()[wv_hairLength.getSelect()],CURLYTYPE.values()[wv_curlyType.getSelect()],BANGTYPE.values()[wv_bangType.getSelect()]);
				HairDialog.this.dismiss();
			}
			
		});
		return v;
	}
	public void addListener(HairListener listener){
		this.listener=listener;
	}
	public interface HairListener {
	    void OnFinish(HAIRLENGTH hairLength,CURLYTYPE curlyType,BANGTYPE bangType);
	}

}
