package ys.cloud.sbot.exchange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ys.cloud.sbot.exchange.binance.BinancePriceData;
import ys.cloud.sbot.exchange.binance.BinancePublicService;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class PriceDataService {

    @Autowired
    BinancePriceData binancePriceData;

    static PriceData dollar = PriceData.builder().symbol("USD").perc24Change(0.0).volume(0.0)
            .price(1.0).name("USD").dateTime(LocalDateTime.now()).build();

    public PriceData getPriceData(String id, String exchange){
        return getApiService(exchange).getPriceData(id);
    }

    public Double getBtcValue(String exchange){
        return getApiService(exchange).getBtcValue();
    }

    public PriceData getDollar(){ return dollar;}

    Map<String, PriceData> getAll(String exchange){
        return getApiService(exchange).getAll();
    }

    private PriceApi getApiService(String exchange) {
        switch (exchange.toUpperCase()) {
            case "BINANCE":
                return binancePriceData;
            default:
                throw new RuntimeException(exchange);
        }
    }
}
