package com.mifashow.domain;

import java.io.Serializable;

import com.mifashow.data.Constance;

public class Booking implements Serializable {
	private static final long serialVersionUID = -1961879636149580313L;
	private int agreedPrice;
	  private long agreedTime;
	  private long terminateTime;
	  private long cancelTime;
	  private long bookingId;
	  private String comment;
	  private long commentTime;
	  private long commitTime;
	  private long createTime;
	  private String customerFigure;
	  private long customerId;
	  private String customerName;
	  private Constance.DISCOUNT discount;
	  private long explainTime;
	  private String explanation;
	  private String commentRendering;
	  private int listPrice;
	  private long postingId;
	  private String postingRendering;
	  private int rating;
	  private Constance.BOOKINGSTATUS status;
	  private String stylistFigure;
	  private long stylistId;
	  private String stylistName;
	public Booking() {}
	public int getAgreedPrice() {
		return agreedPrice;
	}
	public void setAgreedPrice(int agreedPrice) {
		this.agreedPrice = agreedPrice;
	}
	public long getAgreedTime() {
		return agreedTime;
	}
	public void setAgreedTime(long agreedTime) {
		this.agreedTime = agreedTime;
	}
	public long getBookingId() {
		return bookingId;
	}
	public void setBookingId(long bookingId) {
		this.bookingId = bookingId;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public long getCommentTime() {
		return commentTime;
	}
	public void setCommentTime(long commentTime) {
		this.commentTime = commentTime;
	}
	public long getCommitTime() {
		return commitTime;
	}
	public void setCommitTime(long commitTime) {
		this.commitTime = commitTime;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public String getCustomerFigure() {
		return customerFigure;
	}
	public void setCustomerFigure(String customerFigure) {
		this.customerFigure = customerFigure;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public Constance.DISCOUNT getDiscount() {
		return discount;
	}
	public void setDiscount(Constance.DISCOUNT discount) {
		this.discount = discount;
	}
	public long getExplainTime() {
		return explainTime;
	}
	public void setExplainTime(long explainTime) {
		this.explainTime = explainTime;
	}
	public String getExplanation() {
		return explanation;
	}
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	public String getCommentRendering() {
		return commentRendering;
	}
	public void setCommentRendering(String commentRendering) {
		this.commentRendering = commentRendering;
	}
	public int getListPrice() {
		return listPrice;
	}
	public void setListPrice(int listPrice) {
		this.listPrice = listPrice;
	}
	public long getPostingId() {
		return postingId;
	}
	public void setPostingId(long postingId) {
		this.postingId = postingId;
	}
	public String getPostingRendering() {
		return postingRendering;
	}
	public void setPostingRendering(String postingRendering) {
		this.postingRendering = postingRendering;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public Constance.BOOKINGSTATUS getStatus() {
		return status;
	}
	public void setStatus(Constance.BOOKINGSTATUS status) {
		this.status = status;
	}
	public String getStylistFigure() {
		return stylistFigure;
	}
	public void setStylistFigure(String stylistFigure) {
		this.stylistFigure = stylistFigure;
	}
	public long getStylistId() {
		return stylistId;
	}
	public void setStylistId(long stylistId) {
		this.stylistId = stylistId;
	}
	public String getStylistName() {
		return stylistName;
	}
	public void setStylistName(String stylistName) {
		this.stylistName = stylistName;
	}
	public long getTerminateTime() {
		return terminateTime;
	}
	public void setTerminateTime(long terminateTime) {
		this.terminateTime = terminateTime;
	}
	public long getCancelTime() {
		return cancelTime;
	}
	public void setCancelTime(long cancelTime) {
		this.cancelTime = cancelTime;
	}
	
}
