package com.mifashow.tool;

import java.util.Calendar;

import com.mifashow.data.Constance;

public class DateHelper {
	public static int getAge(long birthday)
	  {
		Calendar cal = Calendar.getInstance();
		int age,monthOfToday,dayOfToday,monthOfBirthday,dayOfBirthday,yearOfToday,yearOfBirthday;
		if (cal.getTimeInMillis() < birthday)
			age = 0;
		yearOfToday = cal.get(Calendar.YEAR);
		monthOfToday = cal.get(Calendar.MONTH);
		dayOfToday = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTimeInMillis(birthday);
		yearOfBirthday = cal.get(Calendar.YEAR);
		monthOfBirthday = cal.get(Calendar.MONTH);
		dayOfBirthday = cal.get(Calendar.DAY_OF_MONTH);
		age = yearOfToday - yearOfBirthday;
		if ((monthOfToday < monthOfBirthday) || ((monthOfToday == monthOfBirthday) && (dayOfToday < dayOfBirthday))) {
			age--;
		} 
		return age;
	  }
	public static Constance.AGETYPE getAgeType(long birthday){
		int age=getAge(birthday);
		if(age<=5){
			return Constance.AGETYPE.BEFORE5;
		}else if(age>5 && age<=15){
			return Constance.AGETYPE.FROM6TO15;
		}else if(age>15 && age<=21){
			return Constance.AGETYPE.FROM16TO21;
		}else if(age>21 && age<=25){
			return Constance.AGETYPE.FROM22TO25;
		}else if(age>25 && age<=30){
			return Constance.AGETYPE.FROM26TO30;
		}else if(age>30 && age<=35){
			return Constance.AGETYPE.FROM31TO35;
		}else if(age>35){
			return Constance.AGETYPE.AFTER36;
		}
		return null;
	}

}
