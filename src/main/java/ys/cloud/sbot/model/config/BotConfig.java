package ys.cloud.sbot.model.config;

import lombok.*;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;


@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BotConfig {
	
	@Id
    private String id;
	
    private String profileId;
    
	@NotBlank
	private String name ;
	
	private Double stoploss  ;
	
	private Double defaultAmount;
	
	private Boolean costAverage;
	
	private Boolean useSignalSL;
	
	private Boolean useSignalTargets;

}


