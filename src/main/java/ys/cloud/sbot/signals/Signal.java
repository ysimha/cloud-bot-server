package ys.cloud.sbot.signals;


import lombok.*;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Signal {
	
	private String exchange;
	private String quoteAsset;
	private String baseAsset;
	private Double price ;
	private Double stop;
	private Double t1;
	private Double t2;
	private Double t3;
	private String source;

}
