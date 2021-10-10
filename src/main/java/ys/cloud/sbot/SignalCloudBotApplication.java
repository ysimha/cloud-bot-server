package ys.cloud.sbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import ys.cloud.sbot.exchange.ExHelper;

import java.util.Arrays;

@Slf4j
@SpringBootApplication
//@EnableScheduling
public class SignalCloudBotApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(SignalCloudBotApplication.class, args);
		}catch (Throwable throwable){
			throwable.printStackTrace();
			System.exit(1);
		}
	}

	@Bean
	@Profile("!test")
	public CommandLineRunner commandLineRunner(ApplicationContext context){ return args -> {
		log.info("---- could bot application start with args: "+ Arrays.toString(args));
		//	ExHelper.init("x@x~X55X66x3.14x~X@X");
			ExHelper.init("_pass");
		};
	}
}
