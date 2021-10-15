package ys.cloud.sbot.exchange.binance;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.*;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.exchange.ExHelper;
import ys.cloud.sbot.exchange.binance.errors.BinanceApiException;
import ys.cloud.sbot.exchange.binance.model.APIError;

@Service
@Slf4j
public class HttpBinance {
// https://api.binance.com/api/v3/ticker/24hr/BNBBTC
    public static final String BASE_URL = "https://api.binance.com/api/";
	public static final String BASE_URL_US = "https://api.binance.us/api/";
    public static final String ORDER_URL = "v3/order";
	public static final String ACCOUNT_URL = "v3/account";
	public static final String MY_TRADES_URL = "v3/myTrades" ;

	public static final String EXCHANGE_INFO =  BASE_URL_US +"v3/exchangeInfo";
    public static final String CANDLESTICK = BASE_URL_US +"v3/klines";
    public static final String PRICE = BASE_URL_US +"v3/ticker/price";
    public static final String BOOK_TICKER = BASE_URL_US +"v3/ticker/bookTicker";
	public static final String TICKER_24HR = BASE_URL_US + "v3/ticker/24hr";

    // ----------------------- public ------------------------------//
    
    public <T> Mono<T> getResponseJson(String url, Map<String, String> params , Class<T> tClass) {
        ResponseSpec responseSpec = getResponseSpec(url, params);
        return responseSpec.bodyToMono(tClass)
        		.onErrorMap(WebClientResponseException.class, err-> parseError(err,url));
    }

    // ----------------------- generic ---------------------------- //

    public <T> Mono<T> getResponseJson(String url, Map<String, String> params , String key, String secret, Class<T> klass) {
        return getResponseSpec(url, params, key, secret).bodyToMono(klass)
        		.onErrorMap(WebClientResponseException.class, err-> parseError(err,url));
    }

	public <T> Mono<T> postResponseJson(String url, Map<String, String> params, String apikey,String apisecret, Class<T> klass) {
        return postResponseSpec(url, params, apikey, apisecret).bodyToMono(klass)
        		.onErrorMap(WebClientResponseException.class, err-> parseError(err,url));
	}
	
	public <T> Mono<T> deleteResponseJson(String url, Map<String, String> params, String apikey,String apisecret, Class<T> klass) {
		return deleteResponseSpec(url, params, apikey, apisecret).bodyToMono(klass)
        		.onErrorMap(WebClientResponseException.class, err-> parseError(err,url));
	}


	private ResponseSpec getResponseSpec(String url, Map<String, String> params) {
		if (params.isEmpty()==false) {
			String query = buildUriParams(params);
			url = url.concat("?").concat(query);
		}

		ExchangeFilterFunction logHeaders = ExchangeFilterFunction.ofResponseProcessor(response -> {
			response.headers().asHttpHeaders().forEach((name, values) -> {
				values.forEach(value -> {
					if (name.startsWith("x-mbx-used-weight")){
						if (Integer.parseInt(value)>200){
							System.out.println("\nname: "+ name+",  value: "+value+"\n");
						}
					}
				});
			});
			return Mono.just(response);
		});

		//had to increase default buffer size due to
//		exception is org.springframework.core.io.buffer.DataBufferLimitException: Exceeded limit on max bytes to buffer : 104857
//		when calling https://api.binance.com/api/v3/exchangeInfo
		WebClient webClient = WebClient.builder()
					.exchangeStrategies(ExchangeStrategies.builder()
					.codecs(conf -> conf.defaultCodecs()
							.maxInMemorySize( 1024 * 1024 * 4 ))
					.build())
					.baseUrl(url)
					.filter(logHeaders)
					.build();

			return webClient.get()
					.header("Content-Type", "application/x-www-form-urlencoded")
					.retrieve();
	}
    
    private ResponseSpec postResponseSpec(String url, Map<String, String> params, String key, String secret) {
        return  WebClient.create(buildUrl(url, params, secret))
                .post()
                .header("X-MBX-APIKEY", key)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .retrieve();
	}

	private ResponseSpec getResponseSpec(String url,Map<String, String> params, String key, String secret) {
        return  WebClient.create( buildUrl(url, params, secret) )
                .get()
                .header("X-MBX-APIKEY", key)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .retrieve();
    }

	public ResponseSpec deleteResponseSpec(String url, Map<String, String> params, String key,String secret) {
        return  WebClient.create(buildUrl(url, params, secret))
                .delete()
                .header("X-MBX-APIKEY", key)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .retrieve();
	}
	
	private String buildUrl(String url, Map<String, String> params, String secret)  {
		params.put("recvWindow" ,"7000");
		params.put("timestamp" , String.valueOf(System.currentTimeMillis()));

		String query = buildUriParams(params);
		String signature = encode(secret,query);//sha256_B64(query,secret);
		url = url.concat("?").concat(query).concat("&signature=").concat(signature);

		log.debug("\n CALLING URL: "+url);
		return url;
	}

    private String buildUriParams(Map<String, String> params) {
        StringBuilder query = new StringBuilder();
        params.forEach((name,value)-> query.append(name).append("=").append(value).append("&") );
        return  query.toString().substring(0,query.lastIndexOf("&"));
    }

    public static String encode(String key, String data)  {
    	try {
			key = ExHelper.get(key);
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
			sha256_HMAC.init(secret_key);
			return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
    }
    
	private Exception parseError(WebClientResponseException e,String url) {
		try {
			APIError error = new ObjectMapper().readValue( e.getResponseBodyAsString()  , APIError.class); 
			BinanceApiException ex = new BinanceApiException(e,error,url);
			return ex;
		} catch (Exception e1) {
			e1.printStackTrace();
//			throw new RuntimeException(e1);
			return e;
		}
	}
}
