package ys.cloud.sbot.exchange;

import java.util.Map;

public interface PriceApi {

    public PriceData getPriceData(String id) ;
    public Double getBtcValue() ;
//    public PriceData getDollar() ;
    public Map<String, PriceData> getAll() ;
}
