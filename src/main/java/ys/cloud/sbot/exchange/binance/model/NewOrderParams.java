package ys.cloud.sbot.exchange.binance.model;

import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ys.cloud.sbot.exchange.binance.enums.OrderSide;
import ys.cloud.sbot.exchange.binance.enums.OrderType;


@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "hiddenBuilder")
public class NewOrderParams{

	private	String	symbol;	// required	YES	
	private	String	side;	// required	YES	
	private	String	type;	// required	YES	
	private	String	timeInForce	;// required	NO	
	private	String	quantity;	// required	YES	
	private	String	price;	// required	NO	
	private	String	newClientOrderId;	// required	NO	A unique id for the order. Automatically generated if not sent.
	private	Double	stopPrice;	// required	NO	Used with STOP_LOSS, STOP_LOSS_LIMIT, TAKE_PROFIT, and TAKE_PROFIT_LIMIT orders.
	private	Double	icebergQty	;// required	NO	Used with LIMIT, STOP_LOSS_LIMIT, and TAKE_PROFIT_LIMIT to create an iceberg order.
	private	String	newOrderRespType;	// required	NO	Set the response JSON. ACK, RESULT, or FULL; MARKET and LIMIT order types default to FULL, all other orders default to ACK.
	private	Long	recvWindow;	// required	NO	
	private	Long	timestamp	;// required	YES	
	
    public static NewOrderParamsBuilder builder(String symbol,OrderSide side , OrderType type, String quantity,Long timestamp) {
        return hiddenBuilder().symbol(symbol).side(side.name()).type(type.name()).quantity(quantity).timestamp(timestamp);
    }
    
	@SuppressWarnings("unchecked")
	public Map<String,String> toMap(){
        Map<String,Object> map = new ObjectMapper().convertValue(this, Map.class);
        return map.entrySet().stream().filter(e->e.getValue()!=null)
  		      .collect(Collectors.toMap(	  
  		      e -> e.getKey().toString(),
  		      e -> e.getValue().toString()
  		  ));
	}

//	private String round(Double value) {
//		return value.toString().substring(0, 6);
//	}
}
