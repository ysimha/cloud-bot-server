package ys.cloud.sbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.users.UserRepository;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Value("${client.url}")
    private String clientUrl;
    private WebFilter securityFilter = new WebFilter() {
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().put("Access-Control-Expose-Headers", Arrays.asList("X-AUTH-TOKEN"));
            response.getHeaders().put("Access-Control-Allow-Headers", Arrays.asList("X-AUTH-TOKEN", "Authorization", "Content-Type"));
            response.getHeaders().put("Access-Control-Allow-Methods", Arrays.asList("GET", "POST", "PATCH", "DELETE", "PUT", "OPTIONS"));
            response.getHeaders().put("Access-Control-Allow-Origin", Arrays.asList(clientUrl));

            return chain.filter(exchange);
        }
    };

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws Exception {

        return http
                .csrf().disable()
                .httpBasic().securityContextRepository(new WebSessionServerSecurityContextRepository())
                .and()
                .authorizeExchange()
                .pathMatchers(HttpMethod.GET, "/*/public/**").permitAll() /*testing*/
                .pathMatchers(HttpMethod.POST, "/signals/simple").permitAll()/*TODO FIXME*/
                .pathMatchers(HttpMethod.GET, "/auth/user").permitAll() /*login*/
                .pathMatchers(HttpMethod.POST, "/auth/register").permitAll() /*login*/

                .pathMatchers(HttpMethod.GET, "/auth/logout").permitAll()
                .pathMatchers(HttpMethod.GET, "/profile").authenticated()
                .pathMatchers(HttpMethod.POST, "/profile/excattc").authenticated()

                .pathMatchers(HttpMethod.GET, "/bot/config/**").authenticated()
                .pathMatchers(HttpMethod.PUT, "/bot/config/**").authenticated()

                .pathMatchers(HttpMethod.GET, "/bot/instance/**").authenticated()
                .pathMatchers(HttpMethod.POST, "/bot/instance/**").authenticated()

                .pathMatchers(HttpMethod.GET, "/charts/**").authenticated()

                .pathMatchers(HttpMethod.GET, "/exchange/account/**").authenticated()

                .pathMatchers(HttpMethod.GET, "/history").authenticated()

                .pathMatchers(HttpMethod.OPTIONS).permitAll()
//	             .anyExchange().permitAll()
                .and()
                .addFilterAt(securityFilter, SecurityWebFiltersOrder.FIRST)
                .build();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository users) {
        return (username) -> users.findByUsername(username)
                .map(u -> User.withUsername(u.getUsername())
                        .password(u.getPassword())
                        .authorities(u.getRoles().toArray(new String[0]))
                        .accountExpired(!u.isActive())
                        .credentialsExpired(!u.isActive())
                        .disabled(!u.isActive())
                        .accountLocked(!u.isActive())
                        .build()
                );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    //@formatter:on
}
