package ys.cloud.sbot.signals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ys.cloud.sbot.logic.ActiveBotsService;

import javax.validation.Valid;

@RestController
@RequestMapping("/signals")
public class SignalListenerController {
	
	@Autowired ActiveBotsService activeBotService;
	
	@PostMapping("simple") //FIXME need special permissions
	public void simple(@RequestBody @Valid  Signal signal){
		activeBotService.onSignal(signal);
	}

}
