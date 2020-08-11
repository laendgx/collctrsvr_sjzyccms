package com.boco.sjzyc.entity;

import java.io.Serializable;

/**
 * 通信参数实体
 *
 */
public class TCommParam implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1974195627909251482L;
	
	/**
	 * 通信方式
	 */
	private int dwType;
	
	/**
	 * 串口通信对象
	 */
	private TSerialCommParam rcdSerial;

	/**
	 * udp通信对象
	 */
	private TUDPCommParam rcdUDP;

	/**
	 * Tcp通信对象
	 */
	private TTCPCommParam rcdTCP;

	/**
	 * @返回 通信方式
	 */
	public int getDwType() {
		return dwType;
	}

	/**
	 * @设置 通信方式
	 */
	public void setDwType(int dwType) {
		this.dwType = dwType;
	}

	/**
	 * @返回 串口通信对象
	 */
	public TSerialCommParam getRcdSerial() {
		return rcdSerial;
	}

	/**
	 * @设置 串口通信对象
	 */
	public void setRcdSerial(TSerialCommParam rcdSerial) {
		this.rcdSerial = rcdSerial;
	}

	/**
	 * @返回 udp通信对象
	 */
	public TUDPCommParam getRcdUDP() {
		return rcdUDP;
	}

	/**
	 * @设置 udp通信对象
	 */
	public void setRcdUDP(TUDPCommParam rcdUDP) {
		this.rcdUDP = rcdUDP;
	}

	/**
	 * @返回 Tcp通信对象
	 */
	public TTCPCommParam getRcdTCP() {
		return rcdTCP;
	}

	/**
	 * @设置 Tcp通信对象
	 */
	public void setRcdTCP(TTCPCommParam rcdTCP) {
		this.rcdTCP = rcdTCP;
	}
	
	
}
