package com.boco.sjzyc.entity;

import java.io.Serializable;

/**
 * TCP通信参数类
 *
 */
public class TTCPCommParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7822191351734551138L;
	/**
	 * 远端主机IP地址
	 */
	private String szRemoteIP; 
	/**
	 * 远端端口号
	 */
	private int dwRemotePort;
	/**
	 * @返回 远端主机IP地址
	 */
	public String getSzRemoteIP() {
		return szRemoteIP;
	}
	/**
	 * @设置 远端主机IP地址
	 */
	public void setSzRemoteIP(String szRemoteIP) {
		this.szRemoteIP = szRemoteIP;
	}
	/**
	 * @返回 远端端口号
	 */
	public int getDwRemotePort() {
		return dwRemotePort;
	}
	/**
	 * @设置 远端端口号
	 */
	public void setDwRemotePort(int dwRemotePort) {
		this.dwRemotePort = dwRemotePort;
	}
	
	/**
	 * 有参构造方法
	 * @param szRemoteIP
	 * @param dwRemotePort
	 */
	public TTCPCommParam(String szRemoteIP, int dwRemotePort) {
		super();
		this.szRemoteIP = szRemoteIP;
		this.dwRemotePort = dwRemotePort;
	}
	
	/**
	 * 无参构造方法
	 */
	public TTCPCommParam() {
		super();
	}
	
	/**
	 * 将实体转换为字符串
	 */
	@Override
	public String toString() {
		return "TTCPCommParam [szRemoteIP=" + szRemoteIP + ", dwRemotePort="
				+ dwRemotePort + "]";
	} 
	
}
