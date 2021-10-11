package ys.cloud.sbot.prices;

import java.time.LocalDateTime;
import java.util.Map;

public interface IPriceDataService {

    PriceData dollar = PriceData.builder().symbol("USD").perc24Change(0.0).volume(0.0)
            .price(1.0).name("USD").dateTime(LocalDateTime.now()).build();

    PriceData getPriceData(String id, String exchange);

    Double getBtcValue(String exchange);

    PriceData getDollar();

    static  PriceData _getDollar() {
        return dollar;
    }

    Map<String, PriceData> getAll(String exchange);
}
