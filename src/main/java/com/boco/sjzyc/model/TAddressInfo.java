package com.boco.sjzyc.model;

import java.io.Serializable;

/**
 * 设备变量寻址信息
 * @author dgx
 *
 */
public class TAddressInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2034664886746868081L;
	
	/**
	 * 发送命令
	 */
	private int dwSendCmd;
	/**
	 * 接收命令
	 */
	private int dwRecvCmd;
	/**
	 * 功能描述
	 */
	private String szFuncAbbr;
	/**
	 * @return the 发送命令
	 */
	public int getDwSendCmd() {
		return dwSendCmd;
	}
	/**
	 * @param 发送命令 the dwSendCmd to set
	 */
	public void setDwSendCmd(int dwSendCmd) {
		this.dwSendCmd = dwSendCmd;
	}
	/**
	 * @return the 接收命令
	 */
	public int getDwRecvCmd() {
		return dwRecvCmd;
	}
	/**
	 * @param 接收命令 the dwRecvCmd to set
	 */
	public void setDwRecvCmd(int dwRecvCmd) {
		this.dwRecvCmd = dwRecvCmd;
	}
	/**
	 * @return the 功能描述
	 */
	public String getSzFuncAbbr() {
		return szFuncAbbr;
	}
	/**
	 * @param 功能描述 the szFuncAbbr to set
	 */
	public void setSzFuncAbbr(String szFuncAbbr) {
		this.szFuncAbbr = szFuncAbbr;
	}
	
	/**
	 * 有参构造方法
	 * @param dwSendCmd
	 * @param dwRecvCmd
	 * @param szFuncAbbr
	 */
	public TAddressInfo(int dwSendCmd, int dwRecvCmd, String szFuncAbbr) {
		super();
		this.dwSendCmd = dwSendCmd;
		this.dwRecvCmd = dwRecvCmd;
		this.szFuncAbbr = szFuncAbbr;
	}
	
	/**
	 * 无参构造方法
	 */
	public TAddressInfo() {
		super();
	}
	
	
}
