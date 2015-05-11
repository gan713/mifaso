package com.mifashow.tool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

public class Storage {
	public static void saveObject(Context context,String domain,String name,Object value) {  
        SharedPreferences mSharedPreferences = context.getSharedPreferences(domain, Context.MODE_PRIVATE);  
        try {  
            ByteArrayOutputStream baos = new ByteArrayOutputStream();  
            ObjectOutputStream oos = new ObjectOutputStream(baos);  
            oos.writeObject(value);  
  
            String objectBase64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);  
            SharedPreferences.Editor editor = mSharedPreferences.edit();  
            editor.putString(name, objectBase64);  
            editor.commit();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }
	 public static Object getObjectInfo(Context context,String domain,String name) {
	        try {  
	            SharedPreferences mSharedPreferences = context.getSharedPreferences(domain, Context.MODE_PRIVATE);  
	            String personBase64 = mSharedPreferences.getString(name, "");  
	            byte[] base64Bytes = Base64.decode(personBase64.getBytes(),Base64.DEFAULT);
	            if(base64Bytes!=null && base64Bytes.length>0){
	            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);  
	            ObjectInputStream ois = new ObjectInputStream(bais);  
	            return ois.readObject();
	            }
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }
	        return null;
	          
	    }

}
