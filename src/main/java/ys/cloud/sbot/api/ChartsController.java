package ys.cloud.sbot.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.exchange.PublicService;
import ys.cloud.sbot.exchange.binance.model.ChartData;

import java.util.Arrays;

@RestController
@RequestMapping("/charts")
public class ChartsController  {

    @Autowired PublicService publicService ;

    @GetMapping()
    public Mono<ChartData[]> chartData(@AuthenticationPrincipal UsernamePasswordAuthenticationToken principal ,
                                       String exchange, String symbol, String interval){
        return publicService.chartData(exchange,symbol,interval);
    }

    @GetMapping("/tail")
    public Mono<ChartData[]> tailChartData(@AuthenticationPrincipal UsernamePasswordAuthenticationToken principal ,
                                       String exchange, String symbol, String interval, Long last){

        return publicService.chartData(exchange,symbol,interval)
                .map(arr-> Arrays.stream(arr).filter(tick->tick.getOpenTime()>=last).toArray(ChartData[]::new));
    }
}
