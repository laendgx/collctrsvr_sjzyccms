package com.boco.sjzyc.tcpcomm;

/**
 * 端口状态监听接口
 * 1、由采集驱动中的TDriver实现
 * 2、在TTCPComm中触发
 * @author dgx
 *
 */
public interface IPortStatusListener {
	/**
	 * 端口状态改变时触发
	 * true－通讯正常
	 * false-通讯中断
	 * @param status
	 */
	public void onPortStatusChanged(boolean status);
}
