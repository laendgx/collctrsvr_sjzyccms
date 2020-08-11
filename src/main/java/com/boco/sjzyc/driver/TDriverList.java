package com.boco.sjzyc.driver;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 南京金晓CMS驱动TCP
 * 功能：维护该驱动下所有的通讯设备对象;
 * 在使用静态类TDriverList之前，必须先调用Init方法
 * 这是一个单例类，使用getInstance获取实例化的对象
 *
 */
public class TDriverList {
	private static final Logger LOGGER= LoggerFactory.getLogger(TDriverList.class.getName());

    /**
     * 定时器
     */
    private ScheduledExecutorService service;
    
    /**
     * 构造方法
     */
    private TDriverList(){
    	gDriverMap = new HashMap<String,TDriver>();
    	//创建线程定时器
    	service = Executors.newSingleThreadScheduledExecutor();
    }
    
    private static TDriverList singleton = null;
    /**
     * 静态工厂方法
     */
    public static TDriverList getInstance(){
		if (singleton == null){
			singleton = new TDriverList();
		}
		return singleton;
	}
    
    //保存所有通讯设备对象的List
    //private Map<Integer,TDriver> gDriverMap;
	public Map<String,TDriver> gDriverMap;

  	//根据通讯端口号，获得通讯设备对象实例
    public TDriver getDriver(Integer portId){
    	if (gDriverMap != null){
    		if (gDriverMap.containsKey(portId)){
    			return gDriverMap.get(portId);
    		}
    	}
    	return null;
    }
    
    //往gDriverList中增加通讯设备对象
    public boolean add(TDriver driver){
    	if (!gDriverMap.containsKey(driver)){
    		gDriverMap.put(driver.getDevId(), driver);
    		return true;
    	}
    	return false;
    }
    
    //gDriverList中删除特定的通讯设备对象
    public boolean delete(TDriver driver){
		if (gDriverMap != null) {
			if (gDriverMap.containsKey(driver.getDevId())) {
				try {
					gDriverMap.remove(driver.getDevId());
					driver.close();
					return true;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return false;
    }
    
    private Runnable runnable = new Runnable() {		
		@Override
		public void run() {
			try {
				//每个设备的驱动对象，定时下发
				for (Entry<String, TDriver> entry : gDriverMap.entrySet()) {
					TDriver driver = entry.getValue();
					driver.AddLoopCheckPkt();
				}
			} catch (Exception ex) {
				LOGGER.error("定时下发情报板故障检测错误！错误信息：" + ex.getMessage());
			}
		}
	};
    
    //所有的驱动启动运行
	public void startDrivers() {
		if (gDriverMap != null) {
			try {
				for (Entry<String, TDriver> entry : gDriverMap.entrySet()) {
					TDriver driver = entry.getValue();
					driver.startDriver();
				}
			} catch (Exception ex) {
				LOGGER.error("启动三思情报板驱动错误.错误信息：" + ex.getMessage());
			}
			//5000为设备状态采集周期
			service.scheduleWithFixedDelay(runnable, 1000, 5000, TimeUnit.MILLISECONDS);
		}
	}
	
	//所有的驱动停止运行
    public void stopDrivers(){
    	if (gDriverMap != null) {
    		try{
    			for(Entry<String, TDriver> entry : gDriverMap.entrySet()){
    				TDriver driver = entry.getValue();
    				driver.stopDriver();
    			}
    		}catch(Exception ex){
    			LOGGER.error("停止三思情报板驱动错误，stopDrivers。错误信息：" + ex.getMessage());
    		}
    		
    		try{
    			service.shutdownNow();
    		}catch(Exception ex){
    			LOGGER.error("停止三思驱动，故障检测定时器错误。错误信息：" + ex.getMessage());
    		}
    	}
    }
    
}
