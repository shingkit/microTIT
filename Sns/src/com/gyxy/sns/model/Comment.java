package com.gyxy.sns.model;

import cn.bmob.v3.BmobObject;

/**
 * ¸úÌù JavaBean
 * 
 * @author sj
 * 
 */
public class Comment extends BmobObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String content;
	private Post post;
	private User user;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
