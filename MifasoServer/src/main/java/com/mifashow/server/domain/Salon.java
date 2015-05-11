package com.mifashow.server.domain;


public class Salon{
	private long salonId,updateTime,managerId;
	private String name,address,environment,phone,discount,recruitment,managerName,managerFigure;
	private String[] images;
	private float latitude,longitude;
    private int area,cut,permanent,color,braid,treatment;
    private User[] stylists;
	public long getSalonId() {
		return salonId;
	}
	public void setSalonId(long salonId) {
		this.salonId = salonId;
	}
	public long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEnvironment() {
		return environment;
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getRecruitment() {
		return recruitment;
	}
	public void setRecruitment(String recruitment) {
		this.recruitment = recruitment;
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
	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}
	public int getCut() {
		return cut;
	}
	public void setCut(int cut) {
		this.cut = cut;
	}
	public int getPermanent() {
		return permanent;
	}
	public void setPermanent(int permanent) {
		this.permanent = permanent;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public int getBraid() {
		return braid;
	}
	public void setBraid(int braid) {
		this.braid = braid;
	}
	public int getTreatment() {
		return treatment;
	}
	public void setTreatment(int treatment) {
		this.treatment = treatment;
	}
	public User[] getStylists() {
		return stylists;
	}
	public void setStylists(User[] stylists) {
		this.stylists = stylists;
	}
	public long getManagerId() {
		return managerId;
	}
	public void setManagerId(long managerId) {
		this.managerId = managerId;
	}
	public String getManagerName() {
		return managerName;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	public String getManagerFigure() {
		return managerFigure;
	}
	public void setManagerFigure(String managerFigure) {
		this.managerFigure = managerFigure;
	}
    
}
