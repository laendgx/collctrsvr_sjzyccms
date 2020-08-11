package com.boco.sjzyc.constant;

/**
 * 常量类
 * @author dgx
 *
 */
public class DriverConst {
    /**
     * 数据帧的报文头
     */
    public static final byte Const_FrameHead = (byte)0xAA;
    /**
     * 数据帧的报文尾
     */
    public static final byte Const_FrameTail = (byte)0xCC;
    /**
     * 数据帧中的转义
     */
    public static final byte Const_FrameTrans = (byte)0xEE;
    /**
     * 发送超时时间
     */
    public static final int Const_FrameSendTimeOut = 7;
    /**
     * 等待应答次数
     */
    public static final int Const_Timer_WaitAnswer = 3;
    /**
     * 定期检查物理通道是否连接的命令
     */
    public static final int Const_Cmd_Check = 1;
    /**
     * 播放列表
     */
    public static final int Const_Cmd_PlayLst = 0x11;
    /**
     * 下发播放列表名字
     */
    public static final int Const_Frm_SendFileName = 0x11;
    /**
     * 下发播放列表名字的回复
     */
    public static final int Const_Frm_SendFileName_Recv = 0x12;
    /**
     * 下发播放列表内容
     */
    public static final int Const_Frm_SendFileContent = 0x13;
    /**
     * 下发播放列表内容回复
     */
    public static final int Const_Frm_SendFileContent_Recv = 0x14;
    /**
     * 显示指定播放表
     */
    public static final int Const_Cmd_DisPlayLst = 0x1B;
    /**
     * 显示指定播放表回复
     */
    public static final int Const_Cmd_DisPlayLst_Recv = 0x1C;
    /**
     * 控制开关
     */
    public static final int Const_Cmd_ScrOnOff = 0x05;
    /**
     * 控制开关回复
     */
    public static final int Const_Cmd_ScrOnOff_Recv = 0x06;
    /**
     * 读取CMS当前播放表信息
     */
    public static final int Const_Cmd_ReadCms = 5;
    /**
     * 控制屏体亮度
     */
    public static final int Const_Cmd_Bright = 0x07;
    /**
     * 控制屏体亮度返回
     */
    public static final int Const_Cmd_Bright_Recv = 0x08;
    /**
     * 正应答标志1
     */
    public static final int Const_Answer_Success = 0x01;
}
