package com.mifashow.tool.image;

import java.io.IOException;
import java.net.URL;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadImageTask extends AsyncTask<String, Void, Drawable>{

	public DownloadImageTask() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Drawable doInBackground(String... urls) {
		return loadImageFromNetwork(urls[0]);
	}
	
	private Drawable loadImageFromNetwork(String imageUrl)  
	{  
	    Drawable drawable = null;  
	    try {
	        drawable = Drawable.createFromStream(  
	                new URL(imageUrl).openStream(), "image.jpg");  
	    } catch (IOException e) {  
	        Log.d("test", e.getMessage());  
	    }  
	    if (drawable == null) {  
	        Log.d("test", "null drawable");  
	    } else {  
	        Log.d("test", "not null drawable");  
	    }  
	      
	    return drawable ;  
	}  

}
