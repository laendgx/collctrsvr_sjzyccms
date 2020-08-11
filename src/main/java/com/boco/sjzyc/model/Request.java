package com.boco.sjzyc.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.boco.sjzyc.constant.DriverConst;
import com.boco.sjzyc.utils.CoderUtils;

public class Request {
	/**
	 * 设备变量的索引
	 */
	private String index;
	/**
	 * 目的地址
	 */
	private int addr;

	/**
	 * 帧类型
	 */
	private int frameType;

	/**
	 * 数据内容
	 */
	private byte[] data;

	/**
	 * @return the 设备变量的索引
	 */
	public String getIndex() {
		return index;
	}

	/**
	 * @param //设备变量的索引
	 */
	public void setIndex(String index) {
		this.index = index;
	}

	/**
	 * @return the 目的地址
	 */
	public int getAddr() {
		return addr;
	}

	/**
	 * @param //目的地址 the addr to set
	 */
	public void setAddr(int addr) {
		this.addr = addr;
	}

	/**
	 * @return the 帧类型
	 */
	public int getFrameType() {
		return frameType;
	}

	/**
	 * @param //帧类型 the frameType to set
	 */
	public void setFrameType(int frameType) {
		this.frameType = frameType;
	}

	/**
	 * @return the 数据内容
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @param //数据内容 the data to set
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

	//构造方法
	public Request(int addr, int frameType, byte[] data) {
		super();
		this.addr = addr;
		this.frameType = frameType;
		this.data = data;
	}

	//无参构造方法
	public Request() {
		super();
	}

	/**
	 * 获取目的地址、源地址、帧类型、发送数据三方编码后的数据
	 * @return
	 */
	public byte[] getEncoderData(){
		byte hi = (byte)(addr >> 8);
		byte lo = (byte)addr;

		byte[] commands = new byte[]{lo, hi, (byte)this.frameType};

		byte[] heads = new byte[]{DriverConst.Const_FrameHead};
		byte[] tails = new byte[]{DriverConst.Const_FrameTail};

		ByteArrayOutputStream baosTemp = new ByteArrayOutputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baosTemp.write(commands);
			baosTemp.write(data);

			byte[] transfer = CoderUtils.ucTransfer(baosTemp.toByteArray(), baosTemp.toByteArray().length, true);
			baosTemp.close();


			baos.write(heads);
			baos.write(transfer);
			baos.write(tails);

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return baos.toByteArray();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		try {
			return "Request [addr=" + addr + ", frameType=" + frameType + ", data="
					+ new String(data, "GBK") + "]";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

}
