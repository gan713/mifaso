package com.mifashow;

import com.mifashow.tool.TextLengthInputFilter;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class InputDialog extends DialogFragment {
	private InputListener listener; 
	private String title;
	private String text;
	private String hint;
	private int inputType;
	private int lengthLimit;
	private EditText et_input;
	private TextView tv_title,tv_lengthLimit,tv_clear;
	private ImageButton bt_confirm;
	public static InputDialog newInstance(String title,String text,String hint,int inputType,int lengthLimit){
		InputDialog dialog=new InputDialog();
		Bundle bundle=new Bundle();
		bundle.putString("title", title);
		bundle.putString("text", text);
		bundle.putString("hint", hint);
		bundle.putInt("inputType", inputType);
		bundle.putInt("lengthLimit", lengthLimit);
		dialog.setArguments(bundle);
		return dialog;
		
	}
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.title=getArguments().getString("title");
		this.text=getArguments().getString("text");
		this.hint=getArguments().getString("hint");
		this.inputType=getArguments().getInt("inputType");
		this.lengthLimit=getArguments().getInt("lengthLimit");
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_input, container, false);
		this.getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    	this.getDialog().getWindow().requestFeature(STYLE_NO_FRAME);
    	this.getDialog().getWindow().requestFeature(STYLE_NO_TITLE);
    	this.getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		tv_title=(TextView) v.findViewById(R.id.input_tv_title);
		tv_title.setText(title);
		et_input=(EditText) v.findViewById(R.id.input_et_input);
		et_input.setHint(hint);
		et_input.setFilters(new InputFilter[]{new TextLengthInputFilter(lengthLimit)});
		tv_lengthLimit=(TextView) v.findViewById(R.id.input_tv_lengthLimit);
		tv_clear=(TextView) v.findViewById(R.id.input_tv_clear);
		bt_confirm=(ImageButton) v.findViewById(R.id.input_bt_confirm);
		bt_confirm.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(listener!=null)listener.OnFinish(et_input.getText().toString());
				InputDialog.this.dismiss();
			}
			
		});
		tv_lengthLimit.setText(getResources().getString(R.string.info_textLengthLimit,lengthLimit));
		et_input.setText(text);
		tv_clear.setText(getResources().getString(R.string.input_tv_clear,lengthLimit-text.length()-TextLengthInputFilter.getChineseCount(text)));
		tv_clear.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				et_input.setText(null);
				tv_clear.setText(getResources().getString(R.string.input_tv_clear,lengthLimit));
			}
			
		});
		et_input.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence content, int start, int before,
					int count) {
				String newText=et_input.getText().toString();
				tv_clear.setText(getResources().getString(R.string.input_tv_clear,lengthLimit-newText.length()-TextLengthInputFilter.getChineseCount(newText)));
			}
			
		});
		et_input.setInputType(inputType);
		et_input.selectAll();
		
		return v;
	}
	public void addListener(InputListener listener){
		this.listener=listener;
	}
	public interface InputListener {
	    void OnFinish(String inputText);
	}

}
