package com.boco.sjzyc.tcpcomm;

import java.io.ByteArrayOutputStream;

import com.boco.sjzyc.model.Request;
import com.boco.sjzyc.utils.CoderUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * <pre>
 * 数据包格式
 * +——----——+——-----——+——-----——+——----——+——----——+——----——+
 * |  包头	|  地址          |  帧类型      |  帧数据    |  校验码    |  包尾       |
 * +——----——+——-----——+——-----——+——----——+——----——+——----——+
 * </pre>
 * @author -dgx-
 *
 */
public class RequestEncoder {

	public static ByteBuf encode(Request msg)
			throws Exception {
		byte[] transfer = msg.getEncoderData();

		//计算校验
		int crc = CoderUtils.crc16Table(transfer);
		byte[] crcArr = new byte[2];
		crcArr[0] = (byte)crc;
		crcArr[1] = (byte)(crc >> 8);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(transfer);
		baos.write(crcArr);
		baos.flush();
		ByteBuf out = Unpooled.copiedBuffer(baos.toByteArray());
		baos.close();
		return out;
	}

}
