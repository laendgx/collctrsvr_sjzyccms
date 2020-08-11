package com.boco.sjzyc.entity;

import java.io.Serializable;

/**
 * 串口通信参数类
 * @author dgx
 *
 */
public class TSerialCommParam implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -242113437222070570L;
	/**
	 * 串口名称
	 */
	private String szPortName; 
	/**
	 * 波特率
	 */
	private int dwBaudRate; 
	/**
	 * 数据位
	 */
	private int dwByteSize; 
	/**
	 * 停止位
	 */
	private int dwStopBits; 
	/**
	 * 校验
	 */
	private int dwParity;
	/**
	 * @返回 串口名称
	 */
	public String getSzPortName() {
		return szPortName;
	}
	/**
	 * @设置 串口名称
	 */
	public void setSzPortName(String szPortName) {
		this.szPortName = szPortName;
	}
	/**
	 * @返回 波特率
	 */
	public int getDwBaudRate() {
		return dwBaudRate;
	}
	/**
	 * @设置 波特率
	 */
	public void setDwBaudRate(int dwBaudRate) {
		this.dwBaudRate = dwBaudRate;
	}
	/**
	 * @返回 数据位
	 */
	public int getDwByteSize() {
		return dwByteSize;
	}
	/**
	 * @设置 数据位
	 */
	public void setDwByteSize(int dwByteSize) {
		this.dwByteSize = dwByteSize;
	}
	/**
	 * @返回 停止位
	 */
	public int getDwStopBits() {
		return dwStopBits;
	}
	/**
	 * @设置 停止位
	 */
	public void setDwStopBits(int dwStopBits) {
		this.dwStopBits = dwStopBits;
	}
	/**
	 * @返回 校验
	 */
	public int getDwParity() {
		return dwParity;
	}
	/**
	 * @设置 校验
	 */
	public void setDwParity(int dwParity) {
		this.dwParity = dwParity;
	}
	
	/**
	 * 有参构造方法
	 * @param szPortName
	 * @param dwBaudRate
	 * @param dwByteSize
	 * @param dwStopBits
	 * @param dwParity
	 */
	public TSerialCommParam(String szPortName, int dwBaudRate, int dwByteSize,
			int dwStopBits, int dwParity) {
		super();
		this.szPortName = szPortName;
		this.dwBaudRate = dwBaudRate;
		this.dwByteSize = dwByteSize;
		this.dwStopBits = dwStopBits;
		this.dwParity = dwParity;
	}
	
	/**
	 * 无参构造方法
	 */
	public TSerialCommParam() {
		super();
	}
	
	/**
	 * 将实体转换为字符串
	 */
	@Override
	public String toString() {
		return "TSerialCommParam [szPortName=" + szPortName + ", dwBaudRate="
				+ dwBaudRate + ", dwByteSize=" + dwByteSize + ", dwStopBits="
				+ dwStopBits + ", dwParity=" + dwParity + "]";
	} 
	
}
