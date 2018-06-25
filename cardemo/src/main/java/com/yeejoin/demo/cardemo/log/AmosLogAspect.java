package com.yeejoin.demo.cardemo.log;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AmosLogAspect {

    private static final Logger logger = LoggerFactory
            .getLogger(AmosLogAspect.class);

    private long startTimeMillis = 0; // 开始时间
    private long endTimeMillis = 0; // 结束时间
    private HttpServletRequest request = null;

    // Controller层切点
    @Pointcut("execution (* com.yeejoin.demo.cardemo.controller..*.*(..))")
    public void controllerAspect() {
        logger.debug("constructor...");
    }

    /**
     * 方法调用前触发 记录开始时间
     * @param joinPoint
     *
     */
    @Before("controllerAspect()")
    public void before(JoinPoint joinPoint) {
        request = getHttpServletRequest();
        // 打印请求url、输入参数
        logger.info("request uri: " + request.getRequestURI());
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (MapUtils.isNotEmpty(parameterMap)) {
            parameterMap.forEach((key, value) -> {
                logger.info("param key: " + key);
                if (value != null) {
                    StringBuilder sb = new StringBuilder();
                    for (String s : value) {
                        sb.append(s).append("   ");
                    }
                    logger.info("param value: " + sb.toString());
                }
            });
        }
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                logger.info("headerName={},headerValue={}", headerName, headerValue);
            }
        }
        startTimeMillis = System.currentTimeMillis(); // 记录方法开始执行的时间
    }

    /**
     * 方法调用后触发 记录结束时间
     *
     * @param joinPoint
     */
    @SuppressWarnings("all")
    @After("controllerAspect()")
    public void after(JoinPoint joinPoint) {
        request = getHttpServletRequest();
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = null;
        try {
            targetClass = Class.forName(targetName);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        }
        Method[] methods = targetClass.getMethods();
        String operationType = "";
        String operationName = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs != null && clazzs.length == arguments.length
                        && method.getAnnotation(AmosLog.class) != null) {
                    operationName = method.getAnnotation(AmosLog.class)
                            .operationName();
                    operationType = method.getAnnotation(AmosLog.class)
                            .operationType();
                    break;
                }
            }
        }
        logger.info("operationName:{},operationType:{}", operationName,
                operationType);
        endTimeMillis = System.currentTimeMillis();
        // 格式化开始时间
        String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(startTimeMillis);
        // 格式化结束时间
        String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(endTimeMillis);

        long timeSpan = endTimeMillis - startTimeMillis;
        logger.info("操作类型:" + operationType + "，操作名称: " + operationName
                + "，操作开始时间: " + startTime + " ，操作结束时间: " + endTime + "，操作时长："
                + timeSpan + "毫秒");
    }

    /**
     * 获取request
     *
     * @return
     */
    public HttpServletRequest getHttpServletRequest() {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        return request;
    }

    /**
     * 环绕触发
     *
     * @param joinPoint
     * @throws Throwable
     */
    @Around("controllerAspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object retVal = null;
        logger.info("==========开始执行controller环绕通知===============");
        long start = System.currentTimeMillis();
        try {
            retVal = joinPoint.proceed();
        } catch (Throwable e) {
            if (logger.isInfoEnabled()) {
                logger.error(e.getMessage());
            }
        }
        long end = System.currentTimeMillis();
        if (logger.isInfoEnabled()) {
            logger.info("around " + joinPoint + "\tUse time : " + (end - start)
                    + " ms!");
        }
        logger.info("==========结束执行controller环绕通知===============");
        return retVal;
    }

    @AfterThrowing(pointcut = "controllerAspect()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        logger.error("aop执行出错! " + e.getMessage());
    }

}