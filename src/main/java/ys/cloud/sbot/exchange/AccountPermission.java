package ys.cloud.sbot.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountPermission {
	
    @JsonProperty("canTrade")
    private Boolean canTrade;
    @JsonProperty("canWithdraw")
    private Boolean canWithdraw;
    @JsonProperty("canDeposit")
    private Boolean canDeposit;
    
}
