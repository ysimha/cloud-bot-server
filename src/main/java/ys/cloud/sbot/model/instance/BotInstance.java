package ys.cloud.sbot.model.instance;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import ys.cloud.sbot.logic.ContextKeys;
import ys.cloud.sbot.model.State;
import ys.cloud.sbot.users.profile.ExchangeAccount;

import java.time.LocalDateTime;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BotInstance {
	
	@Id
    private String id;
	private String name;
	private String profileId;
	private State state;
	private boolean loop;
	private boolean hasOpenOrder;
	private String method;

	@Builder.Default
	private double defaultStoploss = 4;
	@Builder.Default
	private double defaultAmount = 0.01 ;

	private ExchangeAccount exchangeAccount;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
    
	public String botInfo() { return  " , bot instance: "+this.toString();}
	public String profileId() { return  " , profile id: "+this.profileId;}
	
	public static Mono<BotInstance> fromContext() {
		return Mono.subscriberContext().map(ctx-> ctx.get(ContextKeys.BOT));
	}
	
	public  Context subscriberContext(Context ctx) {
		return ctx.put(ContextKeys.BOT, this);
	}
}
