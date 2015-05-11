package com.mifashow.domain;


import java.io.Serializable;

import com.mifashow.data.Constance;
import com.mifashow.data.Constance.POSTINGTYPE;

public class Posting implements Serializable{
	private static final long serialVersionUID = -1907107210625625911L;
	private String address;
	  private Constance.AGETYPE[] ageTypes;
	  private Constance.BANGTYPE bangType;
	  private int bookedNum;
	  private Constance.WEEKDAY[] bookingDay;
	  private Constance.DISCOUNT[] bookingTime;
	  private int commentedNum;
	  private long createTime;
	  private String createrFigure;
	  private long createrId;
	  private String createrName;
	  private long salonId;
	  private String SalonName;
	  private Constance.CURLYTYPE curlyType;
	  private Constance.FACESHAPE[] faceShapes;
	  private Constance.HAIRLENGTH hairLength;
	  private String[] images;
	  private float latitude;
	  private float longitude;
	  private int markedNum;
	  private long postingId;
	  private int price;
	  private Constance.SERVICETYPE[] serviceTypes;
	  private Constance.SEX sex;
	  private long deleteTime;
	  private POSTINGTYPE postingType;
	public Posting(){}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Constance.AGETYPE[] getAgeTypes() {
		return ageTypes;
	}
	public void setAgeTypes(Constance.AGETYPE[] ageTypes) {
		this.ageTypes = ageTypes;
	}
	public Constance.BANGTYPE getBangType() {
		return bangType;
	}
	public void setBangType(Constance.BANGTYPE bangType) {
		this.bangType = bangType;
	}
	public int getBookedNum() {
		return bookedNum;
	}
	public void setBookedNum(int bookedNum) {
		this.bookedNum = bookedNum;
	}
	public Constance.WEEKDAY[] getBookingDay() {
		return bookingDay;
	}
	public void setBookingDay(Constance.WEEKDAY[] bookingDay) {
		this.bookingDay = bookingDay;
	}
	public Constance.DISCOUNT[] getBookingTime() {
		return bookingTime;
	}
	public void setBookingTime(Constance.DISCOUNT[] bookingTime) {
		this.bookingTime = bookingTime;
	}
	public int getCommentedNum() {
		return commentedNum;
	}
	public void setCommentedNum(int commentedNum) {
		this.commentedNum = commentedNum;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public String getCreaterFigure() {
		return createrFigure;
	}
	public void setCreaterFigure(String createrFigure) {
		this.createrFigure = createrFigure;
	}
	public long getCreaterId() {
		return createrId;
	}
	public void setCreaterId(long createrId) {
		this.createrId = createrId;
	}
	public String getCreaterName() {
		return createrName;
	}
	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}
	public long getSalonId() {
		return salonId;
	}
	public void setSalonId(long salonId) {
		this.salonId = salonId;
	}
	public String getSalonName() {
		return SalonName;
	}
	public void setSalonName(String salonName) {
		SalonName = salonName;
	}
	public Constance.CURLYTYPE getCurlyType() {
		return curlyType;
	}
	public void setCurlyType(Constance.CURLYTYPE curlyType) {
		this.curlyType = curlyType;
	}
	public Constance.FACESHAPE[] getFaceShapes() {
		return faceShapes;
	}
	public void setFaceShapes(Constance.FACESHAPE[] faceShapes) {
		this.faceShapes = faceShapes;
	}
	public Constance.HAIRLENGTH getHairLength() {
		return hairLength;
	}
	public void setHairLength(Constance.HAIRLENGTH hairLength) {
		this.hairLength = hairLength;
	}
	public String[] getImages() {
		return images;
	}
	public void setImages(String[] images) {
		this.images = images;
	}
	public float getLatitude() {
		return latitude;
	}
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	public float getLongitude() {
		return longitude;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	public int getMarkedNum() {
		return markedNum;
	}
	public void setMarkedNum(int markedNum) {
		this.markedNum = markedNum;
	}
	public long getPostingId() {
		return postingId;
	}
	public void setPostingId(long postingId) {
		this.postingId = postingId;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public Constance.SERVICETYPE[] getServiceTypes() {
		return serviceTypes;
	}
	public void setServiceTypes(Constance.SERVICETYPE[] serviceTypes) {
		this.serviceTypes = serviceTypes;
	}
	public Constance.SEX getSex() {
		return sex;
	}
	public void setSex(Constance.SEX sex) {
		this.sex = sex;
	}
	public long getDeleteTime() {
		return deleteTime;
	}
	public void setDeleteTime(long deleteTime) {
		this.deleteTime = deleteTime;
	}
	public POSTINGTYPE getPostingType() {
		return postingType;
	}
	public void setPostingType(POSTINGTYPE postingType) {
		this.postingType = postingType;
	}

	
}
