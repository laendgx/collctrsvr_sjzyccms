package com.boco.sjzyc.utils;

import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 读取配置文件
 *
 */
public class ConfigReaderUtils {
	/**
	 * rabbitmq主机
	 */
	private static String  host;
	/**
	 * 用户名
	 */
	private static String  username;
	/**
	 * 密码
	 */
	private static String  password;
	/**
	 * 端口号
	 */
	private static String  port;
	/**
	 * 发送队列名称
	 */
	private static String  sendQueueName;
	/**
	 * 接收队列名称
	 */
	private static String  ReceiveQueueName;
	/**
	 * rabbitmq交换器名称
	 */
	private static String  exchangeName;

	static {
		try {
			Properties prop = new Properties();
			prop.load(new InputStreamReader(ConfigReaderUtils.class
					.getClassLoader().getResourceAsStream(
							"rabbitmqconfig.properties"), "UTF-8"));

			host = prop.getProperty("host");
			username = prop.getProperty("username");
			password = prop.getProperty("password");
			port = prop.getProperty("port");
			sendQueueName = prop.getProperty("sendQueueName");
			ReceiveQueueName = prop.getProperty("ReceiveQueueName");
			exchangeName = prop.getProperty("exchangeName");

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}


	public static String getHost() {
		return host;
	}

	public static void setHost(String host) {
		ConfigReaderUtils.host = host;
	}

	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		ConfigReaderUtils.username = username;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		ConfigReaderUtils.password = password;
	}

	public static String getPort() {
		return port;
	}

	public static void setPort(String port) {
		ConfigReaderUtils.port = port;
	}

	public static String getSendQueueName() {
		return sendQueueName;
	}

	public static void setSendQueueName(String sendQueueName) {
		ConfigReaderUtils.sendQueueName = sendQueueName;
	}

	public static String getReceiveQueueName() {
		return ReceiveQueueName;
	}

	public static void setReceiveQueueName(String receiveQueueName) {
		ReceiveQueueName = receiveQueueName;
	}

	public static String getExchangeName() {
		return exchangeName;
	}

	public static void setExchangeName(String exchangeName) {
		ConfigReaderUtils.exchangeName = exchangeName;
	}
}
