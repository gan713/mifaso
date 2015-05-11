package com.mifashow.tool;
import java.util.regex.Pattern;

import android.text.InputFilter;
import android.text.Spanned;
public class RegExInputFilter implements InputFilter {
	String regEx = "[\\u4e00-\\u9fa5]";
	public RegExInputFilter(String regEx){
		this.regEx=regEx;
	}

	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		
		return Pattern.compile(regEx).matcher(source.toString()).matches()?source:"";
	}

}
