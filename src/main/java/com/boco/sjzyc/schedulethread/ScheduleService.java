package com.boco.sjzyc.schedulethread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduleService {

    private static final Logger logger= LoggerFactory.getLogger(ScheduleService.class);

    @Autowired
    RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法

    @Autowired
    private Environment env;

    // 每30秒调用一次
    //@Scheduled(cron = "0/20 * * * * ?")// corn语句 没20秒调用一次
    //@Scheduled(cron = "0 0/1 * * * ?") // corn语句  每1分钟调用一次
    @Async
    public void s1() {
        //System.out.println("ScheduleService定时服务运行");
//        SendRabbitmqQueue(env.getProperty("exchangeName"),
//                env.getProperty("sendQueueroutingkey"), GetSendjsonstr());
        //测试设备通讯状态
//        for (Map.Entry<Integer, TDriver> entry : TDriverList.getInstance().gDriverMap.entrySet()) {
//            TDriver driver = entry.getValue();  //获取设备驱动信息
//            Integer sdf=driver.FStatus;
//            String sdfasd=entry.getKey().toString();
//            logger.error("设备通讯编码："+sdfasd+"---状态："+sdf.toString());
//        }
    }

    /**
     * 通讯协议包父类
     */
    public void  SendRabbitmqQueue(String Exchange,String RoutingKey,String jsonstr)
    {
        try {
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(Exchange);
            rabbitTemplate.setRoutingKey(RoutingKey);
            rabbitTemplate.convertAndSend(jsonstr);
            System.out.println("SendRabbitmq: "+"\n"+"Exchange-->" +Exchange+"   "+
                    "RoutingKey-->" +RoutingKey+ "\n"+jsonstr);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
