package com.mifashow.tool;

import com.mifashow.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class AlertHelper {
	public static void showToast(Context context, int resourceId)
	  {
		showToast(context,context.getResources().getString(resourceId));
	  }
	public static void showToast(Context context, String content)
	  {
	    View layout = ((Activity)context).getLayoutInflater().inflate(R.layout.toast, (ViewGroup)((Activity)context).findViewById(R.id.toast_lo));
	    TextView textView = (TextView)layout.findViewById(R.id.toast_tv);
	    textView.setText(content);
	    Toast toast = new Toast(context);
	      toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
	      toast.setDuration(0);
	      toast.setView(layout);
	      toast.show();
	  }
	
	public static void showAlert(Activity activity,String title,String content,String positiveButton,DialogInterface.OnClickListener positiveButtonListener,String negativeButton,DialogInterface.OnClickListener negativeButtonListener){
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    LayoutInflater inflater = activity.getLayoutInflater();
//	    View contentView=inflater.inflate(R.layout.dialog_signin, null);
	    View titleView=inflater.inflate(R.layout.dialog_title, null);
	    TextView tv_title=(TextView)titleView.findViewById(R.id.dialog_title);
	    tv_title.setText(title);
	    builder.setMessage(content)
//	    .setView(contentView)
	    .setCustomTitle(titleView)
	    .setPositiveButton(positiveButton, positiveButtonListener)
	    .setNegativeButton(negativeButton, negativeButtonListener).setCancelable(false).show(); 
	}

}
