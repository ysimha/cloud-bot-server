package ys.cloud.sbot.exchange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ys.cloud.sbot.exchange.binance.BinanceTickerService;

@Service
 class TickerServiceAdaptor {

//    @Autowired BinanceTickerService binanceTickerService;
//
//    public Ticker getTicker(String market,String exchange){
//        return getTickerService(exchange).getTicker(market);
//    };
//
//    public Double getBtcValue(String asset,String exchange){
//        return  getTickerService(exchange).getBtcValue(asset);
//    };
//
//    public Double getUsdValue(String asset,String exchange){
//        return getTickerService(exchange).getUsdValue(asset);
//    };
//
//    private TickerService getTickerService(String exchange) {
//        switch (exchange.toUpperCase()) {
//            case "BINANCE":
//                return binanceTickerService;
//            default:
//                throw new RuntimeException(exchange);
//        }
//    }
}
