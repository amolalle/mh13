package com.mh13;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author amola
 *
 */
public class Tweet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String screenname;
	private String name;
	private String text;
	private String quotedScreenName;
	private String quotedTex;
	private Date createdAt;
	
	List<String> hashTags = new ArrayList<String>();
	
	public String getScreenname() {
		return screenname;
	}
	public void setScreenname(String screenname) {
		this.screenname = screenname;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getQuotedScreenName() {
		return quotedScreenName;
	}
	public void setQuotedScreenName(String quotedScreenName) {
		this.quotedScreenName = quotedScreenName;
	}
	public String getQuotedTex() {
		return quotedTex;
	}
	public void setQuotedTex(String quotedTex) {
		this.quotedTex = quotedTex;
	}
	
	public List<String> getHashTags() {
		return hashTags;
	}
	public void setHashTag(String hashTag){
		hashTags.add(hashTag);
	}
	public void setHashTags(List<String> hashTags) {
		this.hashTags = hashTags;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	@Override
	public String toString() {
		return "Tweet [screenname=" + screenname + ", name=" + name + ", text="
				+ text + ", quotedScreenName=" + quotedScreenName
				+ ", quotedTex=" + quotedTex + ", createdAt=" + createdAt
				+ ", hashTags=" + hashTags + "]";
	}
}
