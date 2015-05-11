package com.mifashow.server.domain;

public class User_salon {
	private long userId,salonId;
	private boolean manager;
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getSalonId() {
		return salonId;
	}
	public void setSalonId(long salonId) {
		this.salonId = salonId;
	}
	public boolean isManager() {
		return manager;
	}
	public void setManager(boolean manager) {
		this.manager = manager;
	}
	

}
