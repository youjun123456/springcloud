package com.yeejoin.demo.cardemo.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpUtil {

	public static String getIp() {
		String hostAddress = "";
		try {
			InetAddress address = InetAddress.getLocalHost();// 获取的是本地的IP地址 //
																// //PC-20140317PXKX/192.168.0.121
			hostAddress = address.getHostAddress();// 192.168.0.121
			System.out.println(hostAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return hostAddress;
	}
	
}
