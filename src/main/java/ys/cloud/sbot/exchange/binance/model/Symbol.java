package ys.cloud.sbot.exchange.binance.model;

import java.util.List;

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
public class Symbol {

	@JsonProperty("symbol")
	private String symbol;
	@JsonProperty("status")
	private String status;
	@JsonProperty("baseAsset")
	private String baseAsset;
	@JsonProperty("baseAssetPrecision")
	private Integer baseAssetPrecision;
	@JsonProperty("quoteAsset")
	private String quoteAsset;
	@JsonProperty("quotePrecision")
	private Integer quotePrecision;
	@JsonProperty("quoteAssetPrecision")
	private Integer quoteAssetPrecision;
	@JsonProperty("baseCommissionPrecision")
	private Integer baseCommissionPrecision;
	@JsonProperty("quoteCommissionPrecision")
	private Integer quoteCommissionPrecision;
	@JsonProperty("orderTypes")
	@Builder.Default
	private List<String> orderTypes = null;
	@JsonProperty("icebergAllowed")
	private Boolean icebergAllowed;
	@JsonProperty("ocoAllowed")
	private Boolean ocoAllowed;
	@JsonProperty("quoteOrderQtyMarketAllowed")
	private Boolean quoteOrderQtyMarketAllowed;
	@JsonProperty("isSpotTradingAllowed")
	private Boolean isSpotTradingAllowed;
	@JsonProperty("isMarginTradingAllowed")
	private Boolean isMarginTradingAllowed;
	@JsonProperty("filters")
	@Builder.Default
	private List<Filter> filters = null;
	@JsonProperty("permissions")
	private List<String> permissions = null;
}
