package ys.cloud.sbot.exchange.binance.log;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

@Slf4j
public class BinanceHitCounter {

    static private final AtomicInteger count = new AtomicInteger(0);
    static private final AtomicInteger currentSec = new AtomicInteger(-1);
    static private final AtomicInteger max = new AtomicInteger(0);
    static private Date maxTime  = new Date();

    //don't need to be 100% accurate - skip thread safety
    static public void hit(int weight){
        int now = LocalTime.now().getMinute();
        if ( currentSec.getAndSet(now) != now){
            int count = BinanceHitCounter.count.getAndSet(0);
            max.set(Math.max(max.intValue(),count));
            maxTime = new Date();
            log.info("=> Binance Hit Counter: "+ count + " max: "+max.intValue()+", at minute before "+now);
            System.out.println("=> Binance Hit Counter: "+ count + " max: "+max.intValue()+ " max time: "+maxTime+", at minute before "+now);
        }
      count.getAndAdd(weight);
    }
}
