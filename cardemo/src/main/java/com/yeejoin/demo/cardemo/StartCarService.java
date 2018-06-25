package com.yeejoin.demo.cardemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 启动类
 * 
 * @author as-youjun
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
public class StartCarService {

	public static void main(String[] args) {
		SpringApplication.run(StartCarService.class, args);
	}

}