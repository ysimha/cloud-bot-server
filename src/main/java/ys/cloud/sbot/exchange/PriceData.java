package ys.cloud.sbot.exchange;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;


@Value
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class PriceData implements Serializable {
    private Double perc24Change;
    private String name;
    private String symbol;
    private Double price;
    private Double volume;
    private LocalDateTime dateTime;
}
