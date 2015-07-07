package com.gyxy.sns.model;

import cn.bmob.v3.BmobObject;

public class MoreInfo extends BmobObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String MEMBER = "普通会员";
	public static final String ADMIN = "管理员";
	public static final String SUPER_ADMIN = "超级管理员";
	
	private User user;
	
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	/**
	 * 角色 普通用户、管理员、超级管理员
	 */
	private String userRole;
	
	private String vip_info;
	
	private String name;
	private String vip_image;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVip_image() {
		return vip_image;
	}
	public void setVip_image(String vip_image) {
		this.vip_image = vip_image;
	}
	public String getUserRole() {
		return userRole;
	}
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	public String getVip_info() {
		return vip_info;
	}
	public void setVip_info(String vip_info) {
		this.vip_info = vip_info;
	}
	

}
