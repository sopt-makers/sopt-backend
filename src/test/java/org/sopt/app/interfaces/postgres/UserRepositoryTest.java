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

    User user1 = new User();
    User user2 = new User();

    @BeforeEach
    void beforeTest() {
        final Long user1PlaygroundId = Long.MAX_VALUE;
        final Long user2PlaygroundId = Long.MAX_VALUE - 1;

        user1 = userRepository.save(
                User.builder()
                        .playgroundId(user1PlaygroundId)
                        .build()
        );
        user2 = userRepository.save(
                User.builder()
                        .playgroundId(user2PlaygroundId)
                        .build()
        );
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

    /* TODO: Implement the following test cases
    @Test
    void findAllIdByPlaygroundIdIn() {
    }

    @Test
    void findAllPlaygroundId() {
    }

    @Test
    void findAllByIdIn() {
    }

     */
}