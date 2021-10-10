package ys.cloud.sbot.exchange.binance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelResponse {
	private String symbol;
	private String origClientOrderId;
	private Integer orderId;
	private String clientOrderId;
}
