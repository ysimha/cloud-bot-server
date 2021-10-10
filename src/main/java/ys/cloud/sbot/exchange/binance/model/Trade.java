package ys.cloud.sbot.exchange.binance.model;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class Trade {

	@JsonProperty("symbol")
	private String symbol;
	@JsonProperty("id")
	private Long id;
	@JsonProperty("orderId")
	private Long orderId;
	@JsonProperty("price")
	private Double price;
	@JsonProperty("qty")
	private Double qty;
	@JsonProperty("commission")
	private Double commission;
	@JsonProperty("commissionAsset")
	private String commissionAsset;
	@JsonProperty("time")
	private Long time;
	@JsonProperty("isBuyer")
	private Boolean isBuyer;
	@JsonProperty("isMaker")
	private Boolean isMaker;
	@JsonProperty("isBestMatch")
	private Boolean isBestMatch;
}
