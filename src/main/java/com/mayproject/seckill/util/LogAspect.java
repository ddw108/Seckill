package com.mayproject.seckill.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;
import java.text.MessageFormat;

@Aspect
@Configuration
public class LogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("@annotation(com.mayproject.seckill.util.MethodLog)")
    public void pointCutMethod() {
    }



    @Around("pointCutMethod()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();
        MethodLog methodLog = method.getAnnotation(MethodLog.class);
        String key = methodLog.methodName();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object o = joinPoint.proceed();
        stopWatch.stop();
        String result = MessageFormat.format("{0}, 耗时:{1}ms\r", key, stopWatch.getTotalTimeMillis());
        LOGGER.info(result);
        System.out.println(result);
        return o;
    }
}
