package org.sopt.app.interfaces.postgres;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.app.common.config.QuerydslConfiguration;
import org.sopt.app.domain.entity.Friend;
import org.sopt.app.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(QuerydslConfiguration.class)
class FriendRepositoryTest {

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserRepository userRepository;

    User user1 = new User();
    User user2 = new User();

    Friend friend = new Friend();

    @BeforeEach
    void beforeTest() {

        user1 = userRepository.save(
                User.builder()
                        .playgroundId(generateUniquePlaygroundId())
                        .build()
        );
        user2 = userRepository.save(
                User.builder()
                        .playgroundId(generateUniquePlaygroundId())
                        .build()
        );

        friend = friendRepository.save(Friend.builder().userId(user1.getId()).friendUserId(user2.getId()).build());
    }

    private Long generateUniquePlaygroundId() {
        long playgroundId = Long.MAX_VALUE;

        for (int i = 0; i < 1000; i++) {
            if (userRepository.findUserByPlaygroundId(playgroundId).isEmpty()) {
                break;
            }
            playgroundId = playgroundId - i;
        }
        return playgroundId;
    }

    @Test
    @DisplayName("SUCCESS_유저 아이디로 친구 찾기")
    void findAllByFriendUserIdSuccess() {
        Assertions.assertEquals(List.of(friend), friendRepository.findAllByFriendUserId(user2.getId()));
    }

    @Test
    @DisplayName("SUCCESS_유저 아이디와 친구 아이디로 친구 찾기")
    void findAllByUserIdAndFriendUserIdInSuccess() {
        Assertions.assertEquals(List.of(friend), friendRepository.findAllByUserIdAndFriendUserIdIn(user1.getId(), List.of(user2.getId())));
    }

}