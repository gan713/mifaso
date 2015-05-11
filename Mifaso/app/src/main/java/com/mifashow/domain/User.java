package com.mifashow.domain;

import java.io.Serializable;

import com.mifashow.data.Constance;

public class User implements Serializable {
	private static final long serialVersionUID = -7348360272894298416L;
	private String about;
	  private String address;
	  private Constance.BANGTYPE bangType;
	  private long birthday;
	  private int bookedNum;
	  private int bookingNum;
	  private int commentedNum;
	  private int commentingNum;
	  private long createTime;
	  private Constance.CURLYTYPE curlyType;
	  private Constance.FACESHAPE faceShape;
	  private String figure;
	  private int followerNum;
	  private int followingNum;
	  private Constance.HAIRLENGTH hairLength;
	  private int height;
	  private float latitude;
	  private float longitude;
	  private int markedNum;
	  private int markingNum;
	  private String password;
	  private int postingNum;
	  private Constance.SEX sex;
	  private String shopImage;
	  private String sign;
	  private long userId;
	  private String userName;
	  private Constance.USERTYPE userType;
	  private int weight;
	  private int rating;
	  private int ranking;
	  private float grade;
	  private long salonId;
	  private boolean salonManager;
	  private String salonName;
	  private String salonPhone;
	  private String salonImages;
	  private String salonAddress;
	  private float salonLatitude;
	  private float salonLongitude;

	public User() {

	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Constance.BANGTYPE getBangType() {
		return bangType;
	}

	public void setBangType(Constance.BANGTYPE bangType) {
		this.bangType = bangType;
	}

	public long getBirthday() {
		return birthday;
	}

	public void setBirthday(long birthday) {
		this.birthday = birthday;
	}

	public int getBookedNum() {
		return bookedNum;
	}

	public void setBookedNum(int bookedNum) {
		this.bookedNum = bookedNum;
	}

	public int getBookingNum() {
		return bookingNum;
	}

	public void setBookingNum(int bookingNum) {
		this.bookingNum = bookingNum;
	}

	public int getCommentedNum() {
		return commentedNum;
	}

	public void setCommentedNum(int commentedNum) {
		this.commentedNum = commentedNum;
	}

	public int getCommentingNum() {
		return commentingNum;
	}

	public void setCommentingNum(int commentingNum) {
		this.commentingNum = commentingNum;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public Constance.CURLYTYPE getCurlyType() {
		return curlyType;
	}

	public void setCurlyType(Constance.CURLYTYPE curlyType) {
		this.curlyType = curlyType;
	}

	public Constance.FACESHAPE getFaceShape() {
		return faceShape;
	}

	public void setFaceShape(Constance.FACESHAPE faceShape) {
		this.faceShape = faceShape;
	}

	public String getFigure() {
		return figure;
	}

	public void setFigure(String figure) {
		this.figure = figure;
	}

	public int getFollowerNum() {
		return followerNum;
	}

	public void setFollowerNum(int followerNum) {
		this.followerNum = followerNum;
	}

	public int getFollowingNum() {
		return followingNum;
	}

	public void setFollowingNum(int followingNum) {
		this.followingNum = followingNum;
	}

	public Constance.HAIRLENGTH getHairLength() {
		return hairLength;
	}

	public void setHairLength(Constance.HAIRLENGTH hairLength) {
		this.hairLength = hairLength;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
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

	public int getMarkingNum() {
		return markingNum;
	}

	public void setMarkingNum(int markingNum) {
		this.markingNum = markingNum;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPostingNum() {
		return postingNum;
	}

	public void setPostingNum(int postingNum) {
		this.postingNum = postingNum;
	}

	public Constance.SEX getSex() {
		return sex;
	}

	public void setSex(Constance.SEX sex) {
		this.sex = sex;
	}

	public String getShopImage() {
		return shopImage;
	}

	public void setShopImage(String shopImage) {
		this.shopImage = shopImage;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Constance.USERTYPE getUserType() {
		return userType;
	}

	public void setUserType(Constance.USERTYPE userType) {
		this.userType = userType;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public float getGrade() {
		return grade;
	}

	public void setGrade(float grade) {
		this.grade = grade;
	}

	public long getSalonId() {
		return salonId;
	}

	public void setSalonId(long salonId) {
		this.salonId = salonId;
	}

	public boolean isSalonManager() {
		return salonManager;
	}

	public void setSalonManager(boolean salonManager) {
		this.salonManager = salonManager;
	}

	public String getSalonName() {
		return salonName;
	}

	public void setSalonName(String salonName) {
		this.salonName = salonName;
	}

	public String getSalonPhone() {
		return salonPhone;
	}

	public void setSalonPhone(String salonPhone) {
		this.salonPhone = salonPhone;
	}

	public String getSalonImages() {
		return salonImages;
	}

	public void setSalonImages(String salonImages) {
		this.salonImages = salonImages;
	}

	public String getSalonAddress() {
		return salonAddress;
	}

	public void setSalonAddress(String salonAddress) {
		this.salonAddress = salonAddress;
	}

	public float getSalonLatitude() {
		return salonLatitude;
	}

	public void setSalonLatitude(float salonLatitude) {
		this.salonLatitude = salonLatitude;
	}

	public float getSalonLongitude() {
		return salonLongitude;
	}

	public void setSalonLongitude(float salonLongitude) {
		this.salonLongitude = salonLongitude;
	}

	//	@Override
//	public boolean equals(Object o){
//		if(this==o)return true;
//		if(o==null)return false;
//		User u=(User) o;
//		return (u.userId==userId && u.userType.ordinal()==userType.ordinal() && u.userName.equals(userName) && u.about.equals(about) && u.address==address && u.bangType==bangType && u.birthday==birthday && u.bookedNum==bookedNum && u.bookingNum==bookingNum && u.commentedNum==commentedNum && u.commentingNum==commentingNum && u.createTime==createTime && u.curlyType==curlyType && u.faceShape==faceShape && u.figure==figure && u.followerNum==followerNum && u.followingNum==followingNum && u.hairLength==hairLength && u.height==height && u.latitude==latitude && u.longitude==longitude && u.markedNum==markedNum && u.markingNum==markingNum && u.password==password && u.postingNum==postingNum && u.sex==sex && u.shopImage==shopImage && u.sign==sign && u.weight==weight);
//		
//	}
	@Override
	public String toString(){
		return new StringBuilder().append(userId).append(userType).append(userName).append(about).append(address).append(bangType).append(birthday).append(bookedNum).append(bookingNum).append(commentedNum).append(commentingNum).append(createTime).append(curlyType).append(faceShape).append(figure).append(followerNum).append(followingNum).append(hairLength).append(height).append(latitude).append(longitude).append(markedNum).append(markingNum).append(password).append(postingNum).append(sex).append(shopImage).append(sign).append(weight).append(grade).append(ranking).append(rating).append(salonId).append(salonManager).append(salonName).append(salonAddress).append(salonLatitude).append(salonLongitude).append(salonImages).toString();
		
	}

	

}
