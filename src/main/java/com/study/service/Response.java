package com.study.service;

import java.io.Serializable;

public class Response implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Integer result;

	public Response(Integer result) {
		this.result = result;
	}	
	
	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}
}
