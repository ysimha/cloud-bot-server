package ys.cloud.sbot.exchange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ys.cloud.sbot.exchange.binance.BinancePriceDataService;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@Service
public class PriceDataService {

    @Autowired
    BinancePriceDataService binancePriceDataService;

    static PriceData dollar = PriceData.builder().symbol("USD").perc24Change(0.0).volume(0.0)
            .price(1.0).name("USD").dateTime(LocalDateTime.of(1999,1,1,0,0)).build();

    public PriceData getPriceData(String id, String exchange){
        if (id.toUpperCase().equals("USDT")) return dollar;
        return getApiService(exchange).getPriceData(id);
    }

    public Double getBtcValue(String exchange){
        return getApiService(exchange).getBtcValue();
    }

    public PriceData getDollar(){ return dollar;}

    Map<String, PriceData> getAll(String exchange){
        return getApiService(exchange).getAll();
    }

//    Map<String, PriceData> getAll(String exchange);

    private PriceApi getApiService(String exchange) {
        switch (exchange.toUpperCase()) {
            case "BINANCE":
            case "BINANCE_US":
                return binancePriceDataService;
            default:
                throw new RuntimeException("price data service. unknown exchange: " + exchange);
        }
    }
}
