package ys.cloud.sbot.exchange.binance;

import org.springframework.stereotype.Service;
import ys.cloud.sbot.exchange.PriceApi;
import ys.cloud.sbot.exchange.PriceDataService;
import ys.cloud.sbot.exchange.PriceData;

import java.util.Map;

@Service
public class BinancePriceData implements PriceApi {

    public PriceData getPriceData(String id) {
        return null;
    }

    public Double getBtcValue() {
        return null;
    }

//    public PriceData getDollar() {
//        return PriceDataService._getDollar();
//    }

    public Map<String, PriceData> getAll() {
        return null;
    }
}
