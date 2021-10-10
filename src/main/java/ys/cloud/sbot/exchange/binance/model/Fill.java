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
public class Fill {

	@JsonProperty("price")
	private Double price;
	@JsonProperty("qty")
	private Double qty;
	@JsonProperty("commission")
	private Double commission;
	@JsonProperty("commissionAsset")
	private String commissionAsset;
	@JsonProperty("tradeId")
	private String tradeId;
}
