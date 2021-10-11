package ys.cloud.sbot.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import ys.cloud.sbot.exchange.binance.enums.OrderSide;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenOrder {

	@Id
	private String id;
	private String botId;
	private OrderSide orderSide;
	private String orderId;
	private String symbol;

}
