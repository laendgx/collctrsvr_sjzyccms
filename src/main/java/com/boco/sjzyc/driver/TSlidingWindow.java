package com.boco.sjzyc.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.boco.sjzyc.model.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 滑动窗口
 * 负责管理发送数据
 *
 */
public class TSlidingWindow {
    private static final Logger LOGGER= LoggerFactory.getLogger(TSlidingWindow.class.getName());
	/**
	 * 发送数据缓存队列
	 */
	private BlockingQueue<Request> frameQueue;
	
	/**
	 * 同步锁
	 */
    private Object _syncHelper;
    
    /**
     * 是否正在发送中
     */
    private boolean fackPending;
    
    /**
     * 当前队列中帧的数量
     */
    private int frameCount;
    
    /**
     * 构造方法
     */
    public TSlidingWindow(){
    	this.frameQueue = new LinkedBlockingQueue<Request>();
    	this._syncHelper = new Object();
    	this.fackPending = false;
    }
    
    /**
     * 释放方法
     */
    public void destory()
    {
        synchronized (_syncHelper) {
        	this.frameQueue.clear();
		}
    }

	/**
	 * @return the 是否正在发送中
	 */
	public boolean isFackPending() {
		synchronized (_syncHelper) {
			return fackPending;
		}		
	}

	/**
	 * @param //是否正在发送中
	 */
	public void setFackPending(boolean fackPending) {
		synchronized (_syncHelper) {
			this.fackPending = fackPending;
		}
	}

	/**
	 * @return the 当前队列中帧的数量
	 */
	public int getFrameCount() {
		synchronized (_syncHelper) {
			return this.frameQueue.size();
		}
	}

	/**
	 * 添加一个数据帧
	 * 数据帧务必要添加 index
	 */
    public void addFrame(List<Request> frmLst)
    {
        synchronized(_syncHelper)
        {
            //LOGGER.info("addFrame:添加一个数据帧");
        	try {
        		for(Request req : frmLst){
        			frameQueue.put(req);
        		}
			} catch (InterruptedException e) {				
				LOGGER.error("TSlidingWindow.addFrame添加数据帧出错！");
			}
        }
    }

    /**
     * 取出当前窗口要发送的数据帧
     * @return
     */
    public Request getCurrentFrame()
    {
        synchronized(_syncHelper)
        {
            Request result = null;
            if (!frameQueue.isEmpty())
            {
                result = frameQueue.peek();
            }
            return result;
        }
    }
    
    /**
     * 从窗口中取出下一个发送数据, 没有数据时返回为空
     * @return
     */
    public Request getNextPacket()
    {
        synchronized(this._syncHelper)
        {
            Request result = null;

            if ((fackPending) || (frameQueue.isEmpty()))
                return result;

            result = frameQueue.peek();
            
            fackPending = true;

            return result;
        } 
    }
    
    /**
     * 处理发送成功
     * @return
     */
    public Request processRecvAnswer()
    {
        synchronized(this._syncHelper)
        {
        	Request result = null;            

            //为了防止异常情况下，无法中断FAckPending状态
            if ((fackPending) && (frameQueue.isEmpty()))
            {
                fackPending = false;
                return result;
            }

            if ((fackPending) && (!frameQueue.isEmpty()))
            {                
                fackPending = false;
                try {
					result = frameQueue.poll();
				} catch (Exception e) {
					LOGGER.error("TSlidingWindow.ProcessRecvAnswer取队列数据出错");
				}
            }
            return result;
        }
    }
    
    /**
     * 处理发送失败
     * @return
     */
    public String processSendTimeOut()
    {
        synchronized(this._syncHelper)
        {
            String result = "-1";

            if (fackPending)
            {
                fackPending = false;

                if (!frameQueue.isEmpty())
                {
                    Request req;
					try {
						req = frameQueue.poll();
						result = req.getIndex();
					} catch (Exception e) {
						LOGGER.error("TSlidingWindow.processSendTimeOut取队列数据失败");
					}
                    
                } 
            }
            return result;
        }
    }

    /**
     * 复位所有未发送数据帧，并通知设备变量发送失败
     * @return
     */
    public List<String> reset()
    {
        synchronized(this._syncHelper)
        {        	
        	List<String> result = new ArrayList<String>();
            
            while(!frameQueue.isEmpty()){
            	try {
					Request request = frameQueue.poll();
					if (request != null){
						result.add(request.getIndex());
					}
				} catch (Exception e) {
					break;
				}
            }

            return result;
        }
    }

}
