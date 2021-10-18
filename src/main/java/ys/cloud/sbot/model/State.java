package ys.cloud.sbot.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ys.cloud.sbot.exchange.binance.model.NewOrderResponse;
import ys.cloud.sbot.signals.Signal;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
//@AllArgsConstructor
public class State {

	private Signal signal;
	private String symbol;

	public State(Signal signal, String symbol) {
		this.signal = signal;
		this.symbol = symbol;
	}

	private double currentAmount = 0.0;
	private Position position =null;
	
	private NewOrderResponse openBuyOrder ;
	private NewOrderResponse openSellOrder ;

	private boolean costAverage = false;
	private boolean costAverageArm = false ;
	private boolean lastTargetTrailing = false;
	private String fileEntry;
	private boolean canUpdate = true;
}
