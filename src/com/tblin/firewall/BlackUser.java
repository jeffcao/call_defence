package com.tblin.firewall;

public class BlackUser {

	private String name;
	private String mobile;
	private int type;
	private boolean iskillcall;
	private boolean iskillsms;
	public static final String BASE_TYPE = "base_type";
	public static final String ENHANCED_TYPE = "enhanced_type";
	public static final String UNKNOWNAME = "未知名称";

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public boolean isIskillcall() {
		return iskillcall;
	}

	public void setIskillcall(boolean iskillcall) {
		this.iskillcall = iskillcall;
	}

	public boolean isIskillsms() {
		return iskillsms;
	}

	public void setIskillsms(boolean iskillsms) {
		this.iskillsms = iskillsms;
	}

	public String getName() {
		return name == null ? UNKNOWNAME : name;
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


}
