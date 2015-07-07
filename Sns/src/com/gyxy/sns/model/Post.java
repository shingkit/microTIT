package com.gyxy.sns.model;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Ьћзг JavaBean
 * 
 * @author sj
 * 
 */
public class Post extends BmobObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title;
	private String content;
	private BmobFile image;
	private Panel panel;
	private boolean top;
	private boolean jp;

	public boolean isJp() {
		return jp;
	}

	public void setJp(boolean jp) {
		this.jp = jp;
	}

	public boolean isTop() {
		return top;
	}

	public void setTop(boolean top) {
		this.top = top;
	}

	public Panel getPanel() {
		return panel;
	}

	public void setPanel(Panel panel) {
		this.panel = panel;
	}

	private BmobRelation comments;
	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BmobRelation getComments() {
		return comments;
	}

	public void setComments(BmobRelation comments) {
		this.comments = comments;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public BmobFile getImage() {
		return image;
	}

	public void setImage(BmobFile image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "Post [title=" + title + ", content=" + content + ", image="
				+ image + ", panel=" + panel + ", comments=" + comments
				+ ", user=" + user.getObjectId() + "username:"
				+ user.getUsername() + "]";
	}

}
