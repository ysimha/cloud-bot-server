package ys.cloud.sbot.exchange.binance.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class NewOrderResponse {
	
	@JsonProperty("symbol")
	private String symbol;
	@JsonProperty("orderId")
	private Long orderId;
	@JsonProperty("clientOrderId")
	private String clientOrderId;
	@JsonProperty("transactTime")
	private Long transactTime;
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
	@JsonProperty("fills")
	private List<Fill> fills = null;
	

}

