package com.mifashow.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MessageBox implements Serializable {
	private static final long serialVersionUID = 7054435137717470271L;
	private ArrayList<Message> messages;
	private int newNum;
	public MessageBox(){
		newNum=0;
		messages=new ArrayList<Message>();
	}
	public List<Message> getMessages() {
		return messages;
	}
	public void setMessages(ArrayList<Message> messages) {
		this.messages = messages;
	}
	public ArrayList<Message> getHandleable(){
		ArrayList<Message> ms=new ArrayList<Message>();
		for(Message m:messages){
			if(!m.isProcessed()){
				ms.add(m);
			}
		}
		return ms;
	}
	public void addMessage(Message message,boolean refresh){
		boolean isNew=true;
		for(Message m:messages){
			if(m.getMessageId()==message.getMessageId()){
				isNew=false;
				break;
			}
		}
		if(isNew){
			this.messages.add(message);
			if(refresh)newNum++;
		}
	}
	public int getNewNum() {
		return newNum;
	}
	public void zeroNewNum(){
		newNum=0;
	}
	public Message getLast(){
		return messages.get(messages.size()-1);
	}
	public void handle(long messageId){
		for(Message m:messages){
			if(m.getMessageId()==messageId){
				m.setProcessed(true);
				break;
			}
		}
	}
	

}
