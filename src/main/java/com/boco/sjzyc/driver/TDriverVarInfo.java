package com.boco.sjzyc.driver;

import java.io.Serializable;

/**
 * 驱动程序提供的设备变量种类信息
 * @author dgx
 *
 */
public class TDriverVarInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3065553867782902562L;
	/**
	 * 变量类型id
	 */
	private Integer dwdevTypeid;
	/**
	 * 变量类型id
	 */
	private Integer dwVariantId;

	/**
	 * 变量类型描述
	 */
	private String szVariantDesc;

	/**
	 * 发送命令
	 */
	private Integer dwSendCmd;
	/**
	 * 接收命令
	 */
	private Integer dwRecvCmd;
	/**
	 * 功能描述
	 */
	private String szFuncAbbr;

	
	/**
	 * @return // 变量id
	 */
	public Integer getDwVariantId() {
		return dwVariantId;
	}

	/**
	 * @param  //变量类型id
	 */
	public void setDwVariantId(Integer dwVariantId) {
		this.dwVariantId = dwVariantId;
	}

	/**
	 * @return the 变量描述
	 */
	public String getSzVariantDesc() {
		return szVariantDesc;
	}

	/**
	 * @param //变量类型描述
	 */
	public void setSzVariantDesc(String szVariantDesc) {
		this.szVariantDesc = szVariantDesc;
	}

	/**
	 * 有参构造方法
	 */
	public TDriverVarInfo(Integer dwVariantId, String szVariantDesc,
						  Integer dwSendCmd,Integer dwRecvCmd,String szFuncAbbr) {
		super();
		this.dwVariantId = dwVariantId;
		this.szVariantDesc = szVariantDesc;
		this.dwSendCmd = dwSendCmd;
		this.dwRecvCmd = dwRecvCmd;
		this.szFuncAbbr = szFuncAbbr;
	}

	/**
	 * 无参构造方法
	 */
	public TDriverVarInfo() {
		super();
	}

	/**
	 * 将实体转换为字符串
	 */
	@Override
	public String toString() {
		return "TDriverVarInfo [dwVariantId=" + dwVariantId
				+ ", szVariantDesc=" + szVariantDesc + "]";
	}


	public int getDwSendCmd() {
		return dwSendCmd;
	}

	public void setDwSendCmd(int dwSendCmd) {
		this.dwSendCmd = dwSendCmd;
	}

	public int getDwRecvCmd() {
		return dwRecvCmd;
	}

	public void setDwRecvCmd(int dwRecvCmd) {
		this.dwRecvCmd = dwRecvCmd;
	}

	public String getSzFuncAbbr() {
		return szFuncAbbr;
	}

	public void setSzFuncAbbr(String szFuncAbbr) {
		this.szFuncAbbr = szFuncAbbr;
	}


	public Integer getDwdevTypeid() {
		return dwdevTypeid;
	}

	public void setDwdevTypeid(Integer dwdevTypeid) {
		this.dwdevTypeid = dwdevTypeid;
	}
}
