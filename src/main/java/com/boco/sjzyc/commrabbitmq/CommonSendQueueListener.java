package com.boco.sjzyc.commrabbitmq;

import com.boco.sjzyc.constant.ConstVarient;
import com.boco.sjzyc.driver.TDriver;
import com.boco.sjzyc.driver.TDriverList;
import com.boco.sjzyc.driver.TDriverVarInfo;
import com.boco.protocolBody.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RabbitListener(queues = "${revQueueName}") //@RabbitListener(queues = "${sendQueueName}")  //
public class CommonSendQueueListener {
    private static final Logger logger= LoggerFactory.getLogger(CommonSendQueueListener.class);

    @Autowired
    RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法

    @Autowired
    private Environment env;

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

    @RabbitHandler
    public void process(String revdatabody) {
        try {
            Protocolbody revprotocolbody =  (Protocolbody)JSONToObj(revdatabody,Protocolbody.class);
            String InfoTypeRev=revprotocolbody.getInfoType();
            switch (InfoTypeRev)
            {
                case InfoType.MSG_CMD_CMS:
                    SendCmsData(revprotocolbody);
                    break;
                case InfoType.MSG_GET_DEVSTATUS:
                    RabbitmqDevStatusSend(revprotocolbody.getSubPackage().getDevId());
                    break;
                default :
                    System.out.println("无效处理数据-->" + revdatabody);
            }
        }catch ( Exception e) {
            logger.error("数据包转发异常"+e.toString());
        }
    }

    public void SendCmsData(Protocolbody revprotocolbody) {
        try {
            TDriver driver = TDriverList.getInstance().gDriverMap.get(revprotocolbody.getSubPackage().getDevId());
            Integer sdf = driver.FStatus;
            if (sdf != ConstVarient.COMM_STATUS_Connected) {
                InvalidDevBack(revprotocolbody,2);//设备通讯中断反馈
            }
            if (driver == null) {
                InvalidDevBack(revprotocolbody,1);//无效设备反馈
            } else {
                DevVarInfo DevVarInfotemp = (DevVarInfo) (revprotocolbody.getSubPackage().getDevVarInfoList().get(0));

                System.out.println("发送数据" + DevVarInfotemp.getDevVarTypeId() + "-->" + DevVarInfotemp.getDevVarValue());
                driver.sendData(revprotocolbody.getBusinessNo(), DevVarInfotemp);
            }
        } catch (Exception e) {
            System.out.println("数据发送异常" + e.toString());
        }
    }

    /**
     * 无效设备id反馈
     */
    public void InvalidDevBack(Protocolbody revprotocolbody,int DevStatus) {
        try {
            //下面为发送成功反馈
            String curTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            Protocolbody Protocolbodytemp = new Protocolbody();
            Protocolbodytemp.setBusinessNo(revprotocolbody.getBusinessNo());
            Protocolbodytemp.setInfoType(revprotocolbody.getInfoType());
            Identity Identitytemp = new Identity();
            Identitytemp.setSourceId("collctrsvr_cms");
            Identitytemp.setTargetId("jkcommctrsvr");
            Identitytemp.setCreateTime(curTime);
            Protocolbodytemp.setIdentity(Identitytemp);
            Protocolbodytemp.setSubPackage(revprotocolbody.getSubPackage());
            ReturnState returnState = new ReturnState();
            returnState.setReturnCode(ReturnCode.ReturnCode_unknown);
            switch (DevStatus) {
                case 1:
                    returnState.setReturnMessage("采集服务无此设备");
                case 2:
                    returnState.setReturnMessage("设备通讯中断" );//("设备通讯中断" + revprotocolbody.getSubPackage().getDevId());
            }
            Protocolbodytemp.setReturnState(returnState);
            JSONObject object = JSONObject.fromObject(Protocolbodytemp);
            String jsonstr = object.toString();

            SendRabbitmqQueue(env.getProperty("exchangeName"), env.getProperty("sendQueueroutingkey"), jsonstr);


        } catch (Exception e) {
            System.out.println("数据发送异常" + e.toString());
        }
    }

    /**
     * 设备检测状态发送
     */
    public void RabbitmqDevStatusSend(String DevId) {
        try {
            if(DevId.equals("ALL")) {
                //每个设备驱动的端口通讯状态
                for (Map.Entry<String, TDriver> entry : TDriverList.getInstance().gDriverMap.entrySet()) {
                    TDriver driver = entry.getValue();
                    System.out.println("上传设备通讯状态");
                    String curTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    String Devid = driver.portInfo.getSzDevId();
                    String DevTypeid = driver.portInfo.getSzDevId().substring(0, 4);
                    Protocolbody Protocolbodytemp = new Protocolbody();
                    Protocolbodytemp.setBusinessNo("-11");
                    Identity Identitytemp = new Identity();
                    Identitytemp.setSourceId("collctrsvr_njjx");
                    Identitytemp.setTargetId("jkcommctrsvr");
                    Identitytemp.setCreateTime(curTime);
                    Protocolbodytemp.setIdentity(Identitytemp);
                    Protocolbodytemp.setInfoType(InfoType.MSG_DATA_CMS);

                    SubPackage subPackage = new SubPackage();
                    subPackage.setOrgId("20300");
                    subPackage.setDevId(Devid);
                    subPackage.setCollCtrTime(curTime);

                    if (driver.devDriverVarInfo == null) return;
                    List<DevVarInfo> DevVarInfolist = new ArrayList<>();
                    for (TDriverVarInfo driverVarInfo : driver.devDriverVarInfo) {
                        if (DevTypeid.equals(driverVarInfo.getDwVariantId().toString().substring(0, 4)))
                            if (driverVarInfo.getSzFuncAbbr().indexOf("COM") >= 0) {
                                try {
                                    DevVarInfo devVarInfo = new DevVarInfo();
                                    devVarInfo.setDevVarTypeId(driverVarInfo.getDwVariantId().toString());
                                    devVarInfo.setDevVarTypeDesc(driverVarInfo.getSzVariantDesc());
                                    devVarInfo.setDevVarValue(String.valueOf(driver.FStatus));
                                    devVarInfo.setDevVarGroupId("1");
                                    DevVarInfolist.add(devVarInfo);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                    }

                    subPackage.setDevVarInfoList(DevVarInfolist);
                    Protocolbodytemp.setSubPackage(subPackage);

                    JSONObject objecttemp = JSONObject.fromObject(Protocolbodytemp);
                    String Sendjsonstr = objecttemp.toString();

                    SendRabbitmqQueue(driver.portInfo.getExchangeName(), driver.portInfo.getSendQueueroutingkey(), Sendjsonstr);
                }
            }
            else{
                TDriver driver = TDriverList.getInstance().gDriverMap.get(DevId);
                System.out.println("上传设备通讯状态");
                String curTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String Devid = driver.portInfo.getSzDevId();
                String DevTypeid = driver.portInfo.getSzDevId().substring(0, 4);
                Protocolbody Protocolbodytemp = new Protocolbody();
                Protocolbodytemp.setBusinessNo("-11");
                Identity Identitytemp = new Identity();
                Identitytemp.setSourceId("collctrsvr_njjx");
                Identitytemp.setTargetId("jkcommctrsvr");
                Identitytemp.setCreateTime(curTime);
                Protocolbodytemp.setIdentity(Identitytemp);
                Protocolbodytemp.setInfoType(InfoType.MSG_DATA_CMS);

                SubPackage subPackage = new SubPackage();
                subPackage.setOrgId("20300");
                subPackage.setDevId(Devid);
                subPackage.setCollCtrTime(curTime);

                if (driver.devDriverVarInfo == null) return;
                List<DevVarInfo> DevVarInfolist = new ArrayList<>();
                for (TDriverVarInfo driverVarInfo : driver.devDriverVarInfo) {
                    if (DevTypeid.equals(driverVarInfo.getDwVariantId().toString().substring(0, 4)))
                        if (driverVarInfo.getSzFuncAbbr().indexOf("COM") >= 0) {
                            try {
                                DevVarInfo devVarInfo = new DevVarInfo();
                                devVarInfo.setDevVarTypeId(driverVarInfo.getDwVariantId().toString());
                                devVarInfo.setDevVarTypeDesc(driverVarInfo.getSzVariantDesc());
                                devVarInfo.setDevVarValue(String.valueOf(driver.FStatus));
                                devVarInfo.setDevVarGroupId("1");
                                DevVarInfolist.add(devVarInfo);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                }
                subPackage.setDevVarInfoList(DevVarInfolist);
                Protocolbodytemp.setSubPackage(subPackage);

                JSONObject objecttemp = JSONObject.fromObject(Protocolbodytemp);
                String Sendjsonstr = objecttemp.toString();

                SendRabbitmqQueue(driver.portInfo.getExchangeName(), driver.portInfo.getSendQueueroutingkey(), Sendjsonstr);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("下发数据异常发生异常" + ex.toString());
        }

    }

    /**
     * rabbitmq通讯发送接口
     */
    public void  SendRabbitmqQueue(String Exchange,String RoutingKey,String jsonstr) {
        try {
            if (jsonstr == null || jsonstr.equals("")) {
                System.out.println("SendRabbitmq数据发送不能为空， " + "\n" + "Exchange-->" + Exchange +
                        "RoutingKey-->" + RoutingKey + "\n" + jsonstr);
                return;
            }
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(Exchange);
            rabbitTemplate.setRoutingKey(RoutingKey);
            rabbitTemplate.convertAndSend(jsonstr);
            System.out.println("SendRabbitmq: " + "\n" + "Exchange-->" + Exchange + "   " +
                    "RoutingKey-->" + RoutingKey + "\n" + jsonstr);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
