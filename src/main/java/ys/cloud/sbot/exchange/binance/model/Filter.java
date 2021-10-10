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
public class Filter {
	@JsonProperty("filterType")
	private String filterType;
	@JsonProperty("minPrice")
	private Double minPrice;
	@JsonProperty("maxPrice")
	private Double maxPrice;
	@JsonProperty("tickSize")
	private Double tickSize;
	@JsonProperty("minQty")
	private Double minQty;
	@JsonProperty("maxQty")
	private Double maxQty;
	@JsonProperty("stepSize")
	private Double stepSize;
	@JsonProperty("minNotional")
	private Double minNotional;
	@JsonProperty("limit")
	private Long limit;
	@JsonProperty("maxNumAlgoOrders")
	private Long maxNumAlgoOrders;
}
