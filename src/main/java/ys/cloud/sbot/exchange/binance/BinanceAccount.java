package ys.cloud.sbot.exchange.binance;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ys.cloud.sbot.exchange.binance.model.Balance;


@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BinanceAccount {
    @JsonProperty("makerCommission")
    private Long makerCommission;
    @JsonProperty("takerCommission")
    private Long takerCommission;
    @JsonProperty("buyerCommission")
    private Long buyerCommission;
    @JsonProperty("sellerCommission")
    private Long sellerCommission;
    @JsonProperty("canTrade")
    private Boolean canTrade;
    @JsonProperty("canWithdraw")
    private Boolean canWithdraw;
    @JsonProperty("canDeposit")
    private Boolean canDeposit;
    @JsonProperty("updateTime")
    private Long updateTime;
    @JsonProperty("balances")
    private List<Balance> balances = null;
}
