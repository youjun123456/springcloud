package com.yeejoin.demo.cardemo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yeejoin.demo.cardemo.log.AmosLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * 
 * @author youjun
 *
 */
@RestController
// @RequestMapping(value = "/cardemo")
public class CarDemoController {

	@Value("${server.port}")
	String port;

	@RequestMapping(value = "/findCars", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	public List<Car> findCars(@RequestBody Car inCar) {
		String ipAndPort = IpUtil.getIp() + ":" + port;
		List<Car> cars = new ArrayList<>();
		inCar.setIpAndPort(ipAndPort);
		cars.add(inCar);
		Car car = new Car();
		car.setName("保时捷");
		car.setModel("陕A0001");
		car.setIpAndPort(ipAndPort);
		cars.add(car);
		car = new Car();
		car.setName("标致");
		car.setModel("陕B1090");
		car.setIpAndPort(ipAndPort);
		cars.add(car);
		return cars;
	}

	@RequestMapping(value = "/testEnable/{content}", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
	@AmosLog(operationType = "测试操作:", operationName = "检测服务是否可用")
	public Map<String, String> testEnable(@PathVariable String content) {
		Map<String, String> resultMap = new HashMap<>();
		resultMap.put("flag", "SUCCESS");
		resultMap.put("content", content);
		return resultMap;
	}

}