package ys.cloud.sbot.model.history;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradingSessionRecord {

//	public TradingSessionRecord(){};
	
	@Id
	private String id;
	private String profileId;

	@CreatedDate
	private LocalDateTime createdDate;

//	@Builder.Default
    private List<TradeData> buyTrades ;//= new ArrayList<TradeData>();
//	@Builder.Default
    private List<TradeData> sellTrades ;//= new ArrayList<TradeData>();
    
	private String source;
	private String symbol;

	private String pastGain;
	private String underStopLoss ;
	
	private double priceMax ;
	private double priceMin ;
	private double stoploss ;
	private double costAverage ;
	private double target1 ;
	private double target2 ;
	private double target3 ;
	private double stoplossAmount;
	private double totalAmountBought;
	private double totalAmountSold;
	private double avePriceBought;
	private double avePriceSold;
	private double commissionEnter;
	private double commissionSell;

	private Date timeStart;
	private Date timeEnd;
}
