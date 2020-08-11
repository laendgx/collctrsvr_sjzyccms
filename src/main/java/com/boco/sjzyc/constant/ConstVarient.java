package com.boco.sjzyc.constant;

/**
 * 采集驱动对外接口使用到的常量
 * @author dgx
 *
 */
public interface ConstVarient {
    /**
     * 通信端口状态
     */
    public final static int COMM_STATUS_BASE = 0x0000FF00;
    /**
     * 通信端口状态－－未知
     */
    public final static int COMM_STATUS_UnKnown = COMM_STATUS_BASE + 1;
    /**
     * 通信端口状态－－连接正常
     */
    public final static int COMM_STATUS_Connected = COMM_STATUS_BASE + 2;
    /**
     * 通信端口状态－－连接中断
     */
    public final static int COMM_STATUS_DisConnect = COMM_STATUS_BASE + 3;
}
