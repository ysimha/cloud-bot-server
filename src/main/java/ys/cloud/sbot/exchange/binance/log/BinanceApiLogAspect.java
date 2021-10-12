package ys.cloud.sbot.exchange.binance.log;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import lombok.extern.java.Log;

@Slf4j
@Aspect
@Component
public class BinanceApiLogAspect {

	@Around("@annotation(BinanceApiLogHandler)")
	public Object catchAPIError(ProceedingJoinPoint joinPoint) throws Throwable {
		
		Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
		Type type = ((MethodSignature) joinPoint.getSignature()).getDeclaringType();
		BinanceApiLogHandler annotation = method.getAnnotation(BinanceApiLogHandler.class);
		if (annotation==null) {
			return joinPoint.proceed();
		}

		BinanceHitCounter.hit(annotation.weight());
		
		boolean logResponse = annotation.logResponse();
	    boolean logUrl =  annotation.logUrl();
	    
	    if (logUrl) {
//			Object url = joinPoint.getArgs()[0];
//			log.info(url.toString());
			log.debug("API CALL ["+annotation.weight()+"]" + type+ " "+ method.getName());
		}

		return joinPoint.proceed();
	}
}
