package com.gyxy.sns.model;

import cn.bmob.v3.BmobObject;

public class AdminApplication extends BmobObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int state_applicating = 1;
	public static final int state_agreed = 2;
	public static final int state_rejected = 3;
	private User user;
	private int state;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
}
