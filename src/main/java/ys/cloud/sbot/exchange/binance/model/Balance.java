package ys.cloud.sbot.exchange.binance.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Balance {

    @JsonProperty("asset")
    private String asset;
    @JsonProperty("free")
    private Double free;
    @JsonProperty("locked")
    private Double locked;

    public Double getBalance(){return free+locked;}
}
