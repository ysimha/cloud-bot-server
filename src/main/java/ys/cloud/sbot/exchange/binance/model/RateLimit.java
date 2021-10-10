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
public class RateLimit {
	@JsonProperty("rateLimitType")
	private String rateLimitType;
	@JsonProperty("interval")
	private String interval;
	@JsonProperty("intervalNum")
	private Integer intervalNum;
	@JsonProperty("limit")
	private Integer limit;
}
