package com.boco.sjzyc.model;

import java.io.ByteArrayOutputStream;

import com.boco.sjzyc.constant.DriverConst;
import com.boco.sjzyc.utils.CoderUtils;

public class Response {
	/**
	 * 设备地址
	 */
	private int addr;
	/**
	 * 帧类型
	 */
	private int frameType;

	/**
	 * 数据
	 */
	private byte[] data;

	/**
	 * 检验码
	 */
	private byte[] checkCrc;

	/**
	 * 构造函数
	 * @param addr
	 * @param data
	 * @param checkCrc
	 * @throws Exception
	 * @throws NumberFormatException
	 */
	public Response(byte[] addr, byte[] frameType, byte[] data, byte[] checkCrc) throws NumberFormatException, Exception {
		super();
		//判断设备地址，并将设备地址转换为整数
		if (addr.length >= 2){
			this.addr = 0;
		} else {
			this.addr = addr[1] * 256 + addr[0];
		}

		//判断帧类型，并将帧类型转换为整数
		if (frameType.length >= 1){
			this.frameType = frameType[0];
		} else {
			this.frameType = -1;
		}
		this.data = data;
		this.checkCrc = checkCrc;
	}

	/**
	 * @return the 设备地址
	 */
	public int getAddr() {
		return addr;
	}

	/**
	 * @return the 帧类型
	 */
	public int getFrameType() {
		return frameType;
	}

	/**
	 * 将设备返回的数据转换为字符串，并返回
	 * @return
	 */
	public String getData(){
		if (this.data == null){
			return null;
		}
		String result = new String(this.data);
		return result;
	}

	/**
	 * 以字节数组形式，返回接收到的数据
	 * @return
	 */
	public byte[] getDataArray(){
		return this.data;
	}

	/**
	 * 判断是否符合校验
	 * @return
	 */
	public boolean isChecked(){
		if (this.data == null || this.checkCrc == null || this.checkCrc.length < 2){
			return false;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			//设备地址
			byte[] addr = new byte[]{(byte)this.addr,(byte)(this.addr >> 8)};
			//帧类型
			byte[] frameType = new byte[]{(byte)this.frameType};
			//包头
			byte[] head = new byte[]{DriverConst.Const_FrameHead};
			//包尾
			byte[] tail = new byte[]{DriverConst.Const_FrameTail};

			baos.write(head);
			baos.write(addr);
			baos.write(frameType);
			baos.write(this.data);
			baos.write(tail);

			byte[] buffer = baos.toByteArray();

			int newCheckValue = CoderUtils.crc16Table(buffer);
			int oldCheckValue = this.checkCrc[0]  + this.checkCrc[1] << 8;

			if (newCheckValue == oldCheckValue){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return false;
	}
}
