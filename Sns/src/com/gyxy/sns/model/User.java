package com.gyxy.sns.model;

import java.io.Serializable;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobRole;
import cn.bmob.v3.datatype.BmobRelation;

public class User extends BmobChatUser implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

 
	
	private MoreInfo moreInfo;
	

	public MoreInfo getMoreInfo() {
		return moreInfo;
	}

	public void setMoreInfo(MoreInfo moreInfo) {
		this.moreInfo = moreInfo;
	}

//	/**
//	 * 角色
//	 */
//	private String userRole;

//	private boolean vipVertified;

	/**
	 * 学号
	 */
	private String stuId;
	/**
	 * 手机号
	 */
	private String phoneNumber;

	// /**
	// * 头像
	// */
	// private BmobFile head;
	/**
	 * 性别
	 */
	private boolean sex;
//	/**
//	 * 认证身份
//	 */
//	private String vip_info;

//	private BmobRelation posts;
//
//	public BmobRelation getPosts() {
//		return posts;
//	}
//
//	public void setPosts(BmobRelation posts) {
//		this.posts = posts;
//	}

//	public String getVip_info() {
//		return vip_info;
//	}
//
//	public void setVip_info(String vip_info) {
//		this.vip_info = vip_info;
//	}

//	public boolean isVip_vertified() {
//		return vipVertified;
//	}
//
//	public void setVip_vertified(boolean vipVertified) {
//		this.vipVertified = vipVertified;
//	}

	public boolean getSex() {
		return sex;
	}

	public void setSex(boolean sex) {
		this.sex = sex;
	}

	// public BmobFile getHead() {
	// return head;
	// }
	// public void setHead(BmobFile head) {
	// this.head = head;
	// }
	public String getStuId() {
		return stuId;
	}

	public void setStuId(String stuId) {
		this.stuId = stuId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String toString() {
		return "User [moreInfo=" + moreInfo + ", stuId=" + stuId + ", sex="
				+ sex + ", getAvatar()=" + getAvatar() + ", getUsername()="
				+ getUsername() + ", getObjectId()=" + getObjectId() + "]";
	}

 
//
//	public String getUserRole() {
//		return userRole;
//	}
//
//	public void setUserRole(String userRole) {
//		this.userRole = userRole;
//	}
 

}
