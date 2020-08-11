package com.boco.sjzyc.tcpcomm;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.boco.sjzyc.model.Request;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TCP通信类
 * @author dgx
 *
 */
public class TUDPComm implements Runnable {
	private static Logger LOGGER = LoggerFactory.getLogger(TUDPComm.class.getName());
	//定时连接线程
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		
	/**
	 * 通讯端口状态监听器
	 */
	private IPortStatusListener portStatusListener;
	/**
	 * 数据上传监听器
	 */
	private IMessageListener messageListener;
	
	/**
	 * 线程池
	 */
	private NioEventLoopGroup group = new NioEventLoopGroup();
	/**
	 * 服务类
	 */
	private Bootstrap bootstrap;
	/**
	 * 状态监听标识
	 */
    private ChannelFutureListener channelFutureListener = null;
    /**
     * 会话
     */
    private Channel channel;
    /**
     * 远端UDP接收地址
     */
    private InetSocketAddress remoteAddress =null;

    private String host_;  
	private int remotePort_;
	private int localPort_;
    /**
     * 关闭标识
     */
    private boolean isCloseFlag = false;
    
    //构造方法
    public TUDPComm(String serverIP, int remotePort, int localPort, IPortStatusListener portStatusListener, IMessageListener messageListener) {
    	this.host_ = serverIP;
    	this.remotePort_ = remotePort;
    	this.localPort_ = localPort;
    	
		this.portStatusListener = portStatusListener;
		this.messageListener = messageListener;
		initClient();
    }

    /**
     *  初始化客户端
     */
    private void initClient() {   
    	bootstrap = null;
        // Client服务启动器 3.x的ClientBootstrap
        // 改为Bootstrap，且构造函数变化很大，这里用无参构造。
        bootstrap = new Bootstrap();
        // 指定EventLoopGroup
        bootstrap.group(group);
        
        // 指定channel类型
        bootstrap.channel(NioDatagramChannel.class);
        // 指定Handler
        bootstrap.handler(new MsgDealHandler(messageListener));
        
    }

    /**
     * 连接到服务端
     */
    private void doConnect() {    	
    	//LOGGER.info("Host-" + host_ + ", Port-" + remotePort_ + " doConnect");
        if (channel != null){
        	try{
        		channel.close();
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        }
        try {
        	channel = bootstrap.bind(localPort_).sync().channel();
            remoteAddress = new InetSocketAddress(host_, remotePort_);
        } catch (Exception e) {
            //上传端口状态
        	portStatusListener.onPortStatusChanged(false);
        	LOGGER.error("Host-" + host_ + ", Port-" + remotePort_ + "关闭连接");
        } 
    }
    
    /**
     * 下发数据
     */
    public void sendMsg(Object request){
    	try{
    		if (channel.isActive()){
    			ByteBuf out = RequestEncoder.encode((Request)request);
    			channel.writeAndFlush(new DatagramPacket(out, remoteAddress));
    		}
    	}catch(Exception ex){
    		LOGGER.error("Host-" + host_ + ", Port-" + remotePort_ + " 下发数据失败！");
    	}
    }

	/**
	 * 设置退出标识
	 */
	public void setCloseFlag(boolean isCloseFlag) {
		this.isCloseFlag = isCloseFlag;
	}


	public void shutdown(){
    	this.isCloseFlag = true;
    	if (channel != null){
    		try{
    			channel.close();
    		}catch(Exception e){}
    	}
    	
    	try{
    		group.shutdownGracefully();
    	}catch(Exception e){}
    	
    	try{
    		executor.shutdownNow();
    	}catch(Exception e){}
    }

	/**
	 * 连接端口
	 */
	@Override
	public void run() {
		doConnect();		
	}
}
