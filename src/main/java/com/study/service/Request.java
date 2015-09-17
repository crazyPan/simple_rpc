package com.study.service;

import java.io.Serializable;

public class Request implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Integer num1;
	public Integer num2;
	
	public Request(Integer num1,Integer num2) {
		this.num1 = num1;
		this.num2 = num2;
	}
	
	public Integer getNum1() {
		return num1;
	}
	public void setNum1(Integer num1) {
		this.num1 = num1;
	}
	public Integer getNum2() {
		return num2;
	}
	public void setNum2(Integer num2) {
		this.num2 = num2;
	}
}
