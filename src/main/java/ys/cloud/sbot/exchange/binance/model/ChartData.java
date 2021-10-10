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
public class ChartData {

	private Long openTime;
	private Double open;
	private Double high;
	private Double low;
	private Double close;
	private Double volume;
	private Long closeTime;
	private Double numberOfTrades;
}
