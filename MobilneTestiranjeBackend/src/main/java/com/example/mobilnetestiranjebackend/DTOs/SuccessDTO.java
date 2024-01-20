package com.example.mobilnetestiranjebackend.DTOs;

public class SuccessDTO {
	
	private String message;

	public SuccessDTO(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
