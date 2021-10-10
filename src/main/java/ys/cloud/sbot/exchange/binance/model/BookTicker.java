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
public class BookTicker {

	@JsonProperty("symbol")
	public String symbol;
	@JsonProperty("bidPrice")
	public Double bidPrice;
	@JsonProperty("bidQty")
	public Double bidQty;
	@JsonProperty("askPrice")
	public Double askPrice;
	@JsonProperty("askQty")
	public Double askQty;
	
}
