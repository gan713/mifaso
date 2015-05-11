package com.mifashow.server.domain;

public class BaseMessage {
	private long messageId;
	private int messageType;
	private long fromUserId;
	private long toUserId;
	private String content;
	private long createTime;
	private long objectId;
	private boolean read;
	private boolean processed;
	public long getMessageId() {
		return messageId;
	}
	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}
	public int getMessageType() {
		return messageType;
	}
	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}
	public long getFromUserId() {
		return fromUserId;
	}
	public void setFromUserId(long fromUserId) {
		this.fromUserId = fromUserId;
	}
	public long getToUserId() {
		return toUserId;
	}
	public void setToUserId(long toUserId) {
		this.toUserId = toUserId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public long getObjectId() {
		return objectId;
	}
	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
	public boolean isProcessed() {
		return processed;
	}
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
}
