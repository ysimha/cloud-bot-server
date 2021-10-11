package ys.cloud.sbot.model.history;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.Date;


@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeData {
	
	@Id
    private Long id;
	
	private Long tradeId ;
	private String orderSide;
	private String symbol;
	private Long orderId;
	private Double price;
	private Double qty;
	private Double commission;
	private String commissionAsset;
	private Date date;
	
}
