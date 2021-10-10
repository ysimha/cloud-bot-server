package ys.cloud.sbot.exchange;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Balance {

	private String id;
	private String name;
	private Double balance;
	private Double available;
	private Double pending;
	private Object cryptoAddress;
}
