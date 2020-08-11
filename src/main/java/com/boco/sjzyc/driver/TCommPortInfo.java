package com.boco.sjzyc.driver;

import java.io.Serializable;
/**
 * 用于注册通信端口的信息
 * @author dgx
 *
 */
public class TCommPortInfo implements Serializable {

	private static final long serialVersionUID = -3583308138889115179L;
	/**
	 * rabbitmqtong通讯交换器名称
	 */
	private String exchangeName;
	/**
	 * rabbitmqtong通讯发送队列路由key值
	 */
	private String sendQueueroutingkey;
	/**
	 * 设备编号
	 */
	private String szDevId;
	/**
	 * 远端主机IP地址
	 */
	private String szRemoteIP;
	/**
	 * 远端端口号
	 */
	private Integer dwRemotePort;
	/**
	 * 本地端口号
	 */
	private int dwLocalPort;
	/**
	 * 通信设备的地址信息
	 */
	private String szAddressParam;
	/**
	 * @return the 通信设备的地址信息
	 */
	public String getSzAddressParam() {
		return szAddressParam;
	}

	/**
	 * @param //通信设备的地址信息
	 */
	public void setSzAddressParam(String szAddressParam) {
		this.szAddressParam = szAddressParam;
	}

	/**
	 * 有参构造方法
	 * @param szAddressParam
	 */
	public TCommPortInfo( String szDevId,String szRemoteIP,
						  String dwRemotePort,String dwLocalPort, String szAddressParam) {
		super();
		this.szDevId = szDevId;
		this.szRemoteIP = szRemoteIP;
		this.dwRemotePort = Integer.valueOf(dwRemotePort);
		this.dwLocalPort = Integer.valueOf(dwLocalPort);
		this.szAddressParam = szAddressParam;
	}

	/**
	 * 无参构造方法
	 */
	public TCommPortInfo() {
		super();
	}

	/**
	 * 将实体转换为字符串
	 */
	@Override
	public String toString() {
		return "TCommPortInfo [szDeviceId=" + szDevId
				+ ", szRemoteIP=" + szRemoteIP + ", dwRemotePort=" + dwRemotePort
				+ ", dwLocalPort=" + dwLocalPort + ", szAddressParam="
				+ szAddressParam + "]";
	}


	public String getSzDevId() {
		return szDevId;
	}

	public void setSzDevId(String szDevId) {
		this.szDevId = szDevId;
	}

	public String getExchangeName() {
		return exchangeName;
	}

	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}

	public String getSendQueueroutingkey() {
		return sendQueueroutingkey;
	}

	public void setSendQueueroutingkey(String sendQueueroutingkey) {
		this.sendQueueroutingkey = sendQueueroutingkey;
	}

	public String getSzRemoteIP() {
		return szRemoteIP;
	}

	public void setSzRemoteIP(String szRemoteIP) {
		this.szRemoteIP = szRemoteIP;
	}

	public Integer getDwRemotePort() {
		return dwRemotePort;
	}

	public void setDwRemotePort(Integer dwRemotePort) {
		this.dwRemotePort = dwRemotePort;
	}

	public int getDwLocalPort() {
		return dwLocalPort;
	}

	public void setDwLocalPort(int dwLocalPort) {
		this.dwLocalPort = dwLocalPort;
	}
}
