package ys.cloud.sbot.exchange.binance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ys.cloud.sbot.exchange.PriceApi;
import ys.cloud.sbot.exchange.PriceDataService;
import ys.cloud.sbot.exchange.PriceData;
import ys.cloud.sbot.exchange.binance.model.Ticker24hr;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class BinancePriceDataService implements PriceApi {

    @Autowired
    private BinancePublicService binancePublicService;

    private final Map<String,PriceData> coinDateMap = new ConcurrentHashMap<>();

    public PriceData getPriceData(String id) {
        return coinDateMap.get(id.toUpperCase()) ;
    }

    public Double getBtcValue() {
        return coinDateMap.get("BTC").getPrice();
    }

    public Map<String, PriceData> getAll() {
        return coinDateMap;
    }

    @Scheduled(fixedRate = 60000)
    private void loadPriceData(){

        log.info(">>>> Binance load price data ");

        binancePublicService.getTicker24Hr()
                .flatMapMany(Flux::fromArray)
                .filter(t -> t.getSymbol().endsWith("USDT"))
                .map(ticker24hr ->
                        PriceData.builder()
                                .dateTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(ticker24hr.getCloseTime()), ZoneId.systemDefault()))
                                .symbol(ticker24hr.getSymbol().replace("USDT",""))
                                .name(ticker24hr.getSymbol().replace("USDT",""))
                                .price(_d(ticker24hr.getLastPrice()))
                                .perc24Change(_d(ticker24hr.getPriceChange()))
                                .volume(_d(ticker24hr.getVolume()))
                                .build())
            .subscribe(priceData ->
                    coinDateMap.put(priceData.getSymbol().toUpperCase(),priceData)
            );
    }

    private Double _d(String str) {
        return Double.parseDouble(str);
    }

}
