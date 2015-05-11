package com.mifashow.server.domain;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;

import com.sina.sae.storage.SaeStorage;

public class Constance {
	public static enum FACESHAPE{
		STANDARD,JIA,SHEN,YOU,GUO,CIRCLE,SQUARE;
	}
	public static enum HAIRLENGTH{
		LONG,MIDDLE,SHORT
	}
	public static enum BANGTYPE{
		PART,LONGSIDE,SHORTSIDE,STRAIGHT
	}
	public static enum CURLYTYPE{
		STRAIGHT,CURLY
	}
	public static enum SEX{
		FEMALE,MALE
	}
	public static enum USERTYPE{
		CUSTOMER,STYLIST
	}
	public static enum SERVICETYPE{
		CUT,PERMANENT,COLOR,BRAID,TREATMENT
	}
	public static enum AGETYPE{
		BEFORE5,FROM6TO15,FROM16TO21,FROM22TO25,FROM26TO30,FROM31TO35,AFTER36
	}
	public static enum WEEKDAY{
		SUN,MON,TUE,WED,THU,FRI,SAT
	}
	public static enum DISCOUNT{
		NO,DR0,DR10,DR20,DR30,DR40,DR50,DR60,DR70,DR80,DR90
	}
	public static enum MESSAGETYPE{
		CHAT,BOOK,COMMIT,COMMENT,EXPLAIN,ARBITRATE,CANCEL,REPORT,FOLLOW,INVITE,JOIN
	}
	public static enum REPORTTYPE{
		BADPHOTO,BADTEXT,BADADDRESS,OTHER
	}
	public static enum BOOKINGSTATUS{
		COMMITTING,COMMENTING,EXPLAINING,ARBITRATING,TERMINATE,CANCELED;
	}
	public static enum POSTINGTYPE{
		POSTER,SHOW
	}
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
	public static AGETYPE getAgeType(long birthday){
		int age=getAge(birthday);
		if(age<=5){
			return AGETYPE.BEFORE5;
		}else if(age>5 && age<=15){
			return AGETYPE.FROM6TO15;
		}else if(age>15 && age<=21){
			return AGETYPE.FROM16TO21;
		}else if(age>21 && age<=25){
			return AGETYPE.FROM22TO25;
		}else if(age>25 && age<=30){
			return AGETYPE.FROM26TO30;
		}else if(age>30 && age<=35){
			return AGETYPE.FROM31TO35;
		}else if(age>35){
			return AGETYPE.AFTER36;
		}
		return null;
	}
	public static String buildSet(Enum[] values){
		if(values==null)return null;
		StringBuilder sb=new StringBuilder();
		for(Enum value:values){
			sb.append(value.name()).append(',');
		}
		if(sb.length()>0)sb.deleteCharAt(sb.length()-1);
		return sb.toString();
		
	}
	public static <T extends Enum<T>> T[] parseSet(Class<T> enumType,String str){
		if(enumType==null || str==null) return null;
		String[] values=str.split(",");
		T[] enums=(T[]) Array.newInstance(enumType, values.length);
		for(int i=0;i<values.length;i++){
			enums[i]=Enum.valueOf(enumType, values[i]);
		}
		return enums;
		
	}
	public static int RE=6371;
	public  static String parseAreaCode(float latitude,float longitude,int distance){
		String latitudeCode=""+(int) ((latitude+90)*Math.PI*RE/(180*distance));
		String longitudeCode=""+(int) ((longitude+180)*Math.PI*RE*Math.cos(Math.toRadians(latitude))/(180*distance));
		return ("00000"+latitudeCode).substring(latitudeCode.length())+("00000"+longitudeCode).substring(longitudeCode.length());
	}
	public static byte[] scaleImageBytes(byte[] inputBytes,int size){
		byte[] outputBytes = null;
		try {
			BufferedImage inputBI=ImageIO.read(new ByteArrayInputStream(inputBytes));
			Image image = inputBI.getScaledInstance(size, size, Image.SCALE_FAST);
			BufferedImage outputImage = new BufferedImage(size, size,BufferedImage.TYPE_INT_RGB);
			Graphics graphics = outputImage.getGraphics();  
            graphics.drawImage(image, 0, 0, null);  
            graphics.dispose(); 
			ByteArrayOutputStream out = new ByteArrayOutputStream(); 
			ImageIO.write(outputImage, "jpg", out);  
            outputBytes = out.toByteArray();  
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outputBytes;
		
	}
	private final static String SAE_DOMAIN="mifashow";
	public static void writeFile(String fileName,byte[] content){
		SaeStorage storage=new SaeStorage();
		storage.write(SAE_DOMAIN, fileName, content);
	}
	public static String getUrlByFileName(String fileName){
		SaeStorage storage=new SaeStorage();
		if(storage.fileExists(SAE_DOMAIN, fileName)){
			return storage.getUrl(SAE_DOMAIN, fileName);
		}
		return null;
	}
	public static void deleteFile(String fileName){
		SaeStorage storage=new SaeStorage();
		storage.delete(SAE_DOMAIN, fileName);
	}
	public static int getFileCount(){
		SaeStorage storage=new SaeStorage();
		return storage.getFilesNum(SAE_DOMAIN, "");
	}
	public static List<String> getFileList(int limitCount,int skipPosition){
		SaeStorage storage=new SaeStorage();
		return storage.getList(SAE_DOMAIN, "*", limitCount, skipPosition);
	}
	public static byte[] getFileBytes(String fileName){
		SaeStorage storage=new SaeStorage();
		return storage.read(SAE_DOMAIN, fileName);
	}
	
	
//	public static final String SAE_ACCESSKEY="xywz25y2z0",SAE_SECRETKEY="1zh2523zm2mz23kjkz21hzy3k12yjxl3211wh1xh",SAE_APPNAME="mifashow";

}
