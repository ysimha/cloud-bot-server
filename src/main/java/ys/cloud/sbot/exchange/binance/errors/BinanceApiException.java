package ys.cloud.sbot.exchange.binance.errors;

import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ys.cloud.sbot.exchange.binance.model.APIError;

@Data
@ToString
@EqualsAndHashCode(callSuper=true)
public class BinanceApiException extends RuntimeException {

	private static final long serialVersionUID = 7144535941181085586L;

	private APIError error;
	private String url;

	public BinanceApiException(WebClientResponseException e, APIError error ,String url) {
		super(e.getMessage()+", "+error.toString()+", url: "+url, e);
		this.setError(error);
		this.setUrl(url);
	}
}
