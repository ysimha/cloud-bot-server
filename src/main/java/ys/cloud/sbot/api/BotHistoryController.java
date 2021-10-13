package ys.cloud.sbot.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ys.cloud.sbot.model.history.TradingSessionRecord;
import ys.cloud.sbot.model.history.TradingSessionRecordRepository;
import ys.cloud.sbot.users.UsersBase;

@RestController
@RequestMapping("/history")
public class BotHistoryController  extends UsersBase {

    @Autowired
    TradingSessionRecordRepository tradingSessionRecordRepository;

    @GetMapping(path = "", produces = "application/stream+json")
    public Flux<TradingSessionRecord> get(@AuthenticationPrincipal UsernamePasswordAuthenticationToken principal){
        return 	getProfile(principal)
                .flatMapMany(profile-> tradingSessionRecordRepository.findByProfileId(profile.getId()));
    }
}
