package ys.cloud.sbot.model;


import lombok.*;
import ys.cloud.sbot.exchange.binance.model.NewOrderResponse;
import ys.cloud.sbot.exchange.binance.model.Symbol;
import ys.cloud.sbot.signals.Signal;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class State {

	private double currentAmount = 0.0;
	private Symbol symbol;	
	private Position position =null;
	
	private NewOrderResponse openBuyOrder ;
	private NewOrderResponse openSellOrder ;
	
	boolean costAverage = false;
	boolean costAverageArm = false ;
	
	boolean lastTargetTrailing = false;
	
	private String fileEntry;
	
	private Signal signal;
	
	private boolean canUpdate = true;
}
