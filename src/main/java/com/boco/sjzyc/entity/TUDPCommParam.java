package com.boco.sjzyc.entity;

import java.io.Serializable;

/**
 * UDP通信参数类
 * @author dgx
 *
 */
public class TUDPCommParam implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6640596947100032708L;
	/**
	 * 远端主机IP地址
	 */
	private String szRemoteIP; 
	/**
	 * 远端端口号
	 */
	private int dwRemotePort; 
	/**
	 * 本地端口号
	 */
	private int dwLocalPort;
	
	/**
	 * 有参构造方法
	 * @param szRemoteIP
	 * @param dwRemotePort
	 * @param dwLocalPort
	 */
	public TUDPCommParam(String szRemoteIP, int dwRemotePort, int dwLocalPort) {
		super();
		this.szRemoteIP = szRemoteIP;
		this.dwRemotePort = dwRemotePort;
		this.dwLocalPort = dwLocalPort;
	}

	/**
	 * 无参构造方法
	 */
	public TUDPCommParam() {
		super();
	}

	/**
	 * 将实体转换为字符串
	 */
	@Override
	public String toString() {
		return "TUDPCommParam [szRemoteIP=" + szRemoteIP + ", dwRemotePort="
				+ dwRemotePort + ", dwLocalPort=" + dwLocalPort + "]";
	}

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
	 * @返回 本地端口号
	 */
	public int getDwLocalPort() {
		return dwLocalPort;
	}

	/**
	 * @设置 本地端口号
	 */
	public void setDwLocalPort(int dwLocalPort) {
		this.dwLocalPort = dwLocalPort;
	} 
	
}
