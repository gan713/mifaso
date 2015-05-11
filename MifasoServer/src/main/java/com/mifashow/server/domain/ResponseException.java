package com.mifashow.server.domain;

import org.springframework.http.HttpStatus;

public class ResponseException extends Exception {
	private static final long serialVersionUID = 5801299863303885945L;
	private String message;
	private HttpStatus status;
	public ResponseException(String message,HttpStatus status){
		setMessage(message);
		setStatus(status);
	}
	@Override
	public String getMessage(){
		return message;
	}
	public void setMessage(String message){
		this.message=message;
	}
	public HttpStatus getStatus() {
		return status;
	}
	public void setStatus(HttpStatus status) {
		this.status = status;
	}
	

}
