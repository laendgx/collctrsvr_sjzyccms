package com.boco.sjzyc.tcpcomm;

/**
 * 通信端口接收并解析成Response实体后触发
 * 在MsgDealHandler类中触发。
 * 在TDriver类中实现
 * @author dgx
 *
 */
public interface IMessageListener {
	/**
	 * 将解析成Response实体上传
	 * @param obj
	 */
	public void onMessageRecv(Object obj);
}
