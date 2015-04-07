package com.tblin.HandsetGuardant;

public class BlockEvent {

	private String name;
	private String mobile;
	private long time;
	private int type;
	private String contnet;
	private String address;
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContnet() {
		return contnet;
	}

	public void setContnet(String contnet) {
		this.contnet = contnet;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public BlockEvent(String name,String mobile,long time){
		this.name = name;
		this.mobile = mobile;
		this.time = time;
	}
	public BlockEvent(String name, String mobile, long time,String address,int k) {
		this.name = name;
		this.mobile = mobile;
		this.time = time;
		this.address=address;
		
	}
	public BlockEvent(String name, String mobile, long time,String content,String address,boolean s) {
		this.name = name;
		this.mobile = mobile;
		this.time = time;
		this.contnet=content;
		this.address=address;
		
		
	}
	public BlockEvent(String name,String mobile,long time,String content){
		this.name = name;
		this.mobile = mobile;
		this.time = time;
		this.contnet=content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
