package ys.cloud.sbot.logic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.exchange.TradeRecord;
import ys.cloud.sbot.exchange.binance.enums.OrderSide;
import ys.cloud.sbot.model.Position;
import ys.cloud.sbot.model.State;
import ys.cloud.sbot.model.history.TradeData;
import ys.cloud.sbot.model.history.TradingSessionRecord;
import ys.cloud.sbot.model.history.TradingSessionRecordRepository;
import ys.cloud.sbot.model.instance.BotInstance;

import java.util.Date;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PositionRecorder {
	
	@Autowired
    TradingSessionRecordRepository tradingSessionRecordRepository;

	public Mono<TradingSessionRecord> saveTradingSession(BotInstance bot) {
		
		State state = bot.getState();
		Position p = state.getPosition();

		TradingSessionRecord pd = TradingSessionRecord.builder()
			.profileId(bot.getProfileId())
			.source(state.getSignal()==null?"":state.getSignal().getSource())
			.symbol(symbol(p.getBuyTrades()))
			.pastGain(Boolean.toString(p.isPastGain()))
			.underStopLoss(Boolean.toString(p.isUnderStopLoss()))
			.priceMax(p.getPriceMax())
			.priceMin(p.getPriceMin())
			.stoploss(p.getStoploss())
			.costAverage(p.getCostAverage())
			.target1(p.getTargets().get(0))
			.target2(p.getTargets().get(1))
			.target3(p.getTargets().get(2))
			.stoplossAmount(p.getStoplossAmount())
			.totalAmountBought(calcTotalAmount(p.getBuyTrades()))
			.totalAmountSold(calcTotalAmount(p.getSellTrades()))
			.avePriceBought(calcAveragePrice(p.getBuyTrades()))
			.avePriceSold(calcAveragePrice(p.getSellTrades()))
			.commissionEnter(calcTotalCommission(p.getBuyTrades()))
			.commissionSell(calcTotalCommission(p.getSellTrades()))
			.build();
	
			pd.setBuyTrades(createTradeData(p.getBuyTrades(), OrderSide.BUY));
			pd.setSellTrades(createTradeData(p.getSellTrades(),OrderSide.SELL));
			
			pd.setTimeStart(new Date(p.getBuyTrades().get(0).getTime()));
			pd.setTimeEnd(new Date(p.getSellTrades().get(p.getSellTrades().size() - 1).getTime()));

		log.debug("saving position data: "+pd);
		
		return tradingSessionRecordRepository.insert(pd);
	}

	private List<TradeData> createTradeData(List<TradeRecord> trades, OrderSide orderSide) {
		
		return trades.stream().map( t->
			TradeData.builder()
				.tradeId(t.getId())
				.orderSide(orderSide.name())
				.symbol(t.getSymbol())
				.orderId(t.getOrderId())
				.price(t.getPrice())
				.qty(t.getQty())
				.commission(t.getCommission())
				.commissionAsset(t.getCommissionAsset())
				.date(new Date(t.getTime()))
				.build())
				.collect(Collectors.toList());

	}

	private String symbol(List<TradeRecord> buyTrades) {
		return buyTrades.get(0).getSymbol();
	}
	
	private double calcTotalCommission(List<TradeRecord> trades) {
		return trades.stream().mapToDouble(TradeRecord::getCommission).sum();
	}

	private double calcAveragePrice(List<TradeRecord> trades) {
		OptionalDouble average = trades.stream().mapToDouble(TradeRecord::getPrice).average();
		return average.isPresent()? average.getAsDouble():0.0;
	}

	private double calcTotalAmount( List<TradeRecord> trades) {
		return trades.stream().mapToDouble(t-> t.getPrice() * t.getQty()).sum();
	}
}
