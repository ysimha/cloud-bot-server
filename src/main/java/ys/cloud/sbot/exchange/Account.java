package ys.cloud.sbot.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ys.cloud.sbot.exchange.binance.model.Balance;

import java.util.List;


@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
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
