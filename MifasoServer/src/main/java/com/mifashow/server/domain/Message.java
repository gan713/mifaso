package com.mifashow.server.domain;

import com.mifashow.server.domain.Constance.MESSAGETYPE;

public class Message {
	private long messageId;
	private MESSAGETYPE messageType;
	private long fromUserId;
	private String fromUserName;
	private String fromUserFigure;
	private long toUserId;
	private String toUserName;
	private String toUserFigure;
	private String content;
	private String photos;
	private long objectId;
	private String objectValues;
	private long createTime;
	private boolean checked;
	private boolean processed;
	public long getMessageId() {
		return messageId;
	}
	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}
	public MESSAGETYPE getMessageType() {
		return messageType;
	}
	public void setMessageType(MESSAGETYPE messageType) {
		this.messageType = messageType;
	}
	public long getFromUserId() {
		return fromUserId;
	}
	public void setFromUserId(long fromUserId) {
		this.fromUserId = fromUserId;
	}
	public String getFromUserName() {
		return fromUserName;
	}
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}
	public String getFromUserFigure() {
		return fromUserFigure;
	}
	public void setFromUserFigure(String fromUserFigure) {
		this.fromUserFigure = fromUserFigure;
	}
	public long getToUserId() {
		return toUserId;
	}
	public void setToUserId(long toUserId) {
		this.toUserId = toUserId;
	}
	public String getToUserName() {
		return toUserName;
	}
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}
	public String getToUserFigure() {
		return toUserFigure;
	}
	public void setToUserFigure(String toUserFigure) {
		this.toUserFigure = toUserFigure;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPhotos() {
		return photos;
	}
	public void setPhotos(String photos) {
		this.photos = photos;
	}
	public long getObjectId() {
		return objectId;
	}
	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}
	public String getObjectValues() {
		return objectValues;
	}
	public void setObjectValues(String objectValues) {
		this.objectValues = objectValues;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public boolean isProcessed() {
		return processed;
	}
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
}
