package ys.cloud.sbot.logic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.exchange.TradingService;
import ys.cloud.sbot.exchange.binance.BinanceExchangeInfoService;
import ys.cloud.sbot.exchange.binance.BinanceTickerService;
import ys.cloud.sbot.exchange.binance.enums.OrderSide;
import ys.cloud.sbot.exchange.binance.enums.OrderType;
import ys.cloud.sbot.exchange.binance.enums.TimeInForce;
import ys.cloud.sbot.exchange.binance.model.*;
import ys.cloud.sbot.users.profile.ExchangeAccount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class Trader {
	
	@Autowired TradingService tradingService;
	@Autowired BinanceTickerService binanceTickerService;
	@Autowired BinanceExchangeInfoService binanceExchangeInfoService;

	public Mono<GetOrderResponse> getOrder(ExchangeAccount exchangeAccount, String symbolName, String openOrder_uuid)  {
		log.debug("get order -  symbol: "+symbolName+ ", orderid: "+openOrder_uuid);

		Map<String, String> params = new HashMap<String, String>();
		params.put("symbol", symbolName);
		params.put("orderId", openOrder_uuid);
		params.put("timestamp", Long.toString(System.currentTimeMillis()));
		return tradingService.getOrder(exchangeAccount, params);
	}

	//FIXME code=-1013, msg=Filter failure: MIN_NOTIONAL - price * quantity is too low to be a valid order for the symbol.
	public Mono<NewOrderResponse> buyOrderMarket(ExchangeAccount exchangeAccount, String symbol, double amount)  {
		log.debug("buyOrderMarket -  market: "+ symbol + ", amount: "+amount);
		
		//FIXME get book orders
		Double ask = binanceTickerService.getTicker(symbol).getAsk();
		
		log.debug("ask price:  "+ask);
		
		String quantity = calcQuantity( amount/ask , getLotSize(getSymbol(symbol))) ;
		log.debug("quantity (amount/ask): "+quantity);

		Map<String, String> params = NewOrderParams
				.builder( symbol,
						OrderSide.BUY, 
						OrderType.MARKET, 
						quantity,
						Long.valueOf(System.currentTimeMillis()))
				.build().toMap();
		
		return tradingService.newOrder(exchangeAccount, params);
	}

	//FIXME code=-1013, msg=Filter failure: MIN_NOTIONAL - price * quantity is too low to be a valid order for the symbolName.
	public Mono<NewOrderResponse>  sellOrderLimit(ExchangeAccount exchangeAccount, String symbolName, double limit, double qty) {

		Symbol symbol = getSymbol(symbolName);
		Filter priceFilter = getPriceFilter(symbol);
		Filter lotSize = getLotSize(symbol);
		log.debug("sellOrderLimit -  market: "+symbolName+ ", amount: "+ round(limit,priceFilter));
		
		String quantity = calcQuantity( qty , lotSize) ;
		log.debug("quantity (amount/limit): "+quantity);
		
		Map<String, String> params = NewOrderParams
				.builder( symbolName, 
						OrderSide.SELL, 
						OrderType.LIMIT, 
						quantity,
						Long.valueOf(System.currentTimeMillis()))
				.timeInForce(TimeInForce.GTC.name())
				.price(round(limit,priceFilter))
				.build().toMap();

		return tradingService.newOrder(exchangeAccount, params);
	}

	//FIXME code=-1013, msg=Filter failure: MIN_NOTIONAL  - price * quantity is too low to be a valid order for the symbol.
	public  Mono<NewOrderResponse> sellOrderMarket(ExchangeAccount exchangeAccount, String symbol, Double quantity)  {

		Filter lotSize = getLotSize(getSymbol(symbol));
		log.debug("sellOrderMarket, quantity: "+quantity+", filtered quantity: "+calcQuantity(quantity,lotSize));

		Map<String, String> params = NewOrderParams
				.builder( symbol,
						OrderSide.SELL, 
						OrderType.MARKET, 
						calcQuantity(quantity,lotSize), 
						Long.valueOf(System.currentTimeMillis()))
				.build().toMap();

		return tradingService.newOrder(exchangeAccount, params);
	}

	private Symbol getSymbol(String symbol) {
		return binanceExchangeInfoService.resolveSymbol(symbol);
	}

	public Mono<CancelResponse> cancel(ExchangeAccount exchangeAccount, String symbol, String openOrder_uuid)  {

		log.debug("cancel order -  symbol: "+symbol+ ", order id: "+openOrder_uuid);

		Map<String, String> params = new HashMap<String, String>();
		params.put("symbol", symbol);
		params.put("orderId", openOrder_uuid);
		params.put("timestamp", Long.toString(System.currentTimeMillis()));

		return tradingService.cancelOrder(exchangeAccount, params);
	}

	//---------------------------------------------------------------------------
	
	private static Filter getLotSize(Symbol symbol) {
		return symbol.getFilters().stream().filter(f->f.getFilterType().equals("LOT_SIZE")).findFirst().get();
	}
	
	public static Filter getPriceFilter(Symbol symbol) {
		return symbol.getFilters().stream().filter(f->f.getFilterType().equals("PRICE_FILTER")).findFirst().get();
	}
	
	public static String calcQuantity(Double quantity, Filter lOT_SIZE) {
		if (lOT_SIZE == null) {
			return quantity.toString().substring(0, 6);
		} 
		if ( lOT_SIZE.getMaxQty() < quantity || lOT_SIZE.getMinQty() > quantity ) {
			throw new RuntimeException("quantity: " + quantity + ", " + lOT_SIZE);
		}
		return scale(quantity, lOT_SIZE.getStepSize());
	}
	
	private static String scale(Double value, double num) {
		int scale = 0;
		while (num < 1) {
		    num *= 10;
		    scale++;
		}
		BigDecimal rawValue = BigDecimal.valueOf(value);
		BigDecimal round = rawValue.setScale(scale, RoundingMode.FLOOR);
		return round.toString();
	}

	static public String round(double value, Filter RICE_FILTER) {
		BigDecimal rawValue = BigDecimal.valueOf(value);
		BigDecimal round = rawValue.setScale( extractScale(RICE_FILTER) , RoundingMode.FLOOR);
		return round.toPlainString();
	}

	static private int extractScale(Filter PRICE_FILTER) {
		Double tickSize = PRICE_FILTER.getTickSize();
		String text = new DecimalFormat("#.########").format(tickSize);
		int integerPlaces = text.indexOf('.');
		return text.length() - integerPlaces - 1;
	}
}
