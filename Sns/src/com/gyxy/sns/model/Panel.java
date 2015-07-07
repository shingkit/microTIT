package com.gyxy.sns.model;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

public class Panel extends BmobObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private BmobRelation posts;
	private boolean isPreset;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BmobRelation getPosts() {
		return posts;
	}

	public void setPosts(BmobRelation posts) {
		this.posts = posts;
	}

	public boolean isPreset() {
		return isPreset;
	}

	public void setPreset(boolean isPreset) {
		this.isPreset = isPreset;
	}

	@Override
	public String toString() {
		return "Panel [name=" + name + ", isPreset=" + isPreset
				+ ", getObjectId()=" + getObjectId() + "]";
	}

}
