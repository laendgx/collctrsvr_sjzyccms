package com.boco.sjzyc.tcpcomm;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息处理
 */
public class MsgDealHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	private IMessageListener messageListener;

	public MsgDealHandler(IMessageListener messageListener) {
		super();
		this.messageListener = messageListener;
	}

	//接收消息
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet)
			throws Exception {
		ByteBuf buffer = packet.copy().content();
		List<Object> out = new ArrayList<Object>();
		ResponseDecoder.decode(buffer, out);
		for(Object obj : out){
			messageListener.onMessageRecv(obj);
		}
	}

	//断开连接时处理
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}




}
