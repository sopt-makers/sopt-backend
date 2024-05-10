package org.sopt.app.interfaces.postgres;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.app.common.config.QuerydslConfiguration;
import org.sopt.app.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(QuerydslConfiguration.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

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
    @DisplayName("SUCCESS_유저 아이디로 유저 찾기")
    void SUCCESS_findUserById() {
        Assertions.assertEquals(Optional.of(user1), userRepository.findUserById(user1.getId()));
    }

    @Test
    @DisplayName("SUCCESS_플레이그라운드 아이디로 유저 찾기")
    void SUCCESS_findUserByPlaygroundId() {
        Assertions.assertEquals(Optional.of(user1), userRepository.findUserByPlaygroundId(user1.getPlaygroundId()));
    }

    @Test
    @DisplayName("SUCCESS_플레이그라운드 아이디 리스트로 유저 리스트 찾기")
    void SUCCESS_findAllByPlaygroundIdIn() {
        //given
        final List<Long> playgroundIds = List.of(user1.getPlaygroundId(), user2.getPlaygroundId());

        //then
        Assertions.assertEquals(List.of(user1, user2), userRepository.findAllByPlaygroundIdIn(playgroundIds));
    }


    @Test
    @DisplayName("SUCCESS_플레이그라운드 아이디 리스트로 유저 아이디 리스트 찾기")
    void SUCCESS_findAllIdByPlaygroundIdIn() {
        //given
        final List<Long> playgroundIds = List.of(user1.getPlaygroundId(), user2.getPlaygroundId());

        //then
        Assertions.assertEquals(List.of(user1.getId(), user2.getId()),
                userRepository.findAllIdByPlaygroundIdIn(playgroundIds));
    }


    @Test
    @DisplayName("SUCCESS_모든 유저의 플레이그라운드 아이디 찾기")
    void SUCCESS_findAllPlaygroundId() {
        //given
        final List<Long> playgroundIds = List.of(user1.getPlaygroundId(), user2.getPlaygroundId());

        //then
        Assertions.assertTrue(userRepository.findAllPlaygroundId().containsAll(playgroundIds));
    }

    @Test
    @DisplayName("SUCCESS_모든 유저의 유저 아이디 찾기")
    void SUCCESS_findAllByIdIn() {
        //given
        final List<Long> userIds = List.of(user1.getId(), user2.getId());

        //then
        Assertions.assertEquals(List.of(user1, user2), userRepository.findAllByIdIn(userIds));
    }
}