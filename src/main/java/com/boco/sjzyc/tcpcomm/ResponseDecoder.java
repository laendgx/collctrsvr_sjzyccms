package com.boco.sjzyc.tcpcomm;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.boco.sjzyc.constant.DriverConst;
import com.boco.sjzyc.model.Response;
import com.boco.sjzyc.utils.CoderUtils;

import io.netty.buffer.ByteBuf;

/**
 * <pre>
 * 数据包格式
 * +——----——+——-------——+——-------——+——----——+——----——+——----——+
 * |  包头	|  设备地址       |    指令码      |  帧数据    |   包尾      | 校验码      |
 * +——----——+——-------——+——-------——+——----——+——----——+——----——+
 * </pre>
 * @author -孙冠义-
 *
 */
public class ResponseDecoder{
	/**
	 * 数据包基本长度
	 */
	public static int BASE_LENTH = 1 + 2 + 1 + 1 + 2;

	public static void decode(ByteBuf buffer,	List<Object> out) throws Exception {
		int currCnt = buffer.readableBytes();
		while(true){
			if(buffer.readableBytes() >= BASE_LENTH){
				//第一个可读数据包的起始位置
				int beginIndex;
				while(true) {
					//包头开始游标点
					beginIndex = buffer.readerIndex();
					//标记初始读游标位置
					buffer.markReaderIndex();
					if (buffer.readByte() == DriverConst.Const_FrameHead) {
						break;
					}

					//未读到包头标识略过一个字节
					buffer.resetReaderIndex();
					buffer.readByte();

					//不满足
					if(buffer.readableBytes() < BASE_LENTH){
						return;
					}
				}

				int cnt = buffer.readableBytes();
				//判断包尾
				byte frameTail = buffer.getByte(cnt - 2);
				if (frameTail != DriverConst.Const_FrameTail){
					buffer.resetReaderIndex();
					return;
				}

				byte[] dealByteArr = new byte[buffer.readableBytes() - 3];
				buffer.readBytes(dealByteArr);

				byte[] transfer = CoderUtils.ucTransfer(dealByteArr, dealByteArr.length, false);
				ByteArrayInputStream bais = new ByteArrayInputStream(transfer);

				//读取设备地址
				byte[] addr = new byte[2];
				bais.read(addr);

				//读取命令类型
				byte[] frameType = new byte[1];
				bais.read(frameType);

				//读取数据
				byte[] data = new byte[bais.available()];
				bais.read(data);

				//读取包尾
				byte[] tail = new byte[1];
				buffer.readBytes(tail);

				//读取校验
				byte[] checkCrc = new byte[2];
				buffer.readBytes(checkCrc);

				Response response = new Response(addr, frameType, data, checkCrc);
				out.add(response);

			} else {
				break;
			}
		}
		//数据不完整，等待完整的包
		return;
	}

}
