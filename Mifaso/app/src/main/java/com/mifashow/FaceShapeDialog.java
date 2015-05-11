package com.mifashow;

import com.mifashow.data.Constance.FACESHAPE;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class FaceShapeDialog extends DialogFragment implements OnClickListener {
	private FaceShapeListener listener;
	private Button bt_standardFace,bt_jiaFace,bt_shenFace,bt_youFace,bt_guoFace,bt_circleFace,bt_squareFace;
	public static FaceShapeDialog newInstance(){
		return new FaceShapeDialog();
		
	}
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_faceshape, container, false);
		this.getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    	this.getDialog().getWindow().requestFeature(STYLE_NO_FRAME);
    	this.getDialog().getWindow().requestFeature(STYLE_NO_TITLE);
    	bt_standardFace=(Button) v.findViewById(R.id.faceshape_bt_standardFace);
    	bt_jiaFace=(Button) v.findViewById(R.id.faceshape_bt_jiaFace);
    	bt_shenFace=(Button) v.findViewById(R.id.faceshape_bt_shenFace);
    	bt_youFace=(Button) v.findViewById(R.id.faceshape_bt_youFace);
    	bt_guoFace=(Button) v.findViewById(R.id.faceshape_bt_guoFace);
    	bt_circleFace=(Button) v.findViewById(R.id.faceshape_bt_circleFace);
    	bt_squareFace=(Button) v.findViewById(R.id.faceshape_bt_squareFace);
    	bt_standardFace.setOnClickListener(this);
    	bt_jiaFace.setOnClickListener(this);
    	bt_shenFace.setOnClickListener(this);
    	bt_youFace.setOnClickListener(this);
    	bt_guoFace.setOnClickListener(this);
    	bt_circleFace.setOnClickListener(this);
    	bt_squareFace.setOnClickListener(this);
		return v;
	}
	public void addListener(FaceShapeListener listener){
		this.listener=listener;
	}
	public interface FaceShapeListener {
	    void OnFinish(FACESHAPE faceShape);
	}
	@Override
	public void onClick(View v) {
		if(listener!=null)listener.OnFinish(FACESHAPE.valueOf((String)v.getTag()));
		dismiss();
		
	}

}
