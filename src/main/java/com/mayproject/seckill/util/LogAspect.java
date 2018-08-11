package com.mayproject.seckill.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;

@Aspect
@Configuration
public class LogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);

    @Around("@annotation(com.mayproject.seckill.util.MethodLog)")
    public void doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch clock = new StopWatch();
        clock.start();
        try {
            joinPoint.proceed();
        }catch (Exception e){
            throw e;
        }finally {
            clock.stop();
            long totalTime = clock.getTotalTimeMillis();
            String targetName = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            Object[] arguments = joinPoint.getArgs();
            Class targetClass = Class.forName(targetName);
            Method[] methods = targetClass.getMethods();
            String processName = new String();
            if (methods.length>0) {
                for (Method method : methods) {
                    if (method.getName().equals(methodName)) {
                        Class[] clazzs = method.getParameterTypes();
                        if (clazzs.length == arguments.length) {
                            if (method.getAnnotation(MethodLog.class) != null) {
                                processName = method.getAnnotation(MethodLog.class).methodName();
                                break;
                            }
                        }
                    }
                }
            }
            System.out.println(processName + " 耗时：" + totalTime + "ms");
            LOGGER.info("耗时：" + totalTime + "ms");
        }
    }

//    @Before("@annotation(com.mayproject.seckill.util.MethodLog)")
//    public void before(){
//        System.out.println("123");
//    }
}
