package ys.cloud.sbot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
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
