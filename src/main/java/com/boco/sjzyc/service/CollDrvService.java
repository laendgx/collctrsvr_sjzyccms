package com.boco.sjzyc.service;

import com.boco.sjzyc.commconfig.devvarinfoconfig.DevVarCollInfo;
import com.boco.sjzyc.commconfig.devvarinfoconfig.DevVarCollInfoDataServiceImpl;
import com.boco.sjzyc.commconfig.devcommconfig.DevcommInfo;
import com.boco.sjzyc.commconfig.devcommconfig.DevcommInfoDataServiceImpl;
import com.boco.sjzyc.commrabbitmq.CommonSendQueueListener;
import com.boco.sjzyc.constant.DriverConst;
import com.boco.sjzyc.driver.TCommPortInfo;
import com.boco.sjzyc.driver.TDriver;
import com.boco.sjzyc.driver.TDriverList;
import com.boco.sjzyc.driver.TDriverVarInfo;
import com.boco.sjzyc.model.Request;
import com.boco.sjzyc.tcpcomm.IMessageListener;
import com.boco.sjzyc.tcpcomm.IPortStatusListener;
import com.boco.sjzyc.tcpcomm.TUDPComm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 服务启动
 * @author dgx
 *
 */
@Service("CollDrvService")
public class CollDrvService implements InitializingBean,DisposableBean {
    private static final Logger logger= LoggerFactory.getLogger(CommonSendQueueListener.class);

    @Autowired
    private Environment env;
    @Autowired
    RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法

    //设备通讯端口devCommConfig.xml接口信息
    @Resource(name="devcommInfoDataServiceImpl")
    private DevcommInfoDataServiceImpl devcommInfoDataServiceImpl;

    //设备通讯端口devVarListConfig.xml接口信息
    @Resource(name="devVarCollInfoDataServiceImpl")
    private DevVarCollInfoDataServiceImpl devVarCollInfoDataServiceImpl;

    /**
     * 盛放通讯端口对象的线程池
     */
    private ExecutorService tcpThreadPool;
    /**
     * 通信对象
     */
    public TUDPComm fUdpComm;
    /**
     * 通讯端口状态监听器
     */
    public IPortStatusListener portStatusListener;
    /**
     * 数据上传监听器
     */
    public IMessageListener messageListener;

    /**
     * 定时器线程
     */
    private ScheduledExecutorService scheduledService;

    /**
     * 初始化线程池 测试tcp通讯
     */
    private void initExecutorService() throws Exception {
        //获取设备通讯信息
        DevcommInfo DevcommInfotemp=devcommInfoDataServiceImpl.getCurDevcommInfo("22610001");
        if(!DevcommInfotemp.getDevId().equals(null)) {
            String devIp = DevcommInfotemp.getDevIp();
            String devPort = DevcommInfotemp.getDevPort();
            String localPort = DevcommInfotemp.getLocalPort();

            fUdpComm = new TUDPComm(devIp, Integer.valueOf(devPort), Integer.valueOf(localPort), portStatusListener, messageListener);
        }

        //初始化线程池
        tcpThreadPool = Executors.newFixedThreadPool(10);
        tcpThreadPool.execute(fUdpComm);

        //初始化定时器线程
        scheduledService = Executors.newSingleThreadScheduledExecutor();
        scheduledService.scheduleWithFixedDelay(waitTaskRunnable, DriverConst.Const_FrameSendTimeOut, DriverConst.Const_FrameSendTimeOut, TimeUnit.SECONDS);
    }

    /**
     * 初始化设备通讯信息
     */
    private void initDriver(){
        try {
            //List<TDriverVarInfo> devDriverVarInfo= new ArrayList<>();//Arrays.asList(TDriverList.getDriverVariantInfo());
            List<DevcommInfo> DevcommInfotempList = devcommInfoDataServiceImpl.getDevcommInfoList();

            for (int i=0;i<DevcommInfotempList.size();i++){
                String DevTypeid=DevcommInfotempList.get(i).getDevId().substring(0,4);
                TCommPortInfo tCommPortInfo=new TCommPortInfo();
                tCommPortInfo.setSzDevId(DevcommInfotempList.get(i).getDevId());
                tCommPortInfo.setSzRemoteIP(DevcommInfotempList.get(i).getDevIp());
                tCommPortInfo.setDwRemotePort(Integer.valueOf(DevcommInfotempList.get(i).getDevPort()));
                tCommPortInfo.setDwLocalPort(Integer.valueOf(DevcommInfotempList.get(i).getLocalPort()));
                tCommPortInfo.setSzAddressParam(DevcommInfotempList.get(i).getDevAddr());
                tCommPortInfo.setExchangeName(env.getProperty("exchangeName").toString());
                tCommPortInfo.setSendQueueroutingkey(env.getProperty("sendQueueroutingkey"));

                List<TDriverVarInfo> devDriverVarInfo= new ArrayList<>();
                List<DevVarCollInfo> DevVarCollInfotempList = devVarCollInfoDataServiceImpl.getCurDevVarCollInfo(DevTypeid);
                for (int j=0;j<DevVarCollInfotempList.size();j++) {
                    TDriverVarInfo TDriverVarInfoTemp=new TDriverVarInfo();
                    TDriverVarInfoTemp.setDwdevTypeid(DevVarCollInfotempList.get(j).getDwdevTypeId());
                    TDriverVarInfoTemp.setDwVariantId(DevVarCollInfotempList.get(j).getDwVariantId());
                    TDriverVarInfoTemp.setSzVariantDesc(DevVarCollInfotempList.get(j).getSzVariantDesc());
                    TDriverVarInfoTemp.setDwSendCmd(DevVarCollInfotempList.get(j).getDwSendCmd());
                    TDriverVarInfoTemp.setDwRecvCmd(DevVarCollInfotempList.get(j).getDwRecvCmd());
                    TDriverVarInfoTemp.setSzFuncAbbr(DevVarCollInfotempList.get(j).getSzFuncAbbr());
                    devDriverVarInfo.add(TDriverVarInfoTemp);
                }
                TDriver TDriverDev=new TDriver(tCommPortInfo,rabbitTemplate,devDriverVarInfo); //设备驱动初始化及TDriverList添加（类构造函数中）
            }

        }catch(Exception ex)
        {
            logger.error("数据转发异常"+ex.toString());
        }
    }

    /**
     * 实例初始化完成后触发
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        initDriver();
        TDriverList.getInstance().startDrivers();


//        for (Map.Entry<Integer, TDriver> entry : TDriverList.getInstance().gDriverMap.entrySet()) {
//            TDriver driver = entry.getValue();  //获取设备驱动信息
//            //driver.sendData(1,"999");
//            Integer sdf=driver.FStatus;
//            String sdfasd=entry.getKey().toString();
//            logger.error(sdfasd+"--->"+sdf.toString());
//        }

       // initExecutorService();//tcp通讯测试
      }

    /**
     * 容器关闭时触发
     */
    @Override
    public void destroy() throws Exception {
        try{
            TDriverList.getInstance().stopDrivers();
        }catch(Exception ex){}

        try{
            fUdpComm.shutdown();
        }catch(Exception ex){}

        try{
            tcpThreadPool.shutdownNow();
        }catch(Exception ex){}

        try{
            scheduledService.shutdownNow();
        }catch(Exception ex){}
    }

    /**
     * 超时检查任务
     */
    private Runnable waitTaskRunnable = new Runnable() {
        @Override
        public void run() {
            String str="123";
            Request sdf=new Request(1,17,str.getBytes());
            fUdpComm.sendMsg(sdf);
        }
    };
}
