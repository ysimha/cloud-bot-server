package ys.cloud.sbot.api;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BotActivation {
	
	private boolean loop;
	@NotBlank
	private String name;
	@NotBlank
	private String exchange;
	@NotBlank
    private String botConfigId;
}
