package com.gyxy.sns.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class AuthApplication extends BmobObject {

	public static final int state_applicating = 0;
	public static final int state_agreed = 1;
	public static final int state_rejected = 2;
	private User user;
	private int state;
	private BmobFile image;
	private String vip_info;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

	public BmobFile getImage() {
		return image;
	}

	public void setImage(BmobFile image) {
		this.image = image;
	}

	public String getVip_info() {
		return vip_info;
	}

	public void setVip_info(String vip_info) {
		this.vip_info = vip_info;
	}

}
