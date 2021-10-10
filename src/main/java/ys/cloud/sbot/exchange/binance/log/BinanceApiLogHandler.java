package ys.cloud.sbot.exchange.binance.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BinanceApiLogHandler {
	public boolean logResponse() default true;
	public boolean logUrl() default true;
	public int weight() ;
}
