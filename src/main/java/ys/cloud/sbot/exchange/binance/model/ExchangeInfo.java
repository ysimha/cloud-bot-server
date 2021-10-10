package ys.cloud.sbot.exchange.binance.model;

import java.util.List;

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
public class ExchangeInfo {

	@JsonProperty("timezone")
	private String timezone;
	@JsonProperty("serverTime")
	private Long serverTime;
	@JsonProperty("rateLimits")
	private List<RateLimit> rateLimits = null;
	@JsonProperty("exchangeFilters")
	private List<Object> exchangeFilters = null;
	@JsonProperty("symbols")
	private List<Symbol> symbols = null;
}
