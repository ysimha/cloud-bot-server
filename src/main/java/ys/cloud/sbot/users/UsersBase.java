package ys.cloud.sbot.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.exceptions.NotAuthenticatedException;
import ys.cloud.sbot.exceptions.ResourceNotFoundException;
import ys.cloud.sbot.users.profile.UserProfile;
import ys.cloud.sbot.users.profile.UserProfileRepository;

public class UsersBase {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    UserProfileRepository userProfileRepository;

    public Mono<User> getUser(UsernamePasswordAuthenticationToken principal) {
        try {
            String username = ((org.springframework.security.core.userdetails.User)principal.getPrincipal()).getUsername();
            return userRepository.findByUsername(username);
        }catch (NullPointerException e){
            throw new NotAuthenticatedException("caller not authenticated");
        }
    }

    public String getUserName(UsernamePasswordAuthenticationToken principal) {
        try {
            return  ((org.springframework.security.core.userdetails.User)principal.getPrincipal()).getUsername();
        }catch (NullPointerException e){
            throw new NotAuthenticatedException("caller not authenticated");
        }
    }

    protected Mono<UserProfile> getProfile(@AuthenticationPrincipal UsernamePasswordAuthenticationToken principal) {
        return userProfileRepository.findById(getUserName(principal))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("profile not found")));
    }
}
