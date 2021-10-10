package ys.cloud.sbot.users;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.exceptions.ConflictException;
import ys.cloud.sbot.exceptions.ResourceExistException;
import ys.cloud.sbot.users.profile.UserProfile;
import ys.cloud.sbot.users.profile.UserProfileRepository;

import javax.validation.Valid;
import java.util.Arrays;

@RestController
@RequestMapping("/auth")
public class AuthController extends UsersBase{

    final private PasswordEncoder passwordEncoder;
    final private UserProfileRepository userProfileRepository;

    public AuthController(PasswordEncoder passwordEncoder, UserProfileRepository userProfileRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userProfileRepository = userProfileRepository;
    }

    @GetMapping("/user")
    public Mono<User> current(@AuthenticationPrincipal UsernamePasswordAuthenticationToken principal) {
        return getUser(principal);
    }

    @GetMapping("/logout")
    public Mono<Void> logout(WebSession session) {
        return session.invalidate();
    }

	@PostMapping("/register")
	public Mono<User> register(Authentication authentication, @RequestBody @Valid Registration user) {

		if (authentication != null && authentication.isAuthenticated()) {
			return Mono.error(
					new ConflictException("authenticated user: " + authentication.getName() + " try to register"));
		}

		return userRepository.findByUsername(user.getUsername()).hasElement()
				.flatMap(b -> (b
						? Mono.error(new ResourceExistException("Username: " + user.getUsername() + " already exist."))
						: Flux.zip(

								Flux.from(userRepository.insert(User.builder().username(user.getUsername())
										.password(passwordEncoder.encode(user.getPassword())).email(user.getUsername())
										.roles(Arrays.asList("ROLE_USER")).build())),

								Flux.from(userProfileRepository
										.insert(UserProfile.builder().id(user.getUsername()).build())))

								.last().flatMap(tp -> Mono.just(tp.getT1()))));
	}

    @GetMapping("/principal")
    public Mono<Authentication> getPrincipal( Mono<Authentication> auMono){
        return auMono;
    }
}
