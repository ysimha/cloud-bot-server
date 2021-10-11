package ys.cloud.sbot.model.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ys.cloud.sbot.model.State;
import ys.cloud.sbot.model.instance.BotInstance;

import java.lang.reflect.Method;

@Aspect
@Component
public class BotInstanceDBAspect {

	@Around("@annotation(BotInstanceDBHandler)")
	public Object processBotInstance(ProceedingJoinPoint joinPoint) throws Throwable {
		
		Method method = ( (MethodSignature) joinPoint.getSignature()).getMethod();
		BotInstanceDBHandler annotation = method.getAnnotation(BotInstanceDBHandler.class);
		if (annotation==null) {
			return joinPoint.proceed();
		}
		
		boolean isSave = annotation.isSave();
	    
	    if (isSave) {
	    	BotInstance instance = (BotInstance) joinPoint.getArgs()[0];
	    	instance.setHasOpenOrder(false);
	    	State state = instance.getState();
			if (state != null) {
				if ( state.getOpenBuyOrder()!=null) {
					instance.setHasOpenOrder(true);
				}
				return joinPoint.proceed(new Object[]{instance});
			}
		}
		return joinPoint.proceed();
	}
}
