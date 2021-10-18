package ys.cloud.sbot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.List;

@Configuration
//@EnableMongoAuditing
//@EnableReactiveMongoAuditing
public class AuditConfiguration {

//    @Bean
//    public AuditorAware<Username> auditor() {
//        return () -> ReactiveSecurityContextHolder.getContext()
//            .map(SecurityContext::getAuthentication)
////            .log()
//            .filter(auth -> auth != null && auth.isAuthenticated())
//            .map(Authentication::getPrincipal)
//            .cast(UserDetails.class)
//            .map( auth -> new Username(auth.getUsername() ) )
//            .switchIfEmpty(Mono.empty())
//            .blockOptional();
//    }
}
