package com.gyxy.sns.model;

import cn.bmob.v3.BmobObject;

public class MoreInfo extends BmobObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String MEMBER = "��ͨ��Ա";
	public static final String ADMIN = "����Ա";
	public static final String SUPER_ADMIN = "��������Ա";
	
	private User user;
	
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	/**
	 * ��ɫ ��ͨ�û�������Ա����������Ա
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
