package com.boco.sjzyc.model;

/**
 * 设备变量的运行信息
 *
 */
public class TAddressParseInfo {
	/**
	 * 设备变量编号
	 */
	private int dwDeviceVarId; 
	/**
	 * 该设备变量在本地的列表中的索引
	 */
	private int dwLocalIndex;  
	/**
	 * 设备变量地址信息
	 */
	private TAddressInfo rcdAddress;
	/**
	 * @return the 设备变量编号
	 */
	public int getDwDeviceVarId() {
		return dwDeviceVarId;
	}
	/**
	 * @param 设备变量编号 the dwDeviceVarId to set
	 */
	public void setDwDeviceVarId(int dwDeviceVarId) {
		this.dwDeviceVarId = dwDeviceVarId;
	}
	/**
	 * @return the 该设备变量在本地的列表中的索引
	 */
	public int getDwLocalIndex() {
		return dwLocalIndex;
	}
	/**
	 * @param 该设备变量在本地的列表中的索引 the dwLocalIndex to set
	 */
	public void setDwLocalIndex(int dwLocalIndex) {
		this.dwLocalIndex = dwLocalIndex;
	}
	/**
	 * @return the 设备变量地址信息
	 */
	public TAddressInfo getRcdAddress() {
		return rcdAddress;
	}
	/**
	 * @param 设备变量地址信息 the rcdAddress to set
	 */
	public void setRcdAddress(TAddressInfo rcdAddress) {
		this.rcdAddress = rcdAddress;
	} 
	
}
