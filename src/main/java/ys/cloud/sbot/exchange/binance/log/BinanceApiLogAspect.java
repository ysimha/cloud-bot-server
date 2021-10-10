package ys.cloud.sbot.exchange.binance.log;

import java.lang.reflect.Method;
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
	
//	private Map<String, String> ignoreList = new HashMap<String,String>();
	
//	@PostConstruct
//	private void populatIgnoreList() {
//		ignoreList.put("getPrivate", "https://api.binance.com/api/v3/order");
//	}

	@Around("@annotation(BinanceApiLogHandler)")
	public Object catchAPIError(ProceedingJoinPoint joinPoint) throws Throwable {
		
		Method method = ( (MethodSignature) joinPoint.getSignature()).getMethod();
		BinanceApiLogHandler annotation = method.getAnnotation(BinanceApiLogHandler.class);
		if (annotation==null) {
			return joinPoint.proceed();
		}

		BinanceHitCounter.hit(annotation.weight());
		
		boolean logRespone = annotation.logResponse();
	    boolean logUrl =  annotation.logUrl();
	    
	    if (logUrl) {
//			Object url = joinPoint.getArgs()[0];
//			log.info(url.toString());
			log.debug("API CALL ["+annotation.weight()+"]" +method.getName());
		}

		return joinPoint.proceed();
	}
}
