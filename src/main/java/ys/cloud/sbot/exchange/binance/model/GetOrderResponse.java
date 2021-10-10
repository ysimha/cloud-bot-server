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
public class GetOrderResponse {
	@JsonProperty("symbol")
	private String symbol;
	@JsonProperty("orderId")
	private Long orderId;
	@JsonProperty("clientOrderId")
	private String clientOrderId;
	@JsonProperty("price")
	private Double price;
	@JsonProperty("origQty")
	private Double origQty;
	@JsonProperty("executedQty")
	private Double executedQty;
	@JsonProperty("cummulativeQuoteQty")
	private Double cummulativeQuoteQty;
	@JsonProperty("status")
	private String status;
	@JsonProperty("timeInForce")
	private String timeInForce;
	@JsonProperty("type")
	private String type;
	@JsonProperty("side")
	private String side;
	@JsonProperty("stopPrice")
	private Double stopPrice;
	@JsonProperty("icebergQty")
	private Double icebergQty;
	@JsonProperty("time")
	private Long time;
	@JsonProperty("updateTime")
	private Long updateTime;
	@JsonProperty("isWorking")
	private Boolean isWorking;
}
