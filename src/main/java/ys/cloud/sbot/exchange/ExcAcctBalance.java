package ys.cloud.sbot.exchange;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcAcctBalance {

	private String id;
	private String name;
	private Double balance;
	private Double available;
	private Double pending;
	private Object cryptoAddress;

	//client additional info
	private Double btcValue;
	private Double fiatValue;
	private Double perc24Change;
	private Double volume;
}
