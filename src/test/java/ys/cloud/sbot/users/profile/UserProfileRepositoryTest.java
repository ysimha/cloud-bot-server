package ys.cloud.sbot.users.profile;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserProfileRepositoryTest {

    @Autowired UserProfileRepository userProfileRepository;

    @Test
    void testFindById(){
        String id = "profile-id";
        UserProfile _userProfile = UserProfile.builder().id(id).build();
        userProfileRepository.insert(_userProfile).block();
        UserProfile userProfile = userProfileRepository.findById(id).block();
        assertEquals(id,userProfile.getId());
    }

}