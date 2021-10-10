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
public class APIError {
	@JsonProperty("code")
	public Integer code;
	@JsonProperty("msg")
	public String msg;
}
