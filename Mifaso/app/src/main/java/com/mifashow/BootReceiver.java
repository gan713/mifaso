package com.mifashow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		Intent intent=new Intent(context,Service.class);
		context.startService(intent);

	}

}
