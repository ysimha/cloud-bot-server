package ys.cloud.sbot.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ys.cloud.sbot.exchange.Ticker;
import ys.cloud.sbot.exchange.TradeRecord;
import ys.cloud.sbot.exchange.binance.model.Filter;
import ys.cloud.sbot.exchange.binance.model.Symbol;
import ys.cloud.sbot.logic.Trader;

import java.util.LinkedList;
import java.util.List;

@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Position {

    private Integer id;
	private List<TradeRecord> buyTrades = new LinkedList<>();
	private List<TradeRecord> sellTrades = new LinkedList<>();
	private Ticker lastTicker;

	private boolean pastGain ;
	private boolean underStopLoss ;
	
	private double priceMax = 0.0;
	private double priceMin = Double.MAX_VALUE;

	private double originalStoploss ;
	private double stoploss ;
	private boolean useTrailingStoploss;

	private double costAverage ;
	private List<Double> targets = new LinkedList<>();

	private double stoplossAmount;
	
	private boolean lastTargetTrailing;

	public Position(Double enterPrice, Double defaultStoploss, State state , Symbol symbol) {
		this.initialize(enterPrice, defaultStoploss,state, symbol);
	}
	
	public void initialize(Double enterPrice, Double defaultStoploss, State state , Symbol symbol) {

		double factor = defaultStoploss/100;

		Double stop = enterPrice - (enterPrice * (factor)); 

		double t1 = enterPrice * 1.0135;
		double t2 = enterPrice * 1.02;
		double t3 = enterPrice + (enterPrice * ((factor*100)/100));

		state.setCanUpdate(true);
		Filter priceFilter = Trader.getPriceFilter(symbol);
	
		t1 = _d( Trader.round(t1, priceFilter));
		if (t1 <= enterPrice) {
			t1 = enterPrice + priceFilter.getTickSize();
			state.setCanUpdate(false);
		}
		
		t2 = _d( Trader.round(t2, priceFilter) );
		if (t2 <= t1) {
			t2 = t1 + priceFilter.getTickSize();
			state.setCanUpdate(false);
		}
		
		t3 = _d(Trader.round(t3, priceFilter));
		if (t3 <= t2) {
			t3 = t2 + priceFilter.getTickSize();
			state.setCanUpdate(false);
		}
		
		this.setStoploss(_d(Trader.round(stop, priceFilter)));
		this.setOriginalStoploss(this.getStoploss());
		this.getTargets().add(t1);
        this.getTargets().add(t2);
        this.getTargets().add(t3);

		this.setCostAverage((stop+enterPrice)/2);
		
		this.setStoplossAmount(enterPrice-stop);
	}
	
	public double nextTarget() {
	    if ( sellTrades.size() < targets.size() ){
	        return targets.get(sellTrades.size());
        }else{
	        return Double.NaN;
        }
	}
	
	public double nextTargetQuantityToSell() {
		switch (sellTrades.size()) {
		case 0:
			return totalQuantity()/2.5;
		case 1:
			return totalQuantity()/2;
		case 2:
			return totalQuantity();
		default:
			return Double.NaN;
		}
	}

	public void updateTicker(Ticker ticker) {

		setLastTicker(ticker);
		
		priceMin = min(priceMin,ticker.getLast());
		priceMax = max(priceMax,ticker.getLast());

		if (sellTrades.size() > 0) {
			double lastStoploss = this.stoploss;
			double newStoploss = ticker.getBid() - stoplossAmount;
			this.stoploss = max(stoploss,newStoploss);
			if (this.stoploss != lastStoploss) {
				log.debug("update stop loss. old value: "+lastStoploss + ", new value: "+this.stoploss);
			}
		}
	}
	
//	public void updateStops() {
//		log.info("update stops values");
//		this.stoploss *= 1.001;
//		this.costAverage *= 1.001;
//
//        for (final ListIterator<Double> i = this.targets.listIterator(); i.hasNext();) {
//            final Double target = i.next();
//            i.set(target*0.999);
//        }
//	}
	
//	public double percentChange() {
//		return (( lastTicker.getLast()/avePriceEnter()) *100.0 )-100;
//	}
	
//	public double avePriceEnter() {
//		return totalInvested() / totalQuantityBuy();
//	}

	public double totalInvested() {
		return buyTrades.stream().mapToDouble(t->  t.getQty() * t.getPrice() ).sum();
	}
	
	private double totalQuantityBuy() {
		return buyTrades.stream().mapToDouble(TradeRecord::getQty).sum();
	}
	
	private double totalQuantitySold() {
		return sellTrades.stream().mapToDouble(TradeRecord::getQty).sum();
	}
	
	public double totalQuantity() {
		return totalQuantityBuy() - totalQuantitySold();
	}

	static private double max(double a, double b) {return Math.max(a, b);}

	static private double min(double a, double b) {
		return Math.min(a, b);
	}
	
	static private double _d(String s) {
		return Double.parseDouble(s);
	}

	public boolean isDone() {
		return isUnderStopLoss() || getSellTrades().size() > 2;
	}
}
