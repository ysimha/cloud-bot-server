package ys.cloud.sbot.exchange;

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
@Builder(toBuilder=true)
public class TradeRecord {

	public TradeRecord(Double price, Double qty, Double commission) {
		super();
		this.price = price;
		this.qty = qty;
		this.commission = commission;
	}
	
	private String symbol;
	private Long id;
	private Long orderId;
	private Double price;
	private Double qty;
	private Double commission;
	private String commissionAsset;
	private Long time;
	private Boolean isBuyer;
	private Boolean isMaker;
	private Boolean isBestMatch;
}
