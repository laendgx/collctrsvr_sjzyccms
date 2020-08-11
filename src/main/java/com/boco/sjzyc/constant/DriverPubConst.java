package com.boco.sjzyc.constant;

/**
 * 驱动内部用到的常量
 * @author dgx
 *
 */
public class DriverPubConst {
	/**
	 * 通信参数分割符
	 */
    public static final String Const_CommSeperator = ";";
    /**
     * 设备变量中属性元素间的分隔符,用于分割
     */
    public static final String Const_ItemSeperator = "\\+";
    /**
     * 设备变量中属性元素间的分隔符,用于连接
     */
    public static final String Const_ItemJoin = "+";
    /**
     * 命令成功
     */
    public static final int Const_CodeSuccessful = 0; 
    /**
     * 一般错误
     */
    public static final int Const_CodeGeneralFailure = 1; 
    /**
     * 没有实现代码
     */
    public static final int Const_CodeNotImplemented = 2; 
    /**
     * 通信故障的数据
     */
    public static final int Const_Value_CommError = 1;
    /**
     * 通信正常的数据
     */
    public static final int Const_Value_CommNormal = 0; 
    /**
     * OMRON通信类型
     */
    public static final int Const_CommType_OMRON = 1; 
    /**
     * TCP通信类型
     */
    public static final int Const_CommType_TCP = 2; 
    /**
     * UDP通信类型
     */
    public static final int Const_CommType_UDP = 3; 
    /**
     * 数据库类型
     */
    public static final int Const_CommType_DataBase = 4; 
    /**
     * 串口通信类型
     */
    public static final int Const_CommType_Serial = 5; 
}
