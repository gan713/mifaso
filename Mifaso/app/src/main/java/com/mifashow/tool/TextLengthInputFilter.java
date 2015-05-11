package com.mifashow.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.InputFilter;
import android.text.Spanned;


public class TextLengthInputFilter implements  InputFilter {

	
        int MAX_EN;
        static String regEx = "[\\u4e00-\\u9fa5]";
  
        public TextLengthInputFilter(int mAX_EN) {  
            super();  
            MAX_EN = mAX_EN;  
        } 
	@Override
	public CharSequence filter(CharSequence source, int start, int end,  
            Spanned dest, int dstart, int dend) {
		int destCount = dest.toString().length()  
                + getChineseCount(dest.toString());  
        int sourceCount = source.toString().length()  
                + getChineseCount(source.toString());  
        if (destCount + sourceCount > MAX_EN) {  
//            Toast.makeText(MainActivity.this, getString(R.string.count),  
//                    Toast.LENGTH_SHORT).show();  
            return "";  

        } else {  
            return source;  
        }  
	}
	public static int getChineseCount(String str) {  
        int count = 0;  
        Pattern p = Pattern.compile(regEx);  
        Matcher m = p.matcher(str);  
        while (m.find()) {  
            for (int i = 0; i <= m.groupCount(); i++) {  
                count = count + 1;  
            }  
        }  
        return count;  
    }  

}
