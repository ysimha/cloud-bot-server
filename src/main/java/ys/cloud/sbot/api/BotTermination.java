package ys.cloud.sbot.api;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class BotTermination {

	@NotBlank
    private String instanceId;
	
	private Boolean sell = false ;
}
