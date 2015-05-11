package com.mifashow.data;

import java.io.File;
import java.lang.reflect.Array;

import com.mifashow.R;
import com.mifashow.domain.User;

public class Constance {
	public static enum FACESHAPE{
		STANDARD(R.id.register_bt_standardface,R.drawable.ic_faceshape_standard),JIA(R.id.register_bt_jiaface,R.drawable.ic_faceshape_jia),SHEN(R.id.register_bt_shenface,R.drawable.ic_faceshape_shen),YOU(R.id.register_bt_youface,R.drawable.ic_faceshape_you),GUO(R.id.register_bt_guoface,R.drawable.ic_faceshape_guo),CIRCLE(R.id.register_bt_circleface,R.drawable.ic_faceshape_circle),SQUARE(R.id.register_bt_squareface,R.drawable.ic_faceshape_square);

		private int buttonID,drawableID;
		private FACESHAPE(int buttonID, int drawableID){
			this.drawableID=drawableID;
			this.buttonID=buttonID;
		}
		public int getDrawableID(){
			return this.drawableID;
		}
		public int getButtonID(){
			return this.buttonID;
		}
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
	public static int getLevel(User user){
		if((user.getUserType()==USERTYPE.STYLIST && (user.getAddress()==null || user.getAddress().length()==0 || user.getShopImage()==null || user.getShopImage().length()==0))||user.getAbout()==null || user.getAbout().length()==0){
			return 1;
		}else if(user.getMarkingNum()==0){
			return 2;
		}else if(user.getFollowingNum()==0){
			return 3;
		}else if(user.getPostingNum()==0){
			return 4;
		}else if(user.getMarkedNum()==0){
			return 5;
		}else if(user.getFollowerNum()==0){
			return 6;
		}else{
			return 7+Math.round(user.getPostingNum()/2+user.getFollowerNum()/10+user.getMarkedNum()/10+user.getCommentingNum()/10+user.getCommentedNum()/10+user.getBookedNum()*2+user.getBookingNum()*2);
		}
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
	public static final int[] constellationEdgeDay = { 20, 19, 21, 21, 21, 22,23, 23, 23, 23, 22, 22 };

	public static final File sdcardTempFile=new File("/mnt/sdcard","mifashow_tmp_pic.jpg");
//	public static final String SERVER_URL="http://192.168.3.102:8080/MifashowServer/";
	public static final String SERVER_URL="http://10.mifashow.sinaapp.com/";

}
