package com.boco.sjzyc.driver;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cms.impl.GroupNJJXCmsProtocol;
import com.boco.sjzyc.constant.ConstVarient;
import com.boco.sjzyc.constant.DriverPubConst;
import com.boco.sjzyc.tcpcomm.*;

import com.boco.sjzyc.utils.CoderUtils;
import com.boco.protocolBody.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import com.boco.sjzyc.constant.DriverConst;
import com.boco.sjzyc.model.Request;
import com.boco.sjzyc.model.Response;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

/**
 * 设备通讯类
 * 每个设备端口对应该类的一个实例
 *
 */
public class TDriver implements IMessageListener, IPortStatusListener {
	private static final Logger LOGGER= LoggerFactory.getLogger(TDriver.class.getName());
	//通讯设备状态
    public int FStatus;
	//等待应答次数
    private int fwaitCount;
	//最大的数据包长度
	private final int maxPktLen = 256;

	//rabbitmq通讯对象
	public RabbitTemplate rabbitTemplate;
	public List<TDriverVarInfo> devDriverVarInfo;
	/**
	 * 驱动的通讯端口编号属性
	 */
	public TCommPortInfo portInfo;
	/**
	 * 编码
	 */
	private MessageToByteEncoder<Request> encoder;			
	/**
	 * 解码
	 */
	ByteToMessageDecoder decoder;	
	/**
	 * 处理器
	 */
	private SimpleChannelInboundHandler<Response> handler;
	/**
	 * 通信对象
	 */
	private TUDPComm fudpComm;

    /**
     * @return the 驱动的通讯端口编号
     */
    public String getDevId() {
        return this.portInfo.getSzDevId();
    }

	/**
	 * 发送数据缓冲区
     * 发送数据滑动窗口
	 */
	private TSlidingWindow fwindow;
	/**
	 * 设备地址
	 */
	private int faddress;
	/**
	 * 盛放通讯端口对象的线程池
	 */
	private ExecutorService tcpThreadPool;
	/**
	 * 定时器线程
	 */
	private ScheduledExecutorService scheduledService;
	//定时器对象
	private Future future = null;
	//要发送的数据
	private Request FPktToSend;

	//构造函数
	public TDriver(TCommPortInfo portInfotemp,RabbitTemplate rabbitTemplatetemp,List<TDriverVarInfo> devDriverVarInfotemp)
    {
		this.portInfo = portInfotemp;
		this.rabbitTemplate=rabbitTemplatetemp;
		this.devDriverVarInfo=devDriverVarInfotemp;
		TDriverList.getInstance().add(this);

		//初始化滑动窗口
		fwindow = new TSlidingWindow();
		
		//获取设备地址
		AddrInfoBean addrBean = new AddrInfoBean();
		if (this.getAddressInfo(portInfo.getSzAddressParam(), addrBean))
		{
			this.faddress = addrBean.getAddr();
		} else{
			this.faddress = 0;
		}
		
		//初始化通信端口
		fudpComm =new TUDPComm(portInfo.getSzRemoteIP(), portInfo.getDwRemotePort(),
				portInfo.getDwLocalPort(), this, this);
    }
    
	/**
	 * 启动驱动，开始运行
	 */
    public void startDriver(){
    	//初始化线程池
		tcpThreadPool = Executors.newFixedThreadPool(1);
		tcpThreadPool.execute(fudpComm);
		
		//初始化定时器线程
		scheduledService = Executors.newSingleThreadScheduledExecutor();
    }
    
    /**
     * 停止驱动
     */
    public void stopDriver(){
    	try{
			fudpComm.shutdown();
    	}catch(Exception ex){}
    	
    	try{
    		tcpThreadPool.shutdownNow();
    	}catch(Exception ex){}
    	
    	try{
    		if (future != null){
    			future.cancel(true);
    		}
    	}catch(Exception ex){}
    	
    	try{
    		scheduledService.shutdownNow();
    	}catch(Exception ex){}    	
    }
	
	/**
	 * 释放
	 */
	public void close(){
		
	}
	/**
	 * 向发送列表中加入轮询数据
	 */
    public void AddLoopCheckPkt(){        
        try
        {
            if (fwindow == null)
                return;
            int cnt = fwindow.getFrameCount();
            if (cnt == 0)
            {
            	Request Frm = new Request(this.faddress, DriverConst.Const_Cmd_Check, new byte[]{});
            	Frm.setIndex("-1");
            	List<Request> list = new ArrayList<Request>();
            	list.add(Frm);
                fwindow.addFrame(list);
				//LOGGER.info("AddLoopCheckPkt:发送心跳包");
                NotifyToSendPkt();
            }

        } catch (Exception ex)
        {
            LOGGER.error("AddLoopCheckPkt:" + ex.getMessage());
        }
    }
	//json字符串与对象之间的转换
	public static<T> Object JSONToObj(String jsonStr,Class<T> obj) {
		T t = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			t = objectMapper.readValue(jsonStr,
					obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

    /**
     * 发送数据方法
     * @param businessNo
     * @param
     * @return
     */
    public boolean sendData(String businessNo, DevVarInfo devVarInfo)
    {
		String devvartypeid=devVarInfo.getDevVarTypeId();
		String sendStr = devVarInfo.getDevVarValue();
		//LOGGER.info("-------------------------->下发数据"+devvartypeid+" "+sendStr);

		boolean result = false;
        Request Frm;

        if (FStatus != ConstVarient.COMM_STATUS_Connected)
        {
            LOGGER.error("下发数据时，通讯状态为离线状态！");
            return result;
        }
		TDriverVarInfo  sendDriverVarInfo=getDriverVarInfo(devvartypeid);
        //为了防止用户查询到错误的显示信息，当查询屏幕显示时屏蔽掉其它下发数据
        byte[] ArrSend = null;
        int fValue;
        String Dummy = "";
        try{
        switch (sendDriverVarInfo.getDwSendCmd())
        {
            case DriverConst.Const_Cmd_PlayLst: //播放表
				//Playlist wewqrwq=  (Playlist)JSONToObj(sendStr,Playlist.class);//下发播放表信息
				GroupNJJXCmsProtocol NJJXCmsProtocol=new GroupNJJXCmsProtocol();
				String playlist = NJJXCmsProtocol.buildProtocal(sendStr);
             	System.out.println("下发播放表--->"+playlist);

            	ByteArrayOutputStream baos = new ByteArrayOutputStream();
            	baos.write("PLAY.LST".getBytes("UTF-8"));

            	//加入文件名结束符
            	byte[] fileNameOverFlagArr = new byte[]{0x2B};
            	baos.write(fileNameOverFlagArr);

            	byte[] connArr = new byte[]{0,0,0,0};
            	baos.write(connArr);

            	baos.write(playlist.getBytes("UTF-8"));

            	ArrSend = baos.toByteArray();
                break;
            case DriverConst.Const_Cmd_Bright: //设备屏幕亮度
				if (sendDriverVarInfo.getSzFuncAbbr().equals("MOD")) {
					try {
					fValue = Integer.parseInt(devVarInfo.getDevVarValue());
					int defaultBri = 23;
					//0-自动; 1-手动
					Dummy = fValue + "" + CoderUtils.IntTo2SizeString(defaultBri)
							+ CoderUtils.IntTo2SizeString(defaultBri)
							+ CoderUtils.IntTo2SizeString(defaultBri);
					} catch (Exception ex) {
						LOGGER.error("控制情报板亮度时，下发的数据无法转换为整型数.错误信息:"
								+ ex.getMessage());
						return result;
					}

				} else {
					//控制亮度，控制亮度前，必须将亮度转换为手动方式
					try {
						fValue = Integer.parseInt(sendStr);
						Dummy = "1" + CoderUtils.IntTo2SizeString(fValue)
								+ CoderUtils.IntTo2SizeString(fValue)
								+ CoderUtils.IntTo2SizeString(fValue);
						ArrSend = Dummy.getBytes("UTF-8");
					} catch (Exception ex) {
						LOGGER.error("控制情报板亮度时，下发的数据无法转换为整型数.错误信息:"
								+ ex.getMessage());
						return result;
					}
				}
                break;
            default:
                Dummy = "";
                ArrSend = new byte[0];
                break;
        }
        } catch(Exception ex){
        	LOGGER.error("TDriver.senddata协议转换出错，错误信息：" + ex.getMessage());
        	return result;
        }

        Frm = new Request(faddress, sendDriverVarInfo.getDwSendCmd(), ArrSend);
        Frm.setIndex(businessNo);
        List<Request> list = new ArrayList<Request>();
        list.add(Frm);
        fwindow.addFrame(list);

        NotifyToSendPkt();
        result = true;
        return result;
    }

	/**
	 * 返回设备控制码
	 */
    public  TDriverVarInfo getDriverVarInfo(String devvartypeid){
		TDriverVarInfo driverVarInforesult=new TDriverVarInfo();
		if(this.devDriverVarInfo==null) return driverVarInforesult;
		for (TDriverVarInfo driverVarInfo : this.devDriverVarInfo) {
			String VariantId=driverVarInfo.getDwVariantId().toString();
			if (VariantId.equals(devvartypeid)){
				driverVarInforesult=driverVarInfo;
				return driverVarInforesult;
			}
		}
		return driverVarInforesult;
	}


	/**
	 * 端口状态发生改变时被触发
	 */
	@Override
	public void onPortStatusChanged(boolean status) {
		if (status){
			this.SetStatus(ConstVarient.COMM_STATUS_Connected);
		} else {
			this.SetStatus(ConstVarient.COMM_STATUS_DisConnect);
		}	
	}

	/**
	 * MsgDealHandler将接收到的数据解析成Response实体时触发
	 */
	@Override
	public void onMessageRecv(Object obj) {
		try {
			//System.out.println("接收数据------------------------------->" );
			Response resp = (Response) obj;
			if (resp == null || resp.getDataArray() == null || resp.getDataArray().length == 0){
				return;
			}
			SetStatus(ConstVarient.COMM_STATUS_Connected);
			
			//停止超时等待
			stopWaitTimer(); 

            Request Frm = fwindow.getCurrentFrame();
            if (Frm == null)
            {
                return;
            }

            int Cmd = Frm.getFrameType(); //获得当前的命令
//            if ((Cmd == DriverConst.Const_Cmd_PlayLst) || (Cmd == DriverConst.Const_Cmd_ScrOnOff))
//            {
//                if (resp.getDataArray()[0] != DriverConst.Const_Answer_Success)
//                {
//                    CheckReSendTime(); //重发
//                    return;
//                }
//            }
            
            Frm = fwindow.processRecvAnswer();
            if (Frm == null)
            {
                return;
            }
			//消息发送交易编码
			String businessNo = Frm.getIndex();
            
            //TAddressInfo Address = new TAddressInfo();
            //应答信息
            if ((Cmd == DriverConst.Const_Cmd_PlayLst) || (Cmd == DriverConst.Const_Cmd_Bright))
            {
                if (resp.getDataArray()[0] == DriverConst.Const_Answer_Success)
                {
					RabbitmqCtrlCmdback(businessNo, true);
                    //数据发送成功
                    NotifyToSendPkt(); //发送下一个数据报文
                }
            }
            else //返回数据信息
                if (Cmd == DriverConst.Const_Cmd_Check)
                {
                    //Address.setDwRecvCmd(Cmd); //查询命令
					RabbitmqCheckback(businessNo,resp.getDataArray());
                    NotifyToSendPkt();
                }

			//应答信息
			switch (Cmd) {
				//下发播放列表文件名称－－返回
				case  DriverConst.Const_Frm_SendFileName_Recv:
					if (resp.getDataArray().length ==1 && resp.getDataArray()[0] == DriverConst.Const_Answer_Success)
					{
						//播放列表名称发布成功
					} else {
						RabbitmqCtrlCmdback(businessNo,false);//数据发送失败
					}
					break;
				//下发播放列表文件内容－返回
				case DriverConst.Const_Frm_SendFileContent_Recv:
					if (resp.getDataArray().length ==3 && resp.getDataArray()[2] == DriverConst.Const_Answer_Success)
					{
						//下发播放列表文件内容发送成功
					} else {
						RabbitmqCtrlCmdback(businessNo,false);//数据发送失败
					}
					break;
				//指定播放列表
				case DriverConst.Const_Cmd_DisPlayLst_Recv:
					if (resp.getDataArray().length ==1 && resp.getDataArray()[0] == DriverConst.Const_Answer_Success)
					{
						//发送成功
						RabbitmqCtrlCmdback(businessNo, true);
					} else {
						RabbitmqCtrlCmdback(businessNo,false);//数据发送失败
					}
					break;
				//开关屏
				case DriverConst.Const_Cmd_ScrOnOff_Recv:
					if (resp.getDataArray().length ==1 && resp.getDataArray()[0] == DriverConst.Const_Answer_Success)
					{
						//发送成功
						RabbitmqCtrlCmdback(businessNo, true);
					} else {
						RabbitmqCtrlCmdback(businessNo,false);//数据发送失败
					}
					break;
				//调节亮度
				case DriverConst.Const_Cmd_Bright_Recv:
					if (resp.getDataArray().length ==1 && resp.getDataArray()[0] == DriverConst.Const_Answer_Success)
					{
						//发送成功
						RabbitmqCtrlCmdback(businessNo, true);
					} else {
						RabbitmqCtrlCmdback(businessNo,false);//数据发送失败
					}
					break;
				default:
					//RabbitmqCtrlCmdback(businessNo,false);//数据发送失败
					break;
			}
			NotifyToSendPkt(); //发送下一个数据报文
		} catch (Exception ex) {
		}
	}
	/**
	 * 设备控制命令反馈
	 */
	public void RabbitmqCtrlCmdback(String businessNo,boolean SendFlag) {
		try {
			String curTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			String Devid=this.portInfo.getSzDevId();
			Protocolbody Protocolbodytemp = new Protocolbody();
			Protocolbodytemp.setBusinessNo(businessNo);
			Protocolbodytemp.setInfoType(InfoType.MSG_CMD_CMS);
			Identity Identitytemp=new Identity();
			Identitytemp.setSourceId("collctrsvr_sjzyc_cms");
			Identitytemp.setTargetId("jkcommctrsvr");
			Identitytemp.setCreateTime(curTime);
			Protocolbodytemp.setIdentity(Identitytemp);

			SubPackage subPackage = new SubPackage();
			subPackage.setOrgId("20300");
			subPackage.setDevId(Devid);
			subPackage.setCollCtrTime(curTime);

			Protocolbodytemp.setSubPackage(subPackage);
			ReturnState returnState=new ReturnState();
			if(SendFlag) {
				returnState.setReturnCode(ReturnCode.ReturnCode_success);
				returnState.setReturnMessage("播放表发送成功");
			}
			else
			{
				returnState.setReturnCode(ReturnCode.ReturnCode_unknown);
				returnState.setReturnMessage("播放表发送失败");
			}
			Protocolbodytemp.setReturnState(returnState);
			JSONObject object = JSONObject.fromObject(Protocolbodytemp);
			String Sendjsonstr = object.toString();

			SendRabbitmqQueue(portInfo.getExchangeName(),portInfo.getSendQueueroutingkey(), Sendjsonstr);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("下发数据异常发生异常" + ex.toString());
		}

	}


	/**
	 * 设备检测状态发送
	 */
	public void RabbitmqCheckback(String businessNo,byte[] Data) {
		try {
			String curTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			String Devid=this.portInfo.getSzDevId();
			String DevTypeid=this.portInfo.getSzDevId().substring(0,4);
			Protocolbody Protocolbodytemp = new Protocolbody();
			Protocolbodytemp.setBusinessNo(businessNo);
			Identity Identitytemp = new Identity();
			Identitytemp.setSourceId("collctrsvr_sjzyc_cms");
			Identitytemp.setTargetId("jkcommctrsvr");
			Identitytemp.setCreateTime(curTime);
			Protocolbodytemp.setIdentity(Identitytemp);
			Protocolbodytemp.setInfoType(InfoType.MSG_DATA_CMS);

			SubPackage subPackage = new SubPackage();
			subPackage.setOrgId("20300");
			subPackage.setDevId(Devid);
			subPackage.setCollCtrTime(curTime);

			if(this.devDriverVarInfo==null) return;
			List<DevVarInfo> DevVarInfolist = new ArrayList<>();
			for (TDriverVarInfo driverVarInfo : this.devDriverVarInfo) {
				if(DevTypeid.equals(driverVarInfo.getDwVariantId().toString().substring(0,4)))
				if (driverVarInfo.getSzFuncAbbr().indexOf("ER") >= 0){
					try{
						String str = new String(Data);
						int parseInt = Integer.parseInt(str, 16);
						int bit = Integer.parseInt(driverVarInfo.getSzFuncAbbr().substring(2, 3));
						int ERValue = (parseInt >> bit) & 1; //获取设备状态值

						DevVarInfo devVarInfo = new DevVarInfo();
						devVarInfo.setDevVarTypeId(driverVarInfo.getDwVariantId().toString());
						devVarInfo.setDevVarTypeDesc(driverVarInfo.getSzVariantDesc());
						devVarInfo.setDevVarValue(String.valueOf(ERValue));
						devVarInfo.setDevVarGroupId("1");
						DevVarInfolist.add(devVarInfo);
					} catch(Exception ex){
						ex.printStackTrace();
					}
				}
			}

			subPackage.setDevVarInfoList(DevVarInfolist);
			Protocolbodytemp.setSubPackage(subPackage);

			JSONObject objecttemp = JSONObject.fromObject(Protocolbodytemp);
			String Sendjsonstr = objecttemp.toString();

			SendRabbitmqQueue(portInfo.getExchangeName(),portInfo.getSendQueueroutingkey(), Sendjsonstr);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("下发数据异常发生异常" + ex.toString());
		}

	}

	/**
	 * 超时检查任务
	 */
	private Runnable waitTaskRunnable = new Runnable() {		
		@Override
		public void run() {
			CheckReSendTime();
		}
	};
	
	/**
	 * 
	 * @param Continue
	 */
	private void startWaitTimer(boolean Continue)
    {
        try
        {
        	if (future == null){
        		future = scheduledService.scheduleWithFixedDelay(waitTaskRunnable, DriverConst.Const_FrameSendTimeOut, DriverConst.Const_FrameSendTimeOut, TimeUnit.SECONDS);
        	}
            if (Continue)
                fwaitCount += 1;
            else
                fwaitCount = 0;
        }
        catch (Exception ex)
        {
            LOGGER.error("StartWaitTimer出错，错误信息：" + ex.getMessage());
        }
    }

  
    /**
     * 停止发送超时检查事件
     */
    private void stopWaitTimer()
    {
        try
        {
        	if (future != null){
				//LOGGER.info("future超时检测停止");
        		future.cancel(true);
        		future = null;
        	}
        }
        catch (Exception ex)
        {
            LOGGER.error("StopWaitTimer出错，错误信息：" + ex.getMessage());
        }
    }

	//获得设备地址信息
    private boolean getAddressInfo(String addrInfo, AddrInfoBean addrBean)
    {
		boolean result = false;

		if (addrBean == null) {
			return result;
		}

		String[] split = addrInfo.trim().split(
				DriverPubConst.Const_ItemSeperator);

		// 判断Src中的设备变量属性是否有效
		if (split.length != 1)
			return result;

		// 进行赋值
		try {
			addrBean.setAddr(Byte.parseByte(split[0]));
			result = true;
		} catch (Exception ex) {
			LOGGER.error("TDriver.GetAddressInfo-->"
					+ ex.getMessage());
		}

		return result;
    }
    
    /**
     * 向串口发送数据
     * @param Continue
     * @return
     */
    private boolean SendPktToComm(boolean Continue)
    {
        boolean result = false;
        if (FPktToSend == null)
        {
            this.SetStatus(ConstVarient.COMM_STATUS_DisConnect);
            return result;
        }

        try
        {
            fudpComm.sendMsg(FPktToSend);;
            startWaitTimer(Continue);
            result = true;
        }
        catch (Exception ex)
        {
            result = false;
            LOGGER.error("SendPktToComm-->" + ex.getMessage());
        }

        return result;
    }
    
    /**
     * 从发送队列中取出一个数据，发送
     * @return
     */
    private boolean SendNextDataPacket()
    {
        boolean result = false;
        Request Pkt;
        Pkt = fwindow.getNextPacket();
        if (Pkt != null)
        {
            FPktToSend = Pkt;
            result = SendPktToComm(false);
        }
        return result;
    }
    
    /**
     * 通知调度对象发送数据
     */
    private void NotifyToSendPkt()
    {
		if (fwindow.getFrameCount() > 0) {
			SendNextDataPacket();
		}
    }
    
    /**
     * 检查发送数据超时
     */
    private void CheckReSendTime()
    {
//        if (FPktToSend != null)
//        	LOGGER.error(this.portInfo.getSzDevIp() + ":" + this.portInfo.getDwDevPort() + "--发送报文超时: " + FPktToSend.toString());
//        else
//        	LOGGER.error(this.portInfo.getSzDevIp() + ":" + this.portInfo.getDwDevPort() +  "--发送报文超时: ");

        int Idx;
        Request Frm;

        //对发送应答超时进行分析，只有轮循检测的超时才认为通讯中断
        if (fwaitCount >= DriverConst.Const_Timer_WaitAnswer) //发送超时
        {
			//LOGGER.error(this.portInfo.getSzDevIp() + ":" + this.portInfo.getDwDevPort() + "--发送报文超时: " + FPktToSend.toString());
        	//设置超时
        	this.SetStatus(ConstVarient.COMM_STATUS_DisConnect);

            stopWaitTimer();

            Frm = fwindow.getCurrentFrame(); //获得当前操作的数据帧
            if (Frm != null)
            {
                //SetStatus(ConstVarient.COMM_STATUS_DisConnect);
            	int frameType = Frm.getFrameType();
                if (Frm.getFrameType() == DriverConst.Const_Cmd_Check) //如果是轮循检测数据帧
                {                        
                    fwindow.processSendTimeOut();
                    NotifyToSendPkt();
                }
                else
                {
					String businessNo=fwindow.processSendTimeOut();
					RabbitmqCtrlCmdback(businessNo,false);//数据发送失败

                    //任务失败 数据发送失败
                    NotifyToSendPkt();
                }
            }
        }
        else
        {
//			LOGGER.info("不超时继续发送-->");
           SendPktToComm(true);
        }
    }
    
    /**
     * 设置通信端口状态
     * 通知与该通信端口关联的所有通信状态类型的设备变量
     * @param Value
     */    
    private void SetStatus(int Value)
    {
        List<Integer> Infos;
        if (Value == ConstVarient.COMM_STATUS_DisConnect)
        {  
        	//stopWaitTimer();
        }

        if (FStatus != Value)
        {
            FStatus = Value;
            //设置通信状态类型的设备变量
            if (FStatus == ConstVarient.COMM_STATUS_Connected)
            {
            	//设备通讯正常 faddressParse.setCommState(true);
                RabbitmqDevStatusSend("-11","0");
            }
            else
            {
            	//设备通讯中断 faddressParse.setCommState(false);
                RabbitmqDevStatusSend("-11","1");
            }
        }
    }

    /**
     * 设备检测状态发送
     * @param businessNo 数据传送标识码
     * @param DevStatusValue 状态值 0正常 1中断
     */
    public void RabbitmqDevStatusSend(String businessNo,String DevStatusValue) {
        try {
            System.out.println("上传设备通讯状态");
            String curTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String Devid=this.portInfo.getSzDevId();
            String DevTypeid=this.portInfo.getSzDevId().substring(0,4);
            Protocolbody Protocolbodytemp = new Protocolbody();
            Protocolbodytemp.setBusinessNo(businessNo);
            Identity Identitytemp = new Identity();
            Identitytemp.setSourceId("collctrsvr_sjzyc_cms");
            Identitytemp.setTargetId("jkcommctrsvr");
            Identitytemp.setCreateTime(curTime);
            Protocolbodytemp.setIdentity(Identitytemp);
            Protocolbodytemp.setInfoType(InfoType.MSG_DATA_CMS);

            SubPackage subPackage = new SubPackage();
            subPackage.setOrgId("20300");
            subPackage.setDevId(Devid);
            subPackage.setCollCtrTime(curTime);

            if(this.devDriverVarInfo==null) return;
            List<DevVarInfo> DevVarInfolist = new ArrayList<>();
            for (TDriverVarInfo driverVarInfo : this.devDriverVarInfo) {
                if(DevTypeid.equals(driverVarInfo.getDwVariantId().toString().substring(0,4)))
                    if (driverVarInfo.getSzFuncAbbr().indexOf("COM") >= 0){
                        try{
                            DevVarInfo devVarInfo = new DevVarInfo();
                            devVarInfo.setDevVarTypeId(driverVarInfo.getDwVariantId().toString());
                            devVarInfo.setDevVarTypeDesc(driverVarInfo.getSzVariantDesc());
                            devVarInfo.setDevVarValue(DevStatusValue);
                            devVarInfo.setDevVarGroupId("1");
                            DevVarInfolist.add(devVarInfo);
                        } catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
            }

            subPackage.setDevVarInfoList(DevVarInfolist);
            Protocolbodytemp.setSubPackage(subPackage);

            JSONObject objecttemp = JSONObject.fromObject(Protocolbodytemp);
            String Sendjsonstr = objecttemp.toString();

            SendRabbitmqQueue(portInfo.getExchangeName(),portInfo.getSendQueueroutingkey(), Sendjsonstr);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("下发数据异常发生异常" + ex.toString());
        }

    }


	/**
	 * rabbitmq通讯发送接口
	 */
	public void  SendRabbitmqQueue(String Exchange,String RoutingKey,String jsonstr)
	{
		try {
			if (jsonstr != null || jsonstr != "") {
				rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
				rabbitTemplate.setExchange(Exchange);
				rabbitTemplate.setRoutingKey(RoutingKey);
				rabbitTemplate.convertAndSend(jsonstr);
//				System.out.println("SendRabbitmq: " + "\n" + "Exchange-->" + Exchange + "   " +
//						"RoutingKey-->" + RoutingKey + "\n" + jsonstr);
			} else {
//				System.out.println("SendRabbitmq数据发送不能为空， " + "\n" + "Exchange-->" + Exchange +
//						"RoutingKey-->" + RoutingKey + "\n" + jsonstr);
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

}

/**
 * 设备地址信息实体
 *
 */
class AddrInfoBean{
	/**
	 * 设备地址信息
	 */
	private byte addr;

	/**
	 * @return 设备地址信息
	 */
	public byte getAddr() {
		return addr;
	}

	/**
	 * @param //设备地址信息
	 */
	public void setAddr(byte addr) {
		this.addr = addr;
	}
	
}